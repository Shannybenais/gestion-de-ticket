package sio.gestionticket.Services;

import sio.gestionticket.Model.Ticket;
import sio.gestionticket.Tools.ConnexionBDD;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class ServiceArchive {
    private Connection cnx;
    private PreparedStatement ps;
    private ResultSet rs;

    public ServiceArchive() {
        cnx = ConnexionBDD.getCnx();
    }

    // Méthode pour récupérer tous les tickets archivés
    public ArrayList<Ticket> getAllArchivedTickets() throws SQLException {
        ArrayList<Ticket> archivedTickets = new ArrayList<>();

        ps = cnx.prepareStatement(
                "SELECT ta.idTicket, ta.nomTicket, ta.dateTicket, e.nomEtat, " +
                        "ta.dateFinSouhaitee, ta.numUser " +
                        "FROM tickets_archives ta " +
                        "INNER JOIN etats e ON ta.numEtat = e.idEtat " +
                        "ORDER BY ta.dateFinReelle DESC");

        rs = ps.executeQuery();

        while (rs.next()) {
            Ticket ticket = new Ticket(
                    rs.getInt(1),
                    rs.getString(2),
                    rs.getString(3),
                    rs.getString(4),
                    rs.getString(5),
                    rs.getInt(6)
            );
            archivedTickets.add(ticket);
        }

        rs.close();
        ps.close();

        return archivedTickets;
    }

    // Méthode pour récupérer les tickets archivés d'un utilisateur spécifique
    public ArrayList<Ticket> getArchivedTicketsByUser(int idUser) throws SQLException {
        ArrayList<Ticket> userArchivedTickets = new ArrayList<>();

        ps = cnx.prepareStatement(
                "SELECT ta.idTicket, ta.nomTicket, ta.dateTicket, e.nomEtat, " +
                        "ta.dateFinSouhaitee, ta.numUser " +
                        "FROM tickets_archives ta " +
                        "INNER JOIN etats e ON ta.numEtat = e.idEtat " +
                        "WHERE ta.numUser = ? " +
                        "ORDER BY ta.dateFinReelle DESC");

        ps.setInt(1, idUser);
        rs = ps.executeQuery();

        while (rs.next()) {
            Ticket ticket = new Ticket(
                    rs.getInt(1),
                    rs.getString(2),
                    rs.getString(3),
                    rs.getString(4),
                    rs.getString(5),
                    rs.getInt(6)
            );
            userArchivedTickets.add(ticket);
        }

        rs.close();
        ps.close();

        return userArchivedTickets;
    }

    // Méthode pour restaurer un ticket archivé (le remettre dans les tickets actifs)
    public void restaurerTicket(int idTicket) throws SQLException {
        // 1. Récupérer les informations du ticket archivé
        ps = cnx.prepareStatement(
                "SELECT nomTicket, dateTicket, numUser, numEtat, dateFinSouhaitee " +
                        "FROM tickets_archives WHERE idTicket = ?");
        ps.setInt(1, idTicket);
        rs = ps.executeQuery();

        if (rs.next()) {
            // 2. Insérer dans la table des tickets actifs
            PreparedStatement psInsert = cnx.prepareStatement(
                    "INSERT INTO tickets (idTicket, nomTicket, dateTicket, numUser, numEtat, dateFinSouhaitee) " +
                            "VALUES (?, ?, ?, ?, ?, ?)");

            psInsert.setInt(1, idTicket);
            psInsert.setString(2, rs.getString("nomTicket"));
            psInsert.setDate(3, rs.getDate("dateTicket"));
            psInsert.setInt(4, rs.getInt("numUser"));
            psInsert.setInt(5, rs.getInt("numEtat"));
            psInsert.setDate(6, rs.getDate("dateFinSouhaitee"));

            psInsert.executeUpdate();
            psInsert.close();

            // 3. Supprimer l'archive
            PreparedStatement psDelete = cnx.prepareStatement("DELETE FROM tickets_archives WHERE idTicket = ?");
            psDelete.setInt(1, idTicket);
            psDelete.executeUpdate();
            psDelete.close();
        }

        rs.close();
        ps.close();
    }
}