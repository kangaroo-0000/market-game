package com.marketmadness.gui;

import javax.swing.*;
import java.awt.*;

public class DicePanel extends JPanel {
    private final JLabel[] labels = {lbl(), lbl(), lbl()};

    private JLabel lbl() {
        JLabel l = new JLabel("H", SwingConstants.CENTER);
        l.setFont(l.getFont().deriveFont(Font.BOLD, 24f));
        l.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY));
        return l;
    }

    public DicePanel() {
        setLayout(new GridLayout(1, 3, 6, 0));
        setBorder(BorderFactory.createTitledBorder("Dice"));
        for (var l : labels) {
            add(l);
        }
    }

    public void update(int[] v) {
        for (int i = 0; i < 3; i++) {
            labels[i].setText(v[i] == -1 ? "H" : String.valueOf(v[i]));
        }
    }
}