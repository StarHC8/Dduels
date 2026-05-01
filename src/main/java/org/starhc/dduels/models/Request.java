package org.starhc.dduels.models;

import java.util.UUID;

public class Request {

    private UUID sender;
    private DuelSession session;


    public Request(UUID sender, DuelSession session) {
        this.sender = sender;
        this.session = session;
    }

    public UUID getSender() {
        return sender;
    }

    public DuelSession getDuelSession() {
        return session;
    }
}
