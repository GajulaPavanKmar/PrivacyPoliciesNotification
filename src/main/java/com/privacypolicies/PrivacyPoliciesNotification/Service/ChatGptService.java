package com.privacypolicies.PrivacyPoliciesNotification.Service;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.json.JSONObject;
import java.util.Collections;
import java.util.List;

@Service
public class ChatGptService {

    @Value("${chatgpt.api.key}")
    private String apiKey;

    private final RestTemplate restTemplate;

    public ChatGptService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }


    public String summarizeText(String text, List<String> instructions) {
        String url = "https://api.openai.com/v1/chat/completions";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.set("Authorization", "Bearer " + apiKey);

        StringBuilder messagesJson = new StringBuilder();
        for (String instruction : instructions) {
            messagesJson.append(String.format("{\"role\": \"system\", \"content\": \"%s\"},", instruction));
        }
        messagesJson.append(String.format("{\"role\": \"user\", \"content\": \"%s\"}", text.replace("\"", "\\\"")));

        String requestBody = String.format("{\"model\": \"gpt-3.5-turbo\", \"messages\": [%s]}", messagesJson.toString());

        HttpEntity<String> request = new HttpEntity<>(requestBody, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);
        if (!response.getStatusCode().is2xxSuccessful()) {
            System.out.println("Error: " + response.getBody());
            return null;  // Handle error accordingly
        }

        // Parse JSON to extract only the content of the assistant's message
        JSONObject json = new JSONObject(response.getBody());
        String summarizedText = json.getJSONArray("choices")
                .getJSONObject(0)
                .getJSONObject("message")
                .getString("content");

        return summarizedText;
    }

    public String compareTexts(String storedPolicy, String currentPolicy, List<String> instructions) {
        String url = "https://api.openai.com/v1/chat/completions";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.set("Authorization", "Bearer " + apiKey);

        StringBuilder messagesJson = new StringBuilder();
        // Add instructions for clarity and context
        for (String instruction : instructions) {
            messagesJson.append(String.format("{\"role\": \"system\", \"content\": \"%s\"},", instruction));
        }
        // Add the texts with clear labeling based on the instruction context
        messagesJson.append(String.format("{\"role\": \"user\", \"content\": \"Previous Policy: %s\"},", storedPolicy.replace("\"", "\\\"")));
        messagesJson.append(String.format("{\"role\": \"user\", \"content\": \"Current Policy: %s\"}", currentPolicy.replace("\"", "\\\"")));

        String requestBody = String.format("{\"model\": \"gpt-3.5-turbo\", \"messages\": [%s]}", messagesJson.toString());

        HttpEntity<String> request = new HttpEntity<>(requestBody, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);
        if (!response.getStatusCode().is2xxSuccessful()) {
            System.out.println("Error: " + response.getBody());
            return null;  // Handle error accordingly
        }

        // Parse JSON to extract only the content of the assistant's message
        JSONObject json = new JSONObject(response.getBody());
        String comparisonResult = json.getJSONArray("choices")
                .getJSONObject(0)
                .getJSONObject("message")
                .getString("content");

        return comparisonResult;
    }



}