package com.games.andromeda.threads;

import com.games.andromeda.logic.GameObject;

public abstract class ServerTimeThread {

    public abstract void onAlmostTime(GameObject.Side side);
    public abstract void onTime(GameObject.Side side);

    private Thread thread;
    private int time;
    private GameObject.Side side;

    public ServerTimeThread() {
        time = 0;
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        break;
                    }

                    if (time > 0) {
                        time--;
                        if (time == 10)
                            onAlmostTime(side);
                        if (time == 0)
                            onTime(side);
                    }
                }
            }
        });
        thread.setDaemon(true);
        thread.start();

    }

    synchronized public void restart(int time, GameObject.Side side) {
        this.time = time;
        this.side = side;
    }

}
