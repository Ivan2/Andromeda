package com.games.andromeda.multiplayer;

import android.util.Log;

import com.games.andromeda.graph.Node;
import com.games.andromeda.logic.Fleet;
import com.games.andromeda.logic.GameObject;
import com.games.andromeda.logic.WorldAccessor;
import com.games.andromeda.message.MoveShipClientMessage;
import com.games.andromeda.message.MoveShipServerMessage;

import org.andengine.extension.multiplayer.protocol.adt.message.server.IServerMessage;

import java.io.IOException;

public class Client {

    public Client() {
        GameClient.getInstance().setMessageReceiver(new GameClient.MessageReceiver() {
            @Override
            public void onMessageReceive(short flag, IServerMessage message) {
                switch (flag) {
                    //TODO flags
                }
                MoveShipServerMessage moveShipServerMessage = (MoveShipServerMessage) message;
                moveShip(moveShipServerMessage.getX(), moveShipServerMessage.getY());
            }
        });
    }

    public void sendMoveShipMessage(Fleet fleet) {
        try {
            GameClient.getInstance().sendMessage(new MoveShipClientMessage(
                    fleet.getPosition().getX(),
                    fleet.getPosition().getY()));
        } catch (IOException e) {
            Log.wtf("PATH", e.toString());
        }
    }

    private void moveShip(float x, float y) {
        Node node = new Node(x, y);
        try {
            WorldAccessor.getInstance().moveFleet(GameObject.Side.EMPIRE, 1, node);
        } catch (Exception e) {
            e.printStackTrace();
            Log.wtf("my stupid exception: ", e.toString());
        }
    }
}
