package sio.gestionticket;

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
import sio.gestionticket.Model.User;
import sio.gestionticket.Services.ServiceUser;
import sio.gestionticket.Tools.ConnexionBDD;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class LoginController implements Initializable {
    // Service permettant d'interagir avec la base de données pour les utilisateurs
    private ServiceUser serviceUser;

    // Champs de saisie pour le login et le mot de passe
    @FXML
    private TextField txtLogin;
    @FXML
    private PasswordField txtMotDePasse;

    // Bouton de connexion
    @FXML
    private Button btnConnexion;

    // Label affichant les erreurs (ex: identifiants incorrects)
    @FXML
    private Label txtErreur;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            // Création d'une connexion à la base de données
            ConnexionBDD cnx = new ConnexionBDD();
            // Initialisation du service utilisateur
            serviceUser = new ServiceUser();
        } catch (ClassNotFoundException e) {
            // Gère le cas où le driver JDBC n'est pas trouvé
            throw new RuntimeException(e);
        } catch (SQLException e) {
            // Gère les erreurs de connexion à la base de données
            throw new RuntimeException(e);
        }
    }

    @FXML
    public void btnConnexionClicked(Event event) throws SQLException, IOException {
        // Vérification si le champ login est vide
        if (txtLogin.getText().isEmpty()) {
            txtErreur.setText("Saisir votre login");
        }
        // Vérification si le champ mot de passe est vide
        else if (txtMotDePasse.getText().isEmpty()) {
            txtErreur.setText("Saisir votre mot de passe");
        }
        else {
            // Vérification des identifiants avec la base de données
            User user = serviceUser.verifierIdentifiants(txtLogin.getText(), txtMotDePasse.getText());

            // Si aucun utilisateur correspondant n'est trouvé
            if (user == null) {
                txtErreur.setText("Identifiants incorrects");
            }
            // Si l'utilisateur a le statut "admin", ouvrir l'interface administrateur
            else if (user.getStatutUser().equals("admin")) {
                FXMLLoader fxmlLoader = new FXMLLoader(LoginApplication.class.getResource("admin-view.fxml"));
                Scene scene = new Scene(fxmlLoader.load());

                Stage stage = new Stage();
                stage.setTitle("Administrateur");
                stage.setScene(scene);
                stage.show();
            }
            // Sinon, ouvrir l'interface utilisateur standard
            else {
                FXMLLoader fxmlLoader = new FXMLLoader(LoginApplication.class.getResource("user-view.fxml"));
                Scene scene = new Scene(fxmlLoader.load());

                // Récupération du contrôleur de la vue utilisateur et transmission des données de l'utilisateur
                UserController userController = fxmlLoader.getController();
                userController.initDatas(user);

                Stage stage = new Stage();
                stage.setTitle("Utilisateur");
                stage.setScene(scene);
                stage.show();
            }
        }
    }
}
