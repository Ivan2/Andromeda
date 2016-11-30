package com.games.andromeda.message;

import com.games.andromeda.graph.Node;
import com.games.andromeda.logic.Base;
import com.games.andromeda.logic.Fleet;
import com.games.andromeda.logic.GameObject;
import com.games.andromeda.logic.Pocket;

import org.andengine.extension.multiplayer.protocol.adt.message.Message;
import org.andengine.extension.multiplayer.protocol.adt.message.client.IClientMessage;
import org.andengine.extension.multiplayer.protocol.adt.message.server.IServerMessage;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class FightMessage extends SideMessage implements IClientMessage, IServerMessage, MessageFlags{
    private Fleet fleet1;
    private Fleet fleet2;
    private int number1;
    private int number2;

    public FightMessage() {}
    public FightMessage(GameObject.Side side, final Fleet fleet1, final Fleet fleet2, int number1, int number2)
    {
        super(side);
        this.fleet1 = fleet1;
        this.fleet2 = fleet2;
        this.number1 = number1;
        this.number2 = number2;
    }

    @Override
    protected void onReadTransmissionData(DataInputStream pDataInputStream) throws IOException {
        super.onReadTransmissionData(pDataInputStream);
        float   fleet1X = pDataInputStream.readFloat(),
                fleet1Y = pDataInputStream.readFloat();
        int fleet1Side = pDataInputStream.readInt(),
            fleet1ShipCount = pDataInputStream.readInt(),
            fleet1Number = pDataInputStream.readInt();
        float   fleet2X = pDataInputStream.readFloat(),
                fleet2Y = pDataInputStream.readFloat();
        int fleet2Side = pDataInputStream.readInt(),
                fleet2ShipCount = pDataInputStream.readInt(),
                fleet2Number = pDataInputStream.readInt();
        GameObject.Side side;
        number1 = fleet1Number;
        number2 = fleet2Number;
        if (fleet1Side == 1)
            side = GameObject.Side.EMPIRE;
        else side = GameObject.Side.FEDERATION;
        try {
            Base b = new Base(side,new Node(fleet1X,fleet1Y));
            Pocket pocket = new Pocket(side);
            pocket.increase(100500);
            fleet1 = Fleet.buy(fleet1ShipCount,b,pocket);
            if (fleet2Side == 1)
                side = GameObject.Side.EMPIRE;
            else side = GameObject.Side.FEDERATION;
            b = new Base(side,new Node(fleet2X,fleet2Y));
            pocket = new Pocket(side);
            pocket.increase(100500);
            fleet2 = Fleet.buy(fleet2ShipCount,b,pocket);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onWriteTransmissionData(DataOutputStream pDataOutputStream) throws IOException {
        super.onWriteTransmissionData(pDataOutputStream);
        pDataOutputStream.writeFloat(fleet1.getPosition().getX());
        pDataOutputStream.writeFloat(fleet1.getPosition().getY());
        if (fleet1.getSide() == GameObject.Side.EMPIRE)
            pDataOutputStream.writeInt(1);
        else
            pDataOutputStream.writeInt(0);
        pDataOutputStream.writeInt(fleet1.getShipCount());
        pDataOutputStream.writeInt(number1);
        pDataOutputStream.writeFloat(fleet2.getPosition().getX());
        pDataOutputStream.writeFloat(fleet2.getPosition().getY());
        if (fleet2.getSide() == GameObject.Side.EMPIRE)
            pDataOutputStream.writeInt(1);
        else
            pDataOutputStream.writeInt(0);
        pDataOutputStream.writeInt(fleet2.getShipCount());
        pDataOutputStream.writeInt(number2);
    }

    @Override
    public short getFlag() {
        return FIGHT_MESSAGE;
    }

    public Fleet getFleet1()
    {
        return fleet1;
    }

    public Fleet getFleet2()
    {
        return fleet2;
    }

    public int getNumber1(){
        return number1;
    }

    public int getNumber2(){
        return number2;
    }
}