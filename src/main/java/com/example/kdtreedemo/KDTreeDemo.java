package com.example.kdtreedemo;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Path;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.graphstream.ui.fx_viewer.FxViewPanel;
import org.graphstream.ui.fx_viewer.FxViewer;
import org.graphstream.ui.javafx.FxGraphRenderer;
import org.graphstream.ui.view.Viewer;

import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.*;

public class KDTreeDemo extends Application {

    private ObservableList<XYChart.Data<Number, Number>> pointList = FXCollections.observableArrayList();
    private Pane treeView = new Pane();

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Setup and Printstream
        primaryStage.setTitle("KD-Demo");
        Timeline timeline = new Timeline();
        System.setProperty("org.graphstream.ui", "javafx");
        // Create a new FileOutputStream to write to output.txt
        FileOutputStream fos = new FileOutputStream("output.txt");
        // Create a new PrintStream that writes to the FileOutputStream
        PrintStream ps = new PrintStream(fos);
        System.setOut(ps);

        // create elements
        Button construct = new Button("Construct");
        construct.setPadding(new Insets(7.5));
        construct.setMinWidth(75);
        construct.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.DASHED, new CornerRadii(5.0), new BorderWidths(1.5))));
        construct.setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));
        construct.setOnMouseEntered(event -> {
            construct.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY, CornerRadii.EMPTY, Insets.EMPTY)));
        });
        construct.setOnMouseExited(event -> {
            construct.setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));
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
        chart.setBackground(new Background(new BackgroundFill(Color.WHITE, new CornerRadii(12.5), Insets.EMPTY)));
        XYChart.Series<Number, Number> pointsSeries = new XYChart.Series<>("Punkte", pointList);
        chart.getData().add(pointsSeries);
        for (final XYChart.Data<Number, Number> data : pointsSeries.getData()) {
            data.getNode().setVisible(false);
        }
        // remove connection line of the points of pointset
        pointsSeries.getNode().setStyle("-fx-stroke: null;");
        chart.setLegendVisible(false);
        chart.setPrefHeight(1080 * 0.85);
        chart.setPrefWidth(1920 * 0.4);
        chart.setMinWidth(1920 * 0.33);
        chart.setVerticalGridLinesVisible(false);


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
                colorList.add(Color.DEEPSKYBLUE);
                colorList.add(Color.RED);
                Random rnd = new Random();
                Map<Integer, Color> colorMap = new HashMap<>();
                final int height = (int) Math.log(KDTree.treeStructure.size()) + 1;
                for (int depth = 0; depth <= height; depth++) {
                    colorMap.put(depth, colorList.get(depth % 2));
                }
                int i = 0;
                for (KDTree.Node node : KDTree.treeStructure) {
                    int dim = node.depth % 2;
                    // Output tree structure
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
                        KeyFrame keyFrame = new KeyFrame(Duration.millis(1000), ae2 -> {
                            double[] splitBounds = getLineBoundsForSplit(node, xAxis, yAxis);
                            lineSeries.add(getSplittingPlaneSeries(splitBounds[0], splitBounds[1], 0, KDTree.treeStructure.get(fi).x));
                            chart.getData().add(lineSeries.get(fi));
                            Path path = (Path) lineSeries.get(fi).getNode().lookup(".chart-series-line");
                            path.setStroke(colorMap.get(node.depth));
                        });
                        timeline.getKeyFrames().add(keyFrame);
                        timeline.setDelay(Duration.millis(500));
                    } else if (dim == 1) {
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

                if (KDTree.treeStructure.size() > 25) {
                    treeView.setScaleX(0.9);
                    treeView.setScaleY(0.9);
                    getTreeNode(KDTree.treeStructure.get(0), 750);
                }
                else {
                    treeView.setScaleX(0.75);
                    treeView.setScaleY(0.75);
                    getTreeNode(KDTree.treeStructure.get(0), 750);
                }


                if (!checkNodeIsInBounds() && treeView.getScaleX() > 0.35) {
                    treeView.setScaleX(treeView.getScaleX() - 0.1);
                    treeView.setScaleY(treeView.getScaleY() - 0.1);
                }



                for (KeyFrame kf : timeline.getKeyFrames()) {
                    timeline.playFrom(kf.getTime());
                }


            } catch (NullPointerException e) {
                e.printStackTrace();
            }

        });



        VBox sidePane = new VBox();
        sidePane.setAlignment(Pos.CENTER);
        sidePane.setSpacing(5);
        sidePane.setPadding(new Insets(10));
        sidePane.setBackground(new Background(new BackgroundFill(Color.WHITE, new CornerRadii(12.5), Insets.EMPTY)));
        sidePane.getChildren().addAll(construct);
        sidePane.setMinWidth(construct.getMinWidth()+12.5);
        chart.setMinWidth(2160*0.4);


        // main horizontal box
        HBox root = new HBox();
        root.setAlignment(Pos.TOP_LEFT);
        root.setSpacing(10);
        root.setPadding(new Insets(15));
        root.autosize();
        HBox treeBox = new HBox();
        treeBox.setAlignment(Pos.TOP_RIGHT);
        treeBox.getChildren().add(treeView);
        treeBox.setPadding(new Insets(5));
        treeBox.setMaxHeight(chart.getMaxHeight());
        treeBox.setFillHeight(true);
        treeBox.setMaxWidth(2560-12.5-sidePane.getWidth()-chart.getWidth()-25);
        treeView.autosize();
        treeBox.autosize();
        treeBox.setPadding(new Insets(15));
        treeBox.setMinHeight(chart.getMinHeight());
        treeBox.setBackground(new Background(new BackgroundFill(Color.WHITE, new CornerRadii(12.5), Insets.EMPTY)));

        root.getChildren().addAll(sidePane, chart, treeBox);
        primaryStage.setScene(new Scene(root, 2560, 1080, Color.WHITE));
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

    private double[] getLineBoundsForSplit(KDTree.Node node, NumberAxis xAxis, NumberAxis yAxis) {
        switch (node.depth % 2) {
            case 0:  // x-Dimension
                if (node.parent == null) {
                    return new double[]{yAxis.getLowerBound(), yAxis.getUpperBound()};
                } else {
                    if (node.y < node.parent.y) { // left child
                        // search for closest splitpoint on left child
                        if (node.depth > 2) {
                            KDTree.Node nextParent = node.parent.parent.parent; // next closest parent on respective split dimension
                            for (int i = KDTree.treeStructure.size() - 1; i >= 0; i--) {
                                if (KDTree.treeStructure.get(i).x == nextParent.x && KDTree.treeStructure.get(i).y == nextParent.y) {
                                    if (nextParent.y < node.parent.y) {
                                        return new double[]{nextParent.y, node.parent.y};
                                    }
                                }
                            }
                        } else if (node.depth > 1) {
                            return new double[]{yAxis.getLowerBound(), node.parent.y};
                        }

                    } else if (node.y > node.parent.y) {
                        // search for closest splitpoint on right child
                        if (node.depth > 2) {
                            KDTree.Node nextParent = node.parent.parent.parent;
                            for (int i = KDTree.treeStructure.size() - 1; i >= 0; i--) {
                                if (nextParent == null) {
                                    return new double[]{node.parent.y, yAxis.getUpperBound()};
                                }
                                if (KDTree.treeStructure.get(i).x == nextParent.x && KDTree.treeStructure.get(i).y == nextParent.y) {
                                    if (nextParent.y > node.parent.y) {
                                        return new double[]{node.parent.y, nextParent.y};
                                    }
                                }
                            }
                        } else if (node.depth > 1) {
                            return new double[]{node.parent.y, yAxis.getUpperBound()};
                        }
                    }
                }
                break;
            case 1: // y-Dimension
                if (node.parent == null) {
                    return new double[]{xAxis.getLowerBound(), xAxis.getUpperBound()};
                } else {
                    if (node.x > node.parent.x) {
                        // search for closest splitpoint on right child
                        if (node.depth > 2) {
                            KDTree.Node nextParent = node.parent.parent.parent;
                            for (int i = KDTree.treeStructure.size() - 1; i >= 0; i--) {
                                if (KDTree.treeStructure.get(i).y == nextParent.y && KDTree.treeStructure.get(i).x == nextParent.x) {
                                    if (nextParent.x > node.parent.x) {
                                        return new double[]{node.parent.x, nextParent.x};
                                    }
                                }
                            }
                        } else if (node.depth == 1) {
                            return new double[]{node.parent.x, xAxis.getUpperBound()};
                        }
                    } else if (node.x < node.parent.x) {
                        // search for closest splitpoint on left child
                        if (node.depth > 2) {
                            KDTree.Node nextParent = node.parent.parent.parent;
                            for (int i = KDTree.treeStructure.size() - 1; i >= 0; i--) {
                                if (KDTree.treeStructure.get(i).y == nextParent.y && KDTree.treeStructure.get(i).x == nextParent.x) {
                                    if (nextParent.x < node.parent.x) {
                                        return new double[]{nextParent.x, node.parent.x};
                                    }
                                }
                            }
                        } else if (node.depth == 1) {
                            return new double[]{xAxis.getLowerBound(), node.parent.x};
                        }
                    }
                }
                break;
            default:
                break;
        }
        return new double[]{0, 0};
    }


    // Creates a group of the Node in the tree and two lines to either child
    // offsetX is a constant predefinded offset for large trees to prevent overlapping nodes deep inside the tree
    private static final List<Integer> yDepth = new ArrayList<>();
    private static final String[] splitDimension = {"x", "y"};

    protected void getTreeNode(KDTree.Node node, double x) {
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

        if (node.left != null) {
            leftChild.setStartX(circle.getCenterX());
            leftChild.setStartY(circle.getCenterY() + circle.getRadius());
            final double newX = circle.getCenterX() - 550 / (node.depth + 1);
            leftChild.setEndX(newX);
            leftChild.setEndY(yDepth.get(node.depth) + 200.0);
            leftChild.setStroke(Color.BLACK);
            leftChild.setStrokeWidth(2);
            getTreeNode(node.left, newX);
        }
        if (node.right != null) {
            rightChild.setStartX(circle.getCenterX());
            rightChild.setStartY(circle.getCenterY() + circle.getRadius());
            final double newX = circle.getCenterX() + 550 / (node.depth + 1);
            rightChild.setEndX(newX);
            rightChild.setEndY(yDepth.get(node.depth) + 200.0);
            rightChild.setStroke(Color.BLACK);
            rightChild.setStrokeWidth(2);
            getTreeNode(node.right, newX);
        }

        treeView.getChildren().add(nodeGroup);

    }
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





