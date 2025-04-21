package sio.gestionticket;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import sio.gestionticket.Model.Ticket;
import sio.gestionticket.Model.User;
import sio.gestionticket.Services.ServiceEtat;
import sio.gestionticket.Services.ServiceTicket;
import sio.gestionticket.Tools.SessionManager;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class UserController {
    // Services pour la gestion des tickets et des états
    ServiceTicket serviceTicket;
    ServiceEtat serviceEtat;

    // Utilisateur connecté
    User user;

    // Token de session
    private String sessionToken;

    // Colonnes du tableau de tickets
    @javafx.fxml.FXML
    private TableColumn tcNomTicket;
    @javafx.fxml.FXML
    private TableColumn tcDateTicket;
    @javafx.fxml.FXML
    private TableColumn tcNumeroTicket;
    @javafx.fxml.FXML
    private TableColumn tcEtatTicket;
    @javafx.fxml.FXML
    private TableColumn tcFinSouhaitee; // Nouvelle colonne pour la date de fin souhaitée

    // TableView pour afficher la liste des tickets
    @javafx.fxml.FXML
    private TableView<Ticket> tvTickets;

    // ComboBox pour la sélection des états des tickets
    @javafx.fxml.FXML
    private ComboBox cboEtats;

    // Bouton permettant de modifier l'état d'un ticket
    @javafx.fxml.FXML
    private Button btnModifier;

    // Bouton pour changer de mot de passe
    @javafx.fxml.FXML
    private Button btnChangePassword;

    // Bouton pour se déconnecter
    @javafx.fxml.FXML
    private Button btnLogout;

    /**
     * Initialise les données de l'utilisateur et charge ses tickets et les états possibles.
     * @param unUser L'utilisateur connecté
     */
    public void initDatas(User unUser) {
        serviceTicket = new ServiceTicket();
        serviceEtat = new ServiceEtat();
        user = unUser;

        try {
            // Initialisation des services de gestion des tickets et des états
            serviceTicket = new ServiceTicket();

            // Configuration des colonnes de la TableView avec les attributs de la classe Ticket
            tcNumeroTicket.setCellValueFactory(new PropertyValueFactory<>("idTicket"));
            tcNomTicket.setCellValueFactory(new PropertyValueFactory<>("nomTicket"));
            tcDateTicket.setCellValueFactory(new PropertyValueFactory<>("dateTicket"));
            tcEtatTicket.setCellValueFactory(new PropertyValueFactory<>("etatTicket"));

            // Configuration de la nouvelle colonne pour la date de fin souhaitée
            if (tcFinSouhaitee != null) {
                tcFinSouhaitee.setCellValueFactory(new PropertyValueFactory<>("dateFinSouhaitee"));
            }

            // Chargement des tickets de l'utilisateur dans la TableView
            tvTickets.setItems(FXCollections.observableArrayList(serviceTicket.getAllTicketsByIdUser(user.getIdUser())));

            // Chargement des états possibles dans la ComboBox
            cboEtats.setItems(FXCollections.observableArrayList(serviceEtat.getAllEtats()));

            // Ajout d'un menu contextuel pour une meilleure expérience utilisateur
            ContextMenu contextMenu = new ContextMenu();

            // Option pour voir les détails du ticket (notamment la date de fin souhaitée)
            MenuItem miVoirDetails = new MenuItem("Voir détails");
            miVoirDetails.setOnAction(event -> {
                if (!checkSession()) return;
                afficherDetailsTicket();
            });

            contextMenu.getItems().add(miVoirDetails);
            tvTickets.setContextMenu(contextMenu);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Définit le token de session
     * @param sessionToken Token de session
     */
    public void setSessionToken(String sessionToken) {
        this.sessionToken = sessionToken;
    }

    /**
     * Vérifie si la session est valide
     * @return true si la session est valide, false sinon
     */
    private boolean checkSession() {
        boolean sessionValide = SessionManager.getInstance().isValidSession(sessionToken);
        if (!sessionValide) {
            logout();
        }
        return sessionValide;
    }

    /**
     * Gère la déconnexion de l'utilisateur
     */
    @javafx.fxml.FXML
    public void btnLogoutClicked(ActionEvent event) {
        logout();
    }

    /**
     * Méthode interne pour la déconnexion
     */
    private void logout() {
        try {
            // Destruction de la session
            if (sessionToken != null) {
                SessionManager.getInstance().destroySession(sessionToken);
            }

            // Ouverture de la page de login
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("login-view.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            Stage stage = new Stage();
            stage.setTitle("Login");
            stage.setScene(scene);
            stage.show();

            // Fermeture de la fenêtre actuelle
            ((Stage) btnModifier.getScene().getWindow()).close();
        } catch (IOException e) {
            afficherAlerte("Erreur lors de la déconnexion: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    /**
     * Ouvre la fenêtre de changement de mot de passe
     */
    @javafx.fxml.FXML
    public void btnChangePasswordClicked(ActionEvent event) {
        if (!checkSession()) return;

        try {
            // Charger la vue de changement de mot de passe
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("change-password-view.fxml"));
            Scene scene = new Scene(fxmlLoader.load());

            // Obtenir le contrôleur et définir l'utilisateur
            ChangePasswordController controller = fxmlLoader.getController();
            controller.setUser(user, null);  // Le hash est récupéré dans le contrôleur

            Stage stage = new Stage();
            stage.setTitle("Changer de mot de passe");
            stage.setScene(scene);
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(btnChangePassword.getScene().getWindow());
            stage.showAndWait();
        } catch (IOException e) {
            afficherAlerte("Erreur lors de l'ouverture de la fenêtre de changement de mot de passe: " + e.getMessage(),
                    Alert.AlertType.ERROR);
        }
    }

    /**
     * Méthode appelée lorsqu'on clique sur le bouton "Modifier".
     * Permet de modifier l'état d'un ticket sélectionné.
     */
    @javafx.fxml.FXML
    public void btnModifierClicked(Event event) throws SQLException {
        if (!checkSession()) return;

        // Vérification si un ticket est sélectionné
        if (tvTickets.getSelectionModel().getSelectedItem() == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Choix du ticket");
            alert.setHeaderText("");
            alert.setContentText("Sélectionner un ticket");
            alert.showAndWait();
        }
        // Vérification si un état est sélectionné dans la ComboBox
        else if (cboEtats.getSelectionModel().getSelectedItem() == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Choix de l'état");
            alert.setHeaderText("");
            alert.setContentText("Sélectionner un état");
            alert.showAndWait();
        }
        else {
            // Récupération de l'ID de l'état sélectionné
            int idEtat = serviceEtat.getIdEtat(cboEtats.getSelectionModel().getSelectedItem().toString());

            // Modification de l'état du ticket sélectionné
            serviceTicket.modifierEtatTicket(tvTickets.getSelectionModel().getSelectedItem().getIdTicket(), idEtat);

            // Rafraîchissement de la TableView pour refléter les modifications
            tvTickets.setItems(FXCollections.observableArrayList(serviceTicket.getAllTicketsByIdUser(user.getIdUser())));
            tvTickets.refresh();
        }
    }

    /**
     * Affiche les détails d'un ticket, y compris sa date de fin souhaitée
     */
    private void afficherDetailsTicket() {
        Ticket ticket = tvTickets.getSelectionModel().getSelectedItem();

        if (ticket == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Sélection requise");
            alert.setHeaderText(null);
            alert.setContentText("Veuillez sélectionner un ticket pour voir ses détails.");
            alert.showAndWait();
            return;
        }

        // Création d'une boîte de dialogue pour afficher les détails
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Détails du ticket #" + ticket.getIdTicket());
        dialog.setHeaderText("Informations détaillées");

        // Création du contenu
        DialogPane dialogPane = dialog.getDialogPane();

        // Texte avec les informations
        StringBuilder content = new StringBuilder();
        content.append("ID: ").append(ticket.getIdTicket()).append("\n");
        content.append("Nom: ").append(ticket.getNomTicket()).append("\n");
        content.append("Date de création: ").append(ticket.getDateTicket()).append("\n");
        content.append("État actuel: ").append(ticket.getEtatTicket()).append("\n");

        // Formatage de la date de fin souhaitée
        if (ticket.getDateFinSouhaitee() != null && !ticket.getDateFinSouhaitee().isEmpty()) {
            content.append("Date de fin souhaitée: ").append(ticket.getDateFinSouhaitee()).append("\n");

            // Vérification si la date de fin souhaitée est passée
            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                LocalDate dateFinSouhaitee = LocalDate.parse(ticket.getDateFinSouhaitee(), formatter);
                LocalDate dateActuelle = LocalDate.now();

                if (dateFinSouhaitee.isBefore(dateActuelle)) {
                    content.append("\nATTENTION: La date de fin souhaitée est dépassée!\n");
                } else {
                    long joursRestants = dateActuelle.until(dateFinSouhaitee).getDays();
                    content.append("Jours restants: ").append(joursRestants).append("\n");
                }
            } catch (Exception e) {
                // En cas d'erreur de parsing de la date, on ne fait rien de spécial
            }
        } else {
            content.append("Date de fin souhaitée: Non définie\n");
        }

        Label label = new Label(content.toString());
        dialogPane.setContent(label);

        // Ajout d'un bouton OK
        ButtonType buttonTypeOk = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        dialogPane.getButtonTypes().add(buttonTypeOk);

        dialog.showAndWait();
    }

    /**
     * Méthode utilitaire pour afficher des alertes.
     */
    private void afficherAlerte(String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(type == Alert.AlertType.ERROR ? "Erreur" :
                (type == Alert.AlertType.WARNING ? "Avertissement" : "Information"));
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
