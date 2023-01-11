package com.example.kdtreedemo;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Box;
import javafx.scene.shape.Cylinder;
import javafx.scene.shape.Shape3D;
import javafx.scene.shape.Sphere;
import javafx.scene.transform.Rotate;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        StackPane root = new StackPane();

// Erstellen Sie eine 3D-Form und fügen Sie sie zu root hinzu
        Shape3D shape = new Box(300,300,300);

        root.getChildren().add(shape);

// Erstellen Sie Rotate-Transformationen für die x-, y- und z-Achse
        Rotate rx = new Rotate(0, Rotate.X_AXIS);
        Rotate ry = new Rotate(0, Rotate.Y_AXIS);
        Rotate rz = new Rotate(0, Rotate.Z_AXIS);

// Fügen Sie die Rotate-Transformationen zu shape hinzu
        shape.getTransforms().addAll(rx, ry, rz);

// Erstellen Sie Event-Handler für Mousedragged-Ereignisse, um die Rotate-Transformationen zu aktualisieren
            shape.setOnMouseDragged(event -> {
                // Verringern Sie den Winkel um den gedreht wird, indem Sie ihn durch einen Faktor teilen
                double factor = 0.05;
                rx.setAngle(rx.getAngle() + event.getSceneY() * factor);
                ry.setAngle(ry.getAngle() + event.getSceneX() * factor);
            });




// Erstellen Sie eine Scene und zeigen Sie sie an
        Scene scene = new Scene(root, 800, 600, true);
        stage.setScene(scene);
        stage.show();

    }

    public static void main(String[] args) {
        launch();
    }
}