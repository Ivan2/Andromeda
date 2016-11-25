package com.games.andromeda.message;

import com.games.andromeda.logic.Base;

public class BaseCreationMessage extends SideNodeMessage implements MessageFlags{
    public BaseCreationMessage() {
    }

    public BaseCreationMessage(Base base) {
        super(base.getSide(), base.getNode());
    }
    @Override
    public short getFlag()
    {
        return BASE_CREATION_MESSAGE;
    }
}