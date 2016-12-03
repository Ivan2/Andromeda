package com.games.andromeda.message;

import com.games.andromeda.logic.GameObject;

import org.andengine.extension.multiplayer.protocol.adt.message.client.IClientMessage;
import org.andengine.extension.multiplayer.protocol.adt.message.server.IServerMessage;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class RandomEventMessage extends SideMessage implements IClientMessage, IServerMessage, MessageFlags {

    public RandomEventMessage() {}

    public RandomEventMessage(GameObject.Side side) {
        super(side);
    }

    @Override
    protected void onReadTransmissionData(DataInputStream pDataInputStream) throws IOException {
        super.onReadTransmissionData(pDataInputStream);
    }

    @Override
    protected void onWriteTransmissionData(DataOutputStream pDataOutputStream) throws IOException {
        super.onWriteTransmissionData(pDataOutputStream);
    }

    @Override
    public short getFlag() {
        return RANDOM_EVENT_MESSAGE;
    }
}