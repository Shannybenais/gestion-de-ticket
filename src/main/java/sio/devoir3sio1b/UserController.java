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

import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class UserController
{
    // Services pour interagir avec les données des tickets et des états
    ServiceTicket serviceTicket;
    ServiceEtat serviceEtat;

    // Utilisateur courant
    User user;

    // Colonnes du tableau pour afficher les informations des tickets
    @javafx.fxml.FXML
    private TableColumn tcNomTicket;
    @javafx.fxml.FXML
    private TableColumn tcDateTicket;
    @javafx.fxml.FXML
    private TableColumn tcNumeroTicket;
    @javafx.fxml.FXML
    private TableColumn tcEtatTicket;

    // Tableau des tickets
    @javafx.fxml.FXML
    private TableView<Ticket> tvTickets;

    // ComboBox pour sélectionner les états disponibles
    @javafx.fxml.FXML
    private ComboBox cboEtats;

    // Bouton pour modifier l'état du ticket
    @javafx.fxml.FXML
    private Button btnModifier;

    /**
     * Initialise les données pour le tableau et la ComboBox en fonction de l'utilisateur donné.
     * @param unUSer L'utilisateur pour lequel les tickets et les états sont affichés.
     */
    public void initDatas(User unUSer)
    {
        // Instanciation des services
        serviceTicket = new ServiceTicket();
        serviceEtat = new ServiceEtat();
        user = unUSer;

        try {
            // Initialisation des colonnes du tableau avec les propriétés des tickets
            serviceTicket = new ServiceTicket();
            tcNumeroTicket.setCellValueFactory(new PropertyValueFactory<>("idTicket"));
            tcNomTicket.setCellValueFactory(new PropertyValueFactory<>("nomTicket"));
            tcDateTicket.setCellValueFactory(new PropertyValueFactory<>("dateTicket"));
            tcEtatTicket.setCellValueFactory(new PropertyValueFactory<>("etatTicket"));

            // Remplissage du tableau avec les tickets de l'utilisateur
            tvTickets.setItems(FXCollections.observableArrayList(serviceTicket.getAllTicketsByIdUser(user.getIdUser())));

            // Remplissage de la ComboBox avec tous les états disponibles
            cboEtats.setItems(FXCollections.observableArrayList(serviceEtat.getAllEtats()));
        } catch (SQLException e) {
            // Gestion des exceptions liées à la base de données
            throw new RuntimeException(e);
        }
    }

    /**
     * Méthode appelée lors du clic sur le bouton "Modifier".
     * Permet de changer l'état du ticket sélectionné.
     * @param event L'événement déclenché par le clic sur le bouton.
     * @throws SQLException Si une erreur survient lors de l'interaction avec la base de données.
     */
    @javafx.fxml.FXML
    public void btnModifierClicked(Event event) throws SQLException {
        // Vérifie si un ticket est sélectionné
        if(tvTickets.getSelectionModel().getSelectedItem()==null)
        {
            // Affiche une alerte si aucun ticket n'est sélectionné
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Choix du ticket");
            alert.setHeaderText("");
            alert.setContentText("Sélectionner un ticket");
            alert.showAndWait();
        }
        // Vérifie si un état est sélectionné dans la ComboBox
        else if(cboEtats.getSelectionModel().getSelectedItem() == null)
        {
            // Affiche une alerte si aucun état n'est sélectionné
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Choix de l'état");
            alert.setHeaderText("");
            alert.setContentText("Sélectionner un état");
            alert.showAndWait();
        }
        else
        {
            // Récupère l'identifiant de l'état sélectionné
            int idEtat = serviceEtat.getIdEtat(cboEtats.getSelectionModel().getSelectedItem().toString());

            // Modifie l'état du ticket sélectionné
            serviceTicket.modifierEtatTicket(tvTickets.getSelectionModel().getSelectedItem().getIdTicket(),idEtat);

            // Met à jour la liste des tickets dans le tableau
            tvTickets.setItems(FXCollections.observableArrayList(serviceTicket.getAllTicketsByIdUser(user.getIdUser())));
            tvTickets.refresh(); // Rafraîchit l'affichage du tableau
        }
    }
}
