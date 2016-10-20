package com.games.andromeda.logic.phases;


import com.games.andromeda.logic.GameObject;

import java.util.EnumMap;
import java.util.Iterator;
import java.util.Map;

public class GamePhases implements Iterable<HandlingStrategy>{
    private PhaseIterator iter;

    @Override
    public Iterator<HandlingStrategy> iterator() {
        return iter;
    }

    public enum PhaseType{
        LEVEL_PREPARATION_BASES,
        LEVEL_PREPARATION_FLEETS,
        RANDOM_EVENTS,
        INCOME_EARNING,
        MONEY_SPENDING,
        FLEET_MOVING,
        FLEET_BATTLE,
    }

    private Map<PhaseType, HandlingStrategy> strategyMap;
    private boolean initialized = false;

    public GamePhases(){
        iter = new PhaseIterator();
        strategyMap = new EnumMap<>(PhaseType.class);
        strategyMap.put(PhaseType.LEVEL_PREPARATION_BASES, new LevelPreparationStrategy());
        strategyMap.put(PhaseType.LEVEL_PREPARATION_FLEETS, new FleetPreparationStrategy());
        strategyMap.put(PhaseType.RANDOM_EVENTS, new RandomEventStrategy());
        strategyMap.put(PhaseType.INCOME_EARNING, new IncomeEarningStrategy());
        strategyMap.put(PhaseType.MONEY_SPENDING, new MoneySpendingStrategy());
        strategyMap.put(PhaseType.FLEET_MOVING, new FleetMovingStrategy());
        strategyMap.put(PhaseType.FLEET_BATTLE, new FleetBattleStrategy());
    }

    public GameObject.Side getSide(){
        return iter.getSide();
    }



    class PhaseIterator implements Iterator<HandlingStrategy>{

        private PhaseType[] order;
        private int idx;
        private int start_idx;

        private boolean second;

        public PhaseIterator(){
            idx = start_idx = 0;
            second = true;
            order = PhaseType.values();
        }


        protected boolean nextTurn(){
            second = !second;
            return second;
        }

        public GameObject.Side getSide(){
            return second ? GameObject.Side.FEDERATION: GameObject.Side.EMPIRE;
        }

        @Override
        public boolean hasNext() {
            return true;
        }

        @Override
        public HandlingStrategy next() {
            if (nextTurn()){
                idx++;
                if (idx >= order.length){
                    start_idx = 2;
                    idx = start_idx;
                }
            }
            HandlingStrategy result = strategyMap.get(order[idx]);
            result.startPhase(getSide());
            return result;
        }
    }
}
