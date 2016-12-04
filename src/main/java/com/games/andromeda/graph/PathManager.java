package com.games.andromeda.graph;

import com.games.andromeda.logic.Fleet;
import com.games.andromeda.logic.WorldAccessor;

import org.apache.commons.collections15.Transformer;

import java.util.ArrayList;
import java.util.List;

import edu.uci.ics.jung.algorithms.shortestpath.DijkstraShortestPath;
import edu.uci.ics.jung.graph.SparseGraph;

public class PathManager {

    private Node start, end, previous;
    private DijkstraShortestPath<Node, Edge> solver;

    public PathManager(){
        solver = new DijkstraShortestPath<>(
                WorldAccessor.getInstance().getMap().getRepresentation(),
                new EdgeTransformer(),
                true);
    }

    public void addNode(int nodeID){
        end = getNode(nodeID);
    }

    /**
     * Построение кратчайшего пути по результатам, накопленным при перетаскивании
     * @return список вершин и вес пути
     */
    public PathInfo getPath(){
        List<Integer> result = goToNearestSystem();
        Node newStart = WorldAccessor.getInstance().getNodes().get(last(result));
        List<Edge> edges = solver.getPath(newStart, end);
        int weight = distanceToInt(solver.getDistance(start, newStart)) +
                         distanceToInt(solver.getDistance(newStart, end));
        for (Edge edge: edges) {
            result.add(getAnotherVertex(last(result), edge));
        }
        return new PathInfo(result, weight);
    }

    private int getAnotherVertex(int prev, Edge edge){
        return (prev == edge.getNode1().getId()) ? edge.getNode2().getId(): edge.getNode1().getId();
    }

    private int last(List<Integer> list){
        return list.get(list.size() - 1);
    }

    /**
     * Построение пути до ближайшей звездной системы / до финиша с учетом прошлого направления движения
     * @return путь, как минимум содержащий индекс node начала пути
     */
    private List<Integer> goToNearestSystem(){
        List<Integer> path = new ArrayList<>();
        Node current = start;
        SparseGraph<Node, Edge> graph = WorldAccessor.getInstance().getMap().getRepresentation();
        while ((current.getSystemType() == Node.SystemType.MINI) && !current.equals(end)){
            for (Node neighbour: graph.getNeighbors(start)){
                if (!neighbour.equals(previous)){
                    path.add(current.getId());
                    current = neighbour;
                    previous = current;
                    break;
                }
            }
        }
        path.add(current.getId());
        return path;
    }

    public void reset(){
        start = end = null;
    }

    public void start(Fleet fleet){
        reset();
        start = getNode(fleet.getPosition());
        previous = getNode(fleet.getPrevPosition());
    }

    private Node getNode(Integer id){
        if (id == null) return null;
        return WorldAccessor.getInstance().getNodes().get(id);
    }

    private int distanceToInt(Number num){
        if (num == null){
            return 0;
        }
        return (int) ((double) num);
    }

    private class EdgeTransformer implements Transformer<Edge, Integer> {
        @Override
        public Integer transform(Edge edge) {
            return edge.getWeight();
        }
    }
}
