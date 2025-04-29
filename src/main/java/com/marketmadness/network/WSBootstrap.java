package com.marketmadness.network;

import org.glassfish.tyrus.server.Server;

/** Starts the embedded WebSocket server (context /ws). */
public class WSBootstrap {

    public static void start(int port) {

        Thread t = new Thread(() -> {

            Server server = new Server(
                    "0.0.0.0",
                    port,
                    "/ws",                    // context root **/ws**
                    null,
                    MMWebSocketServer.class); // endpoint path "/"

            try   { server.start(); Thread.currentThread().join(); }
            catch (Exception e){ e.printStackTrace(); }
            finally { server.stop(); }

        }, "WS-Server");

        t.setDaemon(true);
        t.start();
    }
}
