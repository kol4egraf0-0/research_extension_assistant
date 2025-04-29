package com.research.expansion;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class ResearchService {
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
        return request.getContent(); //заглушка
    }

    public String buildPrompt(ResearchRequest request) { //prompt
            StringBuilder prompt = new StringBuilder();
            switch (request.getOperation()){
                case "summarize": //итог, стирает и пишет нужную инфу в 3-4 предложения
                    prompt.append("Provide a clear and concise summary of the following in the few setnces:\n\n");
                    break;
                case "Согласовать":
                    prompt.append("Based on the following information: suggest related topics and further reading.Format the response with clear headings and bullet point\n\n");
                    break;

                    default:
                        throw new IllegalArgumentException("Unknown operation: " + request.getOperation());
            }
            prompt.append(request.getContent());
            return prompt.toString();
    }
}
