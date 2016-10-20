package com.games.andromeda.logic.phases;


import com.games.andromeda.graph.Node;
import com.games.andromeda.logic.GameObject;

import java.util.LinkedList;
import java.util.List;

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
        return true;
    }

    @Override
    public void autoApplyChanges() {
        // todo set maxSize - result.size() random empty systems to friendly
        applyChanges();
    }
}
