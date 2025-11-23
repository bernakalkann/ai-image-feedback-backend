package com.imagefeedback.dto;

import lombok.Data;
import java.util.List;

// package com.imagefeedback.dto;
@Data
public class OpenAiResponse {
    private List<Choice> choices;

    @Data
    public static class Choice {
        private Message message;
    }

    @Data
    public static class Message {
        private String role;
        // Bu String, içinde istediğimiz JSON formatındaki (score, analysis, suggestions) veriyi taşıyacak.
        private String content;
    }
}