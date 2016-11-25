package com.games.andromeda.message;

import com.games.andromeda.logic.GameObject;

import org.andengine.extension.multiplayer.protocol.adt.message.client.ClientMessage;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Created by eugeny on 20.10.16.
 */
public class MoveShipClientMessage extends ClientMessage implements MessageFlags{



    private float x;
    private float y;
    private int num;
    private int side;

    public MoveShipClientMessage()
    {
    }

    public MoveShipClientMessage(final float x, final float y, final int num, final int side)
    {
        this.x = x;
        this.y = y;
        this.num = num;
        this.side = side;
    }

    @Override
    protected void onReadTransmissionData(final DataInputStream pDataInputStream) throws IOException {
        x = pDataInputStream.readFloat();
        y = pDataInputStream.readFloat();
        num = pDataInputStream.readInt();
        side = pDataInputStream.readInt();
    }

    @Override
    protected void onWriteTransmissionData(final DataOutputStream pDataOutputStream) throws IOException {
        pDataOutputStream.writeFloat(x);
        pDataOutputStream.writeFloat(y);
        pDataOutputStream.writeInt(num);
        pDataOutputStream.writeInt(side);
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

    public int getNum()
    {
        return num;
    }

    public int getSide()
    {
        return side;
    }
}
