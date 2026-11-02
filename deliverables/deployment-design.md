# Deployment design (AWS)

I did not deploy the app. This is how I would, on AWS, for a small internal tool. Cost figures are rough estimates from memory, not quotes, and should be checked in the AWS Pricing Calculator.

## Architecture

```
Browser
  |  HTTPS
  v
CloudFront (ACM certificate, optional custom domain)
  |-- /*      --> S3 bucket (React build, private, reached through CloudFront)
  |-- /api/*  --> App Runner service (Spring Boot container from ECR)
                    |-- RDS PostgreSQL (private subnets, via VPC connector)
                    |-- Secrets Manager (Gemini API key)
                    |-- Gemini API over the internet (or Amazon Bedrock, see below)
                    '-- CloudWatch Logs
```

## Components and why

| Component | Choice | Reason |
|---|---|---|
| Front end hosting | S3 and CloudFront | Static files are cheap and fast. Serving `/api/*` from the same CloudFront domain means one origin, so no CORS configuration, matching the proxy used in development |
| Back end hosting | AWS App Runner running the Spring Boot container (ECS Fargate if App Runner isn't available in the chosen region) | Runs a container with HTTPS, scaling and deploys without managing servers. Fargate is the fallback with more setup |
| Database | RDS PostgreSQL, db.t4g.micro, single-AZ, automated backups | Replaces the in-memory repository. Filtering and sorting suit a relational database. Keeps the same `CandidateRepository` interface, swapped for JPA. DynamoDB would also work and is cheaper at low traffic, but the queries here are relational |
| Secrets | AWS Secrets Manager | The Gemini API key stays out of the image and the repo, and is injected as an environment variable |
| AI provider | Gemini on a paid tier, or Amazon Bedrock | The free tier may use content to improve Google's products, which is unacceptable for real resumes. Bedrock would keep resume text inside AWS and under the same data terms as the rest of the system |


## Security notes

- The database is private, reached only from the App Runner VPC connector. Each service has a least-privilege IAM role.
- The exercise had no authentication. In production I would add Amazon Cognito and validate its tokens in Spring, and put AWS WAF rate limiting on `/api/*`, especially `/summary`, because every call spends AI quota.
- Resume text is personal data. I would add a data-processing agreement with the AI provider, consider redacting contact details before sending text, and set a retention policy.

## Rough monthly cost (small usage, one environment)

| Item | Estimate (USD) |
|---|---|
| S3 and CloudFront | 1 to 5 |
| App Runner (about 1 vCPU, 2 GB, low traffic) | 10 to 30 |
| RDS PostgreSQL db.t4g.micro, 20 GB storage | 15 to 25 |
| Secrets Manager, ECR, CloudWatch | 3 to 8 |
| AI usage (a few hundred summaries a month) | 1 to 5 |
| **Total** | **about 30 to 70 per month** |

A second environment (staging) would roughly double the database and container lines. Costs drop if the container is paused outside working hours or if the database moves to DynamoDB on-demand.
