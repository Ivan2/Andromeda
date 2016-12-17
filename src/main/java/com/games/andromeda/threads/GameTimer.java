package com.games.andromeda.threads;

public abstract class GameTimer {

    public abstract void onTime(int time);

    private int time;
    private int delay;
    private Thread thread;

    public GameTimer(int time, int delay) {
        this.time = time;
        this.delay = delay;
        createThread();
        thread.setDaemon(true);
        thread.start();
    }

    public GameTimer(){
        this(60, 1000);
    }

    synchronized public void restart() {
        time = 60;
    }

    synchronized public void stop() {
        time = 0;
        //onTime(0);
    }

    private void createThread() {
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(delay);
                    } catch (InterruptedException e) {
                        break;
                    }

                    if (time > 0) {
                        time--;
                        onTime(time);
                    }
                }
            }
        });
    }

}
