package com.research.expansion;

import org.springframework.stereotype.Service;

@Service
public class ResearchService {
    public String processContent(ResearchRequest request) {
        return request.getContent(); //заглушка
    }
}
