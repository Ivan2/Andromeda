package com.games.andromeda.message;

import com.games.andromeda.logic.GameObject;
import com.games.andromeda.logic.WorldAccessor;

import org.andengine.extension.multiplayer.protocol.adt.message.client.IClientMessage;
import org.andengine.extension.multiplayer.protocol.adt.message.server.IServerMessage;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class RandomEventMessage extends SideMessage implements IClientMessage, IServerMessage, MessageFlags {

    private int money;
    private int fleetNumberWithDestroyedShip;
    private int idOfDestroyedBase;
    public RandomEventMessage() {}

    public RandomEventMessage(GameObject.Side side ,int money, int fleetNumberWithDestroyedShip,int idOfDestroyedBase)
    {
        super(side);
        this.money = money;
        this.fleetNumberWithDestroyedShip = fleetNumberWithDestroyedShip;
        this.idOfDestroyedBase = idOfDestroyedBase;
    }

    @Override
    protected void onReadTransmissionData(DataInputStream pDataInputStream) throws IOException {
        super.onReadTransmissionData(pDataInputStream);
        money = pDataInputStream.readInt();
        fleetNumberWithDestroyedShip = pDataInputStream.readInt();
        idOfDestroyedBase = pDataInputStream.readInt();
    }

    @Override
    protected void onWriteTransmissionData(DataOutputStream pDataOutputStream) throws IOException {
        super.onWriteTransmissionData(pDataOutputStream);
        pDataOutputStream.writeInt(money);
        pDataOutputStream.writeInt(fleetNumberWithDestroyedShip);
        pDataOutputStream.writeInt(idOfDestroyedBase);
    }

    @Override
    public short getFlag() {
        return RANDOM_EVENT_MESSAGE;
    }


    public int getMoney()
    {
        return money;
    }

    public int getFleetNumberWithDestroyedShip()
    {
        return fleetNumberWithDestroyedShip;
    }

    public int getIdOfDestroyedBase()
    {
        return idOfDestroyedBase;
    }
}