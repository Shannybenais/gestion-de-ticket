package sio.gestionticket.Services;

import sio.gestionticket.Model.Ticket;
import sio.gestionticket.Tools.ConnexionBDD;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;

public class ServiceTicket
{
    // Déclaration des attributs pour gérer la connexion et les requêtes SQL
    private Connection cnx;          // Connexion à la base de données
    private PreparedStatement ps;    // Objet pour exécuter des requêtes SQL paramétrées
    private ResultSet rs;            // Objet pour stocker les résultats des requêtes SQL

    // Constructeur : initialise la connexion à la base de données
    public ServiceTicket()
    {
        cnx = ConnexionBDD.getCnx(); // Récupération de la connexion à la BDD
    }

    // Méthode pour récupérer tous les tickets d'un utilisateur via son ID
    public ArrayList<Ticket> getAllTicketsByIdUser(int idUser) throws SQLException
    {
        ArrayList<Ticket> desTickets = new ArrayList<>(); // Liste pour stocker les tickets récupérés

        // Préparation de la requête SQL pour récupérer les tickets et leur état - Mise à jour pour inclure dateFinSouhaitee
        ps = cnx.prepareStatement("SELECT tickets.idTicket, tickets.nomTicket, tickets.dateticket, etats.nomEtat, " +
                "tickets.dateFinSouhaitee, tickets.numUser " +
                "FROM tickets " +
                "INNER JOIN etats ON tickets.numEtat = etats.idEtat " +
                "WHERE tickets.numUser = ?");  // Utilisation d'un paramètre sécurisé (évite l'injection SQL)

        ps.setInt(1, idUser); // Attribution du paramètre

        rs = ps.executeQuery(); // Exécution de la requête

        // Parcours des résultats et création des objets Ticket
        while (rs.next())
        {
            Ticket monTicket = new Ticket(
                    rs.getInt(1),   // ID du ticket
                    rs.getString(2), // Nom du ticket
                    rs.getString(3), // Date du ticket
                    rs.getString(4), // État du ticket
                    rs.getString(5), // Date de fin souhaitée
                    rs.getInt(6)     // ID de l'utilisateur assigné
            );
            desTickets.add(monTicket); // Ajout du ticket à la liste
        }

        // Fermeture des ressources pour éviter les fuites de mémoire
        rs.close();
        ps.close();

        return desTickets; // Retourne la liste des tickets
    }

    // Méthode pour récupérer tous les tickets (pour l'administrateur)
    public ArrayList<Ticket> getAllTickets() throws SQLException
    {
        ArrayList<Ticket> desTickets = new ArrayList<>(); // Liste pour stocker les tickets récupérés

        // Préparation de la requête SQL pour récupérer tous les tickets
        ps = cnx.prepareStatement("SELECT tickets.idTicket, tickets.nomTicket, tickets.dateticket, etats.nomEtat, " +
                "tickets.dateFinSouhaitee, tickets.numUser " +
                "FROM tickets " +
                "INNER JOIN etats ON tickets.numEtat = etats.idEtat");

        rs = ps.executeQuery(); // Exécution de la requête

        // Parcours des résultats et création des objets Ticket
        while (rs.next())
        {
            Ticket monTicket = new Ticket(
                    rs.getInt(1),   // ID du ticket
                    rs.getString(2), // Nom du ticket
                    rs.getString(3), // Date du ticket
                    rs.getString(4), // État du ticket
                    rs.getString(5), // Date de fin souhaitée
                    rs.getInt(6)     // ID de l'utilisateur assigné
            );
            desTickets.add(monTicket); // Ajout du ticket à la liste
        }

        // Fermeture des ressources pour éviter les fuites de mémoire
        rs.close();
        ps.close();

        return desTickets; // Retourne la liste des tickets
    }

    // Méthode pour modifier l'état d'un ticket en fonction de son ID
    public void modifierEtatTicket(int idTicket, int idEtat) throws SQLException {
        // Préparation de la requête SQL de mise à jour
        ps = cnx.prepareStatement("UPDATE tickets SET numEtat = ? WHERE idTicket = ?");

        ps.setInt(1, idEtat);    // Attribution du nouvel état
        ps.setInt(2, idTicket);  // Identification du ticket à modifier

        ps.executeUpdate(); // Exécution de la mise à jour

        ps.close(); // Fermeture de la requête
    }

    // Méthode pour insérer un nouveau ticket dans la base de données (version mise à jour)
    public void insererNouveauTicket(String nomTicket, int numUser, int numEtat, Date dateFinSouhaitee) throws SQLException {
        // Préparation de la requête d'insertion mise à jour pour inclure dateFinSouhaitee
        ps = cnx.prepareStatement("INSERT INTO tickets VALUES (NULL, ?, ?, ?, ?, ?)");

        ps.setString(1, nomTicket);                  // Nom du ticket
        ps.setDate(2, Date.valueOf(LocalDate.now())); // Date actuelle
        ps.setInt(3, numUser);                     // ID de l'utilisateur
        ps.setInt(4, numEtat);                     // ID de l'état du ticket

        // Gestion de la date de fin souhaitée (peut être null)
        if (dateFinSouhaitee != null) {
            ps.setDate(5, dateFinSouhaitee);
        } else {
            ps.setNull(5, Types.DATE);
        }

        ps.executeUpdate(); // Exécution de l'insertion

        ps.close(); // Fermeture de la requête
    }

    // Version de compatibilité pour l'ancienne méthode (sans date de fin)
    public void insererNouveauTicket(String nomTicket, int numUser, int numEtat) throws SQLException {
        insererNouveauTicket(nomTicket, numUser, numEtat, null);
    }

    // Méthode pour archiver un ticket terminé
    public void archiverTicket(int idTicket) throws SQLException {
        // 1. Récupérer les informations du ticket
        ps = cnx.prepareStatement("SELECT nomTicket, dateTicket, numUser, numEtat, dateFinSouhaitee " +
                "FROM tickets WHERE idTicket = ? AND numEtat = 4"); // État 4 = Terminé
        ps.setInt(1, idTicket);
        rs = ps.executeQuery();

        if (rs.next()) {
            // 2. Insérer dans la table d'archives
            PreparedStatement psInsert = cnx.prepareStatement(
                    "INSERT INTO tickets_archives (idTicket, nomTicket, dateTicket, dateFinReelle, numUser, numEtat, dateFinSouhaitee) " +
                            "VALUES (?, ?, ?, ?, ?, ?, ?)");

            psInsert.setInt(1, idTicket);
            psInsert.setString(2, rs.getString("nomTicket"));
            psInsert.setDate(3, rs.getDate("dateTicket"));
            psInsert.setDate(4, Date.valueOf(LocalDate.now())); // Date d'archivage = aujourd'hui
            psInsert.setInt(5, rs.getInt("numUser"));
            psInsert.setInt(6, rs.getInt("numEtat"));
            psInsert.setDate(7, rs.getDate("dateFinSouhaitee"));

            psInsert.executeUpdate();
            psInsert.close();

            // 3. Supprimer le ticket original
            PreparedStatement psDelete = cnx.prepareStatement("DELETE FROM tickets WHERE idTicket = ?");
            psDelete.setInt(1, idTicket);
            psDelete.executeUpdate();
            psDelete.close();
        }

        rs.close();
        ps.close();
    }

    // Méthode pour réattribuer un ticket à un autre utilisateur
    public void attribuerTicketAUtilisateur(int idTicket, int idNouvelUtilisateur) throws SQLException {
        ps = cnx.prepareStatement("UPDATE tickets SET numUser = ? WHERE idTicket = ?");

        ps.setInt(1, idNouvelUtilisateur);
        ps.setInt(2, idTicket);

        ps.executeUpdate();
        ps.close();
    }

    // Méthode pour mettre à jour la date de fin souhaitée
    public void updateDateFinSouhaitee(int idTicket, Date dateFinSouhaitee) throws SQLException {
        ps = cnx.prepareStatement("UPDATE tickets SET dateFinSouhaitee = ? WHERE idTicket = ?");

        if (dateFinSouhaitee != null) {
            ps.setDate(1, dateFinSouhaitee);
        } else {
            ps.setNull(1, Types.DATE);
        }

        ps.setInt(2, idTicket);

        ps.executeUpdate();
        ps.close();
    }
}