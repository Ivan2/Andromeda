package com.games.andromeda.level;

import android.content.Context;
import android.util.SparseArray;

import com.games.andromeda.graph.Edge;
import com.games.andromeda.graph.MyGraph;
import com.games.andromeda.graph.Node;
import com.games.andromeda.message.StartGameMessage;

import org.andengine.util.color.Color;

import java.io.IOException;
import java.util.Random;

public class LevelLoader {
    public class MapFormatException extends Exception {}
    private SparseArray<Node> map;
    private MyGraph result;
    private int miniSystemID = -1;

    private LevelLoader(){
        map = new SparseArray<>();
        result = new MyGraph();
    }

    private void addVertex(Integer id, Float x, Float y, Node.SystemType type){
        Node node = new Node(id, x, y, type);
        map.put(id, node);
        result.addNode(node);
    }

    private void addEdge(Integer firstId, Integer secondId, Integer weight, Color color) {
        Edge edge = new Edge(map.get(firstId), map.get(secondId), 1, color);
        if (weight <= 1)
            result.addEdge(edge);
        else {
            Node node1 = edge.getNode1();
            Node node2 = edge.getNode2();
            //угол наклона прямой (ребра)
            double a = Math.atan2(node2.getY()-node1.getY(), node2.getX()-node1.getX());
            //длина ребра
            double d = getD(node1, node2) / weight;
            float dx = (float)(Math.cos(a)*d);
            float dy = (float)(Math.sin(a)*d);
            //деление ребер и добавление мини систем
            for (int i=0; i<weight-1; i++) {
                Node node = new Node(miniSystemID--, node1.getX()+dx, node1.getY()+dy, Node.SystemType.MINI);
                result.addNode(node);
                result.addEdge(new Edge(node1, node, 1, edge.getColor()));
                node1 = node;
            }
            result.addEdge(new Edge(node1, node2, 1, edge.getColor()));
        }
    }

    private double getD(Node node1, Node node2) {
        return Math.sqrt(Math.pow(node1.getX()-node2.getX(), 2)
                + Math.pow(node1.getY()-node2.getY(), 2));
    }

    private boolean processLine(String[] line) throws MapFormatException {
        if (line == null){
            return false;
        }
        Random random = new Random();
        switch(line[0]){
            case "V":
                addVertex(
                        Integer.parseInt(line[1]),
                        Float.parseFloat(line[2]),
                        Float.parseFloat(line[3]),
                        Node.SystemType.EMPTY);
                break;
            case "E":
                addEdge(Integer.parseInt(line[1]),
                        Integer.parseInt(line[2]),
                        Integer.parseInt(line[3]),
                        new Color(random.nextFloat(), random.nextFloat(), random.nextFloat()));
                break;
            case "H":
                addVertex(
                        Integer.parseInt(line[1]),
                        Float.parseFloat(line[2]),
                        Float.parseFloat(line[3]),
                        Node.SystemType.HYPER);
                break;
            default:
                throw new MapFormatException();
        }
        return true;
    }

    /**
     * Фабричный метод для загрузки карты из файла
     * @param context
     * @param file
     * @return
     * @throws IOException
     * @throws MapFormatException
     */
    public static MyGraph loadMap(Context context, String file) throws IOException,
            MapFormatException {
        LevelLoader loader = new LevelLoader();
        ResourceReader csvReader = new ResourceReader(context, file);
        String buf;
        while ((buf = csvReader.readLine()) != null){
            loader.processLine(buf.split(";"));
        }
        return loader.result;
    }

    /**
     * Фабричный метод для загрузки карты из собщения с сервера
     * @param message
     * @return
     */
    public static MyGraph loadMap(StartGameMessage message) {
        LevelLoader loader = new LevelLoader();
        for (Node node : message.getNodeList())
            loader.addVertex(node.getId(), node.getX(), node.getY(), node.getSystemType());
        for (StartGameMessage.TempEdge edge : message.getEdgeList())
            loader.addEdge(edge.node1ID, edge.node2ID, edge.weight, edge.color);
        return loader.result;
    }

}
