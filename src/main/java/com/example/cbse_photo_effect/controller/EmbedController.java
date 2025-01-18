package com.example.cbse_photo_effect.controller;

import com.example.cbse_photo_effect.service.CryptographyService;
import com.example.cbse_photo_effect.service.ImageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

@RestController
@RequestMapping("/api/embed")
public class EmbedController {
    private static final Logger logger = LoggerFactory.getLogger(EmbedController.class);
    CryptographyService cryptographyService;
    private final ImageService imageService;

    @Autowired
    public EmbedController(ImageService imageService, CryptographyService cryptographyService) {
        this.imageService = imageService;
        this.cryptographyService = cryptographyService;
    }

    @PostMapping
    public ResponseEntity<String> embedData(@RequestParam("data") String data,
                                            @RequestParam("image") MultipartFile image) {
        try {
            // Validate the length of the input data
            if (data.length() < 81 || data.length() > 96) {
                return ResponseEntity.badRequest()
                        .body("Error: The length of the data must be between 81 and 96 characters. Currently character:" + data.length());
            }

            // Encrypt and encode the data
            String dataToEmbed = cryptographyService.encryptAndEncode(data);

            // Embed the data into the image and get the output path
            String embeddedImagePath = imageService.embedDataIntoImage(dataToEmbed, image.getBytes());

            return ResponseEntity.ok("Data embedded successfully!\nImage path:\n" + embeddedImagePath
                                        + "\ndataToEmbed length: " + dataToEmbed.length());
        } catch (IOException e) {
            return ResponseEntity.internalServerError().body("Error processing image");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @PostMapping("/extract")
    public ResponseEntity<String> extractData(@RequestParam("image") MultipartFile image) {
        try {
            // Validate file size
            if (image.getSize() > 20 * 1024 * 1024) {
                return ResponseEntity.badRequest().body("Error: File size exceeds the maximum limit of 10MB.");
            }
            String extractedData = imageService.extractDataFromImage(image.getBytes());

            String dataToSave = cryptographyService.decodeAndDecrypt(extractedData);

            // Save extracted data to a file
            String outputPath = "output/extracted-data.txt";
            File outputFile = new File(outputPath);
            if (!outputFile.getParentFile().exists()) {
                outputFile.getParentFile().mkdirs(); // Create directories if they don't exist
            }
            try (FileWriter writer = new FileWriter(outputFile)) {
                writer.write(dataToSave);
            }

            return ResponseEntity.ok("Data extracted saved in:\n" + outputFile.getAbsolutePath() + "\nContent:\n" + dataToSave);
        } catch (IOException e) {
            return ResponseEntity.internalServerError().body("Error processing image");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

