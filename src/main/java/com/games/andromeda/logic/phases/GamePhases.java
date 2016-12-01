package com.games.andromeda.logic.phases;


import com.games.andromeda.logic.GameObject;

import java.util.EnumMap;
import java.util.Iterator;
import java.util.Map;

public class GamePhases implements Iterable<HandlingStrategy>{
    private UnifiedPhaseIterator iterator;
    
    @Override
    public Iterator<HandlingStrategy> iterator() {
        return iterator;
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

    public GamePhases(){
        iterator = new UnifiedPhaseIterator();
        strategyMap = new EnumMap<>(PhaseType.class);
        strategyMap.put(PhaseType.LEVEL_PREPARATION_BASES, new LevelPreparationStrategy());
        strategyMap.put(PhaseType.LEVEL_PREPARATION_FLEETS, new FleetPreparationStrategy());
        strategyMap.put(PhaseType.RANDOM_EVENTS, new RandomEventStrategy());
        strategyMap.put(PhaseType.INCOME_EARNING, new IncomeEarningStrategy());
        strategyMap.put(PhaseType.MONEY_SPENDING, new MoneySpendingStrategy());
        strategyMap.put(PhaseType.FLEET_MOVING, new FleetMovingStrategy());
        strategyMap.put(PhaseType.FLEET_BATTLE, new FleetBattleStrategy());
    }


    public HandlingStrategy getCurrentPhase() {
        return iterator.current;
    }

    public PhaseType getPhaseType(){
        return iterator.getIterator().getPhaseType();
    }
    
    abstract class PhaseIterator implements Iterator<HandlingStrategy>{
        protected int idx;
        protected boolean firstPlayer;

        public PhaseIterator() {
            this.firstPlayer = true;
        }

        public GameObject.Side getSide(){
            return firstPlayer ? GameObject.Side.FEDERATION: GameObject.Side.EMPIRE;
        }

        public abstract PhaseType getPhaseType();
    }
    
    class UnifiedPhaseIterator implements Iterator<HandlingStrategy>{
        private NormalPhaseIterator iter;
        private ZeroPhaseIterator zeroIter;

        private HandlingStrategy current;


        public UnifiedPhaseIterator() {
            this.zeroIter = new ZeroPhaseIterator();
            this.iter = new NormalPhaseIterator();
        }

        @Override
        public boolean hasNext() {
            return true;
        }

        @Override
        public HandlingStrategy next() {
            current = getIterator().next();
            return current;
        }

        public PhaseIterator getIterator() {
            return zeroIter.hasNext() ? zeroIter: iter;
        }
    }

    class ZeroPhaseIterator extends PhaseIterator{

        public ZeroPhaseIterator() {
            super();
            idx = 0;
        }

        @Override
        public PhaseType getPhaseType() {
            return idx<=2 ? PhaseType.LEVEL_PREPARATION_BASES: PhaseType.LEVEL_PREPARATION_FLEETS;
        }

        @Override
        public boolean hasNext() {
            return idx < 4;
        }

        @Override
        public HandlingStrategy next() {
            firstPlayer = !firstPlayer;
            idx++;
            HandlingStrategy phase = strategyMap.get(getPhaseType());
            phase.startPhase(getSide());
            return phase;
        }
    }

    class NormalPhaseIterator extends PhaseIterator{

        private PhaseType[] order;
        private int start_idx;


        public NormalPhaseIterator() {
            super();
            idx = 1;
            start_idx = 2;
            order = PhaseType.values();
        }

        @Override
        public PhaseType getPhaseType() {
            return order[idx];
        }

        @Override
        public boolean hasNext() {
            return true;
        }

        @Override
        public HandlingStrategy next() {
            idx++;
            if (idx >= order.length){
                start_idx = 2;
                idx = start_idx;
                firstPlayer = !firstPlayer;
            }
            HandlingStrategy result = strategyMap.get(getPhaseType());
            result.startPhase(getSide());
            return result;
        }


    }
}
