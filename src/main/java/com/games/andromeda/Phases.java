package com.games.andromeda;

import android.util.Log;

import com.games.andromeda.logic.GameObject;
import com.games.andromeda.logic.phases.GamePhases;
import com.games.andromeda.logic.phases.HandlingStrategy;
import com.games.andromeda.threads.GameTimer;
import com.games.andromeda.ui.UI;

import java.util.Iterator;

public class Phases {

    private static Phases instance;

    public static Phases getInstance() {
        if (instance == null)
            instance = new Phases();
        return instance;
    }

    private GamePhases gamePhases;
    private Iterator<HandlingStrategy> iterator;
    private HandlingStrategy strategy;
    private GameTimer gameTimer;

    public GameObject.Side side;

    private Phases() {
        gamePhases = new GamePhases();
        iterator = gamePhases.iterator();
    }

    public void startGame(GameTimer gameTimer) {
        this.gameTimer = gameTimer;

        Log.wtf("side", side+"");
        nextPhase();
    }

    public HandlingStrategy getPhase() {
        return strategy;
    }

    public void serverEndPhase() {
        strategy.autoApplyChanges();
        endPhase();
    }

    public void clientEndPhase() {
        strategy.autoApplyChanges();//TODO applyChanges()
        endPhase();
    }

    public void endPhase() {
        UI.getInstance().setEnabled(false);
        gameTimer.stop();
        nextPhase();
    }

    private void nextPhase() {
        strategy = iterator.next();
        UI.getInstance().repaintPhaseName(strategy.getTextDescription());
        if (strategy.getSide() == side) {
            gameTimer.restart();
            UI.getInstance().setEnabled(true);
        } else {
            UI.getInstance().setEnabled(false);
        }
    }

    /*public void onOtherEndPhase() {
        Log.wtf(strategy.getSide()+" "+ WorldAccessor.getInstance().side, "end phase");
    }*/

}
