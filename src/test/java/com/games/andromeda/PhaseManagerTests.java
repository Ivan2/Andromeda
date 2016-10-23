package com.games.andromeda;

import com.games.andromeda.logic.GameObject;
import com.games.andromeda.logic.phases.FleetBattleStrategy;
import com.games.andromeda.logic.phases.FleetMovingStrategy;
import com.games.andromeda.logic.phases.FleetPreparationStrategy;
import com.games.andromeda.logic.phases.GamePhases;
import com.games.andromeda.logic.phases.HandlingStrategy;
import com.games.andromeda.logic.phases.IncomeEarningStrategy;
import com.games.andromeda.logic.phases.LevelPreparationStrategy;
import com.games.andromeda.logic.phases.MoneySpendingStrategy;
import com.games.andromeda.logic.phases.RandomEventStrategy;

import org.junit.Test;

import java.util.Iterator;

import static org.junit.Assert.assertEquals;

public class PhaseManagerTests {
    @Test
    public void testOrder() throws Exception{
        GamePhases phases = new GamePhases();
        Iterator<HandlingStrategy> iterator = phases.iterator();
        HandlingStrategy phase;

        phase = iterator.next();
        assertEquals(phase.getClass(), LevelPreparationStrategy.class);
        assertEquals(phase.getSide(), GameObject.Side.EMPIRE);

        phase = iterator.next();
        assertEquals(phase.getClass(), LevelPreparationStrategy.class);
        assertEquals(phase.getSide(), GameObject.Side.FEDERATION);

        phase = iterator.next();
        assertEquals(phase.getClass(), FleetPreparationStrategy.class);
        assertEquals(phase.getSide(), GameObject.Side.EMPIRE);

        phase = iterator.next();
        assertEquals(phase.getClass(), FleetPreparationStrategy.class);
        assertEquals(phase.getSide(), GameObject.Side.FEDERATION);

        phase = iterator.next();
        assertEquals(phase.getClass(), RandomEventStrategy.class);
        assertEquals(phase.getSide(), GameObject.Side.FEDERATION);

        phase = iterator.next();
        assertEquals(phase.getClass(), IncomeEarningStrategy.class);
        assertEquals(phase.getSide(), GameObject.Side.FEDERATION);

        phase = iterator.next();
        assertEquals(phase.getClass(), MoneySpendingStrategy.class);
        assertEquals(phase.getSide(), GameObject.Side.FEDERATION);

        phase = iterator.next();
        assertEquals(phase.getClass(), FleetMovingStrategy.class);
        assertEquals(phase.getSide(), GameObject.Side.FEDERATION);

        phase = iterator.next();
        assertEquals(phase.getClass(), FleetBattleStrategy.class);
        assertEquals(phase.getSide(), GameObject.Side.FEDERATION);

        phase = iterator.next();
        assertEquals(phase.getClass(), RandomEventStrategy.class);
        assertEquals(phase.getSide(), GameObject.Side.EMPIRE);

        phase = iterator.next();
        assertEquals(phase.getClass(), IncomeEarningStrategy.class);
        assertEquals(phase.getSide(), GameObject.Side.EMPIRE);

        phase = iterator.next();
        assertEquals(phase.getClass(), MoneySpendingStrategy.class);
        assertEquals(phase.getSide(), GameObject.Side.EMPIRE);

        phase = iterator.next();
        assertEquals(phase.getClass(), FleetMovingStrategy.class);
        assertEquals(phase.getSide(), GameObject.Side.EMPIRE);

        phase = iterator.next();
        assertEquals(phase.getClass(), FleetBattleStrategy.class);
        assertEquals(phase.getSide(), GameObject.Side.EMPIRE);

        phase = iterator.next();
        assertEquals(phase.getClass(), RandomEventStrategy.class);
        assertEquals(phase.getSide(), GameObject.Side.FEDERATION);

    }
}
