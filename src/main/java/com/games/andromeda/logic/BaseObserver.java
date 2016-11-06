package com.games.andromeda.logic;

import com.games.andromeda.graph.Node;

public interface BaseObserver {
    void onBaseChanged(Base base);
    void onBaseDestroyed(Node node);
}
