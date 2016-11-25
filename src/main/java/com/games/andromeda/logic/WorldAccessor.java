package com.games.andromeda.logic;

import android.util.Log;

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
    private Map<Node, Base> bases;
    private Pocket empirePocket;
    private Pocket federationPocket;
    private Set<FleetObserver> fleetObservers;
    private Set<BaseObserver> baseObservers;

    protected WorldAccessor(){
        bases = new HashMap<>();
        fleets = new Fleet[6];
        baseObservers = new HashSet<>();
        fleetObservers = new HashSet<>();
    }

    public void setMap(MyGraph graph){
        map = graph;
    }

    public Fleet[] getAllFleets() {
        return fleets;
    }

    public Fleet getFleet(GameObject.Side side, int number){
        return fleets[calcFleetIndex(side, number)];
    }

    public void setFleet(Fleet fleet, int number){
        this.fleets[calcFleetIndex(fleet.getSide(), number)] = fleet;
        for(FleetObserver observer: fleetObservers){
            observer.onFleetChanged(fleet, number);
        }
    }

    public void setAllFleets(Fleet[] fleets) {
        this.fleets = fleets;
    }

    public MyGraph getMap() {
        return map;
    }

    public void setBase(Base base){
        bases.put(base.getNode(), base);
        for(BaseObserver observer: baseObservers){
            observer.onBaseChanged(base);
        }
    }

    public Collection<Base> getBases() {
        return bases.values();
    }

    public void destroyBase(Node node){
        bases.remove(node);
        for(BaseObserver observer: baseObservers){
            observer.onBaseDestroyed(node);
        }
    }

    public void destroyBase(Base base){
        destroyBase(base.getNode());
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

    public void moveFleet(GameObject.Side side, int number, Node position){
        Fleet fleet = getFleet(side, number);
        fleet.setPosition(position); // todo makeMove

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
