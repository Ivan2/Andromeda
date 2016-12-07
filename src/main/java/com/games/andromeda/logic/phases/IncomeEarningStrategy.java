package com.games.andromeda.logic.phases;


import com.games.andromeda.Phases;
import com.games.andromeda.logic.Base;
import com.games.andromeda.logic.Fleet;
import com.games.andromeda.logic.Pocket;
import com.games.andromeda.logic.WorldAccessor;
import com.games.andromeda.multiplayer.Client;
import com.games.andromeda.ui.UI;

public class IncomeEarningStrategy extends CommonHandlingStrategy<Void, Void>{
    @Override
    public Void handlePhaseEvent(Void input) { // пользователь не влияет на эту фазу
        return null;
    }

    @Override
    public boolean applyChanges() {
        // доходы от баз
        Pocket pocket = WorldAccessor.getInstance().getPocket(Phases.getInstance().side);
        int start = pocket.getTotal();
        for(Base base: WorldAccessor.getInstance().getBases().values()){
            if (base.getSide() == pocket.getSide()){
                pocket.increase(base.getProfit());
            }
        }
        // восстановление энергии и щитов
        for(int fleetNumber = 1; fleetNumber <= 3; ++fleetNumber) {
            Fleet fleet = WorldAccessor.getInstance().getFleet(pocket.getSide(), fleetNumber);
            if (fleet != null) {
                fleet.restoreShields();
                fleet.restoreEnergy();
            }
        }
        UI.toast("Получен доход от баз: " + (pocket.getTotal() - start));
        Client.getInstance().sendPocketChangesMessage(pocket.getTotal());
        return true;
    }

    @Override
    public void autoApplyChanges() {
        applyChanges();
    }

}
