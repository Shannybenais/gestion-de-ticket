package sio.gestionticket.Model; // Définition du package où se trouve la classe Ticket

public class Ticket // Déclaration de la classe Ticket
{
    // Déclaration des attributs privés de la classe Ticket
    private int idTicket;      // Identifiant unique du ticket
    private String nomTicket;  // Nom ou titre du ticket
    private String dateTicket; // Date de création ou d'émission du ticket (devrait être un objet Date si des opérations de date sont nécessaires)
    private String etatTicket; // État du ticket (exemple : "Ouvert", "Fermé", "En cours")

    // Constructeur de la classe permettant d'initialiser un ticket avec ses attributs
    public Ticket(int idTicket, String nomTicket, String dateTicket, String etatTicket) {
        this.idTicket = idTicket;   // Initialisation de l'ID du ticket
        this.nomTicket = nomTicket; // Initialisation du nom du ticket
        this.dateTicket = dateTicket; // Initialisation de la date du ticket
        this.etatTicket = etatTicket; // Initialisation de l'état du ticket
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
}
