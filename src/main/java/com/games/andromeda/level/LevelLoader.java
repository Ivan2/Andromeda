package com.games.andromeda.level;

import android.content.Context;
import android.util.SparseArray;

import com.games.andromeda.graph.Edge;
import com.games.andromeda.graph.MyGraph;
import com.games.andromeda.graph.Node;

import org.andengine.util.color.Color;

import java.io.IOException;
import java.util.Random;

public class LevelLoader {
    public class MapFormatException extends Exception {}
    private SparseArray<Node> map;
    private MyGraph result;

    private LevelLoader(){
        map = new SparseArray<>();
        result = new MyGraph();
    }

    private void addVertex(Integer id, Float x, Float y, Node.SystemType type){
        Node node = new Node(x, y, type);
        map.put(id, node);
        result.addNode(node);
    }

    private void addEdge(Integer firstId, Integer secondId, Integer weight){
        Random random = new Random();
        result.addEdge(new Edge(map.get(firstId), map.get(secondId), weight,
                new Color(random.nextFloat(), random.nextFloat(), random.nextFloat())));
    }


    private boolean processLine(String[] line) throws MapFormatException {
        if (line == null){
            return false;
        }
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
                        Integer.parseInt(line[3]));
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
            MapFormatException{
        LevelLoader loader = new LevelLoader();
        ResourceReader csvReader = new ResourceReader(context, file);
        String buf;
        while ((buf = csvReader.readLine()) != null){
            loader.processLine(buf.split(";"));
        };
        return loader.result;
    }



}
