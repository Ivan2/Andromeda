package com.games.andromeda.graph;

import org.andengine.util.color.Color;

import java.util.Collection;

import edu.uci.ics.jung.graph.SparseGraph;

public class MyGraph {

    private SparseGraph<Node, Edge> graph;
    private Node hyper;

    public MyGraph() {
        graph = new SparseGraph<>();
    }

    public void addNode(Node node) {
        graph.addVertex(node);
        if (node.getSystemType() == Node.SystemType.HYPER) {
            if (hyper == null) {
                hyper = node;
            } else {
                graph.addEdge(new Edge(node, hyper, 0, Color.BLUE), node, hyper);
                hyper = null;
            }
        }
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
