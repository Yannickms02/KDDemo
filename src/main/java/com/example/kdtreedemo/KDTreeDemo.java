package com.example.kdtreedemo;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Path;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.*;

public class KDTreeDemo extends Application {

    private final ObservableList<XYChart.Data<Number, Number>> pointList = FXCollections.observableArrayList();
    private final Pane treeView = new Pane();
    private long visualTime = 0;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Screen
        primaryStage.setTitle("KD-Demo");
        Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();
        final double screenWidth = primaryScreenBounds.getWidth();
        final double screenHeight = primaryScreenBounds.getHeight();

        // Setup
        Timeline timeline = new Timeline();
        System.setProperty("org.graphstream.ui", "javafx");
        // Create a new FileOutputStream to write to output.txt
        FileOutputStream fos = new FileOutputStream("output.txt");
        // Create a new PrintStream that writes to the FileOutputStream
        PrintStream ps = new PrintStream(fos);
        System.setOut(ps);

        // create elements
        // Headline for sidebar menu
        Text heading = new Text();
        heading.setText("O p t i o n s");
        heading.setFont(Font.font("Impact", FontWeight.NORMAL, 22.5));

        // construction sub-menu
        Text subHeadlineConstruction = new Text();
        subHeadlineConstruction.setFont(Font.font("Arial", FontWeight.NORMAL, 15));
        subHeadlineConstruction.setText("Construction");
        subHeadlineConstruction.setFont(Font.font("Verdana", FontWeight.BOLD, 12.5));
        subHeadlineConstruction.setUnderline(true);
        subHeadlineConstruction.setTranslateY(screenHeight * 0.05);

        Button randomizePoints = new Button();
        randomizePoints.setText("Randomized Point-set");
        randomizePoints.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, new CornerRadii(2.5), new BorderWidths(1))));
        randomizePoints.setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));
        // animate mouse hover on button
        randomizePoints.setOnMouseEntered(event -> {
            randomizePoints.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, new CornerRadii(2.5), new BorderWidths(1))));
            randomizePoints.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY, CornerRadii.EMPTY, Insets.EMPTY)));
        });
        randomizePoints.setOnMouseExited(event -> {
            randomizePoints.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, new CornerRadii(2.5), new BorderWidths(1))));
            randomizePoints.setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));
        });
        randomizePoints.autosize();
        randomizePoints.setTranslateY(screenHeight * 0.05 + 25);
        // on ENTER, create random set of 25 points
        randomizePoints.setOnAction(actionEvent -> {
            Random rnd = new Random();
            for (int i = 0; i < 25; i++) {
                final double xValue = rnd.nextDouble(0, 50);
                final double yValue = rnd.nextDouble(0, 50);
                pointList.add(new XYChart.Data<Number, Number>(xValue, yValue));
            }
        });

        // button to confirm construction process
        Button construct = new Button("Construct");
        construct.setPadding(new Insets(7.5));
        construct.setMinWidth(75);
        construct.setTranslateY(screenHeight * 0.05 + 75);
        construct.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.DASHED, new CornerRadii(5.0), new BorderWidths(1.5))));
        construct.setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));
        construct.setOnMouseEntered(event -> {
            construct.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY, CornerRadii.EMPTY, Insets.EMPTY)));
        });
        construct.setOnMouseExited(event -> {
            construct.setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));
        });

        // sub-menu for operations with tree
        Text operationsText = new Text("Operations");
        operationsText.setFont(Font.font("Verdana", FontWeight.BOLD, 12.5));
        operationsText.setUnderline(true);

        // nearest neighbor search
        Text nearestNeighborText = new Text();
        nearestNeighborText.setText("Nearest Neighbor Search");
        TextField nnsTextField = new TextField();
        nnsTextField.setOpacity(0.5);
        nnsTextField.setText("Point of type: (x, y)");
        nnsTextField.autosize();
        Text nearestNeighborResult = new Text();
        Text nearestNeighborResultHeadline = new Text();
        nearestNeighborResultHeadline.setUnderline(true);
        nearestNeighborResultHeadline.setFont(Font.font("Arial", FontWeight.NORMAL, 12.5));
        nearestNeighborResult.autosize();
        nearestNeighborResult.setTextAlignment(TextAlignment.CENTER);
        nearestNeighborResultHeadline.autosize();

        // insertion of point
        Text insertText = new Text();
        insertText.setText("Insert Node");
        TextField insertTextField = new TextField();
        insertTextField.setOpacity(0.5);
        insertTextField.setText("Point of type: (x, y)");
        insertTextField.autosize();

        // metrics panel
        Text subheadlineMetrics = new Text();
        subheadlineMetrics.setText("Build Time");
        subheadlineMetrics.setFont(Font.font("Arial", FontWeight.BOLD, 12.5));

        Text buildTime = new Text();
        buildTime.setText("Tree: unknown");
        Text visualBuildTime = new Text();
        visualBuildTime.setText("Visual: unknown");

        // tree information
        Text subHeadlineTreeProperties = new Text();
        subHeadlineTreeProperties.setText("Tree Properties");
        subHeadlineTreeProperties.setFont(Font.font("Arial", FontWeight.BOLD, 12.5));
        Text treeHeight = new Text();
        Text pointSetDisplay = new Text();
        pointList.addListener((ListChangeListener.Change<? extends XYChart.Data<Number, Number>> change) -> {
            while (change.next()) {
                if (change.wasAdded()) {
                    for (XYChart.Data<Number, Number> point : change.getAddedSubList()) {
                        pointSetDisplay.setText("Last added: p(" + Math.round(point.getXValue().doubleValue()) + ", " + Math.round(point.getYValue().doubleValue()) + ")");

                    }
                }
            }
        });


        // Create chart
        NumberAxis xAxis = new NumberAxis();
        xAxis.setAutoRanging(true);
        xAxis.setLabel("X-Axis");
        NumberAxis yAxis = new NumberAxis();
        yAxis.setAutoRanging(true);
        yAxis.setLabel("Y-Axis");
        LineChart<Number, Number> chart = new LineChart<>(xAxis, yAxis);
        chart.setTitle("Hyperplanes");
        chart.setBackground(new Background(new BackgroundFill(Color.WHITE, new CornerRadii(12.5), Insets.EMPTY)));
        XYChart.Series<Number, Number> pointsSeries = new XYChart.Series<>("Punkte", pointList);
        insertTextField.setOnAction(event -> {
            String input = insertTextField.getText();
            String[] values = input.split(",");
            double x = Double.parseDouble(values[0].substring(1));
            double y = Double.parseDouble(values[1].substring(1, values[1].length() - 1));
            pointsSeries.getData().add(new XYChart.Data<>(x, y));
            pointsSeries.getData().get(pointsSeries.getData().size() - 1).getNode().setStyle("-fx-background-color: red; -fx-background-radius: 1.5; -fx-background-insets: 0;");
            try {
                KDTree.insert(KDTree.treeStructure.isEmpty() ? null : KDTree.treeStructure.get(0), new Point(x, y), null, 0);
            } catch (NullPointerException npe) {
                System.out.println("Couldn't add point to tree!");
            }
        });

        // list of hyperplanes used in chart
        ArrayList<XYChart.Series<Number, Number>> lineSeries = new ArrayList<>();

        // confirm nearest NNS by ENTER
        nnsTextField.setOnAction(event -> {
            // extract coordinates from input
            String input = nnsTextField.getText();
            String[] values = input.split(",");
            double x = Double.parseDouble(values[0].substring(1));
            double y = Double.parseDouble(values[1].substring(1, values[1].length() - 1));
            pointsSeries.getData().add(new XYChart.Data<>(x, y));
            pointsSeries.getData().get(pointsSeries.getData().size() - 1).getNode().setStyle("-fx-background-color: blue; -fx-background-radius: 2; -fx-background-insets: 0;");
            try {
                // measure query time
                final long currentTime = System.nanoTime();
                KDTree.searchTree(KDTree.treeStructure.get(0), new Point(x, y), 0, null);
                final long endTime = System.nanoTime();
                final long queryTime = endTime - currentTime;

                // output of results
                nearestNeighborResultHeadline.setText("NN of " + input + ":");
                String subStringPoint = String.format("(%.2f, %.2f)", KDTree.closestNeighbor.bestNode.x, KDTree.closestNeighbor.bestNode.y);
                String subStringDist = String.format("Distance: %.2f", Math.sqrt(KDTree.closestNeighbor.distance));
                String subStringQueryTime = String.format("Found in %.2f ms", queryTime * 0.000001);
                nearestNeighborResult.setText(subStringPoint + "\n" + subStringDist + "\n" + subStringQueryTime);
                XYChart.Series<Number, Number> distanceLine = new XYChart.Series<>();
                XYChart.Data<Number, Number> startData = new XYChart.Data<>(KDTree.closestNeighbor.bestNode.x, KDTree.closestNeighbor.bestNode.y);
                XYChart.Data<Number, Number> endData = new XYChart.Data<>(x, y);
                distanceLine.getData().add(startData);
                distanceLine.getData().add(endData);
                distanceLine.setName("Distance from the closest node");

                chart.getData().add(distanceLine);
            } catch (NullPointerException npe) {
                nearestNeighborResult.setText("Must create tree first!");
            }
        });

        // add entire list of Points to chart as data
        chart.getData().add(pointsSeries);

        // remove connection line of the points of pointset
        pointsSeries.getNode().setStyle("-fx-stroke: null;");
        chart.setLegendVisible(false);
        chart.setPrefHeight(screenHeight * 0.85);
        chart.setPrefWidth(screenWidth * 0.4);
        chart.setMinWidth(screenWidth * 0.33);
        chart.setVerticalGridLinesVisible(false);


        // Add points to chart via mouse click
        chart.setOnMousePressed(event -> {
            double x = xAxis.getValueForDisplay(event.getX()).doubleValue();
            double y = yAxis.getValueForDisplay(event.getY()).doubleValue();
            pointList.add(new XYChart.Data<Number, Number>(x, y));
            System.out.printf("New Point (%.2f, %.2f) added!%n", x, y);
        });

        // begin construction of tree
        construct.setOnAction(event -> {
            long currentTime = System.nanoTime();
            KDTree kdTree = new KDTree(pointList);

            // disable dynamic resizing of axes as points have been inserted
            xAxis.setAutoRanging(false);
            yAxis.setAutoRanging(false);

            try {
                // predefined color palette for each depth
                List<Color> colorList = new ArrayList<>();
                colorList.add(Color.DEEPSKYBLUE);
                colorList.add(Color.RED);
                Map<Integer, Color> colorMap = new HashMap<>();
                final int height = (int) Math.log(KDTree.treeStructure.size()) + 1;
                for (int depth = 0; depth <= height; depth++) {
                    colorMap.put(depth, colorList.get(depth % 2));
                }
                int i = 0;
                for (KDTree.Node node : KDTree.treeStructure) {
                    int dim = node.depth % 2;
                    // output tree structure
                    if (node.depth > 0) {
                        try {
                            System.out.printf("%n(%2.2f, %2.2f) - Depth: %2d - Parent: (%2.2f, %2.2f) - Left: (%2.2f, %2.2f) - Right: (%2.2f, %2.2f)", node.x, node.y, node.depth, node.parent.x, node.parent.y, node.left.x, node.left.y, node.right.x, node.right.y);
                        } catch (NullPointerException e) {
                            if (node.left == null && node.right == null) {
                                System.out.printf("%n(%2.2f, %2.2f) - Depth: %2d - Parent: (%2.2f, %2.2f)", node.x, node.y, node.depth, node.parent.x, node.parent.y);
                            } else if (node.left != null && node.right == null) {
                                System.out.printf("%n(%2.2f, %2.2f) - Depth: %2d - Parent: (%2.2f, %2.2f) - Left: (%2.2f, %2.2f)", node.x, node.y, node.depth, node.parent.x, node.parent.y, node.left.x, node.left.y);
                            } else if (node.left == null && node.right != null) {
                                System.out.printf("%n(%2.2f, %2.2f) - Depth: %2d - Parent: (%2.2f, %2.2f) - Right: (%2.2f, %2.2f)", node.x, node.y, node.depth, node.parent.x, node.parent.y, node.right.x, node.right.y);
                            }
                        }
                    } else {
                        try {
                            System.out.printf("%n(%2.2f, %2.2f) - Depth: %2d - %22s - Left: (%2.2f, %2.2f) - Right: (%2.2f, %2.2f)", node.x, node.y, node.depth, "", node.left.x, node.left.y, node.right.x, node.right.y);
                        } catch (NullPointerException e) {
                            if (node.left == null && node.right == null) {
                                System.out.printf("%n(%2.2f, %2.2f) - Depth: %2d", node.x, node.y, node.depth);
                            } else if (node.left != null && node.right == null) {
                                System.out.printf("%n(%2.2f, %2.2f) - Depth: %2d - %22s - Left: (%2.2f, %2.2f)", node.x, node.y, node.depth, "", node.left.x, node.left.y);
                            } else if (node.left == null && node.right != null) {
                                System.out.printf("%n(%2.2f, %2.2f) - Depth: %2d - %22s - Right: (%2.2f, %2.2f)", node.x, node.y, node.depth, "", node.right.x, node.right.y);
                            }
                        }
                    }

                    // animate hyperplane creation
                    final int fi = i;
                    if (dim == 0) {
                        KeyFrame keyFrame = new KeyFrame(Duration.millis(250), ae2 -> {
                            double[] splitBounds = getLineBoundsForSplit(node, xAxis, yAxis);
                            lineSeries.add(getSplittingPlaneSeries(splitBounds[0], splitBounds[1], 0, KDTree.treeStructure.get(fi).x));
                            chart.getData().add(lineSeries.get(fi));
                            Path path = (Path) lineSeries.get(fi).getNode().lookup(".chart-series-line");
                            path.setStroke(colorMap.get(node.depth));
                            path.setStrokeWidth(1.5);
                            path.setOpacity(0.75);

                        });
                        timeline.getKeyFrames().add(keyFrame);
                        timeline.setDelay(Duration.millis(250));
                        timeline.play();
                    } else if (dim == 1) {
                        KeyFrame keyFrame = new KeyFrame(Duration.millis(250), ae2 -> {
                            double[] splitBounds = getLineBoundsForSplit(node, xAxis, yAxis);
                            lineSeries.add(getSplittingPlaneSeries(splitBounds[0], splitBounds[1], 1, KDTree.treeStructure.get(fi).y));
                            chart.getData().add(lineSeries.get(fi));
                            Path path = (Path) lineSeries.get(fi).getNode().lookup(".chart-series-line");
                            path.setStroke(colorMap.get(node.depth));
                            path.setStrokeWidth(1.5);
                            path.setOpacity(0.75);
                        });
                        timeline.getKeyFrames().add(keyFrame);
                        timeline.setDelay(Duration.millis(250));
                        timeline.play();
                    }
                    i++;
                }

                // scaling of tree
                if (KDTree.treeStructure.size() > 25) {
                    treeView.setScaleX(0.9);
                    treeView.setScaleY(0.9);
                    getTreeNode(KDTree.treeStructure.get(0), 750);
                } else {
                    treeView.setScaleX(0.75);
                    treeView.setScaleY(0.75);
                    getTreeNode(KDTree.treeStructure.get(0), 750);
                }
                // lower scaling when bounds are exceeded
                if (!checkNodeIsInBounds()) {
                    treeView.setScaleX(treeView.getScaleX() - 0.1);
                    treeView.setScaleY(treeView.getScaleY() - 0.1);
                }

                // output build times for tree and visuals
                visualTime = System.nanoTime() - currentTime;
                buildTime.setText(String.format("Tree: %.2f ms", KDTree.buildTime * 0.000001));
                visualBuildTime.setText(String.format("Visual: %.2f ms", visualTime * 0.000001));
                treeHeight.setText("Nodes: " + KDTree.treeStructure.size() + "\nHeight: " + (int) (Math.log(KDTree.treeStructure.size()) / Math.log(2) + 1));

            } catch (NullPointerException npe) {
                npe.printStackTrace();
            }

        });

        timeline.play();

        // GUI components
        // stats vertical box
        VBox buildTimeStats = new VBox();
        buildTimeStats.setAlignment(Pos.BOTTOM_CENTER);
        buildTimeStats.setPadding(new Insets(7.5));
        buildTimeStats.setSpacing(5);
        buildTimeStats.setBackground(new Background(new BackgroundFill(Color.WHITE, new CornerRadii(12.5), Insets.EMPTY)));
        buildTimeStats.getChildren().addAll(subheadlineMetrics, buildTime, visualBuildTime);
        buildTimeStats.autosize();

        // top heading vbox
        VBox headingBox = new VBox();
        headingBox.setAlignment(Pos.CENTER);
        headingBox.setPadding(new Insets(7.5));
        headingBox.setSpacing(10);
        headingBox.getChildren().addAll(heading, subHeadlineConstruction, randomizePoints, construct);
        heading.setTranslateY(-screenHeight * 0.175);
        subHeadlineConstruction.setTranslateY(-screenHeight * 0.115);
        randomizePoints.setTranslateY(-screenHeight * 0.1);
        construct.setTranslateY(-screenHeight * 0.075);
        headingBox.autosize();

        // operations vbox
        VBox operationsBox = new VBox();
        operationsBox.setAlignment(Pos.TOP_CENTER);
        operationsBox.setPadding(new Insets(7.5));
        operationsBox.setSpacing(10);
        operationsBox.getChildren().addAll(operationsText, insertText, insertTextField, nearestNeighborText, nnsTextField, nearestNeighborResultHeadline, nearestNeighborResult);
        operationsText.setTranslateY(-screenHeight * 0.375);
        insertText.setTranslateY(-screenHeight * 0.37);
        insertTextField.setTranslateY(-screenHeight * 0.37);
        nearestNeighborText.setTranslateY(-screenHeight * 0.365);
        nnsTextField.setTranslateY(-screenHeight * 0.365);
        nearestNeighborResultHeadline.setTranslateY(-screenHeight * 0.365);
        nearestNeighborResult.setTranslateY(-screenHeight * 0.365);
        operationsBox.autosize();

        // tree property box
        VBox propertyBox = new VBox();
        propertyBox.setAlignment(Pos.CENTER);
        propertyBox.setPadding(new Insets(7.5));
        propertyBox.setSpacing(7.5);
        propertyBox.getChildren().addAll(subHeadlineTreeProperties, pointSetDisplay, treeHeight);
        propertyBox.autosize();

        // side menu main box
        VBox sidePane = new VBox();
        sidePane.setAlignment(Pos.BOTTOM_CENTER);
        sidePane.setSpacing(5);
        sidePane.setPadding(new Insets(10));
        sidePane.setBackground(new Background(new BackgroundFill(Color.WHITE, new CornerRadii(12.5), Insets.EMPTY)));
        sidePane.getChildren().addAll(headingBox, operationsBox, propertyBox, buildTimeStats);
        sidePane.setMinWidth(construct.getMinWidth() + buildTimeStats.getWidth() + 12.5);
        sidePane.setFillWidth(true);
        propertyBox.setTranslateY(sidePane.getLayoutBounds().getCenterY() + sidePane.getHeight() * 0.5);
        headingBox.setTranslateY(sidePane.getLayoutBounds().getCenterY() - screenHeight * 0.375);
        chart.setMinWidth(screenWidth * 0.33);
        chart.autosize();

        // main horizontal box
        HBox root = new HBox();
        root.setAlignment(Pos.TOP_LEFT);
        root.setSpacing(10);
        root.setPadding(new Insets(15));
        root.autosize();

        // tree box
        HBox treeBox = new HBox();
        treeBox.setAlignment(Pos.TOP_RIGHT);
        treeBox.getChildren().add(treeView);
        treeBox.setPadding(new Insets(5));
        treeBox.setMaxHeight(chart.getMaxHeight());
        treeBox.setPadding(new Insets(15));
        treeBox.setMaxWidth(screenWidth - chart.getLayoutBounds().getMaxX() - 25);
        VBox backgroundContainer = new VBox();
        backgroundContainer.setBackground(new Background(new BackgroundFill(Color.WHITE, new CornerRadii(12.5), Insets.EMPTY)));
        backgroundContainer.getChildren().add(treeBox);

        // rescale content of treebox to prevent overlap
        treeBox.boundsInLocalProperty().addListener(new ChangeListener<Bounds>() {
            final double maxWidth = treeBox.getMaxWidth() - 15;

            @Override
            public void changed(ObservableValue<? extends Bounds> observable, Bounds oldValue, Bounds newValue) {
                if (newValue.getWidth() > maxWidth) {
                    treeBox.setScaleX(0.85 * maxWidth / newValue.getWidth());
                    treeBox.setScaleY(0.85 * maxWidth / newValue.getWidth());
                }
            }
        });

        // show content to screen
        root.getChildren().addAll(sidePane, chart, backgroundContainer);
        primaryStage.setScene(new Scene(root, screenWidth, screenHeight - 25, Color.WHITE));
        primaryStage.show();
    }

    // creation of hyperplanes / splitting-lines
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

    // internal method to get bounds for hyperplane computation
    private double[] getLineBoundsForSplit(KDTree.Node node, NumberAxis xAxis, NumberAxis yAxis) {
        switch (node.depth % 2) {
            case 0:  // x-Dimension
                if (node.parent == null) {
                    return new double[]{yAxis.getLowerBound(), yAxis.getUpperBound()};
                    // search for closest splitpoint on left child
                } else if (node.y < node.parent.y) {
                    if (node.depth > 2 && node.parent.parent.parent.y < node.parent.y) {
                        return new double[]{node.parent.parent.parent.y, node.parent.y};
                    } else {
                        return new double[]{yAxis.getLowerBound(), node.parent.y};
                    }
                    // search for closest splitpoint on right child
                } else if (node.y > node.parent.y) {
                    if (node.depth > 2 && node.parent.parent.parent.y > node.parent.y) {
                        return new double[]{node.parent.y, node.parent.parent.parent.y};
                    } else {
                        return new double[]{node.parent.y, yAxis.getUpperBound()};
                    }
                }
                break;
            case 1: // y-Dimension
                if (node.parent == null) {
                    return new double[]{xAxis.getLowerBound(), xAxis.getUpperBound()};
                    // search for closest splitpoint on right child
                } else if (node.x > node.parent.x) {
                    if (node.depth > 2 && node.parent.parent.parent.x > node.parent.x) {
                        return new double[]{node.parent.x, node.parent.parent.parent.x};
                    } else {
                        return new double[]{node.parent.x, xAxis.getUpperBound()};
                    }
                    // search for closest splitpoint on left child
                } else if (node.x < node.parent.x) {
                    if (node.depth > 2 && node.parent.parent.parent.x < node.parent.x) {
                        return new double[]{node.parent.parent.parent.x, node.parent.x};
                    } else {
                        return new double[]{xAxis.getLowerBound(), node.parent.x};
                    }
                }
                break;
            default:
                break;
        }
        return new double[]{0, 0};
    }

    // visualize tree starting from the root
    private static final List<Integer> yDepth = new ArrayList<>();
    private static final String[] splitDimension = {"x", "y"};

    protected void getTreeNode(KDTree.Node node, double x) {
        // node representation
        yDepth.add(250 * node.depth + 1);
        Group nodeGroup = new Group();
        Circle circle = new Circle();
        circle.setCenterX(x);
        circle.setCenterY(yDepth.get(node.depth));
        circle.setRadius(47.5);
        circle.setFill(Color.TRANSPARENT);
        circle.setStroke(Color.BLACK);
        circle.setStrokeWidth(2);

        // node information
        Text text = new Text();
        text.setText(String.format("(%.2f, %.2f)", node.x, node.y));
        text.setTranslateX(circle.getCenterX() - text.getLayoutBounds().getWidth() / 2);
        text.setTranslateY(circle.getCenterY() + text.getLayoutBounds().getHeight() / 4);

        Line splitLine = new Line();
        Text splitDim = new Text();
        if (isRightmostNode(node)) {
            splitLine.setStartX(circle.getCenterX() + circle.getRadius() + 100);
            splitLine.setStartY(circle.getCenterY());
            splitLine.setEndX(splitLine.getStartX() + 150);
            splitLine.setEndY(circle.getCenterY());

            splitDim.setText(splitDimension[node.depth % 2]);
            splitDim.setTranslateX(splitLine.getEndX() + 25);
            splitDim.setTranslateY(splitLine.getEndY());
            splitDim.setUnderline(true);
            splitDim.setFont(new Font(22.5));
        }

        // line to children
        Line leftChild = new Line(), rightChild = new Line();
        nodeGroup.getChildren().addAll(circle, text, leftChild, rightChild, splitLine, splitDim);

        // left child
        if (node.left != null) {
            leftChild.setStartX(circle.getCenterX() - circle.getRadius());
            leftChild.setStartY(circle.getCenterY());
            final double newX = circle.getCenterX() - 575.0 / (node.depth + 1);
            leftChild.setEndX(newX);
            leftChild.setEndY(yDepth.get(node.depth) + 200.0);
            leftChild.setStroke(Color.BLACK);
            leftChild.setStrokeWidth(2);
            getTreeNode(node.left, newX);
        }
        // right child
        if (node.right != null) {
            rightChild.setStartX(circle.getCenterX() + circle.getRadius());
            rightChild.setStartY(circle.getCenterY());
            final double newX = circle.getCenterX() + 575.0 / (node.depth + 1);
            rightChild.setEndX(newX);
            rightChild.setEndY(yDepth.get(node.depth) + 200.0);
            rightChild.setStroke(Color.BLACK);
            rightChild.setStrokeWidth(2);
            getTreeNode(node.right, newX);
        }
        treeView.getChildren().add(nodeGroup);
    }

    // internal check whether node is the rightmost and needs annotation of
    // used split-axis
    private boolean isRightmostNode(KDTree.Node node) {
        KDTree.Node current = node;
        while (current.parent != null) {
            if (current.parent.right != current) {
                return false;
            }
            current = current.parent;
        }
        return true;
    }

    private boolean checkNodeIsInBounds() {
        for (Node treeNode : treeView.getChildren()) {
            Bounds boundsInParent = treeNode.getBoundsInParent();
            final double nodeWidth = boundsInParent.getWidth();
            final double nodeHeight = boundsInParent.getHeight();
            if (nodeHeight > treeView.getHeight() || nodeWidth > treeView.getWidth()) {
                return false;
            }
        }
        return true;
    }

}





