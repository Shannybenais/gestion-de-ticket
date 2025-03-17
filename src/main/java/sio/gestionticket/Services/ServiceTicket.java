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

        // Préparation de la requête SQL pour récupérer les tickets et leur état
        ps = cnx.prepareStatement("SELECT tickets.idTicket, tickets.nomTicket, tickets.dateticket, etats.nomEtat " +
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
                    rs.getString(4)  // État du ticket
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

    // Méthode pour insérer un nouveau ticket dans la base de données
    public void insererNouveauTicket(String nomEtat, int numUser, int numEtat) throws SQLException {
        // Préparation de la requête d'insertion
        ps = cnx.prepareStatement("INSERT INTO tickets VALUES (NULL, ?, ?, ?, ?)");

        ps.setString(1, nomEtat);                  // Nom du ticket
        ps.setDate(2, Date.valueOf(LocalDate.now())); // Date actuelle
        ps.setInt(3, numUser);                     // ID de l'utilisateur
        ps.setInt(4, numEtat);                     // ID de l'état du ticket

        ps.executeUpdate(); // Exécution de l'insertion

        ps.close(); // Fermeture de la requête
    }
}
