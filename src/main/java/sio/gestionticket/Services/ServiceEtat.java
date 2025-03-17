package sio.gestionticket.Services; // Définition du package où se trouve la classe ServiceEtat

import sio.gestionticket.Tools.ConnexionBDD; // Import de la classe permettant la connexion à la base de données

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class ServiceEtat // Déclaration de la classe ServiceEtat
{
    // Déclaration des attributs pour gérer la connexion et les requêtes SQL
    private Connection cnx;          // Objet de connexion à la base de données
    private PreparedStatement ps;    // Objet permettant de préparer et exécuter des requêtes SQL
    private ResultSet rs;            // Objet pour stocker le résultat des requêtes SQL

    // Constructeur de la classe : initialise la connexion à la base de données
    public ServiceEtat()
    {
        cnx = ConnexionBDD.getCnx(); // Récupération de la connexion depuis la classe ConnexionBDD
    }

    // Méthode pour récupérer tous les états depuis la base de données
    public ArrayList<String> getAllEtats() throws SQLException
    {
        ArrayList<String> desEtats = new ArrayList<>(); // Liste qui va contenir les noms des états
        ps = cnx.prepareStatement("SELECT nomEtat FROM etats"); // Préparation de la requête SQL
        rs = ps.executeQuery(); // Exécution de la requête et récupération du résultat

        // Parcours du résultat pour ajouter chaque état dans la liste
        while(rs.next())
        {
            desEtats.add(rs.getString(1)); // Ajout du nom de l'état récupéré à la liste
        }

        // Fermeture des ressources pour éviter les fuites de mémoire
        ps.close();
        rs.close();

        return desEtats; // Retourne la liste des états
    }

    // Méthode pour récupérer l'ID d'un état à partir de son nom
    public int getIdEtat(String nomEtat) throws SQLException {
        int idEtat = 0; // Variable pour stocker l'ID de l'état

        // Préparation de la requête SQL avec un paramètre
        ps = cnx.prepareStatement("SELECT idEtat FROM etats WHERE nomEtat = ?");
        ps.setString(1,nomEtat); // Assignation du paramètre dans la requête

        rs = ps.executeQuery(); // Exécution de la requête

        rs.next(); // Passage à la première ligne du résultat
        idEtat = rs.getInt(1); // Récupération de l'ID de l'état

        return idEtat; // Retourne l'ID de l'état correspondant
    }
}
