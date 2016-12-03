package com.games.andromeda.logic;

import com.games.andromeda.Phases;
import com.games.andromeda.graph.MyGraph;
import com.games.andromeda.graph.Node;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
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
    private MyGraph map;
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

    public void setMap(MyGraph graph){
        map = graph;
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

    public Fleet getFleet(GameObject.Side side, int number) {
        return fleets[calcFleetIndex(side, number)];
    }

    public void setFleet(Fleet fleet, int number) {
        this.fleets[calcFleetIndex(fleet.getSide(), number)] = fleet;
        for (FleetObserver observer : fleetObservers) {
            observer.onFleetChanged(fleet, number);
        }
    }

    public void setAllFleets(Fleet[] fleets) {
        this.fleets = fleets;
    }

    public boolean addFleet(Fleet fleet) {
        for (int i=1; i<=3; i++) {
            if (fleets[calcFleetIndex(fleet.getSide(), i)] == null) {
                setFleet(fleet, i);
                return true;
            }
        }
        return false;
    }

    public MyGraph getMap() {
        return map;
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
        bases.remove(nodeID);
        for(BaseObserver observer: baseObservers){
            observer.onBaseDestroyed(nodeID);
        }
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

    public void moveFleet(GameObject.Side side, int number, int nodeID){
        Fleet fleet = getFleet(side, number);
        fleet.setPosition(nodeID); // todo makeMove

        for(FleetObserver observer: fleetObservers){
            observer.onFleetChanged(fleet, number);
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

    public static void init(MyGraph graph){
        getInstance().setPocket(new Pocket(GameObject.Side.FEDERATION));
        getInstance().setPocket(new Pocket(GameObject.Side.EMPIRE));
        getInstance().setMap(graph);
    }

    public static void initBases(Collection<Base> bases){
        for (Base base: bases){
            getInstance().setBase(base);
        }
    }



}
