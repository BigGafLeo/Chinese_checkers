module com.example.trylmaproject {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.trylmaproject to javafx.fxml;
    exports com.example.trylmaproject.server;
    opens com.example.trylmaproject.server to javafx.fxml;
}