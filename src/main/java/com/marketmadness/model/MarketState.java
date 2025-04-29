package com.marketmadness.model;

import java.util.*;

public class MarketState {
    private final DiceSet dice = new DiceSet();
    private double midpoint = 100, spread = 4;
    private double makerCumPL = 0;
    private final List<Trade> trades = new ArrayList<>();
    private int bought = 0, sold = 0;

    public synchronized void submitMarket(double mid, double spr) {
        midpoint = mid;
        spread = spr;
        bought = sold = 0;
        trades.clear();
    }

    public synchronized void addTrade(Trade t) {
        trades.add(t);
        if (t.side() == Side.BUY) {
            bought += t.qty();
        } else {
            sold += t.qty();
        }
    }

    public synchronized TickResult nextRound() {
        dice.roll();
        int sum = dice.realise();
        double settle = switch (sum) {
            case 2, 3, 4, 5, 6 -> midpoint - 1;
            case 7 -> midpoint;
            default -> midpoint + 1;
        };
        double partPL = trades.stream().mapToDouble(t -> t.computePL(settle)).sum();
        int matched = Math.min(bought, sold);
        double spreadRev = matched * spread;
        int net = bought - sold;
        double invPL = net * (settle - midpoint);
        double makerPL = spreadRev - invPL;
        makerCumPL += makerPL;
        trades.clear();
        return new TickResult(sum, settle, makerPL, partPL, makerCumPL, dice.visible());
    }

    public synchronized double bid() {
        return midpoint - spread / 2;
    }

    public synchronized double offer() {
        return midpoint + spread / 2;
    }
}