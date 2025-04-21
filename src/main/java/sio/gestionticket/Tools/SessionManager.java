package sio.gestionticket.Tools;

import sio.gestionticket.Model.User;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SessionManager {
    // Singleton
    private static SessionManager instance;

    // Map des sessions actives : clé = token, valeur = informations de session
    private Map<String, SessionInfo> sessions;

    // Temps d'expiration des sessions en minutes
    private static final int SESSION_EXPIRATION_MINUTES = 30;

    // Classe interne pour stocker les infos de session
    private class SessionInfo {
        User user;
        LocalDateTime lastActivity;
        int loginAttempts;

        public SessionInfo(User user) {
            this.user = user;
            this.lastActivity = LocalDateTime.now();
            this.loginAttempts = 0;
        }

        public void updateActivity() {
            this.lastActivity = LocalDateTime.now();
        }

        public boolean isExpired() {
            return lastActivity.plusMinutes(SESSION_EXPIRATION_MINUTES).isBefore(LocalDateTime.now());
        }
    }

    private SessionManager() {
        sessions = new HashMap<>();
    }

    public static synchronized SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }

    /**
     * Crée une nouvelle session pour un utilisateur
     * @param user L'utilisateur connecté
     * @return Le token de session
     */
    public String createSession(User user) {
        // Génération d'un token aléatoire unique
        String token = UUID.randomUUID().toString();
        sessions.put(token, new SessionInfo(user));
        return token;
    }

    /**
     * Vérifie si une session est valide
     * @param token Le token de session
     * @return true si la session est valide, false sinon
     */
    public boolean isValidSession(String token) {
        if (token == null || !sessions.containsKey(token)) {
            return false;
        }

        SessionInfo session = sessions.get(token);

        // Vérification de l'expiration
        if (session.isExpired()) {
            sessions.remove(token);
            return false;
        }

        // Mise à jour de l'activité
        session.updateActivity();
        return true;
    }

    /**
     * Récupère l'utilisateur associé à une session
     * @param token Le token de session
     * @return L'utilisateur connecté ou null si la session est invalide
     */
    public User getUserFromSession(String token) {
        if (!isValidSession(token)) {
            return null;
        }
        return sessions.get(token).user;
    }

    /**
     * Détruit une session
     * @param token Le token de session à détruire
     */
    public void destroySession(String token) {
        sessions.remove(token);
    }

    /**
     * Gestion des tentatives de connexion (prévention contre les attaques par force brute)
     * @param token Identifiant unique du client (adresse IP ou autre)
     * @return true si l'utilisateur peut tenter une connexion, false s'il est bloqué
     */
    public boolean canAttemptLogin(String clientId) {
        SessionInfo sessionInfo = sessions.getOrDefault(clientId, new SessionInfo(null));

        // Si plus de 5 tentatives en moins de 15 minutes, bloquer
        if (sessionInfo.loginAttempts >= 5 &&
                sessionInfo.lastActivity.plusMinutes(15).isAfter(LocalDateTime.now())) {
            return false;
        }

        // Si le délai de 15 minutes est passé, réinitialiser le compteur
        if (sessionInfo.lastActivity.plusMinutes(15).isBefore(LocalDateTime.now())) {
            sessionInfo.loginAttempts = 0;
        }

        // Incrémenter le compteur et mettre à jour l'heure
        sessionInfo.loginAttempts++;
        sessionInfo.updateActivity();
        sessions.put(clientId, sessionInfo);

        return true;
    }

    /**
     * Nettoyage périodique des sessions expirées
     */
    public void cleanExpiredSessions() {
        sessions.entrySet().removeIf(entry -> entry.getValue().isExpired());
    }
}