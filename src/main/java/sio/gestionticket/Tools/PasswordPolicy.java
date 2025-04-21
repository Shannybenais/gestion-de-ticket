package sio.gestionticket.Tools;

import java.util.regex.Pattern;
import java.security.MessageDigest;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class PasswordPolicy {

    // Longueur minimale d'un mot de passe
    private static final int MIN_LENGTH = 8;

    // Nombre minimal de caractères spéciaux requis
    private static final int MIN_SPECIAL_CHARS = 1;

    // Nombre minimal de chiffres requis
    private static final int MIN_DIGITS = 1;

    // Nombre minimal de lettres majuscules requis
    private static final int MIN_UPPERCASE = 1;

    /**
     * Vérifie si un mot de passe respecte la politique de sécurité
     * @param password Le mot de passe à vérifier
     * @return true si le mot de passe est valide, false sinon
     */
    public static boolean isValid(String password) {
        if (password == null || password.length() < MIN_LENGTH) {
            return false;
        }

        // Vérification des critères
        int specialChars = 0;
        int digits = 0;
        int uppercase = 0;

        for (char c : password.toCharArray()) {
            if (Character.isDigit(c)) {
                digits++;
            } else if (Character.isUpperCase(c)) {
                uppercase++;
            } else if (!Character.isLetterOrDigit(c)) {
                specialChars++;
            }
        }

        return digits >= MIN_DIGITS &&
                uppercase >= MIN_UPPERCASE &&
                specialChars >= MIN_SPECIAL_CHARS;
    }

    /**
     * Vérifie si un mot de passe n'est pas trop basique/commun
     * @param password Le mot de passe à vérifier
     * @param username Le nom d'utilisateur (pour éviter les similarités)
     * @return true si le mot de passe est acceptable, false s'il est trop basique
     */
    public static boolean isNotCommonPassword(String password, String username) {
        // Liste de mots de passe courants à éviter
        String[] commonPasswords = {"password", "123456", "admin", "qwerty"};

        // Vérification des mots de passe courants
        for (String common : commonPasswords) {
            if (password.equalsIgnoreCase(common)) {
                return false;
            }
        }

        // Vérification que le mot de passe n'est pas trop similaire au nom d'utilisateur
        if (password.toLowerCase().contains(username.toLowerCase()) ||
                username.toLowerCase().contains(password.toLowerCase())) {
            return false;
        }

        return true;
    }

    /**
     * Génère un message d'erreur expliquant pourquoi le mot de passe est invalide
     * @param password Le mot de passe à analyser
     * @param username Le nom d'utilisateur
     * @return Un message d'erreur détaillé
     */
    public static String getValidationMessage(String password, String username) {
        StringBuilder message = new StringBuilder();

        if (password == null || password.length() < MIN_LENGTH) {
            message.append("Le mot de passe doit contenir au moins ").append(MIN_LENGTH).append(" caractères.\n");
        }

        int specialChars = 0;
        int digits = 0;
        int uppercase = 0;

        for (char c : password.toCharArray()) {
            if (Character.isDigit(c)) {
                digits++;
            } else if (Character.isUpperCase(c)) {
                uppercase++;
            } else if (!Character.isLetterOrDigit(c)) {
                specialChars++;
            }
        }

        if (digits < MIN_DIGITS) {
            message.append("Le mot de passe doit contenir au moins ").append(MIN_DIGITS).append(" chiffre(s).\n");
        }

        if (uppercase < MIN_UPPERCASE) {
            message.append("Le mot de passe doit contenir au moins ").append(MIN_UPPERCASE).append(" lettre(s) majuscule(s).\n");
        }

        if (specialChars < MIN_SPECIAL_CHARS) {
            message.append("Le mot de passe doit contenir au moins ").append(MIN_SPECIAL_CHARS).append(" caractère(s) spécial(aux).\n");
        }

        if (!isNotCommonPassword(password, username)) {
            message.append("Le mot de passe est trop basique ou trop similaire à votre nom d'utilisateur.\n");
        }

        return message.toString();
    }
    public static boolean verifyPassword(String inputPassword, String storedHash) {
        System.out.println("Début vérification - Password entré: " + inputPassword);
        System.out.println("Hash stocké: " + storedHash);

        // Vérifier si le hash stocké est au format attendu (sel:hash)
        if (storedHash.contains(":")) {
            try {
                // Séparer le sel et le hash
                String[] parts = storedHash.split(":");
                String salt = parts[0];
                String hash = parts[1];

                System.out.println("Sel extrait: " + salt);
                System.out.println("Hash extrait: " + hash);

                // Essai avec différents algorithmes de hachage
                String[] algorithms = {"SHA-256", "SHA-512", "MD5"};

                for (String algorithm : algorithms) {
                    System.out.println("Essai avec l'algorithme: " + algorithm);

                    MessageDigest md = MessageDigest.getInstance(algorithm);
                    md.update(Base64.getDecoder().decode(salt));
                    md.update(inputPassword.getBytes(StandardCharsets.UTF_8));
                    byte[] hashedInput = md.digest();
                    String hashedInputBase64 = Base64.getEncoder().encodeToString(hashedInput);

                    System.out.println("Hash généré avec " + algorithm + ": " + hashedInputBase64);

                    if (hashedInputBase64.equals(hash)) {
                        System.out.println("Match trouvé avec " + algorithm + "!");
                        return true;
                    }
                }

                // Essai avec une méthode différente d'application du sel
                for (String algorithm : algorithms) {
                    System.out.println("Essai avec " + algorithm + " (sel après le mot de passe)");

                    MessageDigest md = MessageDigest.getInstance(algorithm);
                    md.update(inputPassword.getBytes(StandardCharsets.UTF_8));
                    md.update(Base64.getDecoder().decode(salt));
                    byte[] hashedInput = md.digest();
                    String hashedInputBase64 = Base64.getEncoder().encodeToString(hashedInput);

                    System.out.println("Hash généré: " + hashedInputBase64);

                    if (hashedInputBase64.equals(hash)) {
                        System.out.println("Match trouvé!");
                        return true;
                    }
                }

                System.out.println("Aucun match trouvé avec les algorithmes testés.");
                return false;
            } catch (Exception e) {
                System.out.println("Erreur lors de la vérification: " + e.getMessage());
                e.printStackTrace();
                return false;
            }
        } else {
            // Si le mot de passe n'est pas haché (comme pour l'utilisateur "damico")
            boolean match = inputPassword.equals(storedHash);
            System.out.println("Comparaison directe sans hachage: " + match);
            return match;
        }
    }

}