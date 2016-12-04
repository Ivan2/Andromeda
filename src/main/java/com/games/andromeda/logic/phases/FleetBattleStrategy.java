package com.games.andromeda.logic.phases;

import com.games.andromeda.Phases;
import com.games.andromeda.logic.Fleet;
import com.games.andromeda.logic.GameObject;
import com.games.andromeda.logic.WorldAccessor;
import com.games.andromeda.multiplayer.Client;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class FleetBattleStrategy extends CommonHandlingStrategy<Void, Void> {

    @Override
    public Void handlePhaseEvent(Void input) { // пользователь не влияет на эту фазу
        return null;
    } // nothing depends on player

    @Override
    public boolean applyChanges() {
        // todo show battle, fix fleet changes

        HashMap<Integer, AbstractMap.SimpleEntry<List<Fleet>, List<Fleet>>> battles = new HashMap<>();
        GameObject.Side side = Phases.getInstance().side;
        GameObject.Side otherSide = (side== GameObject.Side.EMPIRE) ? GameObject.Side.FEDERATION :
                GameObject.Side.EMPIRE;
        WorldAccessor world = WorldAccessor.getInstance();

        for (int i=1; i<=3; i++) {
            Fleet fleet1 = world.getFleet(side, i);
            if (fleet1 != null)
                for (int j = 1; j <= 3; j++) {
                    Fleet fleet2 = world.getFleet(otherSide, j);
                    if (fleet2 != null)
                        if (fleet1.getPosition() == fleet2.getPosition()) {
                            int nodeID = fleet1.getPosition();
                            if (!battles.containsKey(nodeID)) {
                                List<Fleet> fleets1 = new LinkedList<>();
                                fleets1.add(fleet1);
                                List<Fleet> fleets2 = new LinkedList<>();
                                fleets2.add(fleet2);
                                AbstractMap.SimpleEntry<List<Fleet>, List<Fleet>> fleets =
                                        new AbstractMap.SimpleEntry<>(fleets1, fleets2);
                                battles.put(nodeID, fleets);
                            } else {
                                AbstractMap.SimpleEntry<List<Fleet>, List<Fleet>> fleets = battles.get(nodeID);
                                List<Fleet> fleets1 = fleets.getKey();
                                List<Fleet> fleets2 = fleets.getValue();
                                if (!fleets1.contains(fleet1))
                                    fleets1.add(fleet1);
                                if (!fleets2.contains(fleet2))
                                    fleets2.add(fleet2);
                            }
                        }
                }
        }

        for (Map.Entry<Integer, AbstractMap.SimpleEntry<List<Fleet>, List<Fleet>>> entry : battles.entrySet()) {
            int nodeID = entry.getKey();
            AbstractMap.SimpleEntry<List<Fleet>, List<Fleet>> fleets = entry.getValue();
            List<Fleet> fleets1 = fleets.getKey();
            List<Fleet> fleets2 = fleets.getValue();

            for (Fleet fleet1: fleets1)
                if (fleet1.getShipCount() > 0)
                    for (Fleet fleet2: fleets2)
                        if (fleet2.getShipCount() > 0)
                            fleet1.attack(fleet2);


            for (Fleet fleet: fleets1) {
                Client.getInstance().sendFightMessage(fleet);
                if (fleet.getShipCount() == 0)
                    world.removeFleet(fleet);
            }
            for (Fleet fleet: fleets2) {
                Client.getInstance().sendFightMessage(fleet);
                if (fleet.getShipCount() == 0)
                    world.removeFleet(fleet);
            }
        }

        Client.getInstance().sendEndFightMessage();
        return true;
    }

    @Override
    public void autoApplyChanges() {
        applyChanges();
    }

}
