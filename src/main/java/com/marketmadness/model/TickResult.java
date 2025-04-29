package com.marketmadness.model;

public record TickResult(
        int     diceSum,
        double  realizedPrice,
        double  makerPLthisRound,
        double  partPLthisRound,
        double  makerCumPL,
        int[]   visibleDice,
        boolean makerActive,         // true if user clicked “Submit Market” last round
        boolean participantActive,    // true if user traded (Buy/Sell/Call/Put) last round
        int     buyers,
        int     sellers,
        int     matched,
        double  spreadRevenue,
        double  inventoryPL
) { }
