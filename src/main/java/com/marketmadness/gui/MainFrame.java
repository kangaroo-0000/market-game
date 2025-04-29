package com.marketmadness.gui;

import com.google.gson.Gson;
import com.marketmadness.controller.GameController;
import com.marketmadness.network.MMWebSocketServer;
import com.marketmadness.network.WSBootstrap;

import javax.swing.*;
import java.awt.*;

/**
 * Top-level window: dice, tabs, chart, leaderboard, progress bar.
 * Starts 15-second rounds and shows a countdown bar.
 */
public class MainFrame extends JFrame {

    public MainFrame(String playerName) {
        super("Market Madness – " + playerName);

        /* ---------------- controller ---------------- */
        GameController controller = new GameController(playerName);

        /* ---------------- GUI pieces ---------------- */
        DicePanel        dice   = new DicePanel();
        MarketMakerPanel maker  = new MarketMakerPanel(controller);
        ParticipantPanel part   = new ParticipantPanel(controller);
        PLChartPanel     chart  = new PLChartPanel();
        LeaderboardPanel board  = new LeaderboardPanel("ws://localhost:8025");

        /* progress bar (ticks down from 15 s) */
        JProgressBar roundBar = new JProgressBar(0, 15_000);
        roundBar.setValue(15_000);
        roundBar.setStringPainted(true);
        roundBar.setString("Next round in 15 s");

        /* tabs for the two roles */
        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Market Maker", maker);
        tabs.addTab("Participant",  part);

        /* layout */
        JPanel center = new JPanel(new BorderLayout());
        center.add(tabs,  BorderLayout.CENTER);
        center.add(chart, BorderLayout.SOUTH);

        setLayout(new BorderLayout());
        add(dice,     BorderLayout.NORTH);
        add(center,   BorderLayout.CENTER);
        add(board,    BorderLayout.EAST);
        add(roundBar, BorderLayout.SOUTH);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 820);
        setLocationRelativeTo(null);

        /* ------------ timers ------------ */

        /* secondary timer: update progress bar each second */
        Timer barTimer = new Timer(1_000, ev -> {
            int v = roundBar.getValue() - 1_000;
            if (v <= 0) v = 15_000;
            roundBar.setValue(v);
            roundBar.setString("Next round in " + (v / 1000) + " s");
        });
        barTimer.start();

        /* main 15-second round timer */
        new Timer(15_000, e -> {
            roundBar.setValue(15_000);          // reset bar
            controller.nextRound();
            part.refresh(controller.bid(), controller.offer());
        }).start();

        /* Tick listener: update dice + chart */
        controller.onTick(tr -> {
            dice.update(tr.visibleDice());
            chart.addPoint(tr.makerCumPL());
        });

        /* Broadcast initial zero score so you appear instantly on leaderboard */
        MMWebSocketServer.broadcast(new Gson()
                .toJson(new ScoreMsg(playerName, 0.0)));
    }

    /* simple DTO to reuse the existing leaderboard JSON format */
    private record ScoreMsg(String player, double pl) {}

    /** Called from App.main() */
    public static void launchUI() {
        WSBootstrap.start(8025);               // embedded WS server

        SwingUtilities.invokeLater(() -> {
            String player = JOptionPane.showInputDialog(
                    null, "Enter player name", "Main Menu", JOptionPane.PLAIN_MESSAGE);
            if (player == null || player.isBlank()) player = "Anon";
            new MainFrame(player).setVisible(true);
        });
    }
}