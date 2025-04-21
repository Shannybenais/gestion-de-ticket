package sio.gestionticket.Services;

import sio.gestionticket.Model.Ticket;
import sio.gestionticket.Model.User;
import sio.gestionticket.Tools.ConnexionBDD;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ServiceRapport {
    private Connection cnx;
    private PreparedStatement ps;
    private ResultSet rs;

    public ServiceRapport() {
        cnx = ConnexionBDD.getCnx();
    }

    /**
     * Récupère le nombre de tickets traités par utilisateur
     * @return Map avec l'ID de l'utilisateur en clé et le nombre de tickets en valeur
     */
    public Map<Integer, Integer> getNombreTicketsTraitesParUtilisateur() throws SQLException {
        Map<Integer, Integer> resultat = new HashMap<>();

        ps = cnx.prepareStatement(
                "SELECT numUser, COUNT(*) as nbTickets " +
                        "FROM tickets_archives " +
                        "GROUP BY numUser"
        );

        rs = ps.executeQuery();

        while (rs.next()) {
            resultat.put(rs.getInt("numUser"), rs.getInt("nbTickets"));
        }

        rs.close();
        ps.close();

        return resultat;
    }

    /**
     * Récupère le nombre de tickets traités par utilisateur et par état (priorité)
     * @return Map avec une paire (ID utilisateur, ID état) en clé et le nombre de tickets en valeur
     */
    public Map<String, Integer> getNombreTicketsTraitesParUtilisateurEtPriorite() throws SQLException {
        Map<String, Integer> resultat = new HashMap<>();

        ps = cnx.prepareStatement(
                "SELECT numUser, numEtat, COUNT(*) as nbTickets " +
                        "FROM tickets_archives " +
                        "GROUP BY numUser, numEtat"
        );

        rs = ps.executeQuery();

        while (rs.next()) {
            String cle = rs.getInt("numUser") + "-" + rs.getInt("numEtat");
            resultat.put(cle, rs.getInt("nbTickets"));
        }

        rs.close();
        ps.close();

        return resultat;
    }

    /**
     * Récupère le nombre total de tickets en retard (dateFinSouhaitee dépassée)
     * @return Nombre de tickets en retard
     */
    public int getNombreTicketsEnRetard() throws SQLException {
        int nombre = 0;

        ps = cnx.prepareStatement(
                "SELECT COUNT(*) as nbTickets " +
                        "FROM tickets " +
                        "WHERE dateFinSouhaitee < CURRENT_DATE " +
                        "AND numEtat != 4" // Exclusion des tickets terminés
        );

        rs = ps.executeQuery();

        if (rs.next()) {
            nombre = rs.getInt("nbTickets");
        }

        rs.close();
        ps.close();

        return nombre;
    }

    /**
     * Récupère la liste des tickets en retard
     * @return Liste des tickets en retard
     */
    public ArrayList<Ticket> getListeTicketsEnRetard() throws SQLException {
        ArrayList<Ticket> ticketsEnRetard = new ArrayList<>();

        ps = cnx.prepareStatement(
                "SELECT t.idTicket, t.nomTicket, t.dateTicket, e.nomEtat, " +
                        "t.dateFinSouhaitee, t.numUser " +
                        "FROM tickets t " +
                        "INNER JOIN etats e ON t.numEtat = e.idEtat " +
                        "WHERE t.dateFinSouhaitee < CURRENT_DATE " +
                        "AND t.numEtat != 4" // Exclusion des tickets terminés
        );

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
            ticketsEnRetard.add(ticket);
        }

        rs.close();
        ps.close();

        return ticketsEnRetard;
    }

    /**
     * Récupère le nom de l'état (priorité) à partir de son ID
     * @param idEtat ID de l'état
     * @return Nom de l'état
     */
    public String getNomEtat(int idEtat) throws SQLException {
        String nomEtat = "";

        ps = cnx.prepareStatement("SELECT nomEtat FROM etats WHERE idEtat = ?");
        ps.setInt(1, idEtat);

        rs = ps.executeQuery();

        if (rs.next()) {
            nomEtat = rs.getString("nomEtat");
        }

        rs.close();
        ps.close();

        return nomEtat;
    }

    /**
     * Récupère le nom de l'utilisateur à partir de son ID
     * @param idUser ID de l'utilisateur
     * @return Nom de l'utilisateur
     */
    public String getNomUtilisateur(int idUser) throws SQLException {
        String nomUser = "";

        ps = cnx.prepareStatement("SELECT nomUser FROM users WHERE idUser = ?");
        ps.setInt(1, idUser);

        rs = ps.executeQuery();

        if (rs.next()) {
            nomUser = rs.getString("nomUser");
        }

        rs.close();
        ps.close();

        return nomUser;
    }
}