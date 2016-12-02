package com.games.andromeda.graph;

import com.games.andromeda.logic.Fleet;

import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

public class PathManager {

    private Deque<Integer> path;

    public PathManager(){
        path = new LinkedList<>();
    }

    public boolean addNode(int nodeID){
        if (path.size() > 0)
            if (path.getLast() == nodeID)
                return false;
        path.addLast(nodeID);
        return true;
    }

    public List<Integer> getPath(){
        //todo check path on graph
        return (List<Integer>)path;
    }

    public void reset(){
        path.clear();
    }

    public void start(Fleet fleet){
        reset();
        path.addLast(fleet.getPosition());
    }
}
