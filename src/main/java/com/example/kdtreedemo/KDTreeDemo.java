package com.example.kdtreedemo;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.ScatterChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.Console;
import java.io.PrintStream;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.DoubleStream;

public class KDTreeDemo extends Application {

    private ObservableList<XYChart.Data<Number, Number>> pointList = FXCollections.observableArrayList();

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("KD-Demo");

        TextField textField = new TextField();
        Label inputLabel = new Label();
        inputLabel.setText("Input:");
        textField.setPromptText("Punkte eingeben (x, y)");






        // Erstelle einen Klick-Handler für den Button
        textField.setOnAction(event -> {
            pointList.clear();
            String input = textField.getText();
            input = input.replaceAll("\\s", "");
            String[] pointSets = input.split(";");
            System.out.println(Arrays.toString(pointSets));
            for (String pair : pointSets) {
                // Extract the part of the string containing the numbers
                String numbersString = pair.substring(1, pair.length() - 1);

                // Split the string into an array of strings separated by commas
                String[] numbersArray = numbersString.split(",");

                // Convert the strings to numbers and store them in separate variables
                double x = Double.parseDouble(numbersArray[0]);
                double y = Double.parseDouble(numbersArray[1]);
                pointList.add(new XYChart.Data<Number, Number>(x, y));
            }
        });
        // Create chart
        NumberAxis xAxis = new NumberAxis();
        xAxis.setAutoRanging(true);
        xAxis.setLabel("X-Achse");
        NumberAxis yAxis = new NumberAxis();
        yAxis.setAutoRanging(true);
        yAxis.setLabel("Y-Achse");
        LineChart<Number, Number> chart = new LineChart<>(xAxis, yAxis);

        chart.setTitle("Punkte-Graph");
        XYChart.Series<Number, Number> pointsSeries = new XYChart.Series<>("Punkte", pointList);
        chart.getData().add(pointsSeries);
        for (final XYChart.Data<Number, Number> data : pointsSeries.getData()) {
            data.getNode().setVisible(false);
        }
        // Entfernt die Verbindungslinien zwischen den Punkten
        pointsSeries.getNode().setStyle("-fx-stroke: null;");
        // Erstelle eine neue Serie für die Gerade
        XYChart.Series<Number, Number> lineSeries = new XYChart.Series<>();
        lineSeries.setName("Gerade");

        double x0 = 2.0;
        double m = 0.5;
        double b = 1.0;

// Berechne die Datenpunkte der Geraden
        for (double x = x0; x <= 5; x += 0.1) {
            double y = m * x + b;
            lineSeries.getData().add(new XYChart.Data<>(x, y));
        }
// Entfernt die Punkte von der Geraden
        chart.getData().add(lineSeries);

        for (final XYChart.Data<Number, Number> data : lineSeries.getData()) {
            data.getNode().setVisible(false);
        }
        // Add points to chart via mouse click
        chart.setOnMouseClicked(event -> {
            double x = xAxis.getValueForDisplay(event.getX()).doubleValue()-5;
            double y = yAxis.getValueForDisplay(event.getY()).doubleValue()+5;
            pointList.add(new XYChart.Data<Number, Number>(x, y));
            System.out.printf("New Point (%.2f, %.2f) added!%n", x, y);
        });

        HBox inputContainer = new HBox(inputLabel, textField);
        inputContainer.setAlignment(Pos.CENTER);
        inputContainer.setSpacing(10);

        VBox sidePane = new VBox();
        sidePane.setAlignment(Pos.TOP_CENTER);
        sidePane.setPadding(new Insets(10));
        sidePane.setSpacing(10);
        sidePane.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY, new CornerRadii(15), Insets.EMPTY)));
        sidePane.getChildren().addAll(inputContainer);

        HBox root = new HBox();
        root.setAlignment(Pos.CENTER);
        root.setSpacing(10);
        root.setPadding(new Insets(10));
        root.getChildren().addAll(sidePane, chart);
        primaryStage.setScene(new Scene(root, 800, 600));
        primaryStage.show();
    }


}



