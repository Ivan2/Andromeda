package com.games.andromeda.logic.phases;

import android.media.MediaPlayer;

import com.games.andromeda.Phases;
import com.games.andromeda.R;
import com.games.andromeda.logic.BattleReporter;
import com.games.andromeda.logic.Base;
import com.games.andromeda.logic.Fleet;
import com.games.andromeda.logic.GameObject;
import com.games.andromeda.logic.Pair;
import com.games.andromeda.logic.WorldAccessor;
import com.games.andromeda.multiplayer.Client;
import com.games.andromeda.ui.UI;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;

public class FleetBattleStrategy extends CommonHandlingStrategy<Void, Void> {

    @Override
    public Void handlePhaseEvent(Void input) { // пользователь не влияет на эту фазу
        return null;
    } // nothing depends on player

    @Override
    public boolean applyChanges() {
        Pair<Fleet, Fleet> conflict = detectConflict();
        boolean someDestroyed = false;
        while (conflict != null){
            someDestroyed = true;
            if (singleFight(conflict.getKey(), conflict.getValue())){
                WorldAccessor.getInstance().removeFleet(conflict.getValue());
            } else {
                WorldAccessor.getInstance().removeFleet(conflict.getKey());
            }
            conflict = detectConflict();
        }
        if (someDestroyed) {
            String str = readFile();
            if (str.equals("01")||str.equals("11"))
                MediaPlayer.create(Phases.getInstance().getActivity(), R.raw.explosion).start();
        }

        checkBases();
        UI.getInstance().getShipsLayer().repaint();
        UI.getInstance().getPanel().repaintShipInfo();
        Client.getInstance().sendEndFightMessage(someDestroyed);
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
                    switch (world.getNodes().get(friendlyFleet.getPosition()).getSystemType()){
                        case EMPTY:
                        case ENEMY:
                            return new Pair<>(friendlyFleet, enemyFleet);
                    }
                }
            }
        }
        return null;
    }

    private boolean singleFight(Fleet atacker, Fleet defender)
    {
        return atacker.attack(defender, new BattleReporter() {
            @Override
            public void showFleets(Fleet one, Fleet two) {
                // todo dialog
            }

            @Override
            public void showFleetChanges(Fleet one, Fleet two) {
                // todo dialog
                Client.getInstance().sendChangeFleetMessage(one);
                Client.getInstance().sendChangeFleetMessage(two);
            }
        });
    }

    private GameObject.Side revert(GameObject.Side gameSide){
        return gameSide == GameObject.Side.EMPIRE ? GameObject.Side.FEDERATION : GameObject.Side.EMPIRE;
    }

    @Override
    public String getTextDescription() {
        return "Фаза боя";
    }

    private void checkBases(){
        WorldAccessor world = WorldAccessor.getInstance();
        Map<Integer, Base> bases = world.getBases();

        for (Fleet fleet: world.getAllFleets()){
            if (fleet != null){
                if (bases.containsKey(fleet.getPosition())){
                    Base base = bases.get(fleet.getPosition());
                    if (base.getSide() != fleet.getSide()){
                        Client.getInstance().sendBaseDestructionMessage(fleet.getPosition());
                        world.destroyBase(base);
                    }
                }
            }
        }
    }

    private String readFile() {
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(
                    UI.getInstance().activity.openFileInput("options")));
            String str = "";
            str = br.readLine();
            return str;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "11";
    }
}
