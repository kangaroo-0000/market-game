// File: ParticipantPanel.java
package com.marketmadness.gui;

import com.marketmadness.controller.GameController;
import com.marketmadness.model.OptionTrade;
import com.marketmadness.model.Side;
import com.marketmadness.model.Trade;

import javax.swing.*;
import java.awt.*;

public class ParticipantPanel extends JPanel {

    private final JLabel bidLbl   = new JLabel("-");
    private final JLabel offerLbl = new JLabel("-");
    private final JTextField qty  = new JTextField("10");
    private final MessageLog log  = new MessageLog("Actions");

    public ParticipantPanel(GameController gc) {

        setLayout(new GridBagLayout());
        GridBagConstraints g = new GridBagConstraints();
        g.insets  = new Insets(5, 5, 5, 5);
        g.fill    = GridBagConstraints.HORIZONTAL;   // stretch components
        g.weightx = 1.0;                             // give every cell full width

        addLabel("Bid",   0, 0, g); g.gridx = 1; add(bidLbl,   g);
        addLabel("Offer", 0, 1, g); g.gridx = 1; add(offerLbl, g);
        addLabel("Qty",   0, 2, g); g.gridx = 1; add(qty,      g);

        JButton buy  = new JButton("Buy");
        JButton sell = new JButton("Sell");
        JButton call = new JButton("Call");
        JButton put  = new JButton("Put");

        /* --- button row 1 -------------------------------------------------- */
        g.gridy = 3; g.gridx = 0; add(buy,  g);
        g.gridx = 1;             add(sell, g);

        /* --- button row 2 -------------------------------------------------- */
        g.gridy = 4; g.gridx = 0; add(call, g);
        g.gridx = 1;             add(put,  g);

        /* --- message log spans two columns -------------------------------- */
        g.gridy = 5; g.gridx = 0; g.gridwidth = 2;
        add(log, g);

        /* --- wiring -------------------------------------------------------- */
        buy .addActionListener(e -> trade (gc, Side.BUY));
        sell.addActionListener(e -> trade (gc, Side.SELL));
        call.addActionListener(e -> option(gc, OptionTrade.Type.CALL));
        put .addActionListener(e -> option(gc, OptionTrade.Type.PUT));
    }


    private void addLabel(String txt, int x, int y, GridBagConstraints g) {
        g.gridx = x; g.gridy = y;
        add(new JLabel(txt + ":"), g);
    }

    /* ---------------- plain share trade ---------------- */
    private void trade(GameController gc, Side side) {
        try {
            int    q = Integer.parseInt(qty.getText());
            double p = (side == Side.BUY)
                    ? Double.parseDouble(offerLbl.getText())
                    : Double.parseDouble(bidLbl.getText());

            gc.trade(new Trade(side, p, q));
            log.log(String.format("Placed %s %d @ %.2f", side, q, p));

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Enter a valid quantity.");
        }
    }

    /* ---------------- power-card option ---------------- */
    private void option(GameController gc, OptionTrade.Type type) {
        try {
            int q            = Integer.parseInt(qty.getText());
            double bidPrice  = Double.parseDouble(bidLbl.getText());
            double offerPrice= Double.parseDouble(offerLbl.getText());
            double strike    = (type == OptionTrade.Type.CALL) ? offerPrice : bidPrice;
            double premium   = (offerPrice - bidPrice) * 0.5;

            gc.trade(new OptionTrade(type, strike, premium, q));
            log.log(String.format("Bought %s %d× strike %.2f prem %.2f",
                    type, q, strike, premium));

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Enter a valid quantity.");
        }
    }

    /* refresh bid/offer each tick */
    public void refresh(double bid, double offer) {
        bidLbl  .setText(String.format("%.2f", bid));
        offerLbl.setText(String.format("%.2f", offer));
    }
}
