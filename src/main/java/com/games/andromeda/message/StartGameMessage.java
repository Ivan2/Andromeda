package com.games.andromeda.message;

import org.andengine.extension.multiplayer.protocol.adt.message.server.ServerMessage;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class StartGameMessage extends ServerMessage {

    public static final short FLAG = 6382; //TODO вынести из класса

    @Override
    public short getFlag() {
        return FLAG;
    }

    @Override
    protected void onReadTransmissionData(final DataInputStream pDataInputStream) throws IOException {
    }

    @Override
    protected void onWriteTransmissionData(final DataOutputStream pDataOutputStream) throws IOException {
    }

}
