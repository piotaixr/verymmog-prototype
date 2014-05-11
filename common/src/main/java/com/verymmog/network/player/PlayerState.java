package com.verymmog.network.player;

public enum PlayerState {
    CREATED,
    WAITING_IDENTIFICATION_CONFIRMATION,
    IDENTIFIED_WAITING_LEADER,
    IDENTIFIED_WAITING_POSITION,
    READY,
    ERROR,
    CLOSED
}
