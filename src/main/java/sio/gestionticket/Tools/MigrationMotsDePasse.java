package sio.gestionticket.Tools;

import sio.gestionticket.Services.ServiceUser;
import java.sql.SQLException;

public class MigrationMotsDePasse {
    public static void main(String[] args) {
        try {
            // Initialisation de la connexion
            new ConnexionBDD();

            // Migration des mots de passe
            ServiceUser serviceUser = new ServiceUser();
            serviceUser.migrerMotsDePasseVersBCrypt();

            System.out.println("Migration des mots de passe terminée avec succès.");
        } catch (ClassNotFoundException | SQLException e) {
            System.err.println("Erreur lors de la migration des mots de passe : " + e.getMessage());
            e.printStackTrace();
        }
    }
}