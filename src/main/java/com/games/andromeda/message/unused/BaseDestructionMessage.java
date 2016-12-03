package com.games.andromeda.message.unused;

import com.games.andromeda.message.BasesCreationMessage;
import com.games.andromeda.message.MessageFlags;

public class BaseDestructionMessage extends BasesCreationMessage implements MessageFlags {
    @Override
    public short getFlag()
    {
        return BASE_DESTRUCTION_MESSAGE;
    }
}