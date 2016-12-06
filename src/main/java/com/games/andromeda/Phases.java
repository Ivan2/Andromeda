package com.games.andromeda;

import com.games.andromeda.logic.GameObject;
import com.games.andromeda.logic.phases.GamePhases;
import com.games.andromeda.logic.phases.HandlingStrategy;
import com.games.andromeda.logic.phases.IncomeEarningStrategy;
import com.games.andromeda.logic.phases.RandomEventStrategy;
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
        nextPhase();
    }

    public HandlingStrategy getPhase() {
        return strategy;
    }

    //вызывается сообщением с сервера об окончании времени хода
    public void serverEndPhase() {
        strategy.autoApplyChanges();
        endPhase();
    }

    //вызывается нажатием кнопки
    public void clientEndPhase() {
        strategy.applyChanges();
        endPhase();
    }

    //вызывается сообщением с сервера об окончании фазы
    public void endPhase() {
        //UI.getInstance().setEnabled(false);
        gameTimer.stop();
        nextPhase();
    }

    private void nextPhase() {
        strategy = iterator.next();
        if (strategy.getSide() == side) {
            gameTimer.restart();
            UI.getInstance().setEnabled(true);

            if (strategy instanceof RandomEventStrategy || strategy instanceof IncomeEarningStrategy// ||
                    //strategy instanceof FleetBattleStrategy
                ) {
                strategy.autoApplyChanges();
                nextPhase();
                return;
            }
        } else {
            UI.getInstance().setEnabled(false);
        }
        UI.getInstance().getPanel().repaintPhaseName(strategy.getTextDescription());
    }

}
