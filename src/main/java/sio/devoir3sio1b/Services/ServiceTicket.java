package sio.devoir3sio1b.Services;

import sio.devoir3sio1b.Model.Ticket;
import sio.devoir3sio1b.Tools.ConnexionBDD;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;

/**
 * Service pour gérer les opérations liées aux tickets dans la base de données.
 */
public class ServiceTicket {
    // Connexion à la base de données
    private Connection cnx;

    // Objet pour exécuter les requêtes SQL avec des paramètres
    private PreparedStatement ps;

    // Résultat d'une requête SQL
    private ResultSet rs;

    /**
     * Constructeur de la classe ServiceTicket.
     * Initialise la connexion à la base de données.
     */
    public ServiceTicket() {
        cnx = ConnexionBDD.getCnx(); // Obtient une instance de connexion à la base
    }

    /**
     * Récupère tous les tickets d'un utilisateur spécifique.
     *
     * @param idUser L'ID de l'utilisateur
     * @return Une liste contenant tous les tickets associés à cet utilisateur
     * @throws SQLException En cas d'erreur lors de l'exécution de la requête
     */
    public ArrayList<Ticket> getAllTicketsByIdUser(int idUser) throws SQLException {
        ArrayList<Ticket> desTickets = new ArrayList<>(); // Liste pour stocker les tickets
        ps = cnx.prepareStatement(
                "SELECT tickets.idTicket, tickets.nomTicket, tickets.dateticket, etats.nomEtat " +
                        "FROM tickets " +
                        "INNER JOIN etats ON tickets.numEtat = etats.idEtat " +
                        "WHERE tickets.numUser = ?"
        );
        ps.setInt(1, idUser); // Associe l'ID de l'utilisateur au paramètre
        rs = ps.executeQuery(); // Exécute la requête
        while (rs.next()) { // Parcourt les résultats
            // Crée un ticket avec les données récupérées
            Ticket monTicket = new Ticket(rs.getInt(1), rs.getString(2), rs.getString(3), rs.getString(4));
            desTickets.add(monTicket); // Ajoute le ticket à la liste
        }
        // Ferme les objets de la base de données
        ps.close();
        rs.close();
        return desTickets; // Retourne la liste des tickets
    }

    /**
     * Modifie l'état d'un ticket spécifique.
     *
     * @param idTicket L'ID du ticket à modifier
     * @param idEtat   L'ID du nouvel état à assigner au ticket
     * @throws SQLException En cas d'erreur lors de l'exécution de la requête
     */
    public void modifierEtatTicket(int idTicket, int idEtat) throws SQLException {
        ps = cnx.prepareStatement("UPDATE tickets SET numEtat = ? WHERE idTicket = ?");
        ps.setInt(1, idEtat); // Associe le nouvel état au paramètre
        ps.setInt(2, idTicket); // Associe l'ID du ticket au paramètre
        ps.executeUpdate(); // Exécute la mise à jour
        ps.close(); // Ferme l'objet PreparedStatement
    }

    /**
     * Insère un nouveau ticket dans la base de données.
     *
     * @param nomEtat Le nom du ticket
     * @param numUser L'ID de l'utilisateur créant le ticket
     * @param numEtat L'ID de l'état initial du ticket
     * @throws SQLException En cas d'erreur lors de l'exécution de la requête
     */
    public void insererNouveauTicket(String nomEtat, int numUser, int numEtat) throws SQLException {
        ps = cnx.prepareStatement("INSERT INTO tickets VALUES (NULL, ?, ?, ?, ?)");
        ps.setString(1, nomEtat); // Associe le nom du ticket au paramètre
        ps.setDate(2, Date.valueOf(LocalDate.now())); // Associe la date actuelle au paramètre
        ps.setInt(3, numUser); // Associe l'ID de l'utilisateur au paramètre
        ps.setInt(4, numEtat); // Associe l'état initial au paramètre
        ps.executeUpdate(); // Exécute l'insertion
        ps.close(); // Ferme l'objet PreparedStatement
    }
}
