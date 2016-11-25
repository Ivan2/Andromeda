package com.games.andromeda.message;

import com.games.andromeda.logic.GameObject;

import org.andengine.extension.multiplayer.protocol.adt.message.Message;
import org.andengine.extension.multiplayer.protocol.adt.message.client.IClientMessage;
import org.andengine.extension.multiplayer.protocol.adt.message.server.IServerMessage;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Created by 1038844 on 24.11.2016.
 */

public class EndPhaseMessage extends SideMessage {
    private int phaseNumber;

    public EndPhaseMessage(){
        phaseNumber = -1;
    }

    public EndPhaseMessage(GameObject.Side side,int phaseNumber)
    {
        super(side);
        this.phaseNumber = phaseNumber;
    }

    @Override
    protected void onReadTransmissionData(DataInputStream pDataInputStream) throws IOException {
        super.onReadTransmissionData(pDataInputStream);
        phaseNumber = pDataInputStream.readInt();
    }

    @Override
    protected void onWriteTransmissionData(DataOutputStream pDataOutputStream) throws IOException {
        super.onWriteTransmissionData(pDataOutputStream);
        pDataOutputStream.writeInt(phaseNumber);
    }

    @Override
    public short getFlag() {
        return END_PHASE_MESSAGE;
    }

    public int getPhaseNumber()
    {
        return phaseNumber;
    }
}
