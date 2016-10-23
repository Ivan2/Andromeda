package com.games.andromeda.logic.phases;


import com.games.andromeda.logic.GameObject;

import java.util.LinkedList;
import java.util.List;

public abstract class ListStrategy<TParam, TOut> extends CommonHandlingStrategy<TParam, TOut>{
    protected List<TParam> results;

    public ListStrategy(){
        results = new LinkedList<>();
    }

    @Override
    public void startPhase(GameObject.Side side) {
        super.startPhase(side);
        results.clear();
    }
}
