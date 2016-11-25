package com.games.andromeda.message;

/**
 * Created by 1038844 on 23.11.2016.
 */

public interface MessageFlags {
    public static final short FLAG_MESSAGE_SERVER_CONNECTION_CLOSE = Short.MIN_VALUE;
    public static final short FLAG_MESSAGE_SERVER_CONNECTION_ESTABLISHED = FLAG_MESSAGE_SERVER_CONNECTION_CLOSE + 1;
    public static final short FLAG_MESSAGE_SERVER_CONNECTION_REJECTED_PROTOCOL_MISSMATCH = FLAG_MESSAGE_SERVER_CONNECTION_ESTABLISHED + 1;
    public static final short FLAG_MESSAGE_SERVER_CONNECTION_PONG = FLAG_MESSAGE_SERVER_CONNECTION_REJECTED_PROTOCOL_MISSMATCH + 1;
    public static final short FLAG_MESSAGE_CLIENT_SHOW = FLAG_MESSAGE_SERVER_CONNECTION_PONG  + 1;
    public static final short FLAG_MESSAGE_SERVER_SHOW = FLAG_MESSAGE_CLIENT_SHOW + 1;
    public static final short START_GAME_MESSAGE = FLAG_MESSAGE_SERVER_SHOW + 1;
    public static final short BASE_CREATION_MESSAGE = START_GAME_MESSAGE + 1;
    public static final short BASE_DESTRUCTION_MESSAGE = BASE_CREATION_MESSAGE + 1;
    public static final short FIGHT_MESSAGE = BASE_DESTRUCTION_MESSAGE + 1;
    public static final short FLEET_CREATION_MESSAGE = FIGHT_MESSAGE + 1;
    public static final short FLEET_DESTRUCTION_MESSAGE = FLEET_CREATION_MESSAGE + 1;
    public static final short MOVE_SHIP_MESSAGE = FLEET_DESTRUCTION_MESSAGE + 1;
    public static final short POCKET_CHANGE_MESSAGE = MOVE_SHIP_MESSAGE;
    public static final short RANDOM_EVENT_MESSAGE = POCKET_CHANGE_MESSAGE + 1;
    public static final short SETUP_BASE_MESSAGE = RANDOM_EVENT_MESSAGE + 1;
    public static final short SETUP_FLEET_MESSAGE = SETUP_BASE_MESSAGE + 1;
    public static final short SIDE_MESSAGE = SETUP_FLEET_MESSAGE + 1;
    public static final short SIDE_NODE_LIST_MESSAGE = SIDE_MESSAGE + 1;
    public static final short SIDE_NODE_MESSAGE = SIDE_NODE_LIST_MESSAGE + 1;
    public static final short END_PHASE_MESSAGE = SIDE_NODE_MESSAGE + 1;
}
