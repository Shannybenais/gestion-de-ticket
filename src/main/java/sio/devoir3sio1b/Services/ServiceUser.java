package sio.devoir3sio1b.Services;

import sio.devoir3sio1b.Model.User;
import sio.devoir3sio1b.Tools.ConnexionBDD;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Service permettant de gérer les interactions avec la table `users` dans la base de données.
 */
public class ServiceUser {
    // Connexion à la base de données
    private Connection cnx;

    // Objet pour exécuter les requêtes SQL avec des paramètres
    private PreparedStatement ps;

    // Résultat d'une requête SQL
    private ResultSet rs;

    /**
     * Constructeur de la classe ServiceUser.
     * Initialise la connexion à la base de données.
     */
    public ServiceUser() {
        cnx = ConnexionBDD.getCnx(); // Obtient une instance de connexion à la base
    }

    /**
     * Vérifie les identifiants d'un utilisateur (login et mot de passe) pour la connexion.
     *
     * @param login Nom d'utilisateur (login)
     * @param mdp   Mot de passe de l'utilisateur
     * @return Un objet User si les identifiants sont corrects, sinon null
     * @throws SQLException En cas d'erreur lors de l'exécution de la requête
     */
    public User verifierIdentifiants(String login, String mdp) throws SQLException {
        User user = null; // Initialise l'utilisateur comme null
        ps = cnx.prepareStatement(
                "SELECT idUser, nomUser, statutUser FROM users WHERE loginUser = ? AND pwdUser = ?"
        ); // Prépare la requête SQL
        ps.setString(1, login); // Associe le premier paramètre au login
        ps.setString(2, mdp);   // Associe le deuxième paramètre au mot de passe
        rs = ps.executeQuery(); // Exécute la requête
        if (rs.next()) { // Si une ligne est retournée
            user = new User(
                    rs.getInt(1),  // Récupère l'ID de l'utilisateur
                    rs.getString(2), // Récupère le nom de l'utilisateur
                    rs.getString(3)  // Récupère le statut de l'utilisateur
            );
        }
        // Ferme les objets de la base de données
        ps.close();
        rs.close();
        return user; // Retourne l'utilisateur ou null
    }

    /**
     * Récupère tous les utilisateurs de la table `users`.
     *
     * @return Une liste d'objets User contenant tous les utilisateurs
     * @throws SQLException En cas d'erreur lors de l'exécution de la requête
     */
    public ArrayList<User> getAllUsers() throws SQLException {
        ArrayList<User> mesUsers = new ArrayList<>(); // Liste pour stocker les utilisateurs
        ps = cnx.prepareStatement(
                "SELECT idUser, nomUser, statutUser FROM users"
        ); // Prépare la requête SQL
        rs = ps.executeQuery(); // Exécute la requête
        while (rs.next()) { // Parcourt les résultats
            User u = new User(
                    rs.getInt(1),  // Récupère l'ID de l'utilisateur
                    rs.getString(2), // Récupère le nom de l'utilisateur
                    rs.getString(3)  // Récupère le statut de l'utilisateur
            );
            mesUsers.add(u); // Ajoute l'utilisateur à la liste
        }
        // Ferme les objets de la base de données
        ps.close();
        rs.close();
        return mesUsers; // Retourne la liste des utilisateurs
    }
}
