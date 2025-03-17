package sio.gestionticket;

import javafx.collections.FXCollections;
import javafx.event.Event;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import sio.gestionticket.Model.Ticket;
import sio.gestionticket.Model.User;
import sio.gestionticket.Services.ServiceEtat;
import sio.gestionticket.Services.ServiceTicket;

import java.sql.SQLException;

public class UserController {
    // Services pour la gestion des tickets et des états
    ServiceTicket serviceTicket;
    ServiceEtat serviceEtat;
    // Utilisateur connecté
    User user;

    // Colonnes du tableau de tickets
    @javafx.fxml.FXML
    private TableColumn tcNomTicket;
    @javafx.fxml.FXML
    private TableColumn tcDateTicket;
    @javafx.fxml.FXML
    private TableColumn tcNumeroTicket;
    @javafx.fxml.FXML
    private TableColumn tcEtatTicket;

    // TableView pour afficher la liste des tickets
    @javafx.fxml.FXML
    private TableView<Ticket> tvTickets;

    // ComboBox pour la sélection des états des tickets
    @javafx.fxml.FXML
    private ComboBox cboEtats;

    // Bouton permettant de modifier l'état d'un ticket
    @javafx.fxml.FXML
    private Button btnModifier;

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

            // Chargement des tickets de l'utilisateur dans la TableView
            tvTickets.setItems(FXCollections.observableArrayList(serviceTicket.getAllTicketsByIdUser(user.getIdUser())));

            // Chargement des états possibles dans la ComboBox
            cboEtats.setItems(FXCollections.observableArrayList(serviceEtat.getAllEtats()));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Méthode appelée lorsqu'on clique sur le bouton "Modifier".
     * Permet de modifier l'état d'un ticket sélectionné.
     */
    @javafx.fxml.FXML
    public void btnModifierClicked(Event event) throws SQLException {
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
}
