package sio.devoir3sio1b.Model;

/**
 * Classe représentant un ticket avec ses propriétés et méthodes associées.
 */
public class Ticket {
    // Identifiant unique du ticket
    private int idTicket;

    // Nom ou description du ticket
    private String nomTicket;

    // Date de création ou d'émission du ticket (en format String)
    private String dateTicket;

    // État actuel du ticket (ex : "En cours", "Terminé", etc.)
    private String etatTicket;

    /**
     * Constructeur de la classe Ticket.
     * Permet d'initialiser un ticket avec un identifiant, un nom, une date et un état.
     *
     * @param idTicket   Identifiant unique du ticket
     * @param nomTicket  Nom ou description du ticket
     * @param dateTicket Date d'émission du ticket
     * @param etatTicket État actuel du ticket
     */
    public Ticket(int idTicket, String nomTicket, String dateTicket, String etatTicket) {
        this.idTicket = idTicket;        // Initialisation de l'ID du ticket
        this.nomTicket = nomTicket;     // Initialisation du nom du ticket
        this.dateTicket = dateTicket;   // Initialisation de la date du ticket
        this.etatTicket = etatTicket;   // Initialisation de l'état du ticket
    }

    /**
     * Getter pour obtenir l'identifiant du ticket.
     *
     * @return Identifiant du ticket
     */
    public int getIdTicket() {
        return idTicket;
    }

    /**
     * Getter pour obtenir le nom ou la description du ticket.
     *
     * @return Nom du ticket
     */
    public String getNomTicket() {
        return nomTicket;
    }

    /**
     * Getter pour obtenir la date d'émission du ticket.
     *
     * @return Date du ticket (format String)
     */
    public String getDateTicket() {
        return dateTicket;
    }

    /**
     * Getter pour obtenir l'état actuel du ticket.
     *
     * @return État du ticket
     */
    public String getEtatTicket() {
        return etatTicket;
    }
}
