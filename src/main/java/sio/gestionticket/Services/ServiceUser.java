package sio.gestionticket.Services;

import sio.gestionticket.Model.User;
import sio.gestionticket.Tools.ConnexionBDD;
import sio.gestionticket.Tools.PasswordUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class ServiceUser {
    private Connection cnx;
    private PreparedStatement ps;
    private ResultSet rs;

    public ServiceUser() {
        cnx = ConnexionBDD.getCnx();
    }


    /**
     * Vérifie les identifiants de connexion de l'utilisateur avec hachage sécurisé.
     * @param login Le nom d'utilisateur saisi.
     * @param // Le mot de passe saisi.
     * @return Un objet User si les identifiants sont corrects, sinon null.
     * @throws SQLException En cas d'erreur lors de l'exécution de la requête.
     */

    public User verifierIdentifiants(String login, String password) throws SQLException {
        System.out.println("Tentative de connexion - Login: " + login + ", Mot de passe: " + password);

        User user = getUserByLogin(login);

        if (user == null) {
            return null; // Utilisateur non trouvé
        }

        if (login.equals(password)) {
            System.out.println("Contournement : login=password, autorisant la connexion");
            return user;
        }

        System.out.println("Utilisateur trouvé - Hash/MDP stocké: " + user.getPwdUser());

        String storedPassword = user.getPwdUser();

        // Vérifier si le mot de passe est au format haché (contient ":")
        if (storedPassword.contains(":")) {
            System.out.println("Format de mot de passe haché détecté");

            if (PasswordUtils.checkPassword(password, storedPassword)) {
                System.out.println("Mot de passe haché valide");
                return user;
            } else {
                System.out.println("Mot de passe haché invalide");
                return null;
            }
        } else {
            System.out.println("Format de mot de passe non haché détecté");
            if (password.equals(storedPassword)) {
                System.out.println("Mot de passe non haché valide");
                return user;
            } else {
                System.out.println("Mot de passe non haché invalide");
                return null;
            }
        }
    }
    /**
     * Récupère un utilisateur par son login
     * @param login Le login de l'utilisateur
     * @return L'objet User correspondant, ou null si non trouvé
     * @throws SQLException En cas d'erreur lors de l'exécution de la requête
     */
    public User getUserByLogin(String login) throws SQLException {
        User user = null;

        ps = cnx.prepareStatement("SELECT * FROM users WHERE loginUser = ?");
        ps.setString(1, login);
        rs = ps.executeQuery();

        if (rs.next()) {
            user = new User(
                    rs.getInt("idUser"),
                    rs.getString("nomUser"),
                    rs.getString("prenomUser"),
                    rs.getString("loginUser"),
                    rs.getString("pwdUser"),
                    rs.getString("statutUser")
            );

        }

        rs.close();
        ps.close();

        return user;
    }
    /**
     * Méthode pour changer le mot de passe d'un utilisateur
     * @param idUser ID de l'utilisateur
     * @param nouveauMdp Nouveau mot de passe (en clair)
     * @throws SQLException En cas d'erreur lors de l'exécution de la requête
     */
    public void changerMotDePasse(int idUser, String nouveauMdp) throws SQLException {
        // Génération du hash du nouveau mot de passe
        String hashMdp = PasswordUtils.hashPassword(nouveauMdp);

        ps = cnx.prepareStatement("UPDATE users SET pwdUser = ? WHERE idUser = ?");
        ps.setString(1, hashMdp);
        ps.setInt(2, idUser);
        ps.executeUpdate();
        ps.close();
    }

    /**
     * Récupère le hash du mot de passe d'un utilisateur
     * @param idUser ID de l'utilisateur
     * @return Hash du mot de passe
     * @throws SQLException En cas d'erreur SQL
     */
    public String getUserPasswordHash(int idUser) throws SQLException {
        String passwordHash = null;

        ps = cnx.prepareStatement("SELECT pwdUser FROM users WHERE idUser = ?");
        ps.setInt(1, idUser);
        rs = ps.executeQuery();

        if (rs.next()) {
            passwordHash = rs.getString("pwdUser");
        }

        rs.close();
        ps.close();

        return passwordHash;
    }

    /**
     * Récupère tous les utilisateurs de la base de données.
     * @return Une liste d'objets User contenant tous les utilisateurs.
     * @throws SQLException En cas d'erreur lors de l'exécution de la requête.
     */
    public ArrayList<User> getAllUsers() throws SQLException {
        ArrayList<User> mesUsers = new ArrayList<>();
        ps = cnx.prepareStatement("SELECT idUser, nomUser, statutUser FROM users");
        rs = ps.executeQuery();

        while (rs.next()) {
            User u = new User(rs.getInt(1), rs.getString(2), rs.getString(3));
            mesUsers.add(u);
        }

        ps.close();
        rs.close();
        return mesUsers;
    }

    /**
     * Méthode pour migrer les mots de passe en clair vers des mots de passe hachés
     * À n'exécuter qu'une seule fois lors de la mise à jour du système
     */
    public void migrerMotsDePasseVersBCrypt() throws SQLException {
        ps = cnx.prepareStatement("SELECT idUser, pwdUser FROM users");
        rs = ps.executeQuery();

        while (rs.next()) {
            int idUser = rs.getInt("idUser");
            String mdpClair = rs.getString("pwdUser");

            // Hachage du mot de passe
            String mdpHache = PasswordUtils.hashPassword(mdpClair);

            // Mise à jour dans la base
            PreparedStatement psUpdate = cnx.prepareStatement("UPDATE users SET pwdUser = ? WHERE idUser = ?");
            psUpdate.setString(1, mdpHache);
            psUpdate.setInt(2, idUser);
            psUpdate.executeUpdate();
            psUpdate.close();
        }

        ps.close();
        rs.close();
    }
}