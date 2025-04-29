package com.marketmadness.gui;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

import javax.swing.*;
import java.awt.*;

/** Swing wrapper around a JFreeChart line chart that tracks cumulative P/L. */
public class PLChartPanel extends JPanel {

    private final DefaultCategoryDataset dataset = new DefaultCategoryDataset();
    private int tradeIndex = 0;

    public PLChartPanel() {
        var chart = ChartFactory.createLineChart(
                "Cumulative P/L",
                "Trade",
                "P/L",
                dataset,
                PlotOrientation.VERTICAL,
                false,   // legend
                true,    // tooltips
                false    // URLs
        );

        setLayout(new BorderLayout());
        add(new ChartPanel(chart), BorderLayout.CENTER);
        setPreferredSize(new Dimension(1000, 250));
    }

    /** Add a new point to the chart. */
    public void addPoint(double cumulativePL) {
        dataset.addValue(cumulativePL, "P/L", "T" + (++tradeIndex));
    }
}
