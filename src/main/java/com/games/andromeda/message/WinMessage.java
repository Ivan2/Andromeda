package com.games.andromeda.message;

import com.games.andromeda.logic.GameObject;

/**
 * Created by 1038844 on 08.12.2016.
 */

public class WinMessage extends SideMessage{
    public WinMessage(){}

    public WinMessage(GameObject.Side side)
    {
        super(side);
    }

    @Override
    public short getFlag()
    {
        return WIN_MESSAGE;
    }
}
