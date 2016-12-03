package com.games.andromeda.multiplayer;

import android.util.Log;

import com.games.andromeda.Phases;
import com.games.andromeda.logic.Base;
import com.games.andromeda.logic.Fleet;
import com.games.andromeda.logic.WorldAccessor;
import com.games.andromeda.message.EndFightMessage;
import com.games.andromeda.message.MessageFlags;
import com.games.andromeda.message.MoveFleetMessage;
import com.games.andromeda.message.PocketChangesMessage;
import com.games.andromeda.message.RandomEventMessage;
import com.games.andromeda.message.SetupBasesMessage;
import com.games.andromeda.message.SetupFleetsMessage;
import com.games.andromeda.message.StartGameMessage;
import com.games.andromeda.message.BasesCreationMessage;
import com.games.andromeda.message.FleetsCreationMessage;
import com.games.andromeda.ui.UI;

import org.andengine.extension.multiplayer.protocol.adt.message.server.IServerMessage;

import java.io.IOException;
import java.util.Collection;


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
                        int i = 1;
                        for (Fleet fleet : setupFleetsMessage.getFleets()) {
                            WorldAccessor.getInstance().setFleet(fleet, i);
                            i++;
                        }

                        UI.getInstance().getShipsLayer().repaint();
                        UI.getInstance().getPanel().repaintShipInfo();
                        if (flag == SETUP_FLEET_MESSAGE)
                            Phases.getInstance().endPhase();
                        break;

                    case RANDOM_EVENT_MESSAGE:
                        //RandomEventMessage randomEventMessage = (RandomEventMessage) message;
                        //TODO read message
                        Phases.getInstance().endPhase();
                        break;

                    case POCKET_CHANGE_MESSAGE:
                        PocketChangesMessage pocketChangesMessage = (PocketChangesMessage) message;
                        WorldAccessor.getInstance().getPocket(pocketChangesMessage.getSide())
                                .increase(pocketChangesMessage.getDelta());
                        Phases.getInstance().endPhase();
                        break;

                    case MOVE_FLEET_MESSAGE:
                        //MoveFleetMessage moveFleetMessage = (MoveFleetMessage) message;
                        //TODO read message
                        Phases.getInstance().endPhase();
                        break;

                    case END_FIGHT_MESSAGE:
                        //EndFightMessage endFightMessage = (EndFightMessage) message;
                        //TODO read message
                        Phases.getInstance().endPhase();
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
            Log.wtf("PATH", e.toString());
        }
    }

    public void sendSetupFleetMessage(Collection<Fleet> fleets) {
        try {
            GameClient.getInstance().sendMessage(new SetupFleetsMessage(
                    Phases.getInstance().side, fleets));
        } catch (IOException e) {
            Log.wtf("PATH", e.toString());
        }
    }

    public void sendRandomEventMessage() {
        try {
            GameClient.getInstance().sendMessage(new RandomEventMessage(Phases.getInstance().side));
        } catch (IOException e) {
            Log.wtf("PATH", e.toString());
        }
    }

    public void sendPocketChangesMessage(int delta) {
        try {
            GameClient.getInstance().sendMessage(new PocketChangesMessage
                    (Phases.getInstance().side, delta));
        } catch (IOException e) {
            Log.wtf("PATH", e.toString());
        }
    }

    public void sendMoneySpendingMessage(Collection<Base> bases, Collection<Fleet> fleets, int delta) {
        try {
            GameClient.getInstance().sendMessage(new BasesCreationMessage
                    (Phases.getInstance().side, bases));
            GameClient.getInstance().sendMessage(new FleetsCreationMessage
                    (Phases.getInstance().side, fleets));
            GameClient.getInstance().sendMessage(new PocketChangesMessage
                    (Phases.getInstance().side, delta));
        } catch (IOException e) {
            Log.wtf("PATH", e.toString());
        }
    }

    public void sendMoveFleetMessage() {
        try {
            GameClient.getInstance().sendMessage(new MoveFleetMessage
                    (Phases.getInstance().side));
        } catch (IOException e) {
            Log.wtf("PATH", e.toString());
        }
    }

    public void sendEndFightMessage() {
        try {
            GameClient.getInstance().sendMessage(new EndFightMessage
                    (Phases.getInstance().side));
        } catch (IOException e) {
            Log.wtf("PATH", e.toString());
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
