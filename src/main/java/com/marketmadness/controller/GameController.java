package com.marketmadness.controller;

import com.google.gson.Gson;
import com.marketmadness.model.*;
import com.marketmadness.network.MMWebSocketServer;
import com.marketmadness.persistence.DatabaseManager;

import java.util.function.Consumer;

/**
 * Glue between GUI panels and the core MarketState.
 * • Accepts trades, forwards them to MarketState, stores them in SQLite.
 * • Ticks every 15 s, broadcasts P/L to the WebSocket leaderboard,
 *   and notifies a GUI listener.
 */
public class GameController {

    private final MarketState     state     = new MarketState();
    private final DatabaseManager db        = new DatabaseManager();
    private final Gson            gson      = new Gson();
    private final String          playerId;

    private Consumer<TickResult> listener = tr -> { };

    public GameController(String playerId) {
        this.playerId = playerId;
    }

    /* ------------ GUI hooks ------------ */

    /** GUI registers a callback to repaint dice & chart each tick */
    public void onTick(Consumer<TickResult> l) { listener = l; }

    /** Market-maker submits a market */
    public void submitMarket(double mid, double spr) { state.submitMarket(mid, spr); }

    /** Participant or maker trades; we persist it too */
    public void trade(Trade t) {
        state.addTrade(t);
        db.saveTrade(t);                   // ← write to SQLite
    }

    public double bid()   { return state.bid(); }
    public double offer() { return state.offer(); }

    /** Called every 15 s from MainFrame timer */
    public void nextRound() {
        TickResult tr = state.nextRound();
        listener.accept(tr);               // update GUI

        // push cumulative P/L to all connected clients
        MMWebSocketServer.broadcast(
                gson.toJson(new ScoreMsg(playerId, tr.makerCumPL())));
    }

    /* DTO reused by leaderboard */
    private record ScoreMsg(String player, double pl) { }
}
