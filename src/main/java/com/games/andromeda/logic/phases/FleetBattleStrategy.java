package com.games.andromeda.logic.phases;

public class FleetBattleStrategy extends CommonHandlingStrategy<Void, Void>{
        @Override
        public Void handlePhaseEvent(Void input) { // пользователь не влияет на эту фазу
            return null;
        } // nothing depends on player


        @Override
        public boolean applyChanges() {
            // todo show battle, fix fleet changes
            return true;
        }

        @Override
        public void autoApplyChanges() {
            applyChanges();
        }
}
