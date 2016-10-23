package com.games.andromeda.logic.phases;

public class RandomEventStrategy extends CommonHandlingStrategy<Void, Void> {

    @Override
    public Void handlePhaseEvent(Void input) { // пользователь не влияет на эту фазу
        return null;
    }


    @Override
    public boolean applyChanges() {
        // todo сотворить какую-то рандомную дичь
        return true;
    }

    @Override
    public void autoApplyChanges() {
        applyChanges();
    }
}
