module sio.devoir3sio1b {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    opens sio.gestionticket.Model;
    opens sio.gestionticket to javafx.fxml;
    exports sio.gestionticket;
}