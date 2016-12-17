package com.games.andromeda.message;

import com.games.andromeda.logic.GameObject;

import org.andengine.extension.multiplayer.protocol.adt.message.Message;
import org.andengine.extension.multiplayer.protocol.adt.message.client.IClientMessage;
import org.andengine.extension.multiplayer.protocol.adt.message.server.IServerMessage;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class SideMessage extends Message implements IClientMessage, IServerMessage,MessageFlags {
    private GameObject.Side side;

    public SideMessage() {
    }

    public SideMessage(GameObject.Side side) {
        this.side = side;
    }

    @Override
    protected void onReadTransmissionData(DataInputStream pDataInputStream) throws IOException {
        side = pDataInputStream.readBoolean() ? GameObject.Side.FEDERATION: GameObject.Side.EMPIRE;
    }

    @Override
    protected void onWriteTransmissionData(DataOutputStream pDataOutputStream) throws IOException {
        pDataOutputStream.writeBoolean(side == GameObject.Side.FEDERATION);
    }

    @Override
    public short getFlag() {
        return SIDE_MESSAGE;
    }

    public GameObject.Side getSide() {
        return side;
    }
}
