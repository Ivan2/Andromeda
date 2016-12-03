package com.games.andromeda.graph;

import java.util.List;

public class PathInfo {
    private List<Integer> nodeIds;
    private Integer length;

    PathInfo(List<Integer> nodes, Integer length) {
        this.nodeIds = nodes;
        this.length = length;
    }

    public List<Integer> getNodeIds() {
        return nodeIds;
    }

    public Integer getLength() {
        return length;
    }

    @Override
    public String toString() {
        return nodeIds + ": " + length;
    }
}
