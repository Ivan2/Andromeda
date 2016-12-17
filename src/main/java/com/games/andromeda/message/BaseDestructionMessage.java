package com.games.andromeda.message;

import com.games.andromeda.logic.GameObject;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class BaseDestructionMessage extends SideMessage implements MessageFlags {
    private int id;

    public BaseDestructionMessage(){}

    public BaseDestructionMessage(GameObject.Side side, int nodeId) {
        super(side);
        id = nodeId;
    }

    @Override
    protected void onReadTransmissionData(DataInputStream pDataInputStream) throws IOException {
        super.onReadTransmissionData(pDataInputStream);
        id = pDataInputStream.readInt();
    }

    @Override
    protected void onWriteTransmissionData(DataOutputStream pDataOutputStream) throws IOException {
        super.onWriteTransmissionData(pDataOutputStream);
        pDataOutputStream.writeInt(id);
    }

    @Override
    public short getFlag()
    {
        return BASE_DESTRUCTION_MESSAGE;
    }

    public int getNodeId() {
        return id;
    }
}