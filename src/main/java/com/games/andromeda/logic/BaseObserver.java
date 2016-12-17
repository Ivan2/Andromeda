package com.games.andromeda.logic;

public interface BaseObserver {
    void onBaseChanged(Base base);
    void onBaseDestroyed(int nodeID);
}
