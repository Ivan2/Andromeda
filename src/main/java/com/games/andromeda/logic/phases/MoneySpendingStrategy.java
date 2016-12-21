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
                    UI.getInstance().getPanel().repaintBaseInfo();
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
            case UPGRADE_FLEET:
                results.add(input);
        }
        return null;
    }

    @Override
    public boolean applyChanges() {
        List<Base> bases = new LinkedList<>();
        List<Fleet> newFleets = new LinkedList<>();
        for (Purchase purchase : results) {
            switch (purchase.getKind()) {
                case BUILD_BASE:
                    bases.add(purchase.base);
                    break;
                case BUY_FLEET:
                    newFleets.add(purchase.fleet);
                    break;
                case UPGRADE_FLEET:
                    Client.getInstance().sendChangeFleetMessage(purchase.fleet);
            }
        }
        checkWinCondition();
        Client.getInstance().sendMoneySpendingMessage(bases, newFleets,
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

    private void checkWinCondition(){
        int basesCount = 0;
        for (Base base: WorldAccessor.getInstance().getBases().values()){
            basesCount += (base.getSide() == getSide()) ? 1 : 0;
        }
        if (enoughBases(basesCount)){
            UI.toast("Вы победили!");
            Client.getInstance().sendWinMessage();
            UI.getInstance().finishGame();
        }
    }

    private boolean enoughBases(int count){
        switch (getSide()){
            case EMPIRE:
                return count >= 18;
            case FEDERATION:
                return count >= 16;
        }
        return false;
    }
}
