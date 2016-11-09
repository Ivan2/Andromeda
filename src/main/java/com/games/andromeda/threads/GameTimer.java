package com.games.andromeda.threads;

import android.app.Activity;

import com.games.andromeda.ui.hud.PanelHUD;

public class GameTimer implements Runnable {
    private int time;
    private PanelHUD hud;
    private Activity activity;
    private int delay;

    public GameTimer(Activity activity, PanelHUD hud, int time, int delay) {
        this.time = time;
        this.hud = hud;
        this.activity = activity;
        this.delay = delay;
    }

    public GameTimer(Activity activity, PanelHUD hud){
        this(activity, hud, 59, 1000);
    }

    @Override
    public void run() {
        while (true) {
            try {
                Thread.sleep(delay);
            } catch (InterruptedException e) {
                break;
            }
            time--;
            if (time == -1)
                time = 59;
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    hud.repaintTime(time);
                }
            });
        }
    }
}
