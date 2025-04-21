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
import sio.gestionticket.Tools.SessionManager;

import java.io.IOException;
import java.net.InetAddress;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class LoginController implements Initializable {
    private ServiceUser serviceUser;
    private SessionManager sessionManager;

    @FXML
    private TextField txtLogin;
    @FXML
    private PasswordField txtMotDePasse;
    @FXML
    private Button btnConnexion;
    @FXML
    private Label txtErreur;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            ConnexionBDD cnx = new ConnexionBDD();
            serviceUser = new ServiceUser();
            sessionManager = SessionManager.getInstance();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    public void btnConnexionClicked(Event event) throws SQLException, IOException {
        // Vérification des champs vides
        if (txtLogin.getText().isEmpty()) {
            txtErreur.setText("Saisir votre login");
            return;
        } else if (txtMotDePasse.getText().isEmpty()) {
            txtErreur.setText("Saisir votre mot de passe");
            return;
        }

        // Obtenir un identifiant unique pour le client (peut être l'adresse IP)
        String clientId = InetAddress.getLocalHost().getHostAddress();

        // Vérification des tentatives de connexion
        if (!sessionManager.canAttemptLogin(clientId)) {
            txtErreur.setText("Trop de tentatives, veuillez réessayer dans 15 minutes");
            return;
        }

        // Vérification des identifiants avec la base de données
        User user = serviceUser.verifierIdentifiants(txtLogin.getText(), txtMotDePasse.getText());

        if (user == null) {
            txtErreur.setText("Identifiants incorrects");
            return;
        }

        // Création d'une session pour l'utilisateur
        String sessionToken = sessionManager.createSession(user);

        // Ouverture de la fenêtre appropriée selon le statut de l'utilisateur
        if (user.getStatutUser().equals("admin")) {
            ouvrirFenetreAdmin(sessionToken);
        } else {
            ouvrirFenetreUser(user, sessionToken);
        }

        // Fermeture de la fenêtre de login
        ((Stage) btnConnexion.getScene().getWindow()).close();
    }

    private void ouvrirFenetreAdmin(String sessionToken) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(LoginApplication.class.getResource("admin-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());

        // Passage du token de session au contrôleur
        AdminController adminController = fxmlLoader.getController();
        adminController.setSessionToken(sessionToken);

        Stage stage = new Stage();
        stage.setTitle("Administrateur");
        stage.setScene(scene);
        stage.show();

        // Ajout d'un gestionnaire pour la fermeture de la fenêtre
        stage.setOnCloseRequest(e -> {
            sessionManager.destroySession(sessionToken);
        });
    }

    private void ouvrirFenetreUser(User user, String sessionToken) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(LoginApplication.class.getResource("user-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());

        UserController userController = fxmlLoader.getController();
        userController.initDatas(user);
        userController.setSessionToken(sessionToken);

        Stage stage = new Stage();
        stage.setTitle("Utilisateur");
        stage.setScene(scene);
        stage.show();

        // Ajout d'un gestionnaire pour la fermeture de la fenêtre
        stage.setOnCloseRequest(e -> {
            sessionManager.destroySession(sessionToken);
        });
    }
}