package com.games.andromeda.message;

import com.games.andromeda.logic.GameObject;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class EndPhaseMessage extends SideMessage implements MessageFlags {

    private boolean destroyed;
    public EndPhaseMessage() {}

    public EndPhaseMessage(GameObject.Side side, boolean destroyed) {

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
        return END_PHASE_MESSAGE;
    }

    public boolean getDestroyed()
    {
        return destroyed;
    }

}