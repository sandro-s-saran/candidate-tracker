package com.candidate_tracker.demo.summary;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;

public class GeminiTextGenerator implements TextGenerator {

    private final RestClient restClient;
    private final String model;

    public GeminiTextGenerator(RestClient restClient, String model) {
        this.restClient = restClient;
        this.model = model;
    }

    @Override
    public String generate(String systemPrompt, String userContent) {
        Map<String, Object> body = Map.of(
                "model", model,
                "system_instruction", systemPrompt,
                "input", userContent,
                "store", false);

        InteractionResponse response = restClient.post()
                .uri("/v1beta/interactions")
                .contentType(MediaType.APPLICATION_JSON)
                .body(body)
                .retrieve()
                .body(InteractionResponse.class);

        if (response == null || response.steps() == null) {
            return "";
        }

        return response.steps().stream()
                .filter(step -> "model_output".equals(step.type()) && step.content() != null)
                .flatMap(step -> step.content().stream())
                .filter(part -> "text".equals(part.type()) && part.text() != null)
                .map(Content::text)
                .collect(Collectors.joining());
    }

    record InteractionResponse(List<Step> steps) {}

    record Step(String type, List<Content> content) {}

    record Content(String type, String text) {}
}