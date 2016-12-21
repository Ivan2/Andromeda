package com.games.andromeda.message;

public interface MessageFlags {

    short FLAG_MESSAGE_SERVER_CONNECTION_CLOSE = Short.MIN_VALUE;
    short START_GAME_MESSAGE = FLAG_MESSAGE_SERVER_CONNECTION_CLOSE + 1;

    short SIDE_MESSAGE = START_GAME_MESSAGE + 1;
    short SIDE_NODE_LIST_MESSAGE = SIDE_MESSAGE + 1;

    short SETUP_BASE_MESSAGE = SIDE_NODE_LIST_MESSAGE + 1;
    short SETUP_FLEET_MESSAGE = SETUP_BASE_MESSAGE + 1;
    short RANDOM_EVENT_MESSAGE = SETUP_FLEET_MESSAGE + 1;
    short POCKET_CHANGE_MESSAGE = RANDOM_EVENT_MESSAGE + 1;
    short BASES_CREATION_MESSAGE = POCKET_CHANGE_MESSAGE + 1;
    short MOVE_FLEET_MESSAGE = BASES_CREATION_MESSAGE + 1;
    short FLEETS_CREATION_MESSAGE = MOVE_FLEET_MESSAGE + 1;
    short END_PHASE_MESSAGE = FLEETS_CREATION_MESSAGE + 1;

    short TIME_OVER_MESSAGE = END_PHASE_MESSAGE + 1;
    short TIME_ALMOST_OVER_MESSAGE = TIME_OVER_MESSAGE + 1;

    //unused

    //public static final short FLAG_MESSAGE_SERVER_CONNECTION_ESTABLISHED = FLAG_MESSAGE_SERVER_CONNECTION_CLOSE + 1;
    //public static final short FLAG_MESSAGE_SERVER_CONNECTION_REJECTED_PROTOCOL_MISSMATCH = FLAG_MESSAGE_SERVER_CONNECTION_ESTABLISHED + 1;
    //public static final short FLAG_MESSAGE_SERVER_CONNECTION_PONG = FLAG_MESSAGE_SERVER_CONNECTION_REJECTED_PROTOCOL_MISSMATCH + 1;

    short BASE_DESTRUCTION_MESSAGE = TIME_ALMOST_OVER_MESSAGE + 1;
    short FLEET_STATE_MESSAGE = BASE_DESTRUCTION_MESSAGE + 1;
    short FLEET_DESTRUCTION_MESSAGE = FLEET_STATE_MESSAGE + 1;
    short SIDE_NODE_MESSAGE = FLEET_DESTRUCTION_MESSAGE + 1;
    short FLAG_MESSAGE_CLIENT_SHOW = SIDE_NODE_MESSAGE  + 1;
    short FLAG_MESSAGE_SERVER_SHOW = FLAG_MESSAGE_CLIENT_SHOW + 1;
    short END_GAME_MESSAGE = FLAG_MESSAGE_SERVER_SHOW  + 1;
    short END_GAME_LOOSE_MESSAGE = END_GAME_MESSAGE + 1;

}
