package com.marketmadness.network;

import jakarta.websocket.*;
import jakarta.websocket.server.ServerEndpoint;
import java.util.concurrent.CopyOnWriteArraySet;

/** Holds sessions and lets GameController broadcast. */
@ServerEndpoint("/")                         // root of the context
public class MMWebSocketServer {

    private static final CopyOnWriteArraySet<Session> sessions =
            new CopyOnWriteArraySet<>();

    @OnOpen  public void onOpen (Session s){ sessions.add(s); }
    @OnClose public void onClose(Session s){ sessions.remove(s); }
    @OnError public void onError(Session s, Throwable t){ t.printStackTrace(); }

    public static void broadcast(String msg){
        sessions.forEach(s -> s.getAsyncRemote().sendText(msg));
    }
}
