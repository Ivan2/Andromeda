package com.games.andromeda.message;

import com.games.andromeda.logic.GameObject;

public class EndGameMessage extends SideMessage{
    public EndGameMessage(){}

    public EndGameMessage(GameObject.Side side)
    {
        super(side);
    }

    @Override
    public short getFlag()
    {
        return END_GAME_MESSAGE;
    }
}
