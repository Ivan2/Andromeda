package com.games.andromeda.logic.phases;

import com.games.andromeda.TallerDeclension;
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
        Pocket pocket = WorldAccessor.getInstance().getPocket(getSide());
        int start = pocket.getTotal();
        for(Base base: WorldAccessor.getInstance().getBases().values()){
            if (base.getSide() == getSide()){
                pocket.increase(base.getProfit());
            }
        }
        // восстановление энергии и щитов
        for(Fleet fleet: WorldAccessor.getInstance().getFleetsBySide(getSide())){
            fleet.restoreShields();
            fleet.restoreEnergy();
            Client.getInstance().sendChangeFleetMessage(fleet);
        }

        UI.toast("Получен доход от баз: " + TallerDeclension.getTallerStr(pocket.getTotal() - start));
        Client.getInstance().sendPocketChangesMessage(pocket.getTotal());
        return true;
    }

    @Override
    public void autoApplyChanges() {
        applyChanges();
    }

}
