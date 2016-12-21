package com.games.andromeda;

import android.app.Activity;
import android.media.MediaPlayer;
import android.util.Log;

import com.games.andromeda.ui.dialogs.HelpDialog;
import com.games.andromeda.logic.GameObject;
import com.games.andromeda.logic.phases.GamePhases;
import com.games.andromeda.logic.phases.HandlingStrategy;
import com.games.andromeda.logic.phases.RandomEventStrategy;
import com.games.andromeda.threads.GameTimer;
import com.games.andromeda.ui.UI;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.Random;

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
    private HelpDialog dialog;
    private MediaPlayer mediaPlayer;

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

    //вызывается таймером
    public void serverEndPhase() {
        if (strategy.getSide() != side)
            return;
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
        if (dialog != null && dialog.isVisible())
            dialog.dismiss();
        UI.getInstance().hideAllDialogs();
        gameTimer.stop();
        UI.getInstance().getPanel().repaintTime(0);
        nextPhase();
    }

    private void nextPhase() {
        strategy = iterator.next();
        if (strategy.getSide() == side) {
            gameTimer.restart();
            UI.getInstance().setEnabled(true);

            switch (gamePhases.getPhaseType()){
                case MONEY_SPENDING:
                    playRandomMusic(new int[]
                            {R.raw.music1, R.raw.music2, R.raw.music3, R.raw.music4});
                    break;
                case RANDOM_EVENTS:
                case INCOME_EARNING:
                case FLEET_BATTLE:
                    strategy.autoApplyChanges();
                    nextPhase();
                    return;
            }

            dialog = new HelpDialog();
            String show = readFile();
            if (show != null) {
                if (show.equals("11") || show.equals("10") )
                    dialog.show(activity.getFragmentManager(), "");
            }
            else
                dialog.show(activity.getFragmentManager(), "");
        } else {
            if (strategy instanceof RandomEventStrategy){
                playRandomMusic(new int[]
                        {R.raw.waiting1, R.raw.waiting2, R.raw.waiting3, R.raw.waiting4});
            }
            UI.getInstance().setEnabled(false);
        }
        if (strategy.getSide() == side)
            UI.getInstance().getPanel().repaintPhaseName(strategy.getTextDescription());
        else
            UI.getInstance().getPanel().repaintPhaseName("Ход противника");
        UI.getInstance().getPanel().repaintShipInfo();
    }

    public void setActivity(Activity activity)
    {
        this.activity = activity;
        String sound = readFile();
        if (sound.equals("01")||sound.equals("11")) {
            if (mediaPlayer == null) {
                mediaPlayer = MediaPlayer.create(activity, R.raw.start);
                mediaPlayer.setLooping(true);
                mediaPlayer.start();
            }
        }
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

    public MediaPlayer getMediaPlayer()
    {
        return mediaPlayer;
    }

    private void playRandomMusic(int[] resources){
        String sound = readFile();
        if (sound.equals("01")||sound.equals("11")) {
            Random rand = new Random();
            if (mediaPlayer != null) {
                mediaPlayer.release();
            }
            try {
                mediaPlayer = MediaPlayer.create(activity, resources[rand.nextInt(resources.length)]);
                mediaPlayer.setLooping(true);
                mediaPlayer.start();
            } catch (Exception e) {
                Log.d("Error", e.toString());
            }
        }
    }

    public void dispose()
    {
        try {
            mediaPlayer.release();
        } catch (Exception e)
        {
            Log.wtf("Error", e.getMessage());
        }
        gameTimer.stop();
        instance = null;
    }

}
