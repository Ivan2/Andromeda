package com.games.andromeda.message;

public class SetupFleetsMessage extends SideNodeListMessage {

    @Override
    public short getFlag()
    {
        return  SETUP_FLEET_MESSAGE;
    }
}