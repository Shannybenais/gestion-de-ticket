package sio.gestionticket.Tools;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

public class PasswordUtils {

    /**
     * Hache un mot de passe avec un sel aléatoire en utilisant SHA-512
     * @param password Mot de passe en clair
     * @return String au format "sel:hash" encodé en Base64
     */
    public static String hashPassword(String password) {
        try {
            // Génération d'un sel aléatoire
            SecureRandom random = new SecureRandom();
            byte[] salt = new byte[16];
            random.nextBytes(salt);

            // Hachage du mot de passe avec le sel
            MessageDigest md = MessageDigest.getInstance("SHA-512");
            md.update(salt);
            byte[] hashedPassword = md.digest(password.getBytes());

            // Stockage du sel et du mot de passe haché (sel:hash)
            return Base64.getEncoder().encodeToString(salt) + ":" +
                    Base64.getEncoder().encodeToString(hashedPassword);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Erreur lors du hachage du mot de passe", e);
        }
    }

    /**
     * Vérifie un mot de passe par rapport à son hash stocké
     * @param password Mot de passe en clair à vérifier
     * @param storedHash Hash stocké au format "sel:hash"
     * @return true si le mot de passe correspond, false sinon
     */
    public static boolean checkPassword(String password, String storedHash) {
        try {
            // Séparation du sel et du hash
            String[] parts = storedHash.split(":");
            if (parts.length != 2) {
                return false;
            }

            byte[] salt = Base64.getDecoder().decode(parts[0]);
            byte[] originalHash = Base64.getDecoder().decode(parts[1]);

            // Recalcul du hash avec le sel récupéré
            MessageDigest md = MessageDigest.getInstance("SHA-512");
            md.update(salt);
            byte[] newHash = md.digest(password.getBytes());

            // Comparaison des hashs
            return MessageDigest.isEqual(originalHash, newHash);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Évalue la force d'un mot de passe
     * @param password Mot de passe à évaluer
     * @return Score de 0 (très faible) à 5 (très fort)
     */
    public static int evaluatePasswordStrength(String password) {
        int score = 0;

        // Longueur
        if (password.length() >= 8) score++;
        if (password.length() >= 12) score++;

        // Complexité
        if (password.matches(".*[A-Z].*")) score++;
        if (password.matches(".*[a-z].*")) score++;
        if (password.matches(".*[0-9].*")) score++;
        if (password.matches(".*[^A-Za-z0-9].*")) score++;

        return Math.min(5, score);
    }
}