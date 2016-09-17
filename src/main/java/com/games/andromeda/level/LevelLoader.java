package com.games.andromeda.level;

import com.games.andromeda.graph.Edge;
import com.games.andromeda.graph.MyGraph;
import com.games.andromeda.graph.Node;

public class LevelLoader {

    public static MyGraph createLevel() {
        MyGraph graph = new MyGraph();
        Node node1 = new Node(0.1f, 0.1f, Node.SystemType.ENEMY);
        Node node2 = new Node(0.2f, 0.8f, Node.SystemType.ENEMY);
        Node node3 = new Node(0.6f, 0.4f, Node.SystemType.EMPTY);
        Node node4 = new Node(0.8f, 0.7f, Node.SystemType.EMPTY);
        Node node5 = new Node(0.9f, 0.2f, Node.SystemType.FRIENDLY);

        Edge edge1 = new Edge(node1, node2);
        Edge edge2 = new Edge(node3, node2);
        Edge edge3 = new Edge(node1, node3);
        Edge edge4 = new Edge(node4, node3);
        Edge edge5 = new Edge(node4, node2);
        Edge edge6 = new Edge(node5, node3);
        Edge edge7 = new Edge(node5, node4);
        Edge edge8 = new Edge(node5, node1);

        graph.addNode(node1);
        graph.addNode(node2);
        graph.addNode(node3);
        graph.addNode(node4);
        graph.addNode(node5);

        graph.addEdge(edge1);
        graph.addEdge(edge2);
        graph.addEdge(edge3);
        graph.addEdge(edge4);
        graph.addEdge(edge5);
        graph.addEdge(edge6);
        graph.addEdge(edge7);
        graph.addEdge(edge8);

        return graph;
    }

}
