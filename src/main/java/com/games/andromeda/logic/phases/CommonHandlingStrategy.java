package com.games.andromeda.logic.phases;

import com.games.andromeda.logic.GameObject;

public abstract class CommonHandlingStrategy<TParam, TOut>
        implements HandlingStrategy<TParam, TOut> {
    protected GameObject.Side side;

    @Override
    public void startPhase(GameObject.Side side) {
        this.side = side;
    }

    @Override
    public GameObject.Side getSide() {
        return side;
    }
}
