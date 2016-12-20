package com.games.andromeda.graph;


import com.games.andromeda.logic.Fleet;
import com.games.andromeda.logic.Pair;
import com.games.andromeda.logic.WorldAccessor;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

import edu.uci.ics.jung.graph.SparseGraph;

/**
 * Поиск доступных для перемещения систем
 */
public class BFSSolver {
    private SparseGraph<Node, Edge> graph;
    private int waypoints;
    private Set<Integer> result;
    private Node fleetPosition;

    public BFSSolver(Fleet fleet){
        graph = WorldAccessor.getInstance().getLevel().getRepresentation();
        waypoints = fleet.getMaxWayLength();
        result = new HashSet<>();
        int start = fleet.getPosition();
        fleetPosition = getNode(start);
        if (preDefinedMoves(fleet)){
            bfsSearch(fleetPosition);
        }
        if (result.contains(start)){
            result.remove(start);
        }
    }

    private Node getNode(int idx){
        return WorldAccessor.getInstance().getNodes().get(idx);
    }

    /**
     * Перемещения, которые флот обязан совершить согласно правилам
     * @param fleet флот
     * @return true, если у флота осталась энергия на свободное перемещение
     */
    private boolean preDefinedMoves(Fleet fleet){
        Node current = fleetPosition;
        if (fleet.getPrevPosition() != null) {
            Node previous = getNode(fleet.getPrevPosition());

            while ((current.getSystemType() == Node.SystemType.MINI)) {
                if (waypoints <= 0) return false;
                for (Node neighbour : graph.getNeighbors(current)) {
                    if (!neighbour.equals(previous)) {
                        previous = current;
                        current = neighbour;
                        result.add(current.getId());
                        waypoints--;
                        break;
                    }
                }
            }
            fleetPosition = current;
        }
        return (waypoints > 0);
    }

    private void bfsSearch(Node start){
        Set<Node> visited = new HashSet<>();
        Queue<Pair<Node, Integer>> queue = new LinkedList<>();
        queue.add(new Pair<>(start, waypoints));
        Pair<Node, Integer> current;
        while (queue.size() > 0){
            current = queue.poll();
            visited.add(current.getKey());
            for (Node node: graph.getNeighbors(current.getKey())){
                if (!visited.contains(node)){
                    Edge edge = graph.findEdge(current.getKey(), node);
                    if (edge.getWeight() <= current.getValue()){
                        queue.add(new Pair<>(node, current.getValue() - edge.getWeight()));
                    }
                }
            }
        }
        for (Node node: visited){
            result.add(node.getId());
        }
    }

    /**
     * Единственный способ получить вычесленный результат
     * @return последовательность из Node Id
     */
    public Iterable<Integer> availableNodes(){
        return result;
    }
}
