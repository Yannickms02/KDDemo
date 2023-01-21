package com.example.kdtreedemo;

import javafx.collections.ObservableList;
import javafx.scene.chart.XYChart;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static com.example.kdtreedemo.KDTree.closestNeighbor.distance;


public class KDTree  {
    public static List<Node> treeStructure = new ArrayList<>();
    protected static long buildTime = 0;
    protected static class Node {
        // point's coordinates and checkmark for nearest neighbor search
        double x, y;
        int depth;
        boolean isVisited = false;

        // children and parent
        Node left, right, parent;

        public Node(double x, double y, int depth, Node parent) {
            this.x = x;
            this.y = y;
            this.depth = depth;
            this.parent = parent;
        }
    }

    protected Node root;

    // creates tree from given list of Points
    public KDTree(ObservableList<XYChart.Data<Number, Number>> points) {
        List<Point> listOfPoints  = new ArrayList<>();
        for (XYChart.Data<Number, Number> point : points) {
            listOfPoints.add(new Point(point.getXValue().doubleValue(), point.getYValue().doubleValue()));
        }
        long current = System.nanoTime();
        root = buildTree(listOfPoints, 0, null);
        buildTime = System.nanoTime() - current;
    }

    // internal recursive implementation of tree building
    private Node buildTree(List<Point> points, int depth, Node parent) {
        if (points.isEmpty()) {
            return null;
        }

        // dimension to split along
        int dim = depth % 2;

        // sort points based on chosen dimension
        points.sort((a, b) -> {
            if (dim == 0) {
                return Double.compare(a.x, b.x);
            } else {
                return Double.compare(a.y, b.y);
            }
        });

        // create node of median point
        int medianIndex = points.size() / 2;
        Node node = new Node(points.get(medianIndex).x, points.get(medianIndex).y, depth, parent);
        treeStructure.add(node);

        // create left and right subtrees of node
        node.left = buildTree(points.subList(0, medianIndex), depth + 1, node);
        node.right = buildTree(points.subList(medianIndex + 1, points.size()), depth + 1, node);

        return node;
    }

    // keeps track of best-found neighbor
    protected static class closestNeighbor {
        protected static Node bestNode;
        protected static double distance = Double.MAX_VALUE;
    }
    // insert node into tree by calling this with root node and node to be added
    protected static void insert(Node currentNode, Point point, Node lastNode, int depth) {
        if (currentNode == null) {
            treeStructure.add(new Node(point.x, point.y, depth, lastNode));
            return;
        }
        int dim = currentNode.depth % 2;
        int compare = dim == 0 ? Double.compare(point.x, currentNode.x) : Double.compare(point.y, currentNode.y);
        if (compare < 0) {
            insert(currentNode.left, point, currentNode, depth+1);
        } else if (compare > 0) {
            insert(currentNode.right, point, currentNode, depth+1);
        }
    }

    // nearest neighbor search by calling root node, query point, depth of root and null as lastNode
    protected static void searchTree(Node currentNode, Point target, int depth, Node lastNodeVisited) {
        if (currentNode == null && depth == (int) Math.log(treeStructure.size()+1)) {
            closestNeighbor.bestNode =  lastNodeVisited;
            closestNeighbor.distance = (target.y -  lastNodeVisited.y) * (target.y - lastNodeVisited.y) + (target.x - lastNodeVisited.x) * (target.x - lastNodeVisited.x);
            lastNodeVisited.isVisited = true;
            searchUpwardTree(lastNodeVisited, target, depth-1, lastNodeVisited);
            return;
        }
        else if (currentNode == null && depth > 0) {
            double dist = (target.y - lastNodeVisited.y) * (target.y - lastNodeVisited.y) + (target.x - lastNodeVisited.x) * (target.x - lastNodeVisited.x);
            if (dist < distance) {
                closestNeighbor.bestNode = lastNodeVisited;
                closestNeighbor.distance = dist;
            }
            lastNodeVisited.isVisited = true;
            searchUpwardTree(lastNodeVisited, target, depth-1, lastNodeVisited);
            return;
        }
        int dim = depth % 2;
        int compare = dim == 0 ? Double.compare(target.x, currentNode.x) : Double.compare(target.y, currentNode.y);
        if (compare < 0) {
            searchTree(currentNode.left, target, depth + 1, currentNode);
        } else if (compare > 0) {
            searchTree(currentNode.right, target, depth + 1, currentNode);
        }
    }

    private static void searchUpwardTree(Node currentNode, Point target, int depth, Node lastNodeVisited) {
        if (currentNode == null) {
            double dist = (target.y - lastNodeVisited.y) * (target.y - lastNodeVisited.y) + (target.x - lastNodeVisited.x) * (target.x - lastNodeVisited.x);
            if (dist < distance) {
                closestNeighbor.bestNode = lastNodeVisited;
                closestNeighbor.distance = dist;
            }
            if (lastNodeVisited.left != null && !lastNodeVisited.left.isVisited && lastNodeVisited.right != null && lastNodeVisited.right.isVisited) {
                if (lastNodeVisited.right.x-lastNodeVisited.x < distance) searchTree(lastNodeVisited.left, target, depth + 1, lastNodeVisited);
            }
            else if (lastNodeVisited.right != null && !lastNodeVisited.right.isVisited && lastNodeVisited.left != null &&  lastNodeVisited.left.isVisited) {
                if (lastNodeVisited.right.x-lastNodeVisited.x < distance) searchTree(lastNodeVisited.right, target, depth + 1, lastNodeVisited);
            }
            return;
        }
        if (!currentNode.isVisited) {
            double dist = (target.y - currentNode.y) * (target.y - currentNode.y) + (target.x - currentNode.x) * (target.x - currentNode.x);
            if (dist < closestNeighbor.distance) {
                closestNeighbor.bestNode = currentNode;
                closestNeighbor.distance = dist;
            }
            currentNode.isVisited = true;
        }
        int dim = depth % 2;
        if (currentNode.left != null && !currentNode.left.isVisited && currentNode.right != null && currentNode.right.isVisited) {
            if (dim == 0 ? (currentNode.right.x-currentNode.x) < distance :  (currentNode.right.y-currentNode.y) < distance) searchTree(currentNode.left, target, depth + 1, currentNode);
        }
        else if (currentNode.right != null && !currentNode.right.isVisited && currentNode.left != null &&  currentNode.left.isVisited) {
            if (dim == 0 ? (currentNode.right.x-currentNode.x) < distance :  (currentNode.right.y-currentNode.y) < distance) searchTree(currentNode.right, target, depth + 1, currentNode);
        }
        searchUpwardTree(currentNode.parent, target, depth-1, currentNode);
    }
}

// class for initial point-set
class Point {
    double x, y;

    public Point(double x, double y) {
        this.x = x;
        this.y = y;
    }
}
