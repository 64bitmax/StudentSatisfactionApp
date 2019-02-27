package com.oxfordbrookes.max.studentsatisfactionapp.graphs;

import com.github.mikephil.charting.charts.Chart;

import java.util.ArrayList;
import java.util.List;

public class GraphCollection {
    private List<Chart> charts;
    private int currentChart;
    private int previousChart;

    public GraphCollection() {
        charts = new ArrayList<>();
        currentChart = 0;
        previousChart = currentChart;
    }

    public Chart next() {
        previousChart = currentChart;
        currentChart = currentChart + 1;
        if(currentChart >= size()) {
            currentChart = size() - 1;
            previousChart = currentChart - 1;
        }
        return charts.get(currentChart);
    }

    public Chart current() {
        if(size() > 0) {
            return charts.get(currentChart);
        }
        return null;
    }

    public Chart previous() {
        currentChart = currentChart - 1;
        previousChart = currentChart - 1;
        if(currentChart < 0 || previousChart < 0) {
            currentChart = 0;
            previousChart = 0;
        }
        return charts.get(currentChart);
    }

    public void add(Chart chart) {
        charts.add(chart);
    }

    public int size() {
        return charts.size();
    }

}
