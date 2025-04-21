package sio.gestionticket;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.stage.Stage;
import sio.gestionticket.Tools.PasswordUtils;
import sio.gestionticket.Model.User;
import sio.gestionticket.Services.ServiceUser;
import sio.gestionticket.Tools.PasswordPolicy;

import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class ChangePasswordController implements Initializable {

    @FXML
    private PasswordField txtOldPassword;
    @FXML
    private PasswordField txtNewPassword;
    @FXML
    private PasswordField txtConfirmPassword;
    @FXML
    private Label lblPasswordStrength;
    @FXML
    private Label lblErrorMessage;
    @FXML
    private Button btnCancel;
    @FXML
    private Button btnSave;

    private User currentUser;
    private ServiceUser serviceUser;
    private String oldPasswordHash;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        serviceUser = new ServiceUser();

        // Écouter les changements dans le champ du nouveau mot de passe pour évaluer sa force
        txtNewPassword.textProperty().addListener((observable, oldValue, newValue) -> {
            updatePasswordStrength(newValue);
        });
    }

    public void setUser(User user, String oldPasswordHash) {
        this.currentUser = user;
        this.oldPasswordHash = oldPasswordHash;

        // Si le hash n'est pas fourni, le récupérer de la base de données
        if (oldPasswordHash == null) {
            try {
                this.oldPasswordHash = serviceUser.getUserPasswordHash(user.getIdUser());
            } catch (SQLException e) {
                lblErrorMessage.setText("Erreur lors de la récupération du mot de passe: " + e.getMessage());
            }
        }
    }

    @FXML
    private void btnCancelClicked(ActionEvent event) {
        // Fermer la fenêtre
        ((Stage) btnCancel.getScene().getWindow()).close();
    }

    @FXML
    private void btnSaveClicked(ActionEvent event) {
        lblErrorMessage.setText("");

        // Vérification du mot de passe actuel
        if (!PasswordUtils.checkPassword(txtOldPassword.getText(), oldPasswordHash)) {
            lblErrorMessage.setText("Le mot de passe actuel est incorrect");
            return;
        }

        // Vérification que les deux nouveaux mots de passe correspondent
        if (!txtNewPassword.getText().equals(txtConfirmPassword.getText())) {
            lblErrorMessage.setText("Les nouveaux mots de passe ne correspondent pas");
            return;
        }

        // Vérification de la politique de mot de passe
        if (!PasswordPolicy.isValid(txtNewPassword.getText()) ||
                !PasswordPolicy.isNotCommonPassword(txtNewPassword.getText(), currentUser.getNomUser())) {
            lblErrorMessage.setText(PasswordPolicy.getValidationMessage(
                    txtNewPassword.getText(), currentUser.getNomUser()));
            return;
        }

        try {
            // Changement du mot de passe
            serviceUser.changerMotDePasse(currentUser.getIdUser(), txtNewPassword.getText());

            // Fermeture de la fenêtre
            Stage stage = (Stage) btnSave.getScene().getWindow();
            stage.close();
        } catch (SQLException e) {
            lblErrorMessage.setText("Erreur lors du changement de mot de passe: " + e.getMessage());
        }
    }

    private void updatePasswordStrength(String password) {
        if (password.isEmpty()) {
            lblPasswordStrength.setText("Force du mot de passe : ");
            return;
        }

        // Calcul du score de force du mot de passe
        int score = 0;

        // Longueur
        if (password.length() >= 8) score++;
        if (password.length() >= 12) score++;

        // Complexité
        if (password.matches(".*[A-Z].*")) score++;
        if (password.matches(".*[a-z].*")) score++;
        if (password.matches(".*[0-9].*")) score++;
        if (password.matches(".*[^A-Za-z0-9].*")) score++;

        // Affichage du résultat
        String strength;
        String color;

        switch (score) {
            case 0:
            case 1:
                strength = "Très faible";
                color = "red";
                break;
            case 2:
                strength = "Faible";
                color = "orange";
                break;
            case 3:
                strength = "Moyen";
                color = "yellow";
                break;
            case 4:
                strength = "Fort";
                color = "lightgreen";
                break;
            default:
                strength = "Très fort";
                color = "green";
                break;
        }

        lblPasswordStrength.setText("Force du mot de passe : " + strength);
        lblPasswordStrength.setStyle("-fx-text-fill: " + color);
    }
}