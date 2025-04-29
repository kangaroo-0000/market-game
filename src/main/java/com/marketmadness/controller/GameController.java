// File: src/main/java/com/marketmadness/controller/GameController.java
package com.marketmadness.controller;

import com.google.gson.Gson;
import com.marketmadness.model.*;
import com.marketmadness.network.MMWebSocketServer;
import com.marketmadness.persistence.DatabaseManager;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

public class GameController {
    private final MarketState                state   = new MarketState();
    private final DatabaseManager            db      = new DatabaseManager();
    private final Gson                       gson    = new Gson();
    private final String                     pid;
    private final List<Consumer<TickResult>> listeners = new CopyOnWriteArrayList<>();

    // these track whether YOU acted last round
    private boolean makerSubmitted     = false;
    private boolean participantTraded  = false;

    public GameController(String playerId) {
        this.pid = playerId;
    }

    public void onTick(Consumer<TickResult> l) {
        listeners.add(l);
    }

    public void submitMarket(double m, double s) {
        state.submitMarket(m, s);
        makerSubmitted = true;
    }

    public void trade(Trade t) {
        state.addTrade(t);
        db.saveTrade(t);
        participantTraded = true;
    }

    public double bid()   { return state.bid(); }
    public double offer() { return state.offer(); }

    public void nextRound() {
        // 1) get the raw result (flags all false here)
        TickResult raw = state.nextRound();

        // 2) build a new TickResult with our flags inserted
        TickResult tr = new TickResult(
                raw.diceSum(),               // int
                raw.realizedPrice(),         // double
                raw.makerPLthisRound(),      // double
                raw.partPLthisRound(),       // double
                raw.makerCumPL(),            // double
                raw.visibleDice(),           // int[]
                makerSubmitted,              // boolean → did maker act?
                participantTraded,           // boolean → did participant act?
                raw.buyers(),                // int
                raw.sellers(),               // int
                raw.matched(),               // int
                raw.spreadRevenue(),         // double
                raw.inventoryPL()            // double
        );

        // 3) reset your action-tracking flags for the next round
        makerSubmitted    = false;
        participantTraded = false;

        // 4) notify all listeners
        for (var l : listeners) {
            l.accept(tr);
        }

        // 5) broadcast cumulative maker P/L
        MMWebSocketServer.broadcast(
                gson.toJson(new ScoreMsg(pid, tr.makerCumPL()))
        );
    }


    private record ScoreMsg(String player, double pl) {}
}
