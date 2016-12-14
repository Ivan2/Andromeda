package com.games.andromeda.message;

import com.games.andromeda.logic.GameObject;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class EndFightMessage extends SideMessage implements MessageFlags {

    private boolean destroyed;
    public EndFightMessage() {}

    public EndFightMessage(GameObject.Side side, boolean destroyed) {

        super(side);
        this.destroyed = destroyed;
    }

    @Override
    protected void onReadTransmissionData(DataInputStream pDataInputStream) throws IOException {
        super.onReadTransmissionData(pDataInputStream);
        destroyed = pDataInputStream.readBoolean();
    }

    @Override
    protected void onWriteTransmissionData(DataOutputStream pDataOutputStream) throws IOException {
        super.onWriteTransmissionData(pDataOutputStream);
        pDataOutputStream.writeBoolean(destroyed);
    }

    @Override
    public short getFlag() {
        return END_FIGHT_MESSAGE;
    }

    public boolean getDestroyed()
    {
        return destroyed;
    }

}