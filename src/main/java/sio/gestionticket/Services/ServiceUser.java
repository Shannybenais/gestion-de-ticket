package sio.gestionticket.Services;

import sio.gestionticket.Model.User;
import sio.gestionticket.Tools.ConnexionBDD;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class ServiceUser
{
    // Objet de connexion à la base de données
    private Connection cnx;

    // Objet permettant de préparer des requêtes SQL
    private PreparedStatement ps;

    // Objet contenant les résultats d'une requête SQL
    private ResultSet rs;

    // Constructeur qui initialise la connexion à la base de données
    public ServiceUser()
    {
        cnx = ConnexionBDD.getCnx();
    }

    /**
     * Vérifie les identifiants de connexion de l'utilisateur.
     * @param login Le nom d'utilisateur saisi.
     * @param mdp Le mot de passe saisi.
     * @return Un objet User si les identifiants sont corrects, sinon null.
     * @throws SQLException En cas d'erreur lors de l'exécution de la requête.
     */
    public User verifierIdentifiants(String login, String mdp) throws SQLException {
        User user = null;

        // Prépare une requête SQL pour récupérer l'utilisateur correspondant aux identifiants
        ps = cnx.prepareStatement("select idUser,nomUser, statutUser from users where loginUser = ? and pwdUser = ?");
        ps.setString(1, login);
        ps.setString(2, mdp);

        // Exécute la requête
        rs = ps.executeQuery();

        // Vérifie si un utilisateur a été trouvé
        if (rs.next())
        {
            // Crée un objet User avec les informations récupérées
            user = new User(rs.getInt(1), rs.getString(2), rs.getString(3));
        }

        // Ferme les ressources pour éviter les fuites de mémoire
        ps.close();
        rs.close();

        return user;
    }

    /**
     * Récupère tous les utilisateurs de la base de données.
     * @return Une liste d'objets User contenant tous les utilisateurs.
     * @throws SQLException En cas d'erreur lors de l'exécution de la requête.
     */
    public ArrayList<User> getAllUsers() throws SQLException
    {
        ArrayList<User> mesUsers = new ArrayList<>();

        // Prépare une requête SQL pour récupérer tous les utilisateurs
        ps = cnx.prepareStatement("select idUser,nomUser,statutUser from users");

        // Exécute la requête
        rs = ps.executeQuery();

        // Parcourt les résultats de la requête et ajoute chaque utilisateur à la liste
        while(rs.next())
        {
            User u = new User(rs.getInt(1), rs.getString(2), rs.getString(3));
            mesUsers.add(u);
        }

        // Ferme les ressources pour éviter les fuites de mémoire
        ps.close();
        rs.close();

        return mesUsers;
    }
}
