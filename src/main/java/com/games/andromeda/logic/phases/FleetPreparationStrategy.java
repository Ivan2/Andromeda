package com.games.andromeda.logic.phases;

import android.util.Log;

import com.games.andromeda.Phases;
import com.games.andromeda.graph.Node;
import com.games.andromeda.logic.Base;
import com.games.andromeda.logic.Fleet;
import com.games.andromeda.logic.GameObject;
import com.games.andromeda.logic.WorldAccessor;
import com.games.andromeda.multiplayer.Client;
import com.games.andromeda.ui.UI;

import java.util.ArrayList;
import java.util.Random;

public class FleetPreparationStrategy extends ListStrategy<Fleet, Boolean> {

    public void createFleet(Node node) throws Exception {
        if (results.size() >= 3)
            throw new Exception("Нельзя создавать больше 3 флотов");
        GameObject.Side side = Phases.getInstance().side;
        WorldAccessor world = WorldAccessor.getInstance();
        if (!world.getBases().containsKey(node.getId()))
            throw new Exception("Флот можно создать только на базе");
        Base base = world.getBases().get(node.getId());
        if (base.getSide() != side)
            throw new Exception("Флот можно создать только на своей базе");

        int id = world.getFreeFleetInd(side);
        if (id == 0)
            throw new Exception("Нельзя создавать больше 3 флотов");
        Fleet fleet = new Fleet(id, side, 5, base);
        world.setFleet(fleet);
        results.add(fleet);

        UI.getInstance().getShipsLayer().repaint();
        UI.getInstance().getPanel().repaintShipInfo();
    }

    public void removeFleet(Fleet fleet) throws Exception {
        GameObject.Side side = Phases.getInstance().side;
        if (fleet.getSide() != side)
            throw new Exception("Можно удалить только свой флот");
        if (!results.contains(fleet))
            throw new Exception("Флота не существует");
        WorldAccessor world = WorldAccessor.getInstance();
        world.removeFleet(fleet);
        results.remove(fleet);

        UI.getInstance().getShipsLayer().repaint();
        UI.getInstance().getPanel().repaintShipInfo();
    }

    @Override
    public Boolean handlePhaseEvent(Fleet input) throws Exception {
        return true;
    }

    @Override
    public boolean applyChanges() {
        //if (results.size() != 3)
        //    return false;
        // todo send side and new fleet locations
        Client.getInstance().sendSetupFleetMessage(results);
        return true;
    }

    @Override
    public void autoApplyChanges() {
        // todo choose 3-result.size random friendly systems and add them to results
        //applyChanges();
        Random random = new Random();
        WorldAccessor world = WorldAccessor.getInstance();
        ArrayList<Base> bases = new ArrayList<>(world.getBases().values());
        for (int i=1; i<=3-results.size(); i++) {
            while (true) {
                int ind = random.nextInt(bases.size());
                if (bases.get(ind).getSide() == Phases.getInstance().side) {
                    try {
                        int id = world.getFreeFleetInd(side);
                        if (id != 0) {
                            Fleet fleet = new Fleet(id, Phases.getInstance().side, 8 + random.nextInt(4), bases.get(ind));
                            results.add(fleet);
                            world.setFleet(fleet);
                        }
                    } catch (Exception e) {
                        Log.wtf("error", e.toString());
                    }
                    break;
                }
            }
        }

        UI.getInstance().getShipsLayer().repaint();
        UI.getInstance().getPanel().repaintShipInfo();
        Client.getInstance().sendSetupFleetMessage(results);
    }

    @Override
    public String getTextDescription() {
        return "Фаза расстановки флотов";
    }
}
