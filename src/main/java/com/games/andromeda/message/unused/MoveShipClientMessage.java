package com.games.andromeda.message.unused;

import com.games.andromeda.logic.GameObject;
import com.games.andromeda.message.MessageFlags;
import com.games.andromeda.message.SideMessage;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Created by eugeny on 20.10.16.
 */
public class MoveShipClientMessage extends SideMessage implements MessageFlags {



    private float x;
    private float y;
    private int num;

    public MoveShipClientMessage()
    {
    }

    public MoveShipClientMessage(final float x, final float y, final int num, final GameObject.Side side)
    {
        super(side);
        this.x = x;
        this.y = y;
        this.num = num;
    }

    @Override
    protected void onReadTransmissionData(final DataInputStream pDataInputStream) throws IOException {
        super.onReadTransmissionData(pDataInputStream);
        x = pDataInputStream.readFloat();
        y = pDataInputStream.readFloat();
        num = pDataInputStream.readInt();
    }

    @Override
    protected void onWriteTransmissionData(final DataOutputStream pDataOutputStream) throws IOException {
        super.onWriteTransmissionData(pDataOutputStream);
        pDataOutputStream.writeFloat(x);
        pDataOutputStream.writeFloat(y);
        pDataOutputStream.writeInt(num);
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

}
