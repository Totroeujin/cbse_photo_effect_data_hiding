package com.example.cbse_photo_effect.service;

import com.example.cbse_photo_effect.util.AESUtils;
import com.example.cbse_photo_effect.util.Base64Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import static com.example.cbse_photo_effect.util.AESUtils.decryptAES;
import static com.example.cbse_photo_effect.util.AESUtils.encryptAES;


@Service
public class CryptographyService {
    private static final Logger logger = LoggerFactory.getLogger(CryptographyService.class);

    @Value("${app.encryption.aes-key:12345678901234567890123456789012}")
    private String aesKey;

    @Value("${app.encryption.aes-iv:1234567890123456}")
    private String aesIv;

    @PostConstruct
    public void logValues() {
        if (aesKey == null || aesKey.isEmpty()) {
            throw new IllegalArgumentException("AES Key is not configured properly.");
        }
        if (aesIv == null || aesIv.isEmpty()) {
            throw new IllegalArgumentException("AES IV is not configured properly.");
        }

        logger.info("AES Key: {}", aesKey);
        logger.info("AES IV: {}", aesIv);
    }

    /**
     * Encrypts a string with AES, then encodes the result with Base64.
     * @param data The plaintext string to be encrypted.
     * @return The Base64-encoded encrypted string.
     * @throws Exception If encryption fails.
     */
    public String encryptAndEncode(String data) throws Exception {
        // Encrypt the plaintext using AES
        byte[] encryptedBytes = encryptAES(data, aesKey, aesIv);
        // Encode the encrypted bytes with Base64
        String encodedData = Base64Utils.encodeBase64(encryptedBytes);

        // Check length
        String dataToEmbed = textToBinary(encodedData);
        if (dataToEmbed.length() != 1024){
            throw new IllegalArgumentException("dataToEmbed length (should be 1024): " + dataToEmbed.length());
        }
        return textToBinary(encodedData);
    }

    /**
     * Decodes a Base64 string, then decrypts it with AES.
     * @param encodedBase64Binary The Base64-encoded encrypted string.
     * @return The decrypted plaintext string.
     * @throws Exception If decryption fails.
     */
    public String decodeAndDecrypt(String encodedBase64Binary) throws Exception {
        if (encodedBase64Binary.length() != 1024){
            throw new IllegalArgumentException("Extraction Failed! Cannot Extract Data! Data length: " + encodedBase64Binary.length());
        }
        String encodedText = binaryToString(encodedBase64Binary);

        // Decode the Base64-encoded string to get encrypted bytes
        byte[] decodedBytes = Base64Utils.decodeBase64(encodedText);

        return decryptAES(decodedBytes, aesKey, aesIv);
    }

    public static String textToBinary(String text) {
        StringBuilder binary = new StringBuilder();
        for (char character : text.toCharArray()) {
            binary.append(String.format("%8s", Integer.toBinaryString(character)).replaceAll(" ", "0"));
        }
        return binary.toString();
    }

    public static String binaryToString(String binaryData) {
        StringBuilder text = new StringBuilder();

        // Ensure the binary string length is a multiple of 8
        if (binaryData.length() % 8 != 0) {
            throw new IllegalArgumentException("Binary data length should be a multiple of 8");
        }

        // Process each 8-bit segment
        for (int i = 0; i < binaryData.length(); i += 8) {
            // Extract the 8-bit segment
            String byteString = binaryData.substring(i, i + 8);

            // Convert the 8-bit segment to a character
            char character = (char) Integer.parseInt(byteString, 2);

            // Append the character to the resulting text
            text.append(character);
        }

        return text.toString();
    }
}