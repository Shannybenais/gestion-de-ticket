package sio.gestionticket;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import sio.gestionticket.Model.Ticket;
import sio.gestionticket.Services.ServiceRapport;
import sio.gestionticket.Services.ServiceUser;

import java.net.URL;
import java.sql.SQLException;
import java.util.Map;
import java.util.ResourceBundle;

public class RapportController implements Initializable {


    @FXML
    private TableView<TicketsParUser> tvTicketsParUser;
    @FXML
    private TableColumn<TicketsParUser, String> tcNomUtilisateur1;
    @FXML
    private TableColumn<TicketsParUser, Integer> tcNbTickets1;

    @FXML
    private TableView<TicketsParUserEtPriorite> tvTicketsParUserEtPriorite;
    @FXML
    private TableColumn<TicketsParUserEtPriorite, String> tcNomUtilisateur2;
    @FXML
    private TableColumn<TicketsParUserEtPriorite, String> tcPriorite;
    @FXML
    private TableColumn<TicketsParUserEtPriorite, Integer> tcNbTickets2;

    @FXML
    private Label lblNbTicketsRetard;

    @FXML
    private TableView<Ticket> tvTicketsEnRetard;
    @FXML
    private TableColumn<Ticket, Integer> tcNumeroTicket;
    @FXML
    private TableColumn<Ticket, String> tcNomTicket;
    @FXML
    private TableColumn<Ticket, String> tcDateTicket;
    @FXML
    private TableColumn<Ticket, String> tcEtatTicket;
    @FXML
    private TableColumn<Ticket, String> tcDateFinSouhaitee;
    @FXML
    private TableColumn<Ticket, String> tcUtilisateur;

    @FXML
    private Button btnFermer;
    private String sessionToken;


    private ServiceRapport serviceRapport;
    private ServiceUser serviceUser;

    // Classes internes pour les modèles de données des tableaux
    public static class TicketsParUser {
        private final SimpleStringProperty nomUtilisateur;
        private final SimpleIntegerProperty nbTickets;

        public TicketsParUser(String nomUtilisateur, int nbTickets) {
            this.nomUtilisateur = new SimpleStringProperty(nomUtilisateur);
            this.nbTickets = new SimpleIntegerProperty(nbTickets);
        }

        public String getNomUtilisateur() {
            return nomUtilisateur.get();
        }

        public int getNbTickets() {
            return nbTickets.get();
        }
    }

    public static class TicketsParUserEtPriorite {
        private final SimpleStringProperty nomUtilisateur;
        private final SimpleStringProperty priorite;
        private final SimpleIntegerProperty nbTickets;

        public TicketsParUserEtPriorite(String nomUtilisateur, String priorite, int nbTickets) {
            this.nomUtilisateur = new SimpleStringProperty(nomUtilisateur);
            this.priorite = new SimpleStringProperty(priorite);
            this.nbTickets = new SimpleIntegerProperty(nbTickets);
        }

        public String getNomUtilisateur() {
            return nomUtilisateur.get();
        }

        public String getPriorite() {
            return priorite.get();
        }

        public int getNbTickets() {
            return nbTickets.get();
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        serviceRapport = new ServiceRapport();
        serviceUser = new ServiceUser();

        // Configuration des colonnes
        configureColumns();

        // Chargement des données
        try {
            chargerDonnees();
        } catch (SQLException e) {
            afficherErreur("Erreur lors du chargement des données", e);
        }
    }

    private void configureColumns() {
        // Table tickets par utilisateur
        tcNomUtilisateur1.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getNomUtilisateur()));
        tcNbTickets1.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getNbTickets()).asObject());

        // Table tickets par utilisateur et priorité
        tcNomUtilisateur2.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getNomUtilisateur()));
        tcPriorite.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getPriorite()));
        tcNbTickets2.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getNbTickets()).asObject());

        // Table tickets en retard
        tcNumeroTicket.setCellValueFactory(new PropertyValueFactory<>("idTicket"));
        tcNomTicket.setCellValueFactory(new PropertyValueFactory<>("nomTicket"));
        tcDateTicket.setCellValueFactory(new PropertyValueFactory<>("dateTicket"));
        tcEtatTicket.setCellValueFactory(new PropertyValueFactory<>("etatTicket"));
        tcDateFinSouhaitee.setCellValueFactory(new PropertyValueFactory<>("dateFinSouhaitee"));

        // Pour la colonne utilisateur, on utilise un CellValueFactory personnalisé
        tcUtilisateur.setCellValueFactory(cellData -> {
            try {
                int idUser = cellData.getValue().getNumUser();
                String nomUser = serviceRapport.getNomUtilisateur(idUser);
                return new SimpleStringProperty(nomUser);
            } catch (SQLException e) {
                return new SimpleStringProperty("Erreur");
            }
        });
    }

    private void chargerDonnees() throws SQLException {
        // 1. Nombre de tickets traités par utilisateur
        Map<Integer, Integer> ticketsParUser = serviceRapport.getNombreTicketsTraitesParUtilisateur();
        ObservableList<TicketsParUser> listeTicketsParUser = FXCollections.observableArrayList();

        for (Map.Entry<Integer, Integer> entry : ticketsParUser.entrySet()) {
            String nomUser = serviceRapport.getNomUtilisateur(entry.getKey());
            listeTicketsParUser.add(new TicketsParUser(nomUser, entry.getValue()));
        }

        tvTicketsParUser.setItems(listeTicketsParUser);

        // 2. Nombre de tickets traités par utilisateur et priorité
        Map<String, Integer> ticketsParUserEtPriorite = serviceRapport.getNombreTicketsTraitesParUtilisateurEtPriorite();
        ObservableList<TicketsParUserEtPriorite> listeTicketsParUserEtPriorite = FXCollections.observableArrayList();

        for (Map.Entry<String, Integer> entry : ticketsParUserEtPriorite.entrySet()) {
            String[] parts = entry.getKey().split("-");
            int idUser = Integer.parseInt(parts[0]);
            int idEtat = Integer.parseInt(parts[1]);

            String nomUser = serviceRapport.getNomUtilisateur(idUser);
            String nomEtat = serviceRapport.getNomEtat(idEtat);

            listeTicketsParUserEtPriorite.add(new TicketsParUserEtPriorite(nomUser, nomEtat, entry.getValue()));
        }

        tvTicketsParUserEtPriorite.setItems(listeTicketsParUserEtPriorite);

        // 3. Nombre de tickets en retard
        int nbTicketsRetard = serviceRapport.getNombreTicketsEnRetard();
        lblNbTicketsRetard.setText(String.valueOf(nbTicketsRetard));

        // 4. Liste des tickets en retard
        tvTicketsEnRetard.setItems(FXCollections.observableArrayList(serviceRapport.getListeTicketsEnRetard()));
    }

    @FXML
    private void btnFermerClicked(ActionEvent event) {
        Stage stage = (Stage) btnFermer.getScene().getWindow();
        stage.close();
    }

    private void afficherErreur(String message, Exception e) {
        e.printStackTrace();
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setHeaderText(message);
        alert.setContentText(e.getMessage());
        alert.showAndWait();
    }
    public void setSessionToken(String sessionToken) {
        this.sessionToken = sessionToken;
    }
}