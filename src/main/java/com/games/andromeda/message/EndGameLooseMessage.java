package com.games.andromeda.message;

import com.games.andromeda.logic.GameObject;

/**
 * Created by 1038844 on 20.12.2016.
 */

public class EndGameLooseMessage extends EndGameMessage {

    public EndGameLooseMessage(){}

    public EndGameLooseMessage(GameObject.Side side)
    {
        super(side);
    }

    @Override
    public short getFlag()
    {
        return END_GAME_LOOSE_MESSAGE;
    }
}
