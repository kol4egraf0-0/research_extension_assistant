package com.research.expansion;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.HashMap;
import java.util.Map;

@Service
public class ResearchService {


    @Value("${gemini.api.url}")
    private String geminiApiUrl;


    @Value("${gemini.api.key}")
    private String geminiApiKey;

    private final WebClient webClient;

    private final ObjectMapper objectMapper;

    public ResearchService(WebClient.Builder webClient, ObjectMapper objectMapper) {
        this.webClient = webClient.build();
        this.objectMapper = objectMapper;
    }

    public String processContent(ResearchRequest request) {
        //промпт
        String prompt = buildPrompt(request);
        //запрос к аи
        //тк "contents": [{
        //    "parts":[{"text": "Explain how AI works"}]
        //    }]
        Map<String,Object> requestBody = Map.of(
                "contents", new Object[]{
                        Map.of("parts", new Object[]{
                                Map.of("text", prompt)
                        })
                }
        );
        String response = webClient.post()
                .uri(geminiApiUrl+geminiApiKey)
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(String.class)
                .block();
        System.out.println("Raw API response: " + response);
        return extractTextFromResponse(response);
    }

    //парсинг
    private String extractTextFromResponse(String response) {
        try{
            GeminiResponse geminiResponse = objectMapper.readValue(response, GeminiResponse.class);
            if(geminiResponse.getCandidates() != null && !geminiResponse.getCandidates().isEmpty()) {
                GeminiResponse.Candidate firstCandidate = geminiResponse.getCandidates().get(0);
                if(firstCandidate.getContent() != null && firstCandidate.getContent().getParts() !=null
                        && !firstCandidate.getContent().getParts().isEmpty()) {
                    return firstCandidate.getContent().getParts().get(0).getText();
                }
            }
            return "Нет контента!!!" + response;
        } catch (Exception e){
            return "Ошибка парсинга: " + e.getMessage();
        }
    }

    public String buildPrompt(ResearchRequest request) { //prompt
            StringBuilder prompt = new StringBuilder();
            switch (request.getOperation()){
                case "summarize": //итог, стирает и пишет нужную инфу в 3-4 предложения
                    prompt.append("Provide a clear and concise summary of the following in the few setnces:\n\n");
                    break;
                case "suggested":
                    prompt.append("Based on the following information: suggest related topics and further reading.Format the response with clear headings and bullet point\n\n");
                    break;

                    default:
                        throw new IllegalArgumentException("Unknown operation: " + request.getOperation());
            }
            prompt.append(request.getContent());
            return prompt.toString();
    }
}
