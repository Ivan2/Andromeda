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
        if (edge.getWeight() <= 1)
            graph.addEdge(edge, edge.getNode1(), edge.getNode2());
        else {
            Node node1 = edge.getNode1();
            Node node2 = edge.getNode2();
            //угол наклона прямой (ребра)
            double a = Math.atan2(node2.getY()-node1.getY(), node2.getX()-node1.getX());
            //длина ребра
            double d = getD(node1, node2) / edge.getWeight();
            float dx = (float)(Math.cos(a)*d);
            float dy = (float)(Math.sin(a)*d);
            //деление ребер и добавление мини систем
            for (int i=0; i<edge.getWeight()-1; i++) {
                Node node = new Node(node1.getX()+dx, node1.getY()+dy, Node.SystemType.MINI);
                graph.addVertex(node);
                graph.addEdge(new Edge(node1, node, 1), node1, node);
                node1 = node;
            }
            graph.addEdge(new Edge(node1, node2, 1), node1, node2);
        }
    }

    public Collection<Node> getNodes() {
        return graph.getVertices();
    }

    public Collection<Edge> getEdges() {
        return graph.getEdges();
    }

    private double getD(Node node1, Node node2) {
        return Math.sqrt(Math.pow(node1.getX()-node2.getX(), 2)
                + Math.pow(node1.getY()-node2.getY(), 2));
    }
}
