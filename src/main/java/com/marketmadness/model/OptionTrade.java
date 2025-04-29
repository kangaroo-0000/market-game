package com.marketmadness.model;

public class OptionTrade extends Trade {
    public enum Type {
        CALL,
        PUT
    }

    private final double strike, premium;
    private final Type type;

    public OptionTrade(Type t, double strike, double prem, int qty) {
        super(Side.BUY, -prem, qty);
        this.type = t;
        this.strike = strike;
        this.premium = prem;
    }

    @Override
    public double computePL(double s) {
        double intrinsic = switch (type) {
            case CALL -> Math.max(0, s - strike);
            case PUT -> Math.max(0, strike - s);
        };
        return (intrinsic - premium) * qty();
    }
}