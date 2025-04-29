package com.marketmadness.gui;

import com.marketmadness.network.WSBootstrap;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class MainMenuFrame extends JFrame {

    private static final String DB_URL = "jdbc:sqlite:marketmadness.db";

    public MainMenuFrame() {
        super("Market Madness – Main Menu");

        /* -------- big buttons ----------------------------------------- */
        JButton start   = big("Start Game");
        JButton trades  = big("View Trades");
        JButton about   = big("About");
        JButton credits = big("Credits");
        JButton quit    = big("Quit");

        JPanel grid = new JPanel(new GridLayout(0, 1, 10, 10));
        grid.setBorder(BorderFactory.createEmptyBorder(30, 60, 30, 60));
        grid.add(start); grid.add(trades); grid.add(about); grid.add(credits); grid.add(quit);
        add(grid);

        setSize(400, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        /* -------- wiring ---------------------------------------------- */
        start.addActionListener(e -> {
            String player = JOptionPane.showInputDialog(
                    this, "Enter player name", "Player", JOptionPane.PLAIN_MESSAGE);
            if (player == null || player.isBlank()) player = "Anon";
            new MainFrame(player).setVisible(true);
            setVisible(false);
        });

        trades.addActionListener(e -> showTrades());

        about.addActionListener(e -> JOptionPane.showMessageDialog(
                this,
                """
                • Alternate between “Market Maker” and “Participant” tabs.
                • Maker: set mid-price & spread, earn the edge, manage imbalance.
                • Participant: buy / sell or buy Call / Put based on quote + dice.
                • Rounds resolve every 15 s – watch the progress bar.
                • Leaderboard shows live P/L of everyone running the game.
                """,
                "About Market Madness",
                JOptionPane.INFORMATION_MESSAGE));

        credits.addActionListener(e -> JOptionPane.showMessageDialog(
                this,
                "Market Madness – Java edition\n(c) 2025 Beckham Pan",
                "Credits",
                JOptionPane.INFORMATION_MESSAGE));

        quit.addActionListener(e -> System.exit(0));
    }

    /* util for uniform buttons */
    private static JButton big(String txt) {
        JButton b = new JButton(txt);
        b.setFont(b.getFont().deriveFont(Font.BOLD, 16f));
        return b;
    }

    /* quick DB viewer (last 200 rows) */
    private void showTrades() {
        try (Connection c = DriverManager.getConnection(DB_URL);
             Statement  st = c.createStatement();
             ResultSet  rs = st.executeQuery("""
                     SELECT id, side, price, qty, ts
                     FROM trades ORDER BY id DESC LIMIT 200
                     """)) {

            DefaultTableModel m = new DefaultTableModel(
                    new Object[]{"ID","Side","Price","Qty","Time"}, 0);

            while (rs.next()) {
                m.addRow(new Object[]{
                        rs.getInt("id"),
                        rs.getString("side"),
                        rs.getDouble("price"),
                        rs.getInt("qty"),
                        rs.getString("ts")
                });
            }
            JTable table = new JTable(m);
            table.setAutoCreateRowSorter(true);

            JDialog dlg = new JDialog(this, "Last 200 trades", false);
            dlg.add(new JScrollPane(table));
            dlg.setSize(600, 320);
            dlg.setLocationRelativeTo(this);
            dlg.setVisible(true);

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "No trades found (yet).");
        }
    }

    /* ---------- entry helper ----------------------------------------- */
    public static void launch() {
        WSBootstrap.start(8025);                 // one server per JVM
        SwingUtilities.invokeLater(() -> new MainMenuFrame().setVisible(true));
    }
}
