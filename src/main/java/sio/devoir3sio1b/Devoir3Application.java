package sio.devoir3sio1b;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Classe principale de l'application JavaFX.
 * Elle hérite de la classe `Application` et définit le point d'entrée de l'application.
 */
public class Devoir3Application extends Application {

    /**
     * Méthode appelée au démarrage de l'application.
     * Elle initialise et affiche la fenêtre principale (stage) avec la scène chargée à partir d'un fichier FXML.
     *
     * @param stage La fenêtre principale de l'application.
     * @throws IOException Si une erreur survient lors du chargement du fichier FXML.
     */
    @Override
    public void start(Stage stage) throws IOException {
        // Chargement du fichier FXML contenant la définition de l'interface utilisateur
        FXMLLoader fxmlLoader = new FXMLLoader(Devoir3Application.class.getResource("devoir3-view.fxml"));

        // Création de la scène à partir du contenu chargé
        Scene scene = new Scene(fxmlLoader.load());

        // Définition du titre de la fenêtre
        stage.setTitle("Devoir n°3");

        // Affectation de la scène au stage (fenêtre principale)
        stage.setScene(scene);

        // Affichage de la fenêtre
        stage.show();
    }

    /**
     * Méthode principale (point d'entrée de l'application).
     * Appelle la méthode `launch` pour démarrer l'application JavaFX.
     *
     * @param args Les arguments de ligne de commande.
     */
    public static void main(String[] args) {
        launch(); // Démarre l'application JavaFX
    }
}
