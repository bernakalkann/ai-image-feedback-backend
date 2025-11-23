package com.imagefeedback.controller;

import com.imagefeedback.dto.AnalysisResponse;
import com.imagefeedback.service.ImageAnalysisService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/analysis")
@RequiredArgsConstructor
public class ImageController {

    private final ImageAnalysisService analysisService;

    // Endpoint: POST /api/v1/analysis/image
    @PostMapping(value = "/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<AnalysisResponse> analyzeImage(
            @RequestPart("image") MultipartFile imageFile) {

        // 🛑 GEÇİCİ KONTROL: 400 Bad Request hatasını kimin fırlattığını bulmak için yorum satırı yapıldı
        /*
        if (imageFile.isEmpty() || imageFile.getSize() == 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Lütfen bir görsel dosyası yükleyin.");
        }
        */

        // Tika ile MIME Type doğrulaması Service katmanında yapılacak.

        try {
            AnalysisResponse feedback = analysisService.analyzeImage(imageFile);

            // Eğer dosya boşsa (Empty), service katmanında hata fırlatılması gerekir.
            // Bu geçici yorum satırı, hatanın kaynağını bulmamıza yardımcı olacaktır.

            return ResponseEntity.ok(feedback);
        } catch (IOException e) {
            // Dosya okuma/işleme hatası
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Görsel işlenirken I/O hatası oluştu.", e);
        }
    }
}