package com.games.andromeda.draw;

import com.games.andromeda.logic.Fleet;

public interface Drawer {
    void drawFleets(Fleet one, Fleet two);
    void reDrawFleets(Fleet one, Fleet two);
}
