package com.games.andromeda.logic.phases;


import android.util.Log;

import com.games.andromeda.Phases;
import com.games.andromeda.graph.Node;
import com.games.andromeda.logic.Base;
import com.games.andromeda.logic.GameObject;
import com.games.andromeda.logic.WorldAccessor;
import com.games.andromeda.multiplayer.Client;
import com.games.andromeda.ui.UI;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Random;

public class LevelPreparationStrategy extends ListStrategy<Node, Boolean>{

    private Integer maxSize;

    @Override
    public void startPhase(GameObject.Side side) {
        super.startPhase(side);
        maxSize = (side == GameObject.Side.EMPIRE) ? 10: 6;
    }

    @Override
    public Boolean handlePhaseEvent(Node input) {
        // !!! call SystemsLayer.repaint after that
        if (input.getSystemType() == Node.SystemType.FRIENDLY){
            results.remove(input);
            input.setSystemType(Node.SystemType.EMPTY);
            return true;
        }
        else if (input.getSystemType() == Node.SystemType.EMPTY){
            if (maxSize <= results.size()) return false;
            results.add(input);
            input.setSystemType(Node.SystemType.FRIENDLY);
        }
        return false;
    }

    @Override
    public boolean applyChanges() {
        if (maxSize != results.size()) return false;
        // todo send results to server
        // -side
        // -nodes
        //Client.getInstance().sendSetupBaseMessage();
        return true;
    }

    @Override
    public void autoApplyChanges() {
        // todo set maxSize - result.size() random empty systems to friendly
        //applyChanges();
        Collection<Base> bases = new LinkedList<>();

        Random random = new Random();
        WorldAccessor world = WorldAccessor.getInstance();
        ArrayList<Node> nodes = new ArrayList<>(world.getMap().getNodes());
        for (int i=0; i<6; i++) {
            while (true) {
                int ind = random.nextInt(nodes.size());
                if (nodes.get(ind).getSystemType() == Node.SystemType.EMPTY) {
                    try {
                        Base base = new Base(Phases.getInstance().side, nodes.get(ind));
                        world.setBase(base);
                        bases.add(base);
                    } catch (Base.IncorrectNodeException e) {
                        Log.wtf("error", e.toString());
                    }
                    break;
                }
            }
        }

        UI.getInstance().getSystemsLayer().repaint();
        Client.getInstance().sendSetupBaseMessage(bases);
    }

}
