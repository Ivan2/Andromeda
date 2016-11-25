package com.games.andromeda.multiplayer;

import android.util.Log;

import com.games.andromeda.graph.Node;
import com.games.andromeda.logic.Fleet;
import com.games.andromeda.logic.GameObject;
import com.games.andromeda.logic.WorldAccessor;
import com.games.andromeda.message.EndPhaseMessage;
import com.games.andromeda.message.FightMessage;
import com.games.andromeda.message.MessageFlags;
import com.games.andromeda.message.MoveShipClientMessage;
import com.games.andromeda.message.MoveShipServerMessage;
import com.games.andromeda.message.SideMessage;

import org.andengine.extension.multiplayer.protocol.adt.message.server.IServerMessage;

import java.io.IOException;


public class Client  implements MessageFlags{

    public Client(){
        GameClient.getInstance().setMessageReceiver(new GameClient.MessageReceiver() {
            @Override
            public void onMessageReceive(short flag, IServerMessage message) {
                switch (flag) {
                    case FLAG_MESSAGE_SERVER_SHOW:
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
                        break;
                    case SIDE_MESSAGE:
                        SideMessage sideMessage = (SideMessage) message;

                        break;
                    case BASE_CREATION_MESSAGE:
                    case BASE_DESTRUCTION_MESSAGE:
                    case FLEET_CREATION_MESSAGE:
                    case FLEET_DESTRUCTION_MESSAGE:
                    case POCKET_CHANGE_MESSAGE:
                    case RANDOM_EVENT_MESSAGE:
                    case SETUP_BASE_MESSAGE:
                    case SIDE_NODE_LIST_MESSAGE:
                    case SIDE_NODE_MESSAGE:

                }


            }
        });
    }

    public void sendMoveShipMessage(Fleet fleet, int num) {
        try {
            GameObject.Side side = fleet.getSide();
            int s = 0;
            if (side == GameObject.Side.EMPIRE)
                s=1;
            GameClient.getInstance().sendMessage(new MoveShipClientMessage(
                    fleet.getPosition().getX(),
                    fleet.getPosition().getY(),num, s));
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

    public void sendFightMessage(Fleet attackingFleet, Fleet anotherFleet, int number,int secondNum)
    {
        try {
            GameClient.getInstance().sendMessage(new FightMessage(attackingFleet,anotherFleet,number,secondNum));
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
        world.getFleet(firstFleet.getSide(),num).destroyShips(world.getFleet(firstFleet.getSide(),num).getShipCount() - firstFleet.getShipCount(),num);
        world.getFleet(secondFleet.getSide(),num2).destroyShips(world.getFleet(secondFleet.getSide(),num2).getShipCount() - secondFleet.getShipCount(),num2);
    }

    private void setSide(GameObject.Side side)
    {

    }
}
