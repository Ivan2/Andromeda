package com.games.andromeda.message;

import org.andengine.extension.multiplayer.protocol.adt.message.client.ClientMessage;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Created by eugeny on 20.10.16.
 */
public class MoveShipClientMessage extends ClientMessage {

    private static final short FLAG_MESSAGE_CLIENT_SHOW = 1;

    private float x;
    private float y;

    public MoveShipClientMessage()
    {
    }

    public MoveShipClientMessage(final float x, final float y)
    {
        this.x = x;
        this.y = y;
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

    public short getFlag()
    {
        return FLAG_MESSAGE_CLIENT_SHOW;
    }
}
