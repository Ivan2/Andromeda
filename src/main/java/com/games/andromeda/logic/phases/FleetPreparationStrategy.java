package com.games.andromeda.logic.phases;

import android.util.Log;

import com.games.andromeda.Phases;
import com.games.andromeda.graph.Node;
import com.games.andromeda.logic.Base;
import com.games.andromeda.logic.Fleet;
import com.games.andromeda.logic.WorldAccessor;
import com.games.andromeda.multiplayer.Client;
import com.games.andromeda.ui.UI;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Random;

public class FleetPreparationStrategy extends ListStrategy<Node, Boolean> {

    @Override
    public Boolean handlePhaseEvent(Node input) {
        if (input.getSystemType() == Node.SystemType.FRIENDLY){
            if (results.contains(input)){
                results.remove(input);
                return true;
            }
            if (results.size() <= 3){
                results.add(input);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean applyChanges() {
        if (results.size() != 3) return false;
        // todo send side and new fleet locations
        return true;
    }

    @Override
    public void autoApplyChanges() {
        // todo choose 3-result.size random friendly systems and add them to results
        //applyChanges();
        Collection<Fleet> fleets = new LinkedList<>();

        Random random = new Random();
        WorldAccessor world = WorldAccessor.getInstance();
        ArrayList<Base> bases = new ArrayList<>(world.getBases());
        for (int i=1; i<=3; i++) {
            while (true) {
                int ind = random.nextInt(bases.size());
                if (bases.get(ind).getSide() == Phases.getInstance().side) {
                    try {
                        Fleet fleet = new Fleet(Phases.getInstance().side, 8 + random.nextInt(4), bases.get(ind));
                        fleets.add(fleet);
                        world.setFleet(fleet, i);
                    } catch (Exception e) {
                        UI.toast("er " + e.toString());
                        Log.wtf("error", e.toString());
                    }
                    break;
                }
            }
        }

        UI.getInstance().getShipsLayer().repaint();
        UI.getInstance().repaintHUD();
        Client.getInstance().sendSetupFleetMessage(fleets);
    }
}
