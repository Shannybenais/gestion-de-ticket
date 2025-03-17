package sio.devoir3sio1b.Model;

/**
 * Classe représentant un utilisateur avec ses propriétés et méthodes associées.
 */
public class User {
    // Identifiant unique de l'utilisateur
    private int idUser;

    // Nom ou identifiant de l'utilisateur
    private String nomUser;

    // Statut de l'utilisateur (exemple : "Admin", "Utilisateur", etc.)
    private String statutUser;

    /**
     * Constructeur de la classe User.
     * Permet d'initialiser un utilisateur avec un identifiant, un nom et un statut.
     *
     * @param idUser     Identifiant unique de l'utilisateur
     * @param nomUser    Nom ou identifiant de l'utilisateur
     * @param statutUser Statut ou rôle de l'utilisateur
     */
    public User(int idUser, String nomUser, String statutUser) {
        this.idUser = idUser;          // Initialisation de l'ID de l'utilisateur
        this.nomUser = nomUser;        // Initialisation du nom de l'utilisateur
        this.statutUser = statutUser;  // Initialisation du statut de l'utilisateur
    }

    /**
     * Getter pour obtenir l'identifiant de l'utilisateur.
     *
     * @return Identifiant unique de l'utilisateur
     */
    public int getIdUser() {
        return idUser;
    }

    /**
     * Getter pour obtenir le nom de l'utilisateur.
     *
     * @return Nom ou identifiant de l'utilisateur
     */
    public String getNomUser() {
        return nomUser;
    }

    /**
     * Getter pour obtenir le statut de l'utilisateur.
     *
     * @return Statut ou rôle de l'utilisateur
     */
    public String getStatutUser() {
        return statutUser;
    }
}
