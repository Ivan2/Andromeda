package com.games.andromeda;

import android.app.Application;
import android.content.Context;

/**
 * Created by eugeny on 14.10.16.
 */
public class CurrentApp extends Application {
    private static CurrentApp instance;

    public static CurrentApp getInstance() {
        return instance;
    }

    public static Context getContext(){
        return instance;
        // or return instance.getApplicationContext();
    }

    @Override
    public void onCreate() {
        instance = this;
        super.onCreate();
    }
}
