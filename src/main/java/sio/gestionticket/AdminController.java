package sio.gestionticket;

import javafx.collections.FXCollections;
import javafx.event.Event;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import sio.gestionticket.Model.Ticket;
import sio.gestionticket.Model.User;
import sio.gestionticket.Services.ServiceEtat;
import sio.gestionticket.Services.ServiceTicket;
import sio.gestionticket.Services.ServiceUser;

import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class AdminController implements Initializable
{
    // Services pour interagir avec la base de données
    ServiceUser serviceUser;
    ServiceTicket serviceTicket;
    ServiceEtat serviceEtat;

    // Variable pour stocker l'identifiant de l'utilisateur sélectionné
    int idUSer;

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
    private TableView<Ticket> tvTickets;

    @javafx.fxml.FXML
    private TextField txtNomTicket;

    @javafx.fxml.FXML
    private Button btnInserer;

    @javafx.fxml.FXML
    private ComboBox cboEtats;

    /**
     * Méthode déclenchée lorsqu'un utilisateur est sélectionné dans la table des utilisateurs.
     * Elle récupère les tickets liés à l'utilisateur sélectionné.
     * @param event Événement déclencheur
     * @throws SQLException En cas d'erreur SQL
     */
    @javafx.fxml.FXML
    public void tvUsersClicked(Event event) throws SQLException {
        // Récupère l'identifiant de l'utilisateur sélectionné
        idUSer = tvUsers.getSelectionModel().getSelectedItem().getIdUser();

        // Met à jour la liste des tickets de l'utilisateur sélectionné
        tvTickets.setItems(FXCollections.observableArrayList(serviceTicket.getAllTicketsByIdUser(idUSer)));
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
            // Insère un nouveau ticket dans la base de données
            serviceTicket.insererNouveauTicket
                    (
                            txtNomTicket.getText(),
                            tvUsers.getSelectionModel().getSelectedItem().getIdUser(),
                            serviceEtat.getIdEtat(cboEtats.getSelectionModel().getSelectedItem().toString())
                    );

            // Réinitialise le champ texte
            txtNomTicket.setText("");

            // Met à jour la liste des tickets pour refléter le nouvel ajout
            tvTickets.setItems(FXCollections.observableArrayList(serviceTicket.getAllTicketsByIdUser(idUSer)));
            tvTickets.refresh();
        }
    }
}
