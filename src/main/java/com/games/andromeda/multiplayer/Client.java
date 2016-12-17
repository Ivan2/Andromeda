package com.games.andromeda.multiplayer;

import android.media.MediaPlayer;
import android.util.Log;

import com.games.andromeda.Phases;
import com.games.andromeda.R;
import com.games.andromeda.logic.Base;
import com.games.andromeda.logic.Fleet;
import com.games.andromeda.logic.GameObject;
import com.games.andromeda.logic.WorldAccessor;
import com.games.andromeda.message.BasesCreationMessage;
import com.games.andromeda.message.EndFightMessage;
import com.games.andromeda.message.FightMessage;
import com.games.andromeda.message.FleetsCreationMessage;
import com.games.andromeda.message.MessageFlags;
import com.games.andromeda.message.MoveFleetMessage;
import com.games.andromeda.message.PocketChangesMessage;
import com.games.andromeda.message.RandomEventMessage;
import com.games.andromeda.message.SetupBasesMessage;
import com.games.andromeda.message.SetupFleetsMessage;
import com.games.andromeda.message.StartGameMessage;
import com.games.andromeda.message.WinMessage;
import com.games.andromeda.ui.UI;

import org.andengine.extension.multiplayer.protocol.adt.message.server.IServerMessage;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;


public class Client implements MessageFlags {

    private static Client instance;

    public static Client getInstance() {
        if (instance == null)
            instance = new Client();
        return instance;
    }

    private Client() {
        GameClient.getInstance().setMessageReceiver(new GameClient.MessageReceiver() {
            @Override
            public void onMessageReceive(short flag, IServerMessage message) {
                switch (flag) {
                    /*case FLAG_MESSAGE_SERVER_SHOW:
                        MoveShipServerMessage moveShipServerMessage = (MoveShipServerMessage) message;
                        moveShip(moveShipServerMessage.getX(), moveShipServerMessage.getY(),moveShipServerMessage.getSide(),
                                moveShipServerMessage.getNum());
                        //Log.wtf("nfgnfn","gfn");

                        break;
                    case FIGHT_MESSAGE:
                        FightMessage fightMessage = (FightMessage) message;
                        onFightMessage(fightMessage.getFleet1(),fightMessage.getFleet2(),fightMessage.getNumber1(),fightMessage.getNumber2());

                        break;*/
                    case START_GAME_MESSAGE:
                        StartGameMessage sideMessage = (StartGameMessage) message;
                        Phases.getInstance().side = sideMessage.getSide();
                        break;

                    case SETUP_BASE_MESSAGE:
                    case BASES_CREATION_MESSAGE:
                        SetupBasesMessage setupBasesMessage = (SetupBasesMessage) message;
                        for (Base base : setupBasesMessage.getBases()) {
                            WorldAccessor.getInstance().setBase(base);
                        }
                        UI.getInstance().getSystemsLayer().repaint();
                        if (flag == SETUP_BASE_MESSAGE)
                            Phases.getInstance().endPhase();
                        break;

                    case SETUP_FLEET_MESSAGE:
                    case FLEETS_CREATION_MESSAGE:
                        SetupFleetsMessage setupFleetsMessage = (SetupFleetsMessage) message;
                        for (Fleet fleet : setupFleetsMessage.getFleets())
                            WorldAccessor.getInstance().setFleet(fleet);
                        UI.getInstance().getShipsLayer().repaint();
                        UI.getInstance().getPanel().repaintShipInfo();
                        if (flag == SETUP_FLEET_MESSAGE)
                            Phases.getInstance().endPhase();
                        break;

                    case RANDOM_EVENT_MESSAGE:
                        RandomEventMessage randomEventMessage = (RandomEventMessage) message;
                        GameObject.Side side = randomEventMessage.getSide();
                        int     money = randomEventMessage.getMoney(),
                                num = randomEventMessage.getFleetNumberWithDestroyedShip(),
                                id = randomEventMessage.getIdOfDestroyedBase();
                        WorldAccessor.getInstance().getPocket(side).increase(money);
                        if (num > -1) {
                            WorldAccessor.getInstance().getFleet(side, num).destroyShips(1);
                            UI.getInstance().getShipsLayer().repaint();
                            UI.getInstance().getPanel().repaintShipInfo();
                        }
                        if (id > -1)
                        {

                        }
                        Phases.getInstance().endPhase();
                        break;

                    case POCKET_CHANGE_MESSAGE:
                        PocketChangesMessage pocketChangesMessage = (PocketChangesMessage) message;
                        WorldAccessor.getInstance().getPocket(pocketChangesMessage.getSide())
                                .setTotal(pocketChangesMessage.getTotal());
                        Phases.getInstance().endPhase();
                        break;

                    case MOVE_FLEET_MESSAGE:
                        MoveFleetMessage moveFleetMessage = (MoveFleetMessage) message;
                        MoveFleetMessage.Move move = moveFleetMessage.getMove();
                        Fleet enemy_fleet = WorldAccessor.getInstance().getFleet(
                                moveFleetMessage.getSide(), move.fleetID);
                        if (enemy_fleet != null) {
                            enemy_fleet.setEnergy(move.energy);
                            UI.getInstance().getShipsLayer().moveFleetRemotely(
                                    move.pathInfo,
                                    moveFleetMessage.getSide(),
                                    moveFleetMessage.getMove().fleetID
                            );
                        }
                        UI.getInstance().getPanel().repaintShipInfo();
                        break;

                    case FIGHT_MESSAGE:
                        FightMessage fightMessage = (FightMessage) message;
                        FightMessage.Fight fight = fightMessage.getFight();

                        Fleet fleet = WorldAccessor.getInstance().getFleet(fight.side,
                                fight.fleetID);
                        if (fleet != null) {
                            fleet.setEnergy(fight.energy);
                            fleet.setShips(fight.ships);
                            if (fleet.getShipCount() == 0) {
                                Map<Integer, Base> bases = WorldAccessor.getInstance().getBases();
                                int nodeId = fleet.getPosition();
                                if (bases.containsKey(nodeId)){
                                    Base base = bases.get(nodeId);
                                    if (fleet.getSide() == base.getSide()){
                                        WorldAccessor.getInstance().destroyBase(nodeId);
                                    }
                                }
                                WorldAccessor.getInstance().removeFleet(fleet);
                            }
                        }
                        UI.getInstance().getShipsLayer().repaint();
                        UI.getInstance().getPanel().repaintShipInfo();
                        break;

                    case END_FIGHT_MESSAGE:
                        EndFightMessage endFightMessage = (EndFightMessage) message;
                        if (endFightMessage.getDestroyed())
                            MediaPlayer.create(Phases.getInstance().getActivity(), R.raw.explosion).start();
                        UI.getInstance().getShipsLayer().repaint();
                        UI.getInstance().getPanel().repaintShipInfo();
                        Phases.getInstance().endPhase();
                        break;
                    case WIN_MESSAGE:
                        UI.toast("Вы проиграли(");
                        UI.getInstance().finishGame();
                        break;
                }
            }
        });
    }

    public void sendSetupBaseMessage(Collection<Base> bases) {
        try {
            GameClient.getInstance().sendMessage(new SetupBasesMessage(
                    Phases.getInstance().side, bases));
        } catch (IOException e) {
            Log.wtf("sendSetupBaseMessage error", e.toString());
        }
    }

    public void sendSetupFleetMessage(Collection<Fleet> fleets) {
        try {
            GameClient.getInstance().sendMessage(new SetupFleetsMessage(
                    Phases.getInstance().side, fleets));
        } catch (IOException e) {
            Log.wtf("sendSetupFleetMessage error", e.toString());
        }
    }

    public void sendRandomEventMessage(int money, int numOfFleetWithDestroyedShip, int idOfDestroyedBase) {
        try {
            GameClient.getInstance().sendMessage(new RandomEventMessage(Phases.getInstance().side,money,
                    numOfFleetWithDestroyedShip,idOfDestroyedBase));
        } catch (IOException e) {
            Log.wtf("sendRandomEventMessage error", e.toString());
        }
    }

    public void sendPocketChangesMessage(int total) {
        try {
            GameClient.getInstance().sendMessage(new PocketChangesMessage
                    (Phases.getInstance().side, total));
        } catch (IOException e) {
            Log.wtf("sendPocketChangesMessage error", e.toString());
        }
    }

    public void sendMoneySpendingMessage(Collection<Base> bases, Collection<Fleet> fleets, int total) {
        try {
            GameClient.getInstance().sendMessage(new BasesCreationMessage
                    (Phases.getInstance().side, bases));
            GameClient.getInstance().sendMessage(new FleetsCreationMessage
                    (Phases.getInstance().side, fleets));
            GameClient.getInstance().sendMessage(new PocketChangesMessage
                    (Phases.getInstance().side, total));
        } catch (IOException e) {
            Log.wtf("sendMoneySpendingMessage error", e.toString());
        }
    }

    public void sendMoveFleetMessage(MoveFleetMessage.Move move) {
        try {
            GameClient.getInstance().sendMessage(new MoveFleetMessage
                    (Phases.getInstance().side, move));
        } catch (IOException e) {
            Log.wtf("sendMoveFleetMessage error", e.toString());
        }
    }

    public void sendFightMessage(Fleet fleet) {
        try {
            FightMessage.Fight fight = new FightMessage.Fight(fleet.getId(),
                    fleet.getEnergy(), fleet.getSide(), fleet.getShips());
            GameClient.getInstance().sendMessage(new FightMessage
                    (Phases.getInstance().side, fight));
        } catch (IOException e) {
            Log.wtf("sendEndFightMessage error", e.toString());
        }
    }

    public void sendEndFightMessage(boolean destroyed) {
        try {
            GameClient.getInstance().sendMessage(new EndFightMessage
                    (Phases.getInstance().side,destroyed));
        } catch (IOException e) {
            Log.wtf("sendEndFightMessage error", e.toString());
        }
    }

    public void sendWinMessage()
    {
        try{
            GameClient.getInstance().sendMessage(new WinMessage
                    (Phases.getInstance().side));
        }catch (IOException e) {
            Log.wtf("sendWinMessage error", e.toString());
        }
    }
    /*public void sendMoveShipMessage(Fleet fleet, int num) {
        try {
            GameObject.Side side = fleet.getSide();
            GameClient.getInstance().sendMessage(new MoveShipClientMessage(
                    fleet.getPosition().getX(),
                    fleet.getPosition().getY(),num, side));
        } catch (IOException e) {
            Log.wtf("PATH", e.toString());
        }
    }

    public void sendFightMessage(GameObject.Side side, Fleet attackingFleet, Fleet anotherFleet, int number, int secondNum)
    {
        try {
            attackingFleet.attack(anotherFleet);
            Log.wtf("" + attackingFleet.getShipCount(),"" + anotherFleet.getShipCount());
            GameClient.getInstance().sendMessage(new FightMessage(side,attackingFleet,anotherFleet,number,secondNum));
        } catch (IOException e) {
            Log.wtf("PATH", e.toString());
        }
    }

    private void moveShip(float x, float y, GameObject.Side side, int num) {
        Node node = new Node(x, y);
        try {
            WorldAccessor.getInstance().moveFleet(side, num, node);
        } catch (Exception e) {
            e.printStackTrace();
            Log.wtf("my stupid exception: ", e.toString());
        }
    }

    private void onFightMessage(Fleet firstFleet,  Fleet secondFleet, int num, int num2)
    {
        WorldAccessor world = WorldAccessor.getInstance();
        world.getFleet(firstFleet.getSide(),num).destroyShips(world.getFleet(firstFleet.getSide(),num).getShipCount() - firstFleet.getShipCount());
        world.getFleet(secondFleet.getSide(),num2).destroyShips(world.getFleet(secondFleet.getSide(),num2).getShipCount() - secondFleet.getShipCount());
        Log.wtf(""+ world.getFleet(firstFleet.getSide(),num).getShipCount(),""+ world.getFleet(secondFleet.getSide(),num2).getShipCount());
        ui.getShipsLayer().repaint();
    }
*/
}
