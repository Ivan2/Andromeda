package com.games.andromeda;

import android.app.Activity;
import android.app.FragmentManager;

import com.games.andromeda.logic.GameObject;
import com.games.andromeda.logic.WorldAccessor;
import com.games.andromeda.logic.phases.FleetBattleStrategy;
import com.games.andromeda.logic.phases.GamePhases;
import com.games.andromeda.logic.phases.HandlingStrategy;
import com.games.andromeda.logic.phases.IncomeEarningStrategy;
import com.games.andromeda.logic.phases.RandomEventStrategy;
import com.games.andromeda.threads.GameTimer;
import com.games.andromeda.ui.UI;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Iterator;

import static android.content.Context.MODE_PRIVATE;

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
    private Activity activity;
    public GameObject.Side side;
    private final String OPTIONS_FILE_NAME = "options";

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
        //if (strategy.getSide() != side)
        //return;
        //strategy.autoApplyChanges();
        //endPhase();
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

            if (strategy instanceof RandomEventStrategy || strategy instanceof IncomeEarningStrategy ||
                    strategy instanceof FleetBattleStrategy
                ) {
                strategy.autoApplyChanges();
                nextPhase();
                return;
            }
            HelpDialog dialog = new HelpDialog();
            String show = readFile();
            if (show!=null) {
                if (show.equals("1"))
                    dialog.show(activity.getFragmentManager(), "");
            }
            else
                dialog.show(activity.getFragmentManager(), "");
        } else {
            UI.getInstance().setEnabled(false);
        }
        UI.getInstance().getPanel().repaintPhaseName(strategy.getTextDescription());
        UI.getInstance().getPanel().repaintShipInfo();
    }

    public void setActivity(Activity activity)
    {
        this.activity = activity;
    }



    private String readFile() {
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(
                    activity.openFileInput(OPTIONS_FILE_NAME)));
            String str = "";
            str = br.readLine();
            return str;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Activity getActivity()
    {
        return activity;
    }

}
