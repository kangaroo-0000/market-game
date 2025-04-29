// File: src/main/java/com/marketmadness/model/MarketState.java
package com.marketmadness.model;

import java.util.*;
import com.marketmadness.model.DiceSet;
import com.marketmadness.model.TickResult;


public class MarketState {
    private final DiceSet dice = new DiceSet();

    private double midpoint   = 10.5;
    private double spread     = 1.0;
    private double makerCumPL = 0.0;

    // accumulate all trades this round
    private final java.util.List<Trade> trades = new java.util.ArrayList<>();

    /** Called when the maker hits “Submit Market” */
    public synchronized void submitMarket(double m, double s) {
        this.midpoint = m;
        this.spread   = s;
        trades.clear();     // reset any leftover participant orders
    }

    /** Called whenever either panel executes a trade */
    public synchronized void addTrade(Trade t) {
        trades.add(t);
    }

    /**
     * Called every 15 s.
     * Rolls the dice, computes EV, simulates maker + participant P/L,
     * then returns all the data needed by the GUI.
     */
    public synchronized TickResult nextRound() {
        // 1) roll & reveal dice
        dice.roll();
        int sum = dice.realise();           // also flips all hidden dice

        // 2) get the visible faces for display
        int[] vis = dice.visible();

        // 3) compute participant EV from visible + hidden
        long hiddenCount = Arrays.stream(vis).filter(v -> v == 0).count();
        double visibleSum = Arrays.stream(vis).sum();
        double expectedSum = visibleSum + hiddenCount * 3.5;

        // 4) current quotes
        double bid   = midpoint - spread/2;
        double offer = midpoint + spread/2;

        // 5) maker’s simulated 100 traders
        int TOTAL = 100;
        double edgeBuy  = clamp((offer - expectedSum) / spread,  0, 1);
        double edgeSell = clamp((expectedSum - bid)   / spread,  0, 1);
        int buyers  = (int)Math.round(edgeBuy  * TOTAL);
        int sellers = (int)Math.round(edgeSell * TOTAL);
        int matched  = Math.min(buyers, sellers);
        int imbalance = buyers - sellers;

        // 6) P/L calculations
        double spreadRev       = matched * spread;
        double inventoryPL     = imbalance * (sum - midpoint);
        double makerPLthisRnd  = spreadRev + inventoryPL;
        makerCumPL += makerPLthisRnd;

        // 7) participant P/L = sum of computePL(realizedPrice) over all trades
        double partPLthisRnd = trades.stream()
                .mapToDouble(t -> t.computePL(sum))
                .sum();

        // 8) clear for next round
        trades.clear();

        // 9) package everything for GUI
        return new TickResult(
                sum,                  // diceSum
                sum,                  // realizedPrice
                makerPLthisRnd,       // maker P/L this round
                partPLthisRnd,        // participant P/L this round
                makerCumPL,           // cumulative maker P/L
                vis,                   // the three visible ints (zeros = previously hidden)
                false,
                false,
                buyers,
                sellers,
                matched,
                spreadRev,
                inventoryPL
        );
    }

    public synchronized double bid()   { return midpoint - spread/2; }
    public synchronized double offer() { return midpoint + spread/2; }

    private double clamp(double x, double lo, double hi) {
        return Math.max(lo, Math.min(hi, x));
    }
}
