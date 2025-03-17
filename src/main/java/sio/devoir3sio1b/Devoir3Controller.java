package sio.devoir3sio1b;

import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import sio.devoir3sio1b.Model.User;
import sio.devoir3sio1b.Services.ServiceUser;
import sio.devoir3sio1b.Tools.ConnexionBDD;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class Devoir3Controller implements Initializable
{
    // Service pour gérer les utilisateurs
    private ServiceUser serviceUser;

    // Champs liés au formulaire de connexion
    @FXML
    private TextField txtLogin;
    @FXML
    private Button btnConnexion;
    @FXML
    private Label txtErreur;
    @FXML
    private PasswordField txtMotDePasse;

    /**
     * Méthode appelée lors de l'initialisation du contrôleur.
     * Initialise la connexion à la base de données et le service utilisateur.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            // Établissement de la connexion à la base de données
            ConnexionBDD cnx = new ConnexionBDD();
            // Initialisation du service utilisateur
            serviceUser = new ServiceUser();
        } catch (ClassNotFoundException e) {
            // Gestion des erreurs de chargement du pilote JDBC
            throw new RuntimeException(e);
        } catch (SQLException e) {
            // Gestion des erreurs SQL
            throw new RuntimeException(e);
        }
    }

    /**
     * Gestion de l'événement de clic sur le bouton de connexion.
     * Vérifie les identifiants de l'utilisateur et charge l'interface correspondante.
     * @param event Événement déclenché par le clic sur le bouton.
     * @throws SQLException En cas d'erreur d'accès à la base de données.
     * @throws IOException En cas d'erreur de chargement des fichiers FXML.
     */
    @FXML
    public void btnConnexionClicked(Event event) throws SQLException, IOException {
        // Vérifie si le champ login est vide
        if(txtLogin.getText().isEmpty())
        {
            txtErreur.setText("Saisir votre login");
        }
        // Vérifie si le champ mot de passe est vide
        else if(txtMotDePasse.getText().isEmpty())
        {
            txtErreur.setText("Saisir votre mot de passe");
        }
        else
        {
            // Vérifie les identifiants de l'utilisateur
            User user = serviceUser.verifierIdentifiants(txtLogin.getText(), txtMotDePasse.getText());
            if (user == null) {
                // Affiche un message d'erreur si les identifiants sont incorrects
                txtErreur.setText("Identifiants incorrects");
            }
            else if(user.getStatutUser().equals("admin"))
            {
                // Charge l'interface administrateur si l'utilisateur est un admin
                FXMLLoader fxmlLoader = new FXMLLoader(Devoir3Application.class.getResource("admin-view.fxml"));
                Scene scene = new Scene(fxmlLoader.load());

                Stage stage = new Stage();
                stage.setTitle("Administrateur");
                stage.setScene(scene);
                stage.show();
            }
            else
            {
                // Charge l'interface utilisateur si l'utilisateur est un utilisateur standard
                FXMLLoader fxmlLoader = new FXMLLoader(Devoir3Application.class.getResource("user-view.fxml"));
                Scene scene = new Scene(fxmlLoader.load());
                UserController userController = fxmlLoader.getController();
                // Initialise les données pour l'utilisateur
                userController.initDatas(user);

                Stage stage = new Stage();
                stage.setTitle("Utilisateur");
                stage.setScene(scene);
                stage.show();
            }
        }
    }
}
