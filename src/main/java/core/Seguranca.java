package core;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

public class Seguranca {

    // Transforma uma senha de texto limpo em um Hash SHA-256
    public static String hashearSenha(String senhaOriginal) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(senhaOriginal.getBytes(StandardCharsets.UTF_8));

            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception ex) {
            throw new RuntimeException("Erro crítico ao criptografar senha", ex);
        }
    }
}