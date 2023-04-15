package com.google.android.gms.fit.samples.stepcounter;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.google.android.gms.fit.samples.stepcounter.databinding.Day4barchartBinding;
import java.util.ArrayList;

public class Day4BarGraph extends AppCompatActivity {

    //Data Binding
    private Day4barchartBinding day4barchartBinding;
    private BarChart barChart;

    // Variabeln die wichtig sind für den Tag. Schritte, Datum und das Wissen welcher Tag heute ist
    private int stepValue;
    private String day;
    private String date;
    private String dayNumber;
    private String month;

    // Anzahl der Schritte die als Ziel festgelegt wurden
    private int mondayGoal;
    private int tuesdayGoal;
    private int wednesdayGoal;
    private int thursdayGoal;
    private int fridayGoal;
    private int saturdayGoal;
    private int sundayGoal;

    // Arraylisten für BarEntry
    private ArrayList<BarEntry> barEntries;
    private ArrayList<String> theDates;
    int max1;

    //Beschreibung der Achsen
    Description description;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        day4barchartBinding = DataBindingUtil.setContentView(this, R.layout.day4barchart);

        // Erstelle die Barchart bzw. Graphen
        barChart = day4barchartBinding.day4Barchart;

        // Besorge die allgemeinen Daten die für die täglich neue Darstellung des Graphen angezeigt werden soll.
        Bundle extras = getIntent().getExtras();

        stepValue = extras.getInt("stepsDay4");
        day = extras.getString("day");
        date = extras.getString("date");
        month = extras.getString("month");
        dayNumber = extras.getString("dayNumber");

        // BarChart Inhaltserstellung des Graphen
        if(day.equals("Montag")){
            mondayGoal = (int) extras.getDouble("MondayGoalValue");
            barEntries = new ArrayList<>();
            description = barChart.getDescription();

            barEntries.add(new BarEntry(0f, stepValue));
            BarDataSet barDataSet = new BarDataSet(barEntries, day);
            barDataSet.setColors(new int[] {Color.BLUE});
            barDataSet.setValueTextSize(11f);
            barDataSet.setValueTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
            barChart.getLegend().setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));

            ArrayList<String> theDates = new ArrayList<>();

            // X-Achsen Bearbeitung
            barChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(theDates));
            barChart.getXAxis().setDrawGridLines(false);
            barChart.getXAxis().setDrawLabels(false);
            barChart.getXAxis().setDrawAxisLine(false);
            description.setText("Schrittanzahl für: "+ dayNumber+","+month);

            BarData theData = new BarData(barDataSet);

            // Minimum und Maximum der Y-Achse bestimmen und Achsenbeschreibung auf der rechten Seite ausblenden
            YAxis yAxis = barChart.getAxisLeft();
            yAxis.removeAllLimitLines();
            yAxis.setDrawGridLines(false);
            yAxis.setDrawAxisLine(false);
            max1 = 10000;
            yAxis.setAxisMinimum(0);
            yAxis.setAxisMaximum(max1);
            if(max1 < mondayGoal){
                yAxis.setAxisMaximum(mondayGoal+1000);
            }

            barChart.getAxisRight().setEnabled(false);

            // Erstelle ein Ziel mit der LimitLine

            int goalLine = mondayGoal;
            if(goalLine != 0) {
                LimitLine goalLimitLine = new LimitLine(goalLine, "Ziel: " + mondayGoal);
                goalLimitLine.setLineWidth(0.5f);
                goalLimitLine.setLineColor(getResources().getColor(R.color.orange));
                goalLimitLine.enableDashedLine(10f, 10f, 0f);
                barChart.getAxisLeft().addLimitLine(goalLimitLine);
                barChart.getAxisLeft().setDrawLimitLinesBehindData(true);
            }

            theData.setBarWidth(0.3f);
            barChart.setData(theData);

            // BarChart Einstellungen
            barChart.setTouchEnabled(false);
            barChart.setDragEnabled(false);
            barChart.setScaleEnabled(false);

            // Make edges round
            RoundedEdges barChartRender = new RoundedEdges(barChart,barChart.getAnimator(), barChart.getViewPortHandler());
            barChartRender.setRadius(20);
            barChart.setRenderer(barChartRender);

        }else if (day.equals("Dienstag")){
            tuesdayGoal = (int) extras.getDouble("TuesdayGoalValue");
            barEntries = new ArrayList<>();
            description = barChart.getDescription();

            barEntries.add(new BarEntry(0f, stepValue));
            BarDataSet barDataSet = new BarDataSet(barEntries, day);
            barDataSet.setColors(new int[] {Color.BLUE});
            barDataSet.setValueTextSize(11f);
            barDataSet.setValueTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
            barChart.getLegend().setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));

            ArrayList<String> theDates = new ArrayList<>();
            theDates.add(String.valueOf(tuesdayGoal));

            // X-Achsen Bearbeitung, keine X-Achsen Linien, Rasterstreifenfrei, keine X-Achsen Labels
            barChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(theDates));
            barChart.getXAxis().setDrawGridLines(false);
            barChart.getXAxis().setDrawLabels(false);
            barChart.getXAxis().setDrawAxisLine(false);
            description.setText("Schrittanzahl für: "+ dayNumber+","+month);

            BarData theData = new BarData(barDataSet);

            // Minimum und Maximum der Y-Achse bestimmen und Achsenbeschreibung auf der rechten Seite ausblenden
            YAxis yAxis = barChart.getAxisLeft();
            yAxis.setDrawGridLines(false);
            yAxis.setDrawAxisLine(false);
            yAxis.removeAllLimitLines();
            max1 = 10000;
            yAxis.setAxisMinimum(0);
            yAxis.setAxisMaximum(max1);

            if(max1 <= tuesdayGoal){
                yAxis.setAxisMaximum(tuesdayGoal+1000);
            }

            barChart.getAxisRight().setEnabled(false);

            // Erstelle ein Ziel mit der LimitLine
            int goalLine = tuesdayGoal;
            if(goalLine != 0) {
                LimitLine goalLimitLine = new LimitLine(goalLine, "Ziel: " + tuesdayGoal);
                goalLimitLine.setLineWidth(1f);
                goalLimitLine.setLineColor(getResources().getColor(R.color.orange));
                goalLimitLine.enableDashedLine(10f, 10f, 0f);
                barChart.getAxisLeft().addLimitLine(goalLimitLine);
                barChart.getAxisLeft().setDrawLimitLinesBehindData(true);
            }

            theData.setBarWidth(0.3f);
            barChart.setData(theData);

            // BarChart Einstellungen
            barChart.setTouchEnabled(false);
            barChart.setDragEnabled(false);
            barChart.setScaleEnabled(false);

        }else if (day.equals("Mittwoch")){
            wednesdayGoal = (int) extras.getDouble("WednesdayGoalValue");
            barEntries = new ArrayList<>();
            description = barChart.getDescription();

            barEntries.add(new BarEntry(0f, stepValue));
            BarDataSet barDataSet = new BarDataSet(barEntries, day);
            barDataSet.setColors(new int[] {Color.BLUE});
            barDataSet.setValueTextSize(11f);
            barDataSet.setValueTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
            barChart.getLegend().setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));

            ArrayList<String> theDates = new ArrayList<>();

            // X-Achsen Bearbeitung
            barChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(theDates));
            barChart.getXAxis().setDrawGridLines(false);
            barChart.getXAxis().setDrawLabels(false);
            barChart.getXAxis().setDrawAxisLine(false);
            description.setText("Schrittanzahl für: "+ dayNumber+","+month);

            BarData theData = new BarData(barDataSet);

            // Minimum und Maximum der Y-Achse bestimmen und Achsenbeschreibung auf der rechten Seite ausblenden
            YAxis yAxis = barChart.getAxisLeft();
            yAxis.removeAllLimitLines();
            yAxis.setDrawGridLines(false);
            yAxis.setDrawAxisLine(false);
            max1 = 10000;
            yAxis.setAxisMinimum(0);
            yAxis.setAxisMaximum(max1);
            if(max1 < wednesdayGoal){
                yAxis.setAxisMaximum(wednesdayGoal+1000);
            }

            barChart.getAxisRight().setEnabled(false);

            // Erstelle ein Ziel mit der LimitLine

            int goalLine = wednesdayGoal;
            if(goalLine != 0) {
                LimitLine goalLimitLine = new LimitLine(goalLine, "Ziel: " + wednesdayGoal);
                goalLimitLine.setLineWidth(1f);
                goalLimitLine.setLineColor(getResources().getColor(R.color.orange));
                goalLimitLine.enableDashedLine(10f, 10f, 0f);
                barChart.getAxisLeft().addLimitLine(goalLimitLine);
                barChart.getAxisLeft().setDrawLimitLinesBehindData(true);
            }


            theData.setBarWidth(0.3f);
            barChart.setData(theData);

            // BarChart Einstellungen
            barChart.setTouchEnabled(false);
            barChart.setDragEnabled(false);
            barChart.setScaleEnabled(false);

        }else if (day.equals("Donnerstag")){
            thursdayGoal = (int) extras.getDouble("ThursdayGoalValue");
            barEntries = new ArrayList<>();
            description = barChart.getDescription();

            barEntries.add(new BarEntry(0f, stepValue));
            BarDataSet barDataSet = new BarDataSet(barEntries, day);
            barDataSet.setColors(new int[] {Color.BLUE});
            barDataSet.setValueTextSize(11f);
            barDataSet.setValueTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
            barChart.getLegend().setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));

            ArrayList<String> theDates = new ArrayList<>();
            theDates.add(String.valueOf(mondayGoal));

            // X-Achsen Bearbeitung
            barChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(theDates));
            barChart.getXAxis().setDrawGridLines(false);
            barChart.getXAxis().setDrawLabels(false);
            barChart.getXAxis().setDrawAxisLine(false);
            description.setText("Schrittanzahl für: "+ dayNumber+","+month);

            BarData theData = new BarData(barDataSet);

            // Minimum und Maximum der Y-Achse bestimmen und Achsenbeschreibung auf der rechten Seite ausblenden
            YAxis yAxis = barChart.getAxisLeft();
            yAxis.removeAllLimitLines();
            yAxis.setDrawGridLines(false);
            yAxis.setDrawAxisLine(false);
            max1 = 10000;
            yAxis.setAxisMinimum(0);
            yAxis.setAxisMaximum(max1);
            if(max1 < thursdayGoal){
                yAxis.setAxisMaximum(thursdayGoal+1000);
            }

            barChart.getAxisRight().setEnabled(false);

            // Erstelle ein Ziel mit der LimitLine
            int goalLine = thursdayGoal;
            if(goalLine != 0) {
                LimitLine goalLimitLine = new LimitLine(goalLine, "Ziel: " + thursdayGoal);
                goalLimitLine.setLineWidth(1f);
                goalLimitLine.setLineColor(getResources().getColor(R.color.orange));
                goalLimitLine.enableDashedLine(10f, 10f, 0f);
                barChart.getAxisLeft().addLimitLine(goalLimitLine);
                barChart.getAxisLeft().setDrawLimitLinesBehindData(true);
            }

            theData.setBarWidth(0.3f);
            barChart.setData(theData);

            // BarChart Einstellungen
            barChart.setTouchEnabled(false);
            barChart.setDragEnabled(false);
            barChart.setScaleEnabled(false);

        }else if (day.equals("Freitag")){
            fridayGoal = (int) extras.getDouble("FridayGoalValue");
            barEntries = new ArrayList<>();
            description = barChart.getDescription();

            barEntries.add(new BarEntry(0f, stepValue));
            BarDataSet barDataSet = new BarDataSet(barEntries, day);
            barDataSet.setColors(new int[] {Color.BLUE});
            barDataSet.setValueTextSize(11f);
            barDataSet.setValueTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
            barChart.getLegend().setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));

            ArrayList<String> theDates = new ArrayList<>();
            theDates.add(String.valueOf(fridayGoal));

            // X-Achsen Bearbeitung
            barChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(theDates));
            barChart.getXAxis().setDrawGridLines(false);
            barChart.getXAxis().setDrawLabels(false);
            barChart.getXAxis().setDrawAxisLine(false);
            description.setText("Schrittanzahl für: "+ dayNumber+","+month);

            BarData theData = new BarData(barDataSet);

            // Minimum und Maximum der Y-Achse bestimmen und Achsenbeschreibung auf der rechten Seite ausblenden
            YAxis yAxis = barChart.getAxisLeft();
            yAxis.removeAllLimitLines();
            yAxis.setDrawGridLines(false);
            yAxis.setDrawAxisLine(false);
            max1 = 10000;
            yAxis.setAxisMinimum(0);
            yAxis.setAxisMaximum(max1);
            if(max1 < fridayGoal){
                yAxis.setAxisMaximum(fridayGoal+1000);
            }

            barChart.getAxisRight().setEnabled(false);

            // Erstelle ein Ziel mit der LimitLine
            int goalLine = fridayGoal;
            if(goalLine != 0) {
                LimitLine goalLimitLine = new LimitLine(goalLine, "Ziel: " + fridayGoal);
                goalLimitLine.setLineWidth(1f);
                goalLimitLine.setLineColor(getResources().getColor(R.color.orange));
                goalLimitLine.enableDashedLine(10f, 10f, 0f);
                barChart.getAxisLeft().addLimitLine(goalLimitLine);
                barChart.getAxisLeft().setDrawLimitLinesBehindData(true);
            }


            theData.setBarWidth(0.3f);
            barChart.setData(theData);

            // BarChart Einstellungen
            barChart.setTouchEnabled(false);
            barChart.setDragEnabled(false);
            barChart.setScaleEnabled(false);

        }else if (day.equals("Samstag")){
            saturdayGoal = (int) extras.getDouble("SaturdayGoalValue");
            barEntries = new ArrayList<>();
            description = barChart.getDescription();

            barEntries.add(new BarEntry(0f, stepValue));
            BarDataSet barDataSet = new BarDataSet(barEntries, day);
            barDataSet.setColors(new int[] {Color.BLUE});
            barDataSet.setValueTextSize(11f);
            barDataSet.setValueTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
            barChart.getLegend().setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));

            ArrayList<String> theDates = new ArrayList<>();
            theDates.add(String.valueOf(fridayGoal));

            // X-Achsen Bearbeitung
            barChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(theDates));
            barChart.getXAxis().setDrawGridLines(false);
            barChart.getXAxis().setDrawLabels(false);
            barChart.getXAxis().setDrawAxisLine(false);
            description.setText("Schrittanzahl für: "+ dayNumber+","+month);

            BarData theData = new BarData(barDataSet);

            // Minimum und Maximum der Y-Achse bestimmen und Achsenbeschreibung auf der rechten Seite ausblenden
            YAxis yAxis = barChart.getAxisLeft();
            yAxis.removeAllLimitLines();
            yAxis.setDrawGridLines(false);
            yAxis.setDrawAxisLine(false);
            max1 = 10000;
            yAxis.setAxisMinimum(0);
            yAxis.setAxisMaximum(max1);

            if(max1 < saturdayGoal){
                yAxis.setAxisMaximum(saturdayGoal+1000);
            }

            barChart.getAxisRight().setEnabled(false);

            // Erstelle ein Ziel mit der LimitLine
            int goalLine = saturdayGoal;
            if(goalLine != 0) {
                LimitLine goalLimitLine = new LimitLine(goalLine, "Ziel: " + saturdayGoal);
                goalLimitLine.setLineWidth(1f);
                goalLimitLine.setLineColor(getResources().getColor(R.color.orange));
                goalLimitLine.enableDashedLine(10f, 10f, 0f);
                barChart.getAxisLeft().addLimitLine(goalLimitLine);
                barChart.getAxisLeft().setDrawLimitLinesBehindData(true);
            }

            theData.setBarWidth(0.3f);
            barChart.setData(theData);

            // BarChart Einstellungen
            barChart.setTouchEnabled(false);
            barChart.setDragEnabled(false);
            barChart.setScaleEnabled(false);

        }else if (day.equals("Sonntag")){
            sundayGoal = (int) extras.getDouble("SundayGoalValue");
            barEntries = new ArrayList<>();
            description = barChart.getDescription();

            barEntries.add(new BarEntry(0f, stepValue));
            BarDataSet barDataSet = new BarDataSet(barEntries, day);
            barDataSet.setColors(new int[] {Color.BLUE});
            barDataSet.setValueTextSize(11f);
            barDataSet.setValueTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
            barChart.getLegend().setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));

            ArrayList<String> theDates = new ArrayList<>();
            theDates.add(String.valueOf(fridayGoal));

            // X-Achsen Bearbeitung
            barChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(theDates));
            barChart.getXAxis().setDrawGridLines(false);
            barChart.getXAxis().setDrawLabels(false);
            barChart.getXAxis().setDrawAxisLine(false);
            description.setText("Schrittanzahl für: "+ dayNumber+","+month);

            BarData theData = new BarData(barDataSet);

            // Minimum und Maximum der Y-Achse bestimmen und Achsenbeschreibung auf der rechten Seite ausblenden
            YAxis yAxis = barChart.getAxisLeft();
            yAxis.removeAllLimitLines();
            yAxis.setDrawGridLines(false);
            yAxis.setDrawAxisLine(false);
            max1 = 10000;
            yAxis.setAxisMinimum(0);
            yAxis.setAxisMaximum(max1);

            if(max1 < sundayGoal){
                yAxis.setAxisMaximum(sundayGoal+1000);
            }

            barChart.getAxisRight().setEnabled(false);

            // Erstelle ein Ziel mit der LimitLine
            int goalLine = sundayGoal;

            if(goalLine != 0) {
                LimitLine goalLimitLine = new LimitLine(goalLine, "Ziel: " + sundayGoal);
                goalLimitLine.setLineWidth(1f);
                goalLimitLine.setLineColor(getResources().getColor(R.color.orange));
                goalLimitLine.enableDashedLine(10f, 10f, 0f);
                barChart.getAxisLeft().addLimitLine(goalLimitLine);
                barChart.getAxisLeft().setDrawLimitLinesBehindData(true);
            }

            theData.setBarWidth(0.3f);
            barChart.setData(theData);

            // BarChart Einstellungen
            barChart.setTouchEnabled(false);
            barChart.setDragEnabled(false);
            barChart.setScaleEnabled(false);
        }
    }
}
