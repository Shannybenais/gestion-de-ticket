package sio.gestionticket;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import sio.gestionticket.Model.Ticket;
import sio.gestionticket.Model.User;
import sio.gestionticket.Services.ServiceArchive;
import sio.gestionticket.Services.ServiceEtat;
import sio.gestionticket.Services.ServiceTicket;
import sio.gestionticket.Services.ServiceUser;
import sio.gestionticket.Tools.SessionManager;

import java.io.IOException;
import java.net.URL;
import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class AdminController implements Initializable
{
    // Services pour interagir avec la base de données
    ServiceUser serviceUser;
    ServiceTicket serviceTicket;
    ServiceEtat serviceEtat;
    ServiceArchive serviceArchive;

    // Variable pour stocker le token de session
    private String sessionToken;

    // Variable pour stocker l'identifiant de l'utilisateur sélectionné
    int idUSer;

    // Variable pour suivre si on affiche les archives ou non
    private boolean affichageArchives = false;

    // Déclaration des composants de l'interface utilisateur (liés à FXML)
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
    private TableColumn tcFinSouhaitee;

    @javafx.fxml.FXML
    private TableColumn tcUserTicket;

    @javafx.fxml.FXML
    private TableView<Ticket> tvTickets;

    @javafx.fxml.FXML
    private TextField txtNomTicket;

    @javafx.fxml.FXML
    private Button btnInserer;

    @javafx.fxml.FXML
    private Button btnRapport;

    @javafx.fxml.FXML
    private Button btnChangePassword;

    @javafx.fxml.FXML
    private Button btnLogout;

    @javafx.fxml.FXML
    private ComboBox cboEtats;

    @javafx.fxml.FXML
    private DatePicker dpDateFin;

    @javafx.fxml.FXML
    private ToggleButton btnAfficherArchives;

    @javafx.fxml.FXML
    private MenuItem miArchiverTicket;

    @javafx.fxml.FXML
    private MenuItem miDesarchiverTicket;

    @javafx.fxml.FXML
    private MenuItem miAttribuerTicket;

    /**
     * Définit le token de session pour le contrôleur
     * @param sessionToken Le token de session
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
     * Déconnecte l'utilisateur et ferme la fenêtre
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

            // Retour à la page de login
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("login-view.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            Stage stage = new Stage();
            stage.setTitle("Login");
            stage.setScene(scene);
            stage.show();

            // Fermeture de la fenêtre actuelle
            ((Stage) btnInserer.getScene().getWindow()).close();
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
            // Récupérer l'utilisateur de la session
            User user = SessionManager.getInstance().getUserFromSession(sessionToken);
            if (user == null) {
                logout();
                return;
            }

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
     * Méthode déclenchée lorsqu'un utilisateur est sélectionné dans la table des utilisateurs.
     * Elle récupère les tickets liés à l'utilisateur sélectionné.
     * @param event Événement déclencheur
     * @throws SQLException En cas d'erreur SQL
     */
    @javafx.fxml.FXML
    public void tvUsersClicked(Event event) throws SQLException {
        if (!checkSession()) return;

        if (tvUsers.getSelectionModel().getSelectedItem() != null) {
            // Récupère l'identifiant de l'utilisateur sélectionné
            idUSer = tvUsers.getSelectionModel().getSelectedItem().getIdUser();

            // Met à jour la liste des tickets de l'utilisateur sélectionné
            if (affichageArchives) {
                tvTickets.setItems(FXCollections.observableArrayList(serviceArchive.getArchivedTicketsByUser(idUSer)));
            } else {
                tvTickets.setItems(FXCollections.observableArrayList(serviceTicket.getAllTicketsByIdUser(idUSer)));
            }
        }
    }

    /**
     * Méthode d'initialisation appelée automatiquement lors du chargement du contrôleur.
     * Elle initialise les services et configure les colonnes des tables.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources)
    {
        try
        {
            // Initialisation des services
            serviceUser = new ServiceUser();
            serviceTicket = new ServiceTicket();
            serviceEtat = new ServiceEtat();
            serviceArchive = new ServiceArchive();

            // Configuration des colonnes de la table des utilisateurs
            tcNumeroUser.setCellValueFactory(new PropertyValueFactory<>("idUser"));
            tcNomUser.setCellValueFactory(new PropertyValueFactory<>("nomUser"));

            // Remplissage de la table des utilisateurs avec les données de la base
            tvUsers.setItems(FXCollections.observableArrayList(serviceUser.getAllUsers()));

            // Remplissage de la combobox avec les états disponibles
            cboEtats.setItems(FXCollections.observableArrayList(serviceEtat.getAllEtats()));

            // Configuration des colonnes de la table des tickets
            tcNumeroTicket.setCellValueFactory(new PropertyValueFactory<>("idTicket"));
            tcNomTicket.setCellValueFactory(new PropertyValueFactory<>("nomTicket"));
            tcDateTicket.setCellValueFactory(new PropertyValueFactory<>("dateTicket"));
            tcEtatTicket.setCellValueFactory(new PropertyValueFactory<>("etatTicket"));

            // Configuration des nouvelles colonnes
            if (tcFinSouhaitee != null) {
                tcFinSouhaitee.setCellValueFactory(new PropertyValueFactory<>("dateFinSouhaitee"));
            }

            if (tcUserTicket != null) {
                tcUserTicket.setCellValueFactory(new PropertyValueFactory<>("numUser"));
            }

            // Configuration initiale des options du menu contextuel
            if (miDesarchiverTicket != null) {
                miDesarchiverTicket.setVisible(false);
            }

            // Initialisation du DatePicker avec une date par défaut (aujourd'hui + 7 jours)
            if (dpDateFin != null) {
                dpDateFin.setValue(LocalDate.now().plusDays(7));
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Méthode déclenchée lors du clic sur le bouton "Insérer".
     * Elle ajoute un nouveau ticket après avoir validé les champs.
     * @param event Événement déclencheur
     * @throws SQLException En cas d'erreur SQL
     */
    @javafx.fxml.FXML
    public void btnInsererClicked(Event event) throws SQLException {
        if (!checkSession()) return;

        // Vérifie si un utilisateur est sélectionné
        if (tvUsers.getSelectionModel().getSelectedItem() == null)
        {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Choix de l'utilisateur");
            alert.setHeaderText("");
            alert.setContentText("Sélectionner un utilisateur");
            alert.showAndWait();
        }
        // Vérifie si le champ du nom du ticket est vide
        else if (txtNomTicket.getText().isEmpty())
        {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur de saisie");
            alert.setHeaderText("");
            alert.setContentText("Saisir un nom de ticket");
            alert.showAndWait();
        }
        // Vérifie si un état a été sélectionné
        else if (cboEtats.getSelectionModel().getSelectedItem() == null)
        {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Choix de l'état");
            alert.setHeaderText("");
            alert.setContentText("Sélectionner un état");
            alert.showAndWait();
        }
        else
        {
            // Récupération de la date de fin souhaitée si elle est définie
            Date dateFin = null;
            if (dpDateFin != null && dpDateFin.getValue() != null) {
                dateFin = Date.valueOf(dpDateFin.getValue());
            }

            // Insère un nouveau ticket dans la base de données avec la date de fin souhaitée
            serviceTicket.insererNouveauTicket(
                    txtNomTicket.getText(),
                    tvUsers.getSelectionModel().getSelectedItem().getIdUser(),
                    serviceEtat.getIdEtat(cboEtats.getSelectionModel().getSelectedItem().toString()),
                    dateFin
            );

            // Réinitialise le champ texte
            txtNomTicket.setText("");

            // Met à jour la liste des tickets pour refléter le nouvel ajout
            if (tvUsers.getSelectionModel().getSelectedItem() != null) {
                tvTickets.setItems(FXCollections.observableArrayList(serviceTicket.getAllTicketsByIdUser(idUSer)));
                tvTickets.refresh();
            }
        }
    }

    /**
     * Méthode déclenchée lorsqu'on clique sur le bouton pour afficher/masquer les archives.
     * @param event Événement déclencheur
     */
    @javafx.fxml.FXML
    public void btnAfficherArchivesClicked(ActionEvent event) throws SQLException {
        if (!checkSession()) return;

        if (btnAfficherArchives != null) {
            affichageArchives = btnAfficherArchives.isSelected();
            chargerTickets();
        }
    }

    /**
     * Méthode déclenchée lorsqu'on clique sur le bouton pour voir les rapports.
     * Elle ouvre une nouvelle fenêtre contenant les rapports.
     * @param event Événement déclencheur
     */
    @javafx.fxml.FXML
    public void btnRapportClicked(ActionEvent event) {
        if (!checkSession()) return;

        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("rapport-view.fxml"));
            Scene scene = new Scene(fxmlLoader.load());

            // Transmettre le token de session au contrôleur de rapport
            //RapportController rapportController = fxmlLoader.getController();
           // if (rapportController != null) {
            //    rapportController.setSessionToken(sessionToken);
           // }

            Stage stageRapport = new Stage();
            stageRapport.setTitle("Rapports d'activité");
            stageRapport.setScene(scene);
            stageRapport.initModality(Modality.WINDOW_MODAL);
            stageRapport.initOwner(btnRapport.getScene().getWindow());
            stageRapport.show();
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText("Erreur lors de l'ouverture de la fenêtre de rapport");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }

    /**
     * Méthode pour charger les tickets appropriés selon le mode d'affichage
     * (tickets actifs ou archives).
     */
    private void chargerTickets() throws SQLException {
        if (!checkSession()) return;

        if (affichageArchives) {
            // Afficher les tickets archivés
            if (tvUsers.getSelectionModel().getSelectedItem() != null) {
                tvTickets.setItems(FXCollections.observableArrayList(
                        serviceArchive.getArchivedTicketsByUser(tvUsers.getSelectionModel().getSelectedItem().getIdUser())
                ));
            } else {
                tvTickets.setItems(FXCollections.observableArrayList(serviceArchive.getAllArchivedTickets()));
            }
            if (miArchiverTicket != null) miArchiverTicket.setVisible(false);
            if (miDesarchiverTicket != null) miDesarchiverTicket.setVisible(true);
            if (miAttribuerTicket != null) miAttribuerTicket.setVisible(false);
        } else {
            // Afficher les tickets actifs
            if (tvUsers.getSelectionModel().getSelectedItem() != null) {
                tvTickets.setItems(FXCollections.observableArrayList(
                        serviceTicket.getAllTicketsByIdUser(tvUsers.getSelectionModel().getSelectedItem().getIdUser())
                ));
            } else {
                // Si aucun utilisateur n'est sélectionné, afficher tous les tickets
                tvTickets.setItems(FXCollections.observableArrayList(serviceTicket.getAllTickets()));
            }
            if (miArchiverTicket != null) miArchiverTicket.setVisible(true);
            if (miDesarchiverTicket != null) miDesarchiverTicket.setVisible(false);
            if (miAttribuerTicket != null) miAttribuerTicket.setVisible(true);
        }
        tvTickets.refresh();
    }

    /**
     * Méthode déclenchée lors du clic sur l'option "Archiver" du menu contextuel.
     * @param event Événement déclencheur
     */
    @javafx.fxml.FXML
    public void miArchiverTicketClicked(ActionEvent event) throws SQLException {
        if (!checkSession()) return;

        Ticket ticketSelectionne = tvTickets.getSelectionModel().getSelectedItem();
        if (ticketSelectionne == null) {
            afficherAlerte("Veuillez sélectionner un ticket à archiver.", Alert.AlertType.WARNING);
            return;
        }

        // Vérifier si le ticket est terminé
        if (!"Terminé".equals(ticketSelectionne.getEtatTicket())) {
            afficherAlerte("Seuls les tickets avec l'état 'Terminé' peuvent être archivés.", Alert.AlertType.WARNING);
            return;
        }

        // Archiver le ticket
        serviceTicket.archiverTicket(ticketSelectionne.getIdTicket());
        chargerTickets();
        afficherAlerte("Ticket archivé avec succès.", Alert.AlertType.INFORMATION);
    }

    /**
     * Méthode déclenchée lors du clic sur l'option "Désarchiver" du menu contextuel.
     * @param event Événement déclencheur
     */
    @javafx.fxml.FXML
    public void miDesarchiverTicketClicked(ActionEvent event) throws SQLException {
        if (!checkSession()) return;

        if (!affichageArchives) {
            return; // Ne faire rien si on n'est pas en mode affichage des archives
        }

        Ticket ticketSelectionne = tvTickets.getSelectionModel().getSelectedItem();
        if (ticketSelectionne == null) {
            afficherAlerte("Veuillez sélectionner un ticket à désarchiver.", Alert.AlertType.WARNING);
            return;
        }

        // Restaurer le ticket
        serviceArchive.restaurerTicket(ticketSelectionne.getIdTicket());
        chargerTickets();
        afficherAlerte("Ticket désarchivé avec succès.", Alert.AlertType.INFORMATION);
    }

    /**
     * Méthode déclenchée lors du clic sur l'option "Attribuer" du menu contextuel.
     * @param event Événement déclencheur
     */
    @javafx.fxml.FXML
    public void miAttribuerTicketClicked(ActionEvent event) throws SQLException {
        if (!checkSession()) return;

        Ticket ticketSelectionne = tvTickets.getSelectionModel().getSelectedItem();
        User utilisateurSelectionne = tvUsers.getSelectionModel().getSelectedItem();

        if (ticketSelectionne == null) {
            afficherAlerte("Veuillez sélectionner un ticket à attribuer.", Alert.AlertType.WARNING);
            return;
        }

        if (utilisateurSelectionne == null) {
            afficherAlerte("Veuillez sélectionner un utilisateur à qui attribuer le ticket.", Alert.AlertType.WARNING);
            return;
        }

        // Attribuer le ticket à l'utilisateur sélectionné
        serviceTicket.attribuerTicketAUtilisateur(ticketSelectionne.getIdTicket(), utilisateurSelectionne.getIdUser());
        chargerTickets();
        afficherAlerte("Ticket attribué avec succès à " + utilisateurSelectionne.getNomUser() + ".", Alert.AlertType.INFORMATION);
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