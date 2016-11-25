package com.games.andromeda.message;

public class SetupBasesMessage extends SideNodeListMessage{

    @Override
    public short getFlag()
    {
        return SETUP_BASE_MESSAGE;
    }
}
