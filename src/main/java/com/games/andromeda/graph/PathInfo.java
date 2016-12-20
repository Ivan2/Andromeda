package com.games.andromeda.graph;

import java.util.List;

public class PathInfo {
    private List<Integer> nodeIds;
    private Integer pathWeight;

    public PathInfo(List<Integer> nodes, Integer pathWeight) {
        this.nodeIds = nodes;
        this.pathWeight = pathWeight;
    }

    public List<Integer> getNodeIds() {
        return nodeIds;
    }

    public Integer getPathWeight() {
        return pathWeight;
    }

    @Override
    public String toString() {
        return nodeIds + ": " + pathWeight;
    }
}
