// File: src/main/java/com/marketmadness/gui/ParticipantPanel.java
package com.marketmadness.gui;

import com.marketmadness.controller.GameController;
import com.marketmadness.model.OptionTrade;
import com.marketmadness.model.Side;
import com.marketmadness.model.Trade;
import com.marketmadness.model.TickResult;

import javax.swing.*;
import java.awt.*;
import java.util.concurrent.ThreadLocalRandom;

public class ParticipantPanel extends JPanel {

    private final JLabel bidLbl   = new JLabel("-");
    private final JLabel offerLbl = new JLabel("-");
    private final JTextField qty  = new JTextField("10");
    private final MessageLog log  = new MessageLog("Participant Log");

    public ParticipantPanel(GameController gc) {
        setLayout(new GridBagLayout());
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(5,5,5,5);
        g.fill   = GridBagConstraints.HORIZONTAL;
        g.weightx= 1.0;

        // --- labels & fields ---
        addLabel("Bid",   0, 0, g); g.gridx = 1; add(bidLbl,   g);
        addLabel("Offer", 0, 1, g); g.gridx = 1; add(offerLbl, g);
        addLabel("Qty",   0, 2, g); g.gridx = 1; add(qty,      g);

        // --- buttons ---
        JButton buy  = new JButton("Buy");
        JButton sell = new JButton("Sell");
        JButton call = new JButton("Call");
        JButton put  = new JButton("Put");

        g.gridy = 3; g.gridx = 0; add(buy,  g);
        g.gridx = 1; add(sell, g);
        g.gridy = 4; g.gridx = 0; add(call, g);
        g.gridx = 1; add(put,  g);

        // --- message log spans two cols ---
        g.gridy = 5; g.gridx = 0; g.gridwidth = 2;
        add(log, g);

        // --- wire trade actions ---
        buy .addActionListener(e -> placeTrade(gc, Side.BUY));
        sell.addActionListener(e -> placeTrade(gc, Side.SELL));
        call.addActionListener(e -> placeOption(gc, OptionTrade.Type.CALL));
        put .addActionListener(e -> placeOption(gc, OptionTrade.Type.PUT));

        // --- 1) Randomize quotes every 15s ---
        Timer quoteTimer = new Timer(15_000, e -> {
            int bid   = ThreadLocalRandom.current().nextInt(3, 19);
            int offer = Math.min(18, bid + ThreadLocalRandom.current().nextInt(2, 4));
            refresh(bid, offer);
        });
        quoteTimer.setInitialDelay(0);
        quoteTimer.start();

        // --- 2) On each tick, log realization & your P/L ---
        gc.onTick(tr -> SwingUtilities.invokeLater(() -> {
            log.log(String.format(
                    "Realized price: %.2f | Your round P/L: %.2f",
                    tr.realizedPrice(), tr.partPLthisRound()));
        }));
    }

    private void placeTrade(GameController gc, Side side) {
        try {
            int q = Integer.parseInt(qty.getText());
            double p = (side==Side.BUY)
                    ? Double.parseDouble(offerLbl.getText())
                    : Double.parseDouble(bidLbl.getText());
            gc.trade(new Trade(side, p, q));
            log.log(String.format("%s %d @ %.2f → pending...", side, q, p));
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Enter a valid quantity.");
        }
    }

    private void placeOption(GameController gc, OptionTrade.Type type) {
        try {
            int q             = Integer.parseInt(qty.getText());
            double bidPrice   = Double.parseDouble(bidLbl.getText());
            double offerPrice = Double.parseDouble(offerLbl.getText());
            double strike     = (type == OptionTrade.Type.CALL) ? offerPrice : bidPrice;
            double premium    = (offerPrice - bidPrice) * 0.5;
            gc.trade(new OptionTrade(type, strike, premium, q));
            log.log(String.format("Bought %s %d× @ strike=%.2f prem=%.2f",
                    type, q, strike, premium));
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Enter a valid quantity.");
        }
    }

    public void refresh(double bid, double offer) {
        bidLbl  .setText(String.format("%.2f", bid));
        offerLbl.setText(String.format("%.2f", offer));
    }

    private void addLabel(String txt, int x, int y, GridBagConstraints g) {
        g.gridx = x; g.gridy = y; g.gridwidth = 1;
        add(new JLabel(txt + ":"), g);
    }
}
