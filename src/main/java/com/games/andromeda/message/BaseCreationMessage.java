package com.games.andromeda.message;

import com.games.andromeda.logic.Base;
import com.games.andromeda.logic.WorldAccessor;

public class BaseCreationMessage extends SideNodeMessage implements MessageFlags{
    public BaseCreationMessage() {
    }

    public BaseCreationMessage(Base base) {
        super(base.getSide(), WorldAccessor.getInstance().getNodes().get(base.getNodeID()));
    }
    @Override
    public short getFlag()
    {
        return BASE_CREATION_MESSAGE;
    }
}