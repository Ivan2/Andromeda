package com.games.andromeda.message;

import com.games.andromeda.logic.Base;

public class BaseCreationMessage extends SideNodeMessage {
    public BaseCreationMessage() {
    }

    public BaseCreationMessage(Base base) {
        super(base.getSide(), base.getNode());
    }

}