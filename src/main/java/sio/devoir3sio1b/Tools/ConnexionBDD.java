package sio.devoir3sio1b.Tools;

import java.sql.*;
import java.util.TimeZone;

/**
 * Classe permettant de gérer la connexion à la base de données MySQL.
 */
public class ConnexionBDD {
    // Connexion à la base de données
    private static Connection cnx;

    /**
     * Constructeur de la classe ConnexionBDD.
     * Charge le pilote JDBC et établit la connexion à la base de données.
     *
     * @throws ClassNotFoundException Si le pilote JDBC n'est pas trouvé
     * @throws SQLException           Si une erreur survient lors de la connexion à la base de données
     */
    public ConnexionBDD() throws ClassNotFoundException, SQLException {
        // Nom du pilote JDBC pour MySQL
        String pilote = "com.mysql.jdbc.Driver";
        // Alternativement, vous pouvez utiliser "com.mysql.cj.jdbc.Driver" pour les versions récentes de MySQL.
        // String pilote = "com.mysql.cj.jdbc.Driver";

        // Chargement du pilote JDBC
        Class.forName(pilote);

        // Connexion à la base de données avec l'URL, l'utilisateur et le mot de passe
        // L'URL contient la base de données et le paramètre pour définir le fuseau horaire du serveur
        cnx = DriverManager.getConnection(
                "jdbc:mysql://localhost/bddTicket?serverTimezone=" + TimeZone.getDefault().getID(),
                "root", // Nom d'utilisateur (par défaut "root")
                "root"  // Mot de passe (par défaut "root")
        );
    }

    /**
     * Méthode pour récupérer l'instance de la connexion à la base de données.
     *
     * @return La connexion à la base de données
     */
    public static Connection getCnx() {
        return cnx;
    }
}
