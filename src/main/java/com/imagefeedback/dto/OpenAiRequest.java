package com.imagefeedback.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import java.util.List;
import java.util.Map;

// package com.imagefeedback.dto;
@Data
@Builder
public class OpenAiRequest {
    private String model;
    private List<Message> messages;

    // JSON çıktı istediğimiz için bu alanı ekliyoruz.
    @JsonProperty("response_format")
    private Map<String, String> responseFormat;

    @Data
    @Builder
    public static class Message {
        private String role;
        private List<Content> content;
    }

    @Data
    @Builder
    public static class Content {
        private String type; // 'text' veya 'image_url'
        private String text;

        @JsonProperty("image_url")
        private ImageUrl imageUrl;
    }

    @Data
    @Builder
    public static class ImageUrl {
        // Base64 Data URL (e.g., data:image/jpeg;base64,...)
        private String url;
    }
}