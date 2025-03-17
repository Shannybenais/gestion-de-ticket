package sio.gestionticket.Tools;
import java.sql.*;
import java.util.TimeZone;

public class ConnexionBDD
{
    private static Connection cnx;

    public ConnexionBDD() throws ClassNotFoundException, SQLException {
        String pilote = "com.mysql.jdbc.Driver";
        //String pilote = "com.mysql.cj.jdbc.Driver";
        // chargement du pilote
        Class.forName(pilote);
        // L'objet connexion Ã  la BDD avec le nom de la base, le user et le password
        cnx = DriverManager.getConnection("jdbc:mysql://localhost/bddTicket?serverTimezone="
                + TimeZone.getDefault().getID(), "root", "root");
    }
    public static Connection getCnx() {
        return cnx;
    }
}
