package sio.devoir3sio1b.Services;

import sio.devoir3sio1b.Model.Ticket;
import sio.devoir3sio1b.Tools.ConnexionBDD;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Service permettant de gérer les états liés aux tickets dans la base de données.
 */
public class ServiceEtat {
    // Connexion à la base de données
    private Connection cnx;

    // Objet pour exécuter les requêtes SQL avec des paramètres
    private PreparedStatement ps;

    // Résultat d'une requête SQL
    private ResultSet rs;

    /**
     * Constructeur de la classe ServiceEtat.
     * Initialise la connexion à la base de données.
     */
    public ServiceEtat() {
        cnx = ConnexionBDD.getCnx(); // Obtient une instance de connexion à la base
    }

    /**
     * Récupère tous les noms d'états disponibles dans la table `etats`.
     *
     * @return Une liste contenant les noms des états
     * @throws SQLException En cas d'erreur lors de l'exécution de la requête
     */
    public ArrayList<String> getAllEtats() throws SQLException {
        ArrayList<String> desEtats = new ArrayList<>(); // Liste pour stocker les états
        ps = cnx.prepareStatement("SELECT nomEtat FROM etats"); // Prépare la requête SQL
        rs = ps.executeQuery(); // Exécute la requête
        while (rs.next()) { // Parcourt les résultats
            desEtats.add(rs.getString(1)); // Ajoute le nom de l'état à la liste
        }
        // Ferme les objets de la base de données
        ps.close();
        rs.close();
        return desEtats; // Retourne la liste des noms d'états
    }

    /**
     * Récupère l'ID d'un état en fonction de son nom.
     *
     * @param nomEtat Le nom de l'état
     * @return L'ID de l'état correspondant
     * @throws SQLException En cas d'erreur lors de l'exécution de la requête
     */
    public int getIdEtat(String nomEtat) throws SQLException {
        int idEtat = 0; // Initialise l'ID de l'état
        ps = cnx.prepareStatement("SELECT idEtat FROM etats WHERE nomEtat = ?"); // Prépare la requête SQL
        ps.setString(1, nomEtat); // Associe le paramètre au nom de l'état
        rs = ps.executeQuery(); // Exécute la requête
        if (rs.next()) { // Si une ligne est retournée
            idEtat = rs.getInt(1); // Récupère l'ID de l'état
        }
        // Ferme les objets de la base de données
        ps.close();
        rs.close();
        return idEtat; // Retourne l'ID de l'état
    }
}
