package com.games.andromeda.graph;

public class Edge {

    private Node node1;
    private Node node2;

    public Edge(Node node1, Node node2) {
        this.node1 = node1;
        this.node2 = node2;
    }

    public Node getNode1() {
        return node1;
    }

    public Node getNode2() {
        return node2;
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
