package sio.gestionticket.Model; // Définition du package où se trouve la classe Ticket

public class Ticket // Déclaration de la classe Ticket
{
    // Déclaration des attributs privés de la classe Ticket
    private int idTicket;      // Identifiant unique du ticket
    private String nomTicket;  // Nom ou titre du ticket
    private String dateTicket; // Date de création ou d'émission du ticket
    private String etatTicket; // État du ticket (exemple : "Ouvert", "Fermé", "En cours")
    private String dateFinSouhaitee; // Date de fin souhaitée pour le ticket
    private int numUser; // Identifiant de l'utilisateur assigné au ticket

    // Constructeur complet avec tous les attributs
    public Ticket(int idTicket, String nomTicket, String dateTicket, String etatTicket, String dateFinSouhaitee, int numUser) {
        this.idTicket = idTicket;   // Initialisation de l'ID du ticket
        this.nomTicket = nomTicket; // Initialisation du nom du ticket
        this.dateTicket = dateTicket; // Initialisation de la date du ticket
        this.etatTicket = etatTicket; // Initialisation de l'état du ticket
        this.dateFinSouhaitee = dateFinSouhaitee; // Initialisation de la date de fin souhaitée
        this.numUser = numUser; // Initialisation de l'ID de l'utilisateur
    }

    // Constructeur de compatibilité avec l'existant
    public Ticket(int idTicket, String nomTicket, String dateTicket, String etatTicket) {
        this(idTicket, nomTicket, dateTicket, etatTicket, null, 0);
    }

    // Méthode pour récupérer l'ID du ticket
    public int getIdTicket() {
        return idTicket;
    }

    // Méthode pour récupérer le nom du ticket
    public String getNomTicket() {
        return nomTicket;
    }

    // Méthode pour récupérer la date du ticket
    public String getDateTicket() {
        return dateTicket;
    }

    // Méthode pour récupérer l'état du ticket
    public String getEtatTicket() {
        return etatTicket;
    }

    // Méthode pour récupérer la date de fin souhaitée
    public String getDateFinSouhaitee() {
        return dateFinSouhaitee;
    }

    // Méthode pour définir la date de fin souhaitée
    public void setDateFinSouhaitee(String dateFinSouhaitee) {
        this.dateFinSouhaitee = dateFinSouhaitee;
    }

    // Méthode pour récupérer l'ID de l'utilisateur assigné
    public int getNumUser() {
        return numUser;
    }

    // Méthode pour définir l'ID de l'utilisateur assigné
    public void setNumUser(int numUser) {
        this.numUser = numUser;
    }
}