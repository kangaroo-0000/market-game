package com.marketmadness.gui;

import com.google.gson.Gson;
import com.marketmadness.network.MMWebSocketClient;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

/**
 * Displays a live table of {player, cumulative P/L} received over WebSocket.
 * The server broadcasts JSON: {"player":"Alice","pl":123.45}
 */
public class LeaderboardPanel extends JPanel {

    private final DefaultTableModel model =
            new DefaultTableModel(new Object[] { "Player", "P/L" }, 0);

    private final Gson gson = new Gson();

    public LeaderboardPanel(String hostPort) {          // pass "localhost:8025"
        setLayout(new BorderLayout());
        add(new JScrollPane(new JTable(model)), BorderLayout.CENTER);
        setBorder(BorderFactory.createTitledBorder("Leaderboard"));

        String uri =  hostPort + "/ws/";        // exact server path
        System.out.println("URI: "+ uri);
        new MMWebSocketClient(uri, json -> {
            ScoreMsg m = gson.fromJson(json, ScoreMsg.class);
            SwingUtilities.invokeLater(() -> upsert(m));
        });
    }
    /** Update existing row or insert a new one. */
    private void upsert(ScoreMsg msg) {
        for (int i = 0; i < model.getRowCount(); i++) {
            if (model.getValueAt(i, 0).equals(msg.player)) {
                model.setValueAt(msg.pl, i, 1);
                return;
            }
        }
        model.addRow(new Object[] { msg.player, msg.pl });
    }

    // DTO matching the JSON broadcast
    private static class ScoreMsg {
        String player;
        double pl;
    }
}
