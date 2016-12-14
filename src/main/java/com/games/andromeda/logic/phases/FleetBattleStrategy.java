package com.games.andromeda.logic.phases;

import com.games.andromeda.draw.Drawer;
import com.games.andromeda.logic.Fleet;
import com.games.andromeda.logic.GameObject;
import com.games.andromeda.logic.Pair;
import com.games.andromeda.logic.WorldAccessor;
import com.games.andromeda.multiplayer.Client;
import com.games.andromeda.ui.UI;

public class FleetBattleStrategy extends CommonHandlingStrategy<Void, Void> {

    @Override
    public Void handlePhaseEvent(Void input) { // пользователь не влияет на эту фазу
        return null;
    } // nothing depends on player

    @Override
    public boolean applyChanges() {
        Pair<Fleet, Fleet> conflict = detectConflict();
        while (conflict != null){
            if (singleFight(conflict.getKey(), conflict.getValue())){
                WorldAccessor.getInstance().removeFleet(conflict.getValue());
                WorldAccessor.getInstance().destroyBase(conflict.getKey().getPosition());
            } else {
                WorldAccessor.getInstance().removeFleet(conflict.getKey());
            }
            conflict = detectConflict();
        }

        UI.getInstance().getShipsLayer().repaint();
        UI.getInstance().getPanel().repaintShipInfo();
        Client.getInstance().sendEndFightMessage();
        return true;
    }

    @Override
    public void autoApplyChanges() {
        applyChanges();
    }

    /**
     * @return пара сражающихся флотов либо null, если конфликтов нет
     */
    private Pair<Fleet, Fleet> detectConflict(){
        GameObject.Side enemy = revert(getSide());
        WorldAccessor world = WorldAccessor.getInstance();
        for(Fleet friendlyFleet: world.getFleetsBySide(getSide())){
            for(Fleet enemyFleet: world.getFleetsBySide(enemy)){
                if (friendlyFleet.getPosition() == enemyFleet.getPosition()){
                    return new Pair<>(friendlyFleet, enemyFleet);
                }
            }
        }
        return null;
    }

    private boolean singleFight(Fleet atacker, Fleet defender)
    {
        return atacker.attack(defender, new Drawer() {
            @Override
            public void drawFleets(Fleet one, Fleet two) {
                // todo dialog
            }

            @Override
            public void reDrawFleets(Fleet one, Fleet two) {
                // todo dialog
                Client.getInstance().sendFightMessage(one);
                Client.getInstance().sendFightMessage(two);
            }
        });
    }

    private GameObject.Side revert(GameObject.Side gameSide){
        return gameSide == GameObject.Side.EMPIRE ? GameObject.Side.FEDERATION : GameObject.Side.EMPIRE;
    }

}
