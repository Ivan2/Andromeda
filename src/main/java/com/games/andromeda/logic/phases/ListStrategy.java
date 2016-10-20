package com.games.andromeda.logic.phases;


import com.games.andromeda.logic.GameObject;
import com.games.andromeda.logic.Purchase;

import java.util.LinkedList;
import java.util.List;

public abstract class ListStrategy<TParam, TOut> implements HandlingStrategy<TParam, TOut> {
    protected List<TParam> results;
    protected GameObject.Side side;

    public ListStrategy(){
        results = new LinkedList<>();
    }

    @Override
    public void startPhase(GameObject.Side side) {
        this.side = side;
        results.clear();
    }
}
