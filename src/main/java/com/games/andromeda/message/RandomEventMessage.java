package com.games.andromeda.message;

import org.andengine.extension.multiplayer.protocol.adt.message.Message;
import org.andengine.extension.multiplayer.protocol.adt.message.client.IClientMessage;
import org.andengine.extension.multiplayer.protocol.adt.message.server.IServerMessage;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;


public class RandomEventMessage extends Message implements IClientMessage, IServerMessage, MessageFlags {
    @Override
    protected void onReadTransmissionData(DataInputStream pDataInputStream) throws IOException {

    }

    @Override
    protected void onWriteTransmissionData(DataOutputStream pDataOutputStream) throws IOException {

    }

    @Override
    public short getFlag() {
        return RANDOM_EVENT_MESSAGE;
    }
}