package com.example.kdtreedemo;

import javafx.collections.ObservableList;
import javafx.scene.chart.XYChart;

import java.util.ArrayList;
import java.util.List;

public class KDTree  {
    public static List<Node> treeStructure = new ArrayList<>();
    protected static class Node {
        // Die Koordinaten des Punkts für diesen Knoten
        double x, y;
        int depth;

        // Der linke und rechte Kindknoten
        Node left, right, parent;

        public Node(double x, double y, int depth, Node parent) {
            this.x = x;
            this.y = y;
            this.depth = depth;
            this.parent = parent;
        }
    }

    // Die Wurzel des Baums
    protected Node root;

    // Erstellt einen kd-Baum aus der gegebenen Liste von Punkten
    public KDTree(ObservableList<XYChart.Data<Number, Number>> points) {
        List<Point> listOfPoints  = new ArrayList<>();
        for (XYChart.Data<Number, Number> point : points) {
            listOfPoints.add(new Point(point.getXValue().doubleValue(), point.getYValue().doubleValue()));
        }
        root = buildTree(listOfPoints, 0, null);
    }

    // Rekursive Methode zum Erstellen des Baums
    private Node buildTree(List<Point> points, int depth, Node parent) {
        if (points.isEmpty()) {
            return null;
        }

        // Bestimme die Dimension, entlang derer der Punkt sortiert werden soll (x oder y)
        int dim = depth % 2;

        // Sortiere die Punkte entlang der aktuellen Dimension
        points.sort((a, b) -> {
            if (dim == 0) {
                return Double.compare(a.x, b.x);
            } else {
                return Double.compare(a.y, b.y);
            }
        });

        // Finde den Medianpunkt und erstelle einen Knoten daraus
        int medianIndex = points.size() / 2;
        Node node = new Node(points.get(medianIndex).x, points.get(medianIndex).y, depth, parent);
        treeStructure.add(node);
        // Erstelle die linken und rechten Unterbäume rekursiv
        node.left = buildTree(points.subList(0, medianIndex), depth + 1, node);
        node.right = buildTree(points.subList(medianIndex + 1, points.size()), depth + 1, node);

        return node;
    }
}

// Eine Klasse, die einen 2D-Punkt darstellt
class Point {
    double x, y;

    public Point(double x, double y) {
        this.x = x;
        this.y = y;
    }
}
