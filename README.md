# Candidate Tracker

A small applicant tracking system. Add candidates, move them through a hiring pipeline (Applied, Screening, Interview, Offer, Rejected), keep timestamped notes, and see an AI-generated summary of each candidate's resume. Built as a take-home exercise.

Screenshots are in `docs/screenshots/`. The write-up, AI usage log, learning log and deployment design are in `docs/`.

## What's in the repo

| Folder | What it is |
|---|---|
| `backend/` | Java 21 and Spring Boot (Maven) REST API, built test-first with JUnit 5 |
| `frontend/` | React and TypeScript (Vite) single-page app |
| `scripts/seed.mjs` | Loads made-up sample candidates into the running backend |
| `docs/` | Write-up, AI usage log, learning log, deployment design, screenshots |

## Prerequisites

- JDK 21
- Node.js (a current LTS version) and npm
- Git

## Demo Video
To watch how the application is run, you can watch the youtube video linked below:
https://youtu.be/1_6_aQJ8S48?si=J_q-hogmOJQ8odmz

## Run it

You need two terminals, one for the backend and one for the frontend. Open the terminal in the candidate-tracker folder.

**1. Backend** (API on http://localhost:8080)

```bash
cd backend
./mvnw spring-boot:run        # Windows PowerShell: mvnw spring-boot:run
```

**2. Frontend** (app on http://localhost:5173)

```bash
cd frontend
npm install
npm run dev
```

Open http://localhost:5173. The Vite dev server forwards `/api` requests to the backend, so there is nothing else to configure.

**3. Sample data (optional)**

With the backend running, go back to the candidate-tracker directory, and run this:

```bash
node scripts/seed.mjs
```

This adds seven made-up candidates across all stages, including garbled text, a prompt-injection attempt, HTML and script tags, a non-English resume and a candidate with no resume. Data is held in memory, so it disappears when the backend restarts. Run the script again after a restart (restart first if you want a clean list, otherwise everyone is added twice).

**4. Activating AI summary (optional)**
The summary uses the Google Gemini API. To use it, you must create a `.env` file in the backend folder.

1. Create a key at https://aistudio.google.com/apikey.
2. Put it in `backend/.env`: `GEMINI_API_KEY=your-key-here`. This file is git-ignored.
3. Restart the backend.

The `.env` file should look like this:
``` bash
GEMINI_API_KEY=your-key-here
```

**The app works without a key.** Everything runs and the summary panel simply shows "unavailable".

**Privacy note:** on Google's free tier, prompts and responses may be used to improve its products and reviewed by humans, and Google advises against sending personal data. Use only made-up resumes (such as the seed script's) with a free-tier key.

## Run the tests

```bash
cd backend
./mvnw test                   # Windows PowerShell: mvnw test
```

Tests cover the domain model, the in-memory repository, the service (filtering, sorting, stage changes, notes, deletion), the REST controllers, and the AI summary module, including bad input (garbled text, injected tags and instructions, very long text, provider failures). The AI tests use a fake text generator and a mock HTTP server, so they need no key and make no network calls.

## API

| Method and path | Purpose |
|---|---|
| `POST /api/candidates` | Add a candidate (`name`, `email`, `roleAppliedFor` required; `phone`, `source`, `resumeText` optional) |
| `GET /api/candidates?stage=&role=&sort=` | List candidates. `stage` filters by stage, `role` matches part of the role (ignoring case), `sort` is `DATE_ADDED` (newest first, default) or `STAGE` (pipeline order, then newest first) |
| `GET /api/candidates/{id}` | Candidate details with notes |
| `PATCH /api/candidates/{id}/stage` | Move to any stage, body `{"stage": "INTERVIEW"}` |
| `POST /api/candidates/{id}/notes` | Append a note, body `{"text": "..."}` |
| `DELETE /api/candidates/{id}` | Delete a candidate |
| `POST /api/candidates/{id}/summary` | Generate an AI summary, returns `{"status": "GENERATED" or "NO_RESUME_TEXT" or "UNAVAILABLE", "text": "..."}` |

Invalid input returns 400 and an unknown candidate returns 404.

## Code layout (backend)

```
backend/src/main/java/com/candidate_tracker/demo/
  domain/       Candidate, Stage, Note
  repository/   CandidateRepository (interface), InMemoryCandidateRepository
  service/      CandidateService, SortField, CandidateNotFoundException
  controller/   CandidateController, request records, ApiExceptionHandler
  summary/      The AI feature, self-contained: TextGenerator (the one provider
                boundary), GeminiTextGenerator, CandidateSummaryService, SummaryController
```

## Known limitations

- Data is stored in memory only and is lost on restart.
- No authentication and no rate limiting (out of scope for the exercise), so anyone who can reach the API can spend summary quota.
- Summaries are generated on demand and not stored.
- The frontend has no automated tests yet.
- Delete is permanent. There is no archive.

See `docs/write-up.md` for the design decisions and trade-offs.
