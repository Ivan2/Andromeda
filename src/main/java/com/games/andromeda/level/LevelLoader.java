package com.games.andromeda.level;

import android.content.Context;
import android.graphics.PointF;

import com.games.andromeda.graph.Edge;
import com.games.andromeda.graph.MyGraph;
import com.games.andromeda.graph.Node;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class LevelLoader {
    public class MapFormatException extends Exception {}
    private Map<PointF, Node> map;
    private MyGraph result;

    private LevelLoader(){
        map = new HashMap<>();
        result = new MyGraph();
    }

    private void addVertex(float x, float y, Node.SystemType type){
        PointF point = new PointF(x, y);
        Node node = new Node(point.x, point.y, type);
        map.put(point, node);
        result.addNode(node);
    }

    private void addEdge(float fromX, float fromY, float toX, float toY, int weight){
        PointF first = new PointF(fromX, fromY);
        PointF second = new PointF(toX, toY);
        result.addEdge(new Edge(map.get(first), map.get(second), weight));
    }


    private boolean processLine(String[] line) throws MapFormatException {
        if (line == null){
            return false;
        }
        switch(line.length){
            case 2:
                addVertex(Float.parseFloat(line[0]), Float.parseFloat(line[1]), Node.SystemType.EMPTY);
                break;
            case 5:
                addEdge(Float.parseFloat(line[0]), Float.parseFloat(line[1]),
                        Float.parseFloat(line[2]), Float.parseFloat(line[3]),
                        Integer.parseInt(line[4]));
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
