package com.marketmadness.model;

public class Trade {
    private final Side side;
    private final double price;
    private final int qty;

    public Trade(Side side, double price, int qty) {
        this.side = side;
        this.price = price;
        this.qty   = qty;
    }

    public Side   side()  { return side; }
    public double price() { return price; }
    public int    qty()   { return qty;  }

    public double computePL(double settle) {
        return side == Side.BUY ? (settle - price) * qty
                : (price - settle) * qty;
    }
}