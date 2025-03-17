package sio.devoir3sio1b;

import javafx.collections.FXCollections;
import javafx.event.Event;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import sio.devoir3sio1b.Model.Ticket;
import sio.devoir3sio1b.Model.User;
import sio.devoir3sio1b.Services.ServiceEtat;
import sio.devoir3sio1b.Services.ServiceTicket;
import sio.devoir3sio1b.Services.ServiceUser;

import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

/**
 * Contrôleur pour la gestion des tickets et des utilisateurs dans l'interface administrateur.
 * Implémente `Initializable` pour initialiser les composants JavaFX.
 */
public class AdminController implements Initializable {

    // Services pour accéder aux données des utilisateurs, des tickets, et des états
    ServiceUser serviceUser;
    ServiceTicket serviceTicket;
    ServiceEtat serviceEtat;

    // Identifiant de l'utilisateur sélectionné
    int idUSer;

    // Composants JavaFX
    @javafx.fxml.FXML
    private TableColumn tcNomTicket;
    @javafx.fxml.FXML
    private TableColumn tcDateTicket;
    @javafx.fxml.FXML
    private TableView<User> tvUsers;
    @javafx.fxml.FXML
    private TableColumn tcNomUser;
    @javafx.fxml.FXML
    private TableColumn tcNumeroTicket;
    @javafx.fxml.FXML
    private TableColumn tcEtatTicket;
    @javafx.fxml.FXML
    private TableColumn tcNumeroUser;
    @javafx.fxml.FXML
    private TableView<Ticket> tvTickets;
    @javafx.fxml.FXML
    private TextField txtNomTicket;
    @javafx.fxml.FXML
    private Button btnInserer;
    @javafx.fxml.FXML
    private ComboBox cboEtats;

    /**
     * Gestion du clic sur un utilisateur dans la table des utilisateurs.
     * Charge les tickets associés à l'utilisateur sélectionné.
     */
    @javafx.fxml.FXML
    public void tvUsersClicked(Event event) throws SQLException {
        // Récupération de l'ID de l'utilisateur sélectionné
        idUSer = tvUsers.getSelectionModel().getSelectedItem().getIdUser();

        // Mise à jour des tickets pour l'utilisateur sélectionné
        tvTickets.setItems(FXCollections.observableArrayList(serviceTicket.getAllTicketsByIdUser(idUSer)));
    }

    /**
     * Méthode appelée au démarrage pour initialiser les données et les composants.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            // Initialisation des services
            serviceUser = new ServiceUser();
            serviceTicket = new ServiceTicket();
            serviceEtat = new ServiceEtat();

            // Configuration des colonnes de la table des utilisateurs
            tcNumeroUser.setCellValueFactory(new PropertyValueFactory<>("idUser"));
            tcNomUser.setCellValueFactory(new PropertyValueFactory<>("nomUser"));
            tvUsers.setItems(FXCollections.observableArrayList(serviceUser.getAllUsers())); // Chargement des utilisateurs

            // Configuration de la ComboBox des états
            cboEtats.setItems(FXCollections.observableArrayList(serviceEtat.getAllEtats()));

            // Configuration des colonnes de la table des tickets
            tcNumeroTicket.setCellValueFactory(new PropertyValueFactory<>("idTicket"));
            tcNomTicket.setCellValueFactory(new PropertyValueFactory<>("nomTicket"));
            tcDateTicket.setCellValueFactory(new PropertyValueFactory<>("dateTicket"));
            tcEtatTicket.setCellValueFactory(new PropertyValueFactory<>("etatTicket"));

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Gestion du clic sur le bouton "Insérer".
     * Ajoute un nouveau ticket pour l'utilisateur sélectionné.
     */
    @javafx.fxml.FXML
    public void btnInsererClicked(Event event) throws SQLException {
        // Vérifie qu'un utilisateur est sélectionné
        if (tvUsers.getSelectionModel().getSelectedItem() == null) {
            showAlert(Alert.AlertType.ERROR, "Choix de l'utilisateur", "Sélectionner un utilisateur");
        }
        // Vérifie que le champ du nom du ticket est rempli
        else if (txtNomTicket.getText().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Erreur de saisie", "Saisir un nom de ticket");
        }
        // Vérifie qu'un état est sélectionné
        else if (cboEtats.getSelectionModel().getSelectedItem() == null) {
            showAlert(Alert.AlertType.ERROR, "Choix de l'état", "Sélectionner un état");
        }
        // Si toutes les vérifications passent
        else {
            // Ajoute un nouveau ticket en utilisant les informations saisies
            serviceTicket.insererNouveauTicket(
                    txtNomTicket.getText(),
                    tvUsers.getSelectionModel().getSelectedItem().getIdUser(),
                    serviceEtat.getIdEtat(cboEtats.getSelectionModel().getSelectedItem().toString())
            );

            // Réinitialise le champ du nom du ticket
            txtNomTicket.setText("");

            // Met à jour la liste des tickets pour l'utilisateur sélectionné
            tvTickets.setItems(FXCollections.observableArrayList(serviceTicket.getAllTicketsByIdUser(idUSer)));
            tvTickets.refresh();
        }
    }

    /**
     * Affiche une boîte de dialogue d'alerte.
     *
     * @param alertType Le type d'alerte (ERROR, INFORMATION, etc.).
     * @param title     Le titre de l'alerte.
     * @param content   Le contenu du message.
     */
    private void showAlert(Alert.AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null); // Pas de texte pour l'en-tête
        alert.setContentText(content);
        alert.showAndWait();
    }
}
