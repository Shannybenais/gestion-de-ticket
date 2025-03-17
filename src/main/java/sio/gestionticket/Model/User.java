package sio.gestionticket.Model; // Définition du package où se trouve la classe User

public class User // Déclaration de la classe User
{
    // Déclaration des attributs privés de la classe User
    private int idUser;        // Identifiant unique de l'utilisateur
    private String nomUser;    // Nom de l'utilisateur
    private String statutUser; // Statut de l'utilisateur (exemple : "Admin", "Technicien", "Utilisateur")

    // Constructeur de la classe permettant d'initialiser un utilisateur avec ses attributs
    public User(int idUser, String nomUser, String statutUser) {
        this.idUser = idUser;       // Initialisation de l'ID de l'utilisateur
        this.nomUser = nomUser;     // Initialisation du nom de l'utilisateur
        this.statutUser = statutUser; // Initialisation du statut de l'utilisateur
    }

    // Méthode pour récupérer l'ID de l'utilisateur
    public int getIdUser() {
        return idUser;
    }

    // Méthode pour récupérer le nom de l'utilisateur
    public String getNomUser() {
        return nomUser;
    }

    // Méthode pour récupérer le statut de l'utilisateur
    public String getStatutUser() {
        return statutUser;
    }
}
