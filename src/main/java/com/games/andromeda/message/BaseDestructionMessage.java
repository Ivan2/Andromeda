package com.games.andromeda.message;

public class BaseDestructionMessage extends BaseCreationMessage implements MessageFlags{
    @Override
    public short getFlag()
    {
        return BASE_DESTRUCTION_MESSAGE;
    }
}