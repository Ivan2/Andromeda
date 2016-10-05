package com.games.andromeda.graph;

import com.games.andromeda.logic.Fleet;

import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

public class PathManager {

    private Deque<Node> path;

    public PathManager(){
        path = new LinkedList<>();
    }

    public boolean addNode(Node node){
        if (path.size() > 0)
            if (path.getLast() == node)
                return false;
        path.addLast(node);
        return true;
    }

    public List<Node> getPath(){
        //todo check path on graph
        return (List<Node>)path;
    }

    public void reset(){
        path.clear();
    }

    public void start(Fleet fleet){
        reset();
        path.addLast(fleet.getPosition());
    }
}
