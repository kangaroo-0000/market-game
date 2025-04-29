// File: MarketMakerPanel.java
package com.marketmadness.gui;

import com.marketmadness.controller.GameController;

import javax.swing.*;
import java.awt.*;

public class MarketMakerPanel extends JPanel {

    private final MessageLog log = new MessageLog("Actions");

    public MarketMakerPanel(GameController gc) {
        setLayout(new GridBagLayout());
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(5, 5, 5, 5);
        g.fill   = GridBagConstraints.HORIZONTAL;

        JTextField mid = new JTextField("100");
        JTextField spr = new JTextField("4");

        g.gridx = 0; g.gridy = 0; add(new JLabel("Midpoint"), g);
        g.gridx = 1; add(mid, g);

        g.gridx = 0; g.gridy = 1; add(new JLabel("Spread"), g);
        g.gridx = 1; add(spr, g);

        JButton submit = new JButton("Submit Market");
        g.gridy = 2; g.gridx = 0; g.gridwidth = 2; add(submit, g);

        g.gridy = 3; g.gridx = 0; add(log, g);

        submit.addActionListener(e -> {
            try {
                double m = Double.parseDouble(mid.getText());
                double s = Double.parseDouble(spr.getText());
                gc.submitMarket(m, s);
                log.log(String.format("Set market: mid %.2f / spread %.2f", m, s));
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Enter numeric mid & spread.");
            }
        });
    }
}
