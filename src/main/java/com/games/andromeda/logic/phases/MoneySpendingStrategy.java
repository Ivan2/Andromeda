package com.games.andromeda.logic.phases;

import com.games.andromeda.Phases;
import com.games.andromeda.graph.Node;
import com.games.andromeda.logic.Base;
import com.games.andromeda.logic.Fleet;
import com.games.andromeda.logic.GameObject;
import com.games.andromeda.logic.Pocket;
import com.games.andromeda.logic.Purchase;
import com.games.andromeda.logic.WorldAccessor;
import com.games.andromeda.multiplayer.Client;
import com.games.andromeda.ui.UI;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class MoneySpendingStrategy extends ListStrategy<Purchase, Boolean> {

    @Override
    public Boolean handlePhaseEvent(Purchase input) throws Pocket.NotEnoughMoneyException,
            Base.NoFriendlyFleetException, Base.IncorrectNodeException, Fleet.TooMuchShipsException, Exception{
        //TODO check exceptions
        WorldAccessor world = WorldAccessor.getInstance();
        GameObject.Side side = Phases.getInstance().side;
        switch (input.getKind()) {
            case BUILD_BASE:
                if (world.getBases().containsKey(input.getNode().getId()))
                    throw new Exception("База уже существует");
                if (input.getNode().getSystemType() != Node.SystemType.EMPTY)
                    throw new Exception("Вы не можете построить здесь базу");
                if (input.getNode().getSystemType() != Node.SystemType.EMPTY)
                    throw new Exception("Вы не можете построить здесь базу");
                Fleet fleetInNode = world.getFleetByNode(side, input.getNode().getId());
                if (fleetInNode == null)
                    throw new Exception("Вы не можете построить базу там, где нет флота");
                try {
                    Base base = Base.buy(fleetInNode, world.getPocket(side));
                    world.setBase(base);
                    input.base = base;
                    results.add(input);
                    UI.getInstance().getSystemsLayer().repaint();
                } catch (Exception e) {
                    throw e;
                }
                break;
            case BUY_FLEET:
                int id = world.getFreeFleetInd(side);
                if (id == 0)
                    throw new Exception("Вы не можете создавать больше 3 флотов");
                if (!world.getBases().containsKey(input.getNode().getId()))
                    throw new Exception("Создать флот можно только на базе");
                if (input.getNode().getSystemType() != Node.SystemType.FRIENDLY)
                    throw new Exception("Вы не можете создать здесь флот");

                try {
                    Fleet fleet = Fleet.buy(id, 1, world.getBases().get(input.getNode().getId()),
                            world.getPocket(side));
                    world.setFleet(fleet);
                    input.fleet = fleet;
                    results.add(input);
                    UI.getInstance().getShipsLayer().repaint();
                    UI.getInstance().getPanel().repaintShipInfo();
                } catch (Exception e) {
                    throw e;
                }
                break;
        }
        return null;
    }

    @Override
    public boolean applyChanges() {
        List<Base> bases = new LinkedList<>();
        List<Fleet> fleets = new LinkedList<>();
        for (Purchase purchase : results) {
            switch (purchase.getKind()) {
                case BUILD_BASE:
                    bases.add(purchase.base);
                    break;
                case BUY_FLEET:
                    fleets.add(purchase.fleet);
                    break;
            }
        }
        Map<Integer,Base> allBases = WorldAccessor.getInstance().getBases();
        int basesCount = 0;
        Iterator<Map.Entry<Integer, Base>> it = allBases.entrySet().iterator();
        while (it.hasNext())
        {
            Map.Entry<Integer, Base> pair = it.next();
            if (pair.getValue().getSide() == Phases.getInstance().side)
            {
                basesCount++;
            }
        }
        if (Phases.getInstance().side == GameObject.Side.EMPIRE)
        {
            if (basesCount >= 18)
            {
                UI.toast("Вы победили!");
                Client.getInstance().sendWinMessage();
                UI.getInstance().finishGame();
            }
        }
        else
            if (basesCount >= 16)
            {
                UI.toast("Вы победили!");
                Client.getInstance().sendWinMessage();
                UI.getInstance().finishGame();
            }
        Client.getInstance().sendMoneySpendingMessage(bases, fleets,
                WorldAccessor.getInstance().getPocket(Phases.getInstance().side).getTotal());
        return false;
    }

    @Override
    public void autoApplyChanges() {
        //applyChanges();
        Client.getInstance().sendMoneySpendingMessage(new LinkedList<Base>(),
                new LinkedList<Fleet>(),
                WorldAccessor.getInstance().getPocket(Phases.getInstance().side).getTotal());
    }

    @Override
    public String getTextDescription() {
        return "фаза покупки баз и флотов";
    }
}
