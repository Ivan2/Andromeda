package com.games.andromeda.message;

import org.andengine.extension.multiplayer.protocol.adt.message.server.ServerMessage;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Created by eugeny on 20.10.16.
 */
public class MoveShipServerMessage extends ServerMessage {

    private static final short FLAG_MESSAGE_SERVER_SHOW = 1;

    private float x;
    private float y;

    public MoveShipServerMessage() {

    }

    public MoveShipServerMessage(final float x, final float y) {
        this.x = x;
        this.y = y;
    }

    public void set(final float x, final float y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public short getFlag() {
        return FLAG_MESSAGE_SERVER_SHOW;
    }

    @Override
    protected void onReadTransmissionData(final DataInputStream pDataInputStream) throws IOException {
        x = pDataInputStream.readFloat();
        y = pDataInputStream.readFloat();
    }

    @Override
    protected void onWriteTransmissionData(final DataOutputStream pDataOutputStream) throws IOException {
        pDataOutputStream.writeFloat(x);
        pDataOutputStream.writeFloat(y);
    }

    public float getX()
    {
        return x;
    }

    public float getY()
    {
        return y;
    }
}
