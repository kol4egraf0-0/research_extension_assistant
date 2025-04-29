package com.research.expansion;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)//дальше еще дохуя полей их скипаем!!!
public class GeminiResponse {
    //{
    //    "candidates": [
    //        {
    //            "content": {
    //                "parts": [
    //                    {
    //                        "text":

    @JsonIgnoreProperties(ignoreUnknown = true)
    private List<Candidate> candidateList;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Candidate {
        private Content content;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Content {
        private List<Part> partList;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Part {
        private String text;
    }
}
