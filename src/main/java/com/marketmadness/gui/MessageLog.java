package com.marketmadness.gui;

import javax.swing.*;
import java.awt.*;

/** Scrollable list that shows narrative messages. */
public class MessageLog extends JPanel {

    private final DefaultListModel<String> model = new DefaultListModel<>();

    public MessageLog(String title) {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createTitledBorder(title));
        add(new JScrollPane(new JList<>(model)), BorderLayout.CENTER);
        setPreferredSize(new Dimension(320, 110));
    }

    /** Append a message (keeps last 120) */
    public void log(String msg) {
        model.addElement(msg);
        if (model.size() > 120) model.remove(0);
    }
}
