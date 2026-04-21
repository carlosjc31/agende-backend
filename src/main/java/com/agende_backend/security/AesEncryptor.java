package com.agende_backend.security;

import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Converter
@Component
public class AesEncryptor implements AttributeConverter<String, String> {

    // Mudamos para AES simples (ECB) para não precisar da segunda chave (IV)
    private static final String ALGORITHM = "AES";

    // Tiramos o "static" e mandamos o Spring puxar a chave do application.properties
    @Value("${aes.secret.key}")
    private String secretKey;

    // Converte o dado para o banco
    @Override
    public String convertToDatabaseColumn(String attribute) {
        if (attribute == null) return null;
        try {
            // Se a chave vier vazia, injetamos manualmente para garantir que nunca falha!
            if (secretKey == null) {
                secretKey = "12345678901234567890123456789012";
            }
            // Criamos a chave de assinatura para encriptar
            SecretKeySpec keySpec = new SecretKeySpec(secretKey.getBytes(), ALGORITHM);
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, keySpec);
            return Base64.getEncoder().encodeToString(cipher.doFinal(attribute.getBytes()));
        } catch (Exception e) {
            throw new RuntimeException("Erro ao encriptar o dado", e);
        }
    }
    // Converte o dado do banco
    @Override
    public String convertToEntityAttribute(String dbData) {
        if (dbData == null) return null;
        try {
            if (secretKey == null) {
                secretKey = "12345678901234567890123456789012";
            }
            // Criamos a chave de assinatura para desencriptar
            SecretKeySpec keySpec = new SecretKeySpec(secretKey.getBytes(), ALGORITHM);
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, keySpec);
            return new String(cipher.doFinal(Base64.getDecoder().decode(dbData)));
        } catch (Exception e) {
            throw new RuntimeException("Erro ao desencriptar o dado", e);
        }
    }
}
