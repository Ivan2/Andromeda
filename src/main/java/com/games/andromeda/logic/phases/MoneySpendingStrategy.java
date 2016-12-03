package com.games.andromeda.logic.phases;

import com.games.andromeda.logic.Base;
import com.games.andromeda.logic.Fleet;
import com.games.andromeda.logic.Pocket;
import com.games.andromeda.logic.Purchase;
import com.games.andromeda.multiplayer.Client;

import java.util.LinkedList;

public class MoneySpendingStrategy extends ListStrategy<Purchase, Boolean> {

    @Override
    public Boolean handlePhaseEvent(Purchase input) throws Pocket.NotEnoughMoneyException,
            Base.NoFriendlyFleetException, Base.IncorrectNodeException, Fleet.TooMuchShipsException{
        // trading operations
        results.add(input);
        return null;
    }

    @Override
    public boolean applyChanges() {
        // send result and side
        Client.getInstance().sendMoneySpendingMessage(new LinkedList<Base>(),
                new LinkedList<Fleet>(), 0); //TODO
        return false;
    }

    @Override
    public void autoApplyChanges() {
        //applyChanges();
        Client.getInstance().sendMoneySpendingMessage(new LinkedList<Base>(),
                new LinkedList<Fleet>(), 0);
    }
}
