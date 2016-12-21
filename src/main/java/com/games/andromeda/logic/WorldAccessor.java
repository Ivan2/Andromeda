package com.games.andromeda.logic;

import com.games.andromeda.Phases;
import com.games.andromeda.graph.LevelGraph;
import com.games.andromeda.graph.Node;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class WorldAccessor {
    // singleton part
    private static volatile WorldAccessor instance;

    public static WorldAccessor getInstance(){
        WorldAccessor local = instance;
        if (local == null){
            synchronized (WorldAccessor.class){
                local = instance;
                if (local == null){
                    instance = local = new WorldAccessor();
                }
            }
        }
        return local;
    }

    // storage part
    private Fleet[] fleets;
    private LevelGraph level;
    private Map<Integer, Base> bases;
    private Map<Integer, Node> nodes;
    private Pocket empirePocket;
    private Pocket federationPocket;
    private Set<FleetObserver> fleetObservers;
    private Set<BaseObserver> baseObservers;

    protected WorldAccessor() {
        bases = new HashMap<>();
        nodes = new HashMap<>();
        fleets = new Fleet[6];
        baseObservers = new HashSet<>();
        fleetObservers = new HashSet<>();
    }

    public void setLevel(LevelGraph graph){
        level = graph;
        nodes.clear();
        for (Node node : graph.getNodes())
            nodes.put(node.getId(), node);
    }

    public Map<Integer, Node> getNodes() {
        return nodes;
    }

    public Fleet[] getAllFleets() {
        return fleets;
    }

    public Iterable<Fleet> getFleetsBySide(GameObject.Side side){
        int start, end;
        if (side == GameObject.Side.EMPIRE) {
            start = 0;
            end = 2;
        } else {
            start = 3;
            end = 5;
        }
        List<Fleet> result = new ArrayList<>();
        for(int i = start; i <= end; ++i){
            if ((fleets[i] != null) && fleets[i].getShipCount() > 0){
                result.add(fleets[i]);
            }
        }
        return result;
    }

    public Fleet getFleet(GameObject.Side side, int number) {
        return fleets[calcFleetIndex(side, number)];
    }

    public Fleet getFleetByNode(GameObject.Side side, int nodeID) {
        for (int i=1; i<=3; i++)
            if (fleets[calcFleetIndex(side, i)] != null &&
                    fleets[calcFleetIndex(side, i)].getPosition() == nodeID)
                    return fleets[calcFleetIndex(side, i)];
        return null;
    }

    public void setFleet(Fleet fleet) {
        this.fleets[calcFleetIndex(fleet.getSide(), fleet.getId())] = fleet;
        for (FleetObserver observer : fleetObservers) {
            observer.onFleetChanged(fleet);
        }
    }

    public void setAllFleets(Fleet[] fleets) {
        this.fleets = fleets;
    }

    public int getFreeFleetInd(GameObject.Side side) {
        for (int i=1; i<=3; i++)
            if (fleets[calcFleetIndex(side, i)] == null)
                return i;
        return 0;
    }

    public void removeFleet(Fleet fleet) {
        for (int i=1; i<=3; i++) {
            if (fleets[calcFleetIndex(fleet.getSide(), i)] != null &&
                    fleets[calcFleetIndex(fleet.getSide(), i)].getId() == fleet.getId()) {
                fleets[calcFleetIndex(fleet.getSide(), i)] = null;
                break;
            }
        }
    }

    public LevelGraph getLevel() {
        return level;
    }

    public void setBase(Base base) {
        bases.put(base.getNodeID(), base);
        Node node = nodes.get(base.getNodeID());
        if (base.getSide() == Phases.getInstance().side)
            node.setSystemType(Node.SystemType.FRIENDLY);
        else
            node.setSystemType(Node.SystemType.ENEMY);
        for(BaseObserver observer: baseObservers){
            observer.onBaseChanged(base);
        }
    }

    public Map<Integer, Base> getBases() {
        return bases;
    }

    public void destroyBase(int nodeID){
        if (bases.containsKey(nodeID)) {
            bases.remove(nodeID);
            for (BaseObserver observer : baseObservers) {
                observer.onBaseDestroyed(nodeID);
            }
        }
        nodes.get(nodeID).setSystemType(Node.SystemType.EMPTY);
    }

    public void destroyBase(Base base){
        destroyBase(base.getNodeID());
    }

    private int calcFleetIndex(GameObject.Side side, int number){
        if ((number-1)/3 != 0) throw new IndexOutOfBoundsException("Number must be 1..3");
        int offset = (side == GameObject.Side.EMPIRE) ? -1 : 2;
        return offset + number;
    }

    public void setPocket(Pocket pocket){
        switch (pocket.getSide()){
            case EMPIRE:
                empirePocket = pocket;
                return;
            case FEDERATION:
                federationPocket = pocket;
                return;
        }
    }

    public Pocket getPocket(GameObject.Side side){
        return (side == GameObject.Side.FEDERATION) ? federationPocket : empirePocket;
    }

    public void addFleetObserver(FleetObserver observer){
        fleetObservers.add(observer);
    }

    public void addBaseObserver(BaseObserver observer){
        baseObservers.add(observer);
    }

    public static void init(LevelGraph graph){
        getInstance().setPocket(new Pocket(GameObject.Side.FEDERATION));
        getInstance().setPocket(new Pocket(GameObject.Side.EMPIRE));
        getInstance().setLevel(graph);
    }

    public static void initBases(Collection<Base> bases){
        for (Base base: bases){
            getInstance().setBase(base);
        }
    }

    public void createWorld()
    {
        bases.clear();
        nodes.clear();
        baseObservers.clear();
        fleetObservers.clear();
        fleets = new Fleet[6];
        /*bases = new HashMap<>();
        nodes = new HashMap<>();

        baseObservers = new HashSet<>();
        fleetObservers = new HashSet<>();*/
    }



}
