package com.games.andromeda.multiplayer;

import android.util.Log;

import com.games.andromeda.Phases;
import com.games.andromeda.logic.Base;
import com.games.andromeda.logic.Fleet;
import com.games.andromeda.logic.WorldAccessor;
import com.games.andromeda.message.MessageFlags;
import com.games.andromeda.message.SetupBasesMessage;
import com.games.andromeda.message.SetupFleetsMessage;
import com.games.andromeda.message.SideMessage;
import com.games.andromeda.ui.UI;

import org.andengine.extension.multiplayer.protocol.adt.message.server.IServerMessage;

import java.io.IOException;
import java.util.ArrayList;
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
                    case END_PHASE_MESSAGE:
                        onEndPhaseMessage();
                        break;
                    case FIGHT_MESSAGE:
                        FightMessage fightMessage = (FightMessage) message;
                        onFightMessage(fightMessage.getFleet1(),fightMessage.getFleet2(),fightMessage.getNumber1(),fightMessage.getNumber2());

                        break;*/
                    case SIDE_MESSAGE:
                        SideMessage sideMessage = (SideMessage) message;
                        Phases.getInstance().side = sideMessage.getSide();
                        break;
                    case SETUP_BASE_MESSAGE:
                        SetupBasesMessage setupBasesMessage = (SetupBasesMessage) message;
                        for (Base base : setupBasesMessage.getBases()) {
                            WorldAccessor.getInstance().setBase(base);
                        }

                        UI.getInstance().getSystemsLayer().repaint();
                        Phases.getInstance().endPhase();
                        break;
                    case SETUP_FLEET_MESSAGE:
                        SetupFleetsMessage setupFleetsMessage = (SetupFleetsMessage) message;
                        int i = 1;
                        for (Fleet fleet : setupFleetsMessage.getFleets()) {
                            WorldAccessor.getInstance().setFleet(fleet, i);
                            i++;
                        }

                        UI.getInstance().getShipsLayer().repaint();
                        UI.getInstance().repaintShipInfo();
                        Phases.getInstance().endPhase();
                        break;
                    //case BASE_CREATION_MESSAGE:
                    //case BASE_DESTRUCTION_MESSAGE:
                    //case FLEET_CREATION_MESSAGE:
                    //case FLEET_DESTRUCTION_MESSAGE:
                    //case POCKET_CHANGE_MESSAGE:
                    //case RANDOM_EVENT_MESSAGE:
                    //case SIDE_NODE_LIST_MESSAGE:
                    //case SIDE_NODE_MESSAGE:

                }


            }
        });
    }

    public void sendSetupBaseMessage(Collection<Base> bases) {
        try {
            GameClient.getInstance().sendMessage(new SetupBasesMessage(
                    Phases.getInstance().side,
                    new ArrayList<>(WorldAccessor.getInstance().getMap().getNodes()),
                    bases
            ));
        } catch (IOException e) {
            Log.wtf("PATH", e.toString());
        }
    }

    public void sendSetupFleetMessage(Collection<Fleet> fleets) {
        try {
            GameClient.getInstance().sendMessage(new SetupFleetsMessage(
                    Phases.getInstance().side,
                    new ArrayList<>(WorldAccessor.getInstance().getMap().getNodes()),
                    fleets
            ));
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

    public void sendEndPhaseMessage() {
        try {
            GameClient.getInstance().sendMessage(new EndPhaseMessage());
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

    private void onEndPhaseMessage()
    {
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
