package com.games.andromeda.message;

import com.games.andromeda.logic.GameObject;

import org.andengine.extension.multiplayer.protocol.adt.message.server.ServerMessage;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Created by eugeny on 20.10.16.
 */
public class MoveShipServerMessage extends ServerMessage implements MessageFlags {



    private float x;
    private float y;
    private int num;
    private int side;

    public MoveShipServerMessage() {

    }

    public MoveShipServerMessage(final float x, final float y, final int num, final int side)
    {
        this.x = x;
        this.y = y;
        this.num = num;
        this.side = side;
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

    public int getNum()
    {
        return num;
    }

    public GameObject.Side getSide()
    {
        if (side == 1)
            return GameObject.Side.EMPIRE;
        else return GameObject.Side.FEDERATION;
    }
}
