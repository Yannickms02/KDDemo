module com.example.kdtreedemo {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires org.kordamp.ikonli.javafx;
    requires gs.core;
    requires gs.ui.javafx;

    opens com.example.kdtreedemo to javafx.fxml;
    exports com.example.kdtreedemo;
}