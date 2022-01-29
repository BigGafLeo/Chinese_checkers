module com.example.trylmaproject {
    requires javafx.controls;
    requires javafx.fxml;
	requires java.desktop;
    requires spring.core;
    requires spring.jdbc;
    requires java.sql;


    opens com.example.trylmaproject to javafx.fxml;
    exports com.example.trylmaproject.server;
    opens com.example.trylmaproject.server to javafx.fxml;
}