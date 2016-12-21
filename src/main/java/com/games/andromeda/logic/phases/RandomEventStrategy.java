package com.games.andromeda.logic.phases;

import com.games.andromeda.Phases;
import com.games.andromeda.TallerDeclension;
import com.games.andromeda.logic.WorldAccessor;
import com.games.andromeda.multiplayer.Client;
import com.games.andromeda.ui.UI;

import java.util.Random;

public class RandomEventStrategy extends CommonHandlingStrategy<Void, Void> {

    @Override
    public Void handlePhaseEvent(Void input) { // пользователь не влияет на эту фазу
        return null;
    }


    @Override
    public boolean applyChanges() {
        Random rand = new Random();
        boolean bool = rand.nextBoolean();
        if (bool)
        {
            int type = rand.nextInt(100);
            if (type < 90)
            {
                int money = rand.nextInt(31) - 15;
                WorldAccessor world = WorldAccessor.getInstance();
                int currMoney = world.getPocket(Phases.getInstance().side).getTotal();
                if (currMoney < -1 * money)
                {
                    money = -1 * currMoney;
                    world.getPocket(Phases.getInstance().side).increase(money);
                    Client.getInstance().sendRandomEventMessage(money,-1,-1);
                    if (money > 0)
                        UI.toast("Обнаружен золотой рудник в одной из систем. Вы получаете: " +
                                TallerDeclension.getTallerStr(money));
                    else
                        if (money < 0)
                            UI.toast("На восстановление урона после катаклизма вы тратите " +
                                    TallerDeclension.getTallerStr(-money));
                    return true;
                }
                if (money > 0)
                    UI.toast("Обнаружен золотой рудник в одной из систем. Вы получаете: " +
                            TallerDeclension.getTallerStr(money));
                else
                    if (money < 0)
                        UI.toast("На восстановление урона после катаклизма вы тратите " +
                                TallerDeclension.getTallerStr(-money));
                world.getPocket(Phases.getInstance().side).increase(money);
                Client.getInstance().sendRandomEventMessage(money,-1,-1);
                return true;
            }
            if (type < 99)
            {
                int i = 1;
                while (i < 4)
                {
                    if (WorldAccessor.getInstance().getFleet(Phases.getInstance().side,i) == null)
                        i++;
                    else
                        if (WorldAccessor.getInstance().getFleet(Phases.getInstance().side,i).getShipCount() == 0)
                            i++;
                    else break;
                }
                if (i < 4) {
                    WorldAccessor.getInstance().getFleet(Phases.getInstance().side,i).destroyShips(1);
                    UI.getInstance().getShipsLayer().repaint();
                    UI.getInstance().getPanel().repaintShipInfo();
                    UI.toast("Один из ваших кораблей столкнулся с метеоритом");
                    Client.getInstance().sendRandomEventMessage(0, i, -1);
                    return true;
                }
                Client.getInstance().sendRandomEventMessage(0,-1,-1);
                return true;
            }

        }
        Client.getInstance().sendRandomEventMessage(0,-1,-1);
        return true;
    }

    @Override
    public void autoApplyChanges() {
        applyChanges();
    }
}
