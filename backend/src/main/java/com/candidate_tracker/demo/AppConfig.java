package com.candidate_tracker.demo;

import java.net.http.HttpClient;
import java.time.Clock;
import java.time.Duration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import com.candidate_tracker.demo.repository.CandidateRepository;
import com.candidate_tracker.demo.repository.InMemoryCandidateRepository;
import com.candidate_tracker.demo.summary.GeminiTextGenerator;
import com.candidate_tracker.demo.summary.TextGenerator;

@Configuration
public class AppConfig {

    @Bean
    public CandidateRepository candidateRepository() {
        return new InMemoryCandidateRepository();
    }

    @Bean
    public Clock clock() {
        return Clock.systemUTC();
    }
    
    @Bean
    public TextGenerator textGenerator(
            @Value("${gemini.api-key:}") String apiKey,
            @Value("${gemini.model}") String model) {

        if (apiKey.isBlank()) {
            // No key configured: the app still runs and summaries show as unavailable.
            return (systemPrompt, userContent) -> {
                throw new IllegalStateException("No Gemini API key configured");
            };
        }

        JdkClientHttpRequestFactory requestFactory = new JdkClientHttpRequestFactory(
                HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(5)).build());
        requestFactory.setReadTimeout(Duration.ofSeconds(30));

        RestClient restClient = RestClient.builder()
                .baseUrl("https://generativelanguage.googleapis.com")
                .defaultHeader("x-goog-api-key", apiKey)
                .requestFactory(requestFactory)
                .build();

        return new GeminiTextGenerator(restClient, model);
    }
}