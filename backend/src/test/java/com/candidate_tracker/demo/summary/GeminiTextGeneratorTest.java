package com.candidate_tracker.demo.summary;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.jsonPath;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

class GeminiTextGeneratorTest {

    private static final String BASE_URL = "https://generativelanguage.googleapis.com";

    private MockRestServiceServer server;
    private GeminiTextGenerator generator;

    @BeforeEach
    void setUp() {
        RestClient.Builder builder = RestClient.builder().baseUrl(BASE_URL);
        server = MockRestServiceServer.bindTo(builder).build();
        generator = new GeminiTextGenerator(builder.build(), "test-model");
    }

    @Test
    void sendsModelInstructionsAndInputAndReturnsTheModelText() {
        server.expect(requestTo(BASE_URL + "/v1beta/interactions"))
                .andExpect(method(HttpMethod.POST))
                .andExpect(jsonPath("$.model").value("test-model"))
                .andExpect(jsonPath("$.system_instruction").value("Be brief"))
                .andExpect(jsonPath("$.input").value("<resume>Java</resume>"))
                .andExpect(jsonPath("$.store").value(false))
                .andRespond(withSuccess("""
                        {"status": "completed",
                         "steps": [{"type": "model_output",
                                    "content": [{"type": "text", "text": "Java developer"}]}]}
                        """, MediaType.APPLICATION_JSON));

        assertEquals("Java developer", generator.generate("Be brief", "<resume>Java</resume>"));
        server.verify();
    }

    @Test
    void ignoresStepsThatAreNotModelOutput() {
        server.expect(requestTo(BASE_URL + "/v1beta/interactions"))
                .andRespond(withSuccess("""
                        {"status": "completed",
                         "steps": [{"type": "thought"},
                                   {"type": "model_output",
                                    "content": [{"type": "text", "text": "Only this"}]}]}
                        """, MediaType.APPLICATION_JSON));

        assertEquals("Only this", generator.generate("Be brief", "anything"));
    }

    @Test
    void returnsEmptyTextWhenThereIsNoModelOutput() {
        server.expect(requestTo(BASE_URL + "/v1beta/interactions"))
                .andRespond(withSuccess("{\"status\": \"completed\", \"steps\": []}",
                        MediaType.APPLICATION_JSON));

        assertEquals("", generator.generate("Be brief", "anything"));
    }

    @Test
    void serverErrorsAreThrown() {
        server.expect(requestTo(BASE_URL + "/v1beta/interactions"))
                .andRespond(withStatus(HttpStatus.TOO_MANY_REQUESTS));

        assertThrows(RuntimeException.class, () -> generator.generate("Be brief", "anything"));
    }
}