package com.example.kdtreedemo;

import javafx.animation.KeyFrame;
import javafx.animation.PauseTransition;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.ScatterChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Path;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

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
        // Setup
        primaryStage.setTitle("KD-Demo");
        Timeline timeline = new Timeline();

        // create elements
        TextField textField = new TextField();
        Label inputLabel = new Label();
        inputLabel.setText("Input:");
        Button construct = new Button("Construct");


        // pointset creation handler
        textField.setOnAction(event -> {
            pointList.clear();
            // preprocess of input data
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
        // remove connection line of the points of pointset
        pointsSeries.getNode().setStyle("-fx-stroke: null;");
        chart.setLegendVisible(false);


        ArrayList<XYChart.Series<Number, Number>> lineSeries = new ArrayList<>();


        // Add points to chart via mouse click
        chart.setOnMousePressed(event -> {
            double x = xAxis.getValueForDisplay(event.getX()).doubleValue();
            double y = yAxis.getValueForDisplay(event.getY()).doubleValue();
            pointList.add(new XYChart.Data<Number, Number>(x, y));
            System.out.printf("New Point (%.2f, %.2f) added!%n", x, y);
        });
        construct.setOnAction(event -> {

            System.out.println(pointList.size());

            KDTree kdTree = new KDTree(pointList);
            System.out.println("kd-Tree: " + KDTree.treeStructure.size());
            // disable dynamic resizing of axes as points have been inserted
            xAxis.setAutoRanging(false);
            yAxis.setAutoRanging(false);

                try {
                    // predefined color palette for each depth
                    List<Color> colorList = new ArrayList<>();
                    colorList.add(Color.BLUE);
                    colorList.add(Color.BLACK);
                    colorList.add(Color.GREEN);
                    colorList.add(Color.MAGENTA);
                    colorList.add(Color.RED);
                    colorList.add(Color.CYAN);
                    colorList.add(Color.YELLOW);
                    colorList.add(Color.ALICEBLUE);
                    colorList.add(Color.CORNFLOWERBLUE);
                    colorList.add(Color.DARKRED);
                    colorList.add(Color.DARKBLUE);
                    colorList.add(Color.DARKGREEN);
                    colorList.add(Color.DARKVIOLET);
                    colorList.add(Color.GOLD);
                    colorList.add(Color.GOLDENROD);
                    colorList.add(Color.GREENYELLOW);
                    colorList.add(Color.INDIGO);
                    colorList.add(Color.KHAKI);
                    colorList.add(Color.LAVENDER);
                    Random rnd = new Random();
                    Map<Integer, Color> colorMap = new HashMap<>();
                    final int height = (int) Math.log(KDTree.treeStructure.size())+1;
                    for (int depth = 0; depth <= height; depth++) {
                        colorMap.put(depth, colorList.get(rnd.nextInt(colorList.size()-1)));
                    }
                    int i = 0;
                    for (KDTree.Node node : KDTree.treeStructure) {
                        int dim = node.depth % 2;
                        // Output tree structure
                        if (node.depth > 0) {
                            try {
                                System.out.printf("%n(%.2f, %.2f) - %d - P: (%.2f, %.2f) - L: (%.2f, %.2f) - R: (%.2f, %.2f)", node.x, node.y, node.depth, node.parent.x, node.parent.y, node.left.x, node.left.y, node.right.x, node.right.y);
                            }
                            catch (NullPointerException e) {
                                System.out.printf("%n(%.2f, %.2f) - %d - P: (%.2f, %.2f)", node.x, node.y, node.depth, node.parent.x, node.parent.y);
                            }
                        }
                        else {
                            try {
                                System.out.printf("%n(%.2f, %.2f) - %d - L: (%.2f, %.2f) - R: (%.2f, %.2f)", node.x, node.y, node.depth, node.left.x, node.left.y, node.right.x, node.right.y);
                            }
                            catch (NullPointerException e) {
                                System.out.printf("%n(%.2f, %.2f) - %d", node.x, node.y, node.depth);
                            }
                        }





                        // animate hyperplane creation
                        final int fi = i;
                        if (dim == 0) {
                            KeyFrame keyFrame = new KeyFrame(Duration.millis(1000), ae2 -> {
                                double[] splitBounds = getLineBoundsForSplit(node, xAxis, yAxis);
                                lineSeries.add(getSplittingPlaneSeries(splitBounds[0], splitBounds[1], 0, KDTree.treeStructure.get(fi).x));
                                chart.getData().add(lineSeries.get(fi));
                                Path path = (Path) lineSeries.get(fi).getNode().lookup(".chart-series-line");
                                path.setStroke(colorMap.get(node.depth));
                            });
                            timeline.getKeyFrames().add(keyFrame);
                            timeline.setDelay(Duration.millis(500));
                        }
                        else if (dim == 1) {
                            KeyFrame keyFrame = new KeyFrame(Duration.millis(1000), ae2 -> {
                                double[] splitBounds = getLineBoundsForSplit(node, xAxis, yAxis);
                                lineSeries.add(getSplittingPlaneSeries(splitBounds[0], splitBounds[1], 1, KDTree.treeStructure.get(fi).y));
                                chart.getData().add(lineSeries.get(fi));
                                Path path = (Path) lineSeries.get(fi).getNode().lookup(".chart-series-line");
                                path.setStroke(colorMap.get(node.depth));

                            });
                            timeline.getKeyFrames().add(keyFrame);
                            timeline.setDelay(Duration.millis(500));
                        }
                        i++;
                    }
                    for (int j = 0; j < lineSeries.size(); j++) {
                        lineSeries.get(j).getNode().setVisible(false);
                    }
                    for (KeyFrame kf : timeline.getKeyFrames()) {
                        timeline.playFrom(kf.getTime());
                    }


                }
                catch (Exception e) {
                    System.out.println("Exception");
                }

        });


        HBox inputContainer = new HBox(inputLabel, textField);
        inputContainer.setAlignment(Pos.CENTER);
        inputContainer.setSpacing(10);

        VBox sidePane = new VBox();
        sidePane.setAlignment(Pos.TOP_CENTER);
        sidePane.setPadding(new Insets(10));
        sidePane.setSpacing(10);
        sidePane.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY, new CornerRadii(15), Insets.EMPTY)));
        sidePane.getChildren().addAll(inputContainer, construct);


        // main horizontal box
        HBox root = new HBox();
        root.setAlignment(Pos.CENTER);
        root.setSpacing(10);
        root.setPadding(new Insets(10));
        // tree vertical box
        VBox tree = new VBox();
        tree.setAlignment(Pos.BASELINE_RIGHT);
        tree.setPadding(new Insets(10));
        tree.getChildren().addAll(getTreeNode(1, true, 1, 100, 100), getTreeNode(2, true, 2, 125, 0));

        root.getChildren().addAll(sidePane, chart, tree);
        primaryStage.setScene(new Scene(root, 1920, 1080));
        primaryStage.show();
    }

    protected XYChart.Series<Number, Number> getSplittingPlaneSeries(double start, double end, int dimension, double splitValue) {
        XYChart.Series<Number, Number> lineSeries = new XYChart.Series<>();
        switch (dimension) {
            case 0: // x-Dimension split
                lineSeries.getData().add(new XYChart.Data<>(splitValue, start));
                lineSeries.getData().add(new XYChart.Data<>(splitValue, end));
                break;
            case 1: // y-Dimension split
                lineSeries.getData().add(new XYChart.Data<>(start, splitValue));
                lineSeries.getData().add(new XYChart.Data<>(end, splitValue));
                break;
            default:
                break;
        }
        return lineSeries;
    }
    protected double[] getLineBoundsForSplit(KDTree.Node node, NumberAxis xAxis, NumberAxis yAxis) {
        switch (node.depth % 2) {
            case 0:  // x-Dimension
                if (node.parent == null) {
                    return new double[]{yAxis.getLowerBound(), yAxis.getUpperBound()};
                }
                else {
                    if (node.y < node.parent.y) { // left child
                        // search for closest splitpoint on left child
                        if (node.depth > 3) {
                            KDTree.Node nextParent = node.parent.parent.parent; // next closest parent on respective split dimension
                            for (int i = KDTree.treeStructure.size()-1; i >= 0; i--) {
                                if (KDTree.treeStructure.get(i).x == nextParent.x && KDTree.treeStructure.get(i).y == nextParent.y) {
                                    if (nextParent.y < node.parent.y) {
                                        return new double[]{nextParent.y, node.parent.y};
                                    }
                                }
                            }
                        }
                        else if (node.depth > 1) {
                            return new double[]{yAxis.getLowerBound(), node.parent.y};
                        }

                    }
                    else if (node.y > node.parent.y) {
                        // search for closest splitpoint on right child
                        if (node.depth > 3) {
                            KDTree.Node nextParent = node.parent.parent.parent;
                            for (int i = KDTree.treeStructure.size()-1; i >= 0; i--) {
                                if (nextParent == null) {
                                    return new double[]{node.parent.y, yAxis.getUpperBound()};
                                }
                                if (KDTree.treeStructure.get(i).x == nextParent.x && KDTree.treeStructure.get(i).y == nextParent.y) {
                                    if (nextParent.y > node.parent.y) {
                                        return new double[]{node.parent.y, nextParent.y};
                                    }
                                }
                            }
                        }
                        else if (node.depth > 1) {
                            return new double[]{node.parent.y, yAxis.getUpperBound()};
                        }
                    }
                }
                break;
            case 1: // y-Dimension
                if (node.parent == null) {
                    return new double[]{xAxis.getLowerBound(), xAxis.getUpperBound()};
                }
                else {
                    if (node.x > node.parent.x) {
                        // search for closest splitpoint on right child
                        if (node.depth > 3) {
                            KDTree.Node nextParent = node.parent.parent.parent;
                            for (int i = KDTree.treeStructure.size()-1; i >= 0; i--) {
                                if (KDTree.treeStructure.get(i).y == nextParent.y && KDTree.treeStructure.get(i).x == nextParent.x) {
                                    if (nextParent.x > node.parent.x) {
                                        return new double[]{node.parent.x, nextParent.x};
                                    }
                                }
                            }
                        }
                        else if (node.depth == 1) {
                            return new double[]{node.parent.x, xAxis.getUpperBound()};
                        }
                    }
                    else if (node.x < node.parent.x) {
                        // search for closest splitpoint on left child
                        if (node.depth > 3) {
                            KDTree.Node nextParent = node.parent.parent.parent;
                            for (int i = KDTree.treeStructure.size()-1; i >= 0; i--) {
                                if (KDTree.treeStructure.get(i).y == nextParent.y && KDTree.treeStructure.get(i).x == nextParent.x) {
                                    if (nextParent.x < node.parent.x) {
                                        return new double[]{nextParent.x, node.parent.x};
                                    }
                                }
                            }
                        }
                        else if (node.depth == 1) {
                            return new double[]{xAxis.getLowerBound(), node.parent.x};
                        }
                    }
                }
                break;
            default:
                break;
            }
        return new double[]{0,0};
    }



    // Creates a group of the Node in the tree and two lines to either child
    // nodeNumber displays the text inside the node, isInterNode is to check whether to include the lines for the children
    // depth allows for a dynamic line-length according to the depth inside the tree
    protected Group getTreeNode(int nodeNumber, boolean isInteralNode, int depth, double startX, double startY) {
        // set startY to circle's center coordinate
        startY -= 25;
        // define outer circle
        Circle circle = new Circle();
        circle.setCenterX(startX);
        circle.setCenterY(startY);
        circle.setRadius(25);
        circle.setFill(Color.TRANSPARENT);
        circle.setStroke(Color.BLACK);
        circle.setStrokeWidth(2);

        // node information
        Text text = new Text();
        text.setText(Integer.toString(nodeNumber));
        text.setTranslateX(circle.getCenterX() - text.getLayoutBounds().getWidth() / 2);
        text.setTranslateY(circle.getCenterY() + text.getLayoutBounds().getHeight() / 4);

        // create lines to either child
        Line leftLineHor = new Line(), leftLineVer = new Line(), rightLineHor = new Line(), rightLineVer = new Line();
        if (isInteralNode) {
            // left horizontal part
            leftLineHor.setStartX(circle.getCenterX() - circle.getRadius());
            leftLineHor.setStartY(circle.getCenterY());
            leftLineHor.setEndX(circle.getCenterX() - circle.getRadius() - 250 * 1 / depth);
            leftLineHor.setEndY(circle.getCenterY());
            // left vertical part
            leftLineVer.setStartX(leftLineHor.getEndX());
            leftLineVer.setEndX(leftLineHor.getEndX());
            leftLineVer.setStartY(leftLineHor.getStartY());
            leftLineVer.setEndY(leftLineVer.getStartY() + 250 * 1 / depth);
            // right horizontal part
            rightLineHor.setStartX(circle.getCenterX() + circle.getRadius());
            rightLineHor.setStartY(circle.getCenterY());
            rightLineHor.setEndX(circle.getCenterX() + circle.getRadius() + 250 * 1 / depth);
            rightLineHor.setEndY(circle.getCenterY());
            // right vertical part
            rightLineVer.setStartX(rightLineHor.getEndX());
            rightLineVer.setEndX(rightLineHor.getEndX());
            rightLineVer.setStartY(leftLineHor.getStartY());
            rightLineVer.setEndY(leftLineVer.getStartY() + 250 * 1 / depth);
        }

        Group group = new Group();
        group.getChildren().addAll(circle, text, leftLineHor, leftLineVer, rightLineHor, rightLineVer);

        return group;
    }

}



