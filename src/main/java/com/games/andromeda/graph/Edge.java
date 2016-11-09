package com.games.andromeda.graph;

import org.andengine.util.color.Color;

public class Edge {

    private Node node1;
    private Node node2;
    private int weight;
    private Color color;

    public Edge(Node node1, Node node2, int weight, Color color) {
        this.node1 = node1;
        this.node2 = node2;
        this.weight = weight;
        this.color = color;
    }

    public Node getNode1() {
        return node1;
    }

    public Node getNode2() {
        return node2;
    }

    public int getWeight() {
        return weight;
    }

    public Color getColor() {
        return color;
    }

    @Override
    public int hashCode() {
        return node1.hashCode() + node2.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Edge))
            return false;
        Edge edge = (Edge)obj;
        return edge.node1.equals(node1) && edge.node2.equals(node2);
    }
}
