package com.marketmadness.network;

import jakarta.websocket.*;
import java.net.URI;
import java.util.function.Consumer;

/** Connects to ws://host:port/ws and pipes inbound text to handler. */
public class MMWebSocketClient {

    private Session session;

    public MMWebSocketClient(String uri, Consumer<String> onText){
        try {
            WebSocketContainer c = ContainerProvider.getWebSocketContainer();
            c.connectToServer(new Endpoint(){
                @Override public void onOpen(Session s, EndpointConfig cfg){
                    session = s;
                    s.addMessageHandler(String.class, onText::accept);
                }
                @Override public void onError(Session s, Throwable t){
                    t.printStackTrace();
                }
            }, URI.create(uri));                 
        } catch (Exception e){ e.printStackTrace(); }
    }

    public void send(String txt){
        if(session != null && session.isOpen()){
            session.getAsyncRemote().sendText(txt);
        }
    }
}
