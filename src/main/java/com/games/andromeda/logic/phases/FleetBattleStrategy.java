package com.games.andromeda.logic.phases;

import com.games.andromeda.logic.GameObject;

public class FleetBattleStrategy implements HandlingStrategy<Void, Void>{
        private GameObject.Side side;
        @Override
        public void startPhase(GameObject.Side side) {
            this.side = side;
        }

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
