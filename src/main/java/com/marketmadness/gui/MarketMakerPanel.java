// File: src/main/java/com/marketmadness/gui/MarketMakerPanel.java
package com.marketmadness.gui;

import com.marketmadness.controller.GameController;
import javax.swing.*;
import java.awt.*;

public class MarketMakerPanel extends JPanel {

    private final MessageLog log = new MessageLog("Maker Log");
    private boolean submittedThisRound = false; // ← track if submit was pressed

    public MarketMakerPanel(GameController gc) {
        setLayout(new GridBagLayout());
        GridBagConstraints g = new GridBagConstraints();
        g.insets  = new Insets(5,5,5,5);
        g.fill    = GridBagConstraints.HORIZONTAL;
        g.weightx = 1.0;

        JTextField mid = new JTextField("10.5");
        JTextField spr = new JTextField("1.0");

        g.gridx=0; g.gridy=0; add(new JLabel("Midpoint"), g);
        g.gridx=1; add(mid, g);
        g.gridy=1; g.gridx=0; add(new JLabel("Spread"), g);
        g.gridx=1; add(spr, g);

        JButton submit = new JButton("Submit Market");
        g.gridy=2; g.gridx=0; g.gridwidth=2; add(submit, g);

        g.gridy=3; g.gridx=0; g.gridwidth=2; add(log, g);

        submit.addActionListener(e -> {
            try {
                double m = Double.parseDouble(mid.getText());
                double s = Double.parseDouble(spr.getText());
                gc.submitMarket(m, s);
                log.log(String.format("Market set: mid=%.2f spread=%.2f", m, s));
                submittedThisRound = true;      // ← mark for next tick
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Enter valid numbers.");
            }
        });

        // Log maker's P/L only if they submitted this round
        gc.onTick(tr -> SwingUtilities.invokeLater(() -> {
            if (!submittedThisRound) return;
            log.log(String.format(
                    "Round done: %d buyers, %d sellers (matched %d)%n" +
                            "  Spread rev: %.2f | Inventory P/L: %.2f%n" +
                            "  Round P/L: %.2f | Cum P/L: %.2f",
                    tr.buyers(),
                    tr.sellers(),
                    tr.matched(),
                    tr.spreadRevenue(),
                    tr.inventoryPL(),
                    tr.makerPLthisRound(),
                    tr.makerCumPL()
            ));
            submittedThisRound = false;
        }));
    }
}
