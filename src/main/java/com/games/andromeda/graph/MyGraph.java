package com.games.andromeda.graph;

import java.util.Collection;

import edu.uci.ics.jung.graph.SparseGraph;

public class MyGraph {

    private SparseGraph<Node, Edge> graph;

    public MyGraph() {
        graph = new SparseGraph<>();
    }

    public void addNode(Node node) {
        graph.addVertex(node);
    }

    public void addEdge(Edge edge) {
        graph.addEdge(edge, edge.getNode1(), edge.getNode2());
    }

    public Collection<Node> getNodes() {
        return graph.getVertices();
    }

    public Collection<Edge> getEdges() {
        return graph.getEdges();
    }
}
