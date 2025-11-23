package com.imagefeedback.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.imagefeedback.dto.AnalysisResponse;
import com.imagefeedback.dto.OpenAiRequest;
import com.imagefeedback.dto.OpenAiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tika.Tika; // MIME Type tespiti için
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;
import java.io.IOException;
import java.util.Base64;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class ImageAnalysisService {

    // WebClient'ın adı 'openAiWebClient' olarak değiştirildi
    private final WebClient openAiWebClient;
    private final ObjectMapper objectMapper;
    private final Tika tika = new Tika(); // Tika örneği

    @Value("${openai.model}")
    private String visionModel;

    private static final String SYSTEM_PROMPT =
            "You are an expert AI image aesthetic evaluator. Analyze the image and return a JSON object ONLY with the following structure: {\"score\": double, \"analysis\": \"detailed analysis text\", \"suggestions\": [\"suggestion 1\", \"suggestion 2\"]}. Score must be between 0.0 and 10.0.";
    private static final String USER_PROMPT =
            "Analyze the aesthetic quality of this image, give a score (0.0 to 10.0), provide detailed analysis, and list concrete suggestions for improvement.";


    public AnalysisResponse analyzeImage(MultipartFile imageFile) throws IOException {

        // 1. Base64 Dönüşümü ve Data URL Oluşturma
        byte[] imageBytes = imageFile.getBytes();
        String base64Image = Base64.getEncoder().encodeToString(imageBytes);

        // Tika ile daha doğru MIME tespiti
        String mimeType = tika.detect(imageBytes);
        if (mimeType == null || !mimeType.startsWith("image/")) {
            throw new ResponseStatusException(HttpStatus.UNSUPPORTED_MEDIA_TYPE, "Geçersiz veya desteklenmeyen dosya tipi: " + mimeType);
        }

        String dataUrl = String.format("data:%s;base64,%s", mimeType, base64Image);
        log.info("Base64 ve Data URL hazırlandı. Mime: {}", mimeType);

        // 2. OpenAI İstek Yapısının Oluşturulması
        OpenAiRequest requestBody = buildOpenAiRequest(dataUrl);

        // 3. WebClient ile REST API Çağrısı
        OpenAiResponse response = openAiWebClient.post()
                .body(BodyInserters.fromValue(requestBody))
                .retrieve()
                // Hata Yönetimi
                .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(), clientResponse -> {
                    log.error("OpenAI API hatası: {}", clientResponse.statusCode());
                    return clientResponse.bodyToMono(String.class)
                            .flatMap(errorBody -> {
                                log.error("Hata Detayı: {}", errorBody);
                                return Mono.error(new ResponseStatusException(clientResponse.statusCode(), "OpenAI API'den yanıt alınamadı."));
                            });
                })
                .bodyToMono(OpenAiResponse.class)
                .block();

        // 4. Yanıtı İşleme ve JSON'u Ayrıştırma
        if (response == null || response.getChoices() == null || response.getChoices().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "OpenAI'dan boş veya hatalı yanıt geldi.");
        }

        String jsonContent = response.getChoices().get(0).getMessage().getContent().trim();
        log.info("GPT'den gelen JSON yanıtı: {}", jsonContent);

        try {
            // GPT'nin döndürdüğü JSON string'ini AnalysisResponse DTO'suna dönüştürme
            return objectMapper.readValue(jsonContent, AnalysisResponse.class);
        } catch (JsonProcessingException e) {
            log.error("GPT yanıtındaki JSON ayrıştırılamadı. Yanıt: {}", jsonContent, e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "AI yanıtı beklenen JSON formatında değil.");
        }
    }

    // İstek Objesini Hazırlayan Yardımcı Metot
    private OpenAiRequest buildOpenAiRequest(String dataUrl) {
        OpenAiRequest.ImageUrl imageUrl = OpenAiRequest.ImageUrl.builder().url(dataUrl).build();

        OpenAiRequest.Content imageContent = OpenAiRequest.Content.builder()
                .type("image_url")
                .imageUrl(imageUrl)
                .build();

        OpenAiRequest.Content textContent = OpenAiRequest.Content.builder()
                .type("text")
                .text(USER_PROMPT)
                .build();

        OpenAiRequest.Message userMessage = OpenAiRequest.Message.builder()
                .role("user")
                .content(List.of(textContent, imageContent))
                .build();

        OpenAiRequest.Message systemMessage = OpenAiRequest.Message.builder()
                .role("system")
                .content(List.of(OpenAiRequest.Content.builder().type("text").text(SYSTEM_PROMPT).build()))
                .build();

        return OpenAiRequest.builder()
                .model(visionModel)
                .messages(List.of(systemMessage, userMessage))
                // Yanıtın sadece JSON olmasını garanti etmek için
                .responseFormat(Map.of("type", "json_object"))
                .build();
    }
}