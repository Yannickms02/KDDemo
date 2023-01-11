package com.example.kdtreedemo;

import java.util.List;

public class KDTree {
    private static class Node {
        // Die Koordinaten des Punkts für diesen Knoten
        double x, y;

        // Der linke und rechte Kindknoten
        Node left, right;

        public Node(double x, double y) {
            this.x = x;
            this.y = y;
        }
    }

    // Die Wurzel des Baums
    private Node root;

    // Erstellt einen kd-Baum aus der gegebenen Liste von Punkten
    public KDTree(List<Point> points) {
        root = buildTree(points, 0);
    }

    // Rekursive Methode zum Erstellen des Baums
    private Node buildTree(List<Point> points, int depth) {
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
        Node node = new Node(points.get(medianIndex).x, points.get(medianIndex).y);

        // Erstelle die linken und rechten Unterbäume rekursiv
        node.left = buildTree(points.subList(0, medianIndex), depth + 1);
        node.right = buildTree(points.subList(medianIndex + 1, points.size()), depth + 1);

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
