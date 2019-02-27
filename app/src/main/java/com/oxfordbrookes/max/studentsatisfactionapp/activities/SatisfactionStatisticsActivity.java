package com.oxfordbrookes.max.studentsatisfactionapp.activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.mongodb.client.FindIterable;
import com.oxfordbrookes.max.studentsatisfactionapp.R;
import com.oxfordbrookes.max.studentsatisfactionapp.database.DBClient;
import com.oxfordbrookes.max.studentsatisfactionapp.graphs.GraphCollection;

import org.bson.Document;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class SatisfactionStatisticsActivity extends Activity {
    private DBClient client;
    private Button backButton;
    private Spinner filterSpinner;
    private Spinner typeGraphSpinner;
    private HashMap<String, GraphCollection> charts;
    private ViewSwitcher viewSwitcher;

    private GraphCollection academicSupportGraphs;
    private GraphCollection assessmentAndFeedbackGraphs;
    private GraphCollection learningCommunityGraphs;
    private GraphCollection learningOpportunitiesGraphs;
    private GraphCollection learningResourcesGraphs;
    private GraphCollection organisationAndManagementGraphs;
    private GraphCollection overallTeachingGraphs;
    private GraphCollection studentVoiceGraphs;
    private GraphCollection overallGraphs;

    private HashMap<String, FindIterable<Document>> tweetDocs;

    @SuppressLint("CutPasteId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_satisfaction_statistics);

        client = null;
        try {
            client = new DBClient(getApplicationContext());
        } catch (IOException e) {
            e.printStackTrace();
        }

        final String name = getIntent().getStringExtra("name");
        final String email = getIntent().getStringExtra("email");
        final String university = getIntent().getStringExtra("university");

        charts = new HashMap<>();
        tweetDocs = client.getPredictedTweets();
        backButton = findViewById(R.id.buttonBackStudentSatisfaction);
        filterSpinner = findViewById(R.id.spinnerNssCategoryGraphs);
        typeGraphSpinner = findViewById(R.id.spinnerTypeGraph);
        viewSwitcher = findViewById(R.id.viewSwitcher);

        configureQuestionsSpinner();
        configureGraphTypeSpinner();
        createListeners(email, name, university);

        final View pieChartLayoutView = findViewById(R.id.pieChartLayout);
        pieChartLayoutView.post(new Runnable() {
            @Override
            public void run() {
                setupGraphs(pieChartLayoutView.getHeight(), pieChartLayoutView.getWidth());
            }
        });

        final View barChartLayoutView = findViewById(R.id.barChartLayout);
        barChartLayoutView.post(new Runnable() {
            @Override
            public void run() {
                initBarChart();
            }
        });
    }

    private void createListeners(final String email, final String name, final String university) {
        /** Add listeners to buttons to change graph **/
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                studentSatisfaction(email, name, university);
            }
        });
    }

    private void setupGraphs(int height, int width) {
        /** Academic support **/
        academicSupportGraphs = new GraphCollection();
        academicSupportGraphs.add(createPieChart("Academic support", height, width));

        /** Assessment and feedbach **/
        assessmentAndFeedbackGraphs = new GraphCollection();
        assessmentAndFeedbackGraphs.add(createPieChart("Assessment and feedback", height, width));

        /** Learning community **/
        learningCommunityGraphs = new GraphCollection();
        learningCommunityGraphs.add(createPieChart("Learning community", height, width));

        /** Learning opportunities **/
        learningOpportunitiesGraphs = new GraphCollection();
        learningOpportunitiesGraphs.add(createPieChart("Learning opportunities", height, width));

        /** Learning resources **/
        learningResourcesGraphs = new GraphCollection();
        learningResourcesGraphs.add(createPieChart("Learning resources", height, width));

        /** Organisation and management **/
        organisationAndManagementGraphs = new GraphCollection();
        organisationAndManagementGraphs.add(createPieChart("Organisation and management", height, width));

        /** Overall teaching **/
        overallTeachingGraphs = new GraphCollection();
        overallTeachingGraphs.add(createPieChart("Overall teaching", height, width));

        /** Student voice **/
        studentVoiceGraphs = new GraphCollection();
        studentVoiceGraphs.add(createPieChart("Student voice", height, width));

        /** Overall **/
        overallGraphs = new GraphCollection();
        overallGraphs.add(createPieChart("Overall satisfaction", height, width));

        /** Add all graphs to a map **/
        charts.put("Academic support", academicSupportGraphs);
        charts.put("Assessment and feedback", assessmentAndFeedbackGraphs);
        charts.put("Learning community", learningCommunityGraphs);
        charts.put("Learning opportunities", learningOpportunitiesGraphs);
        charts.put("Learning resources", learningResourcesGraphs);
        charts.put("Organisation and management", organisationAndManagementGraphs);
        charts.put("Overall teaching", overallTeachingGraphs);
        charts.put("Student voice", studentVoiceGraphs);
        charts.put("Overall satisfaction", overallGraphs);
    }

    private void initBarChart() {
        BarChart barChart = findViewById(R.id.barChart);
        barChart.getDescription().setEnabled(false);

        List<BarEntry> positiveEntries = new ArrayList<>();
        List<BarEntry> negativeEntries = new ArrayList<>();

        final Set<String> keys = tweetDocs.keySet();
        keys.remove("Overall satisfaction");
        int count = 0;
        for(String key : keys) {
            int[] sentimentCounts = getSentiments(Objects.requireNonNull(tweetDocs.get(key)));
            positiveEntries.add(new BarEntry(count, sentimentCounts[0]));
            negativeEntries.add(new BarEntry(count, sentimentCounts[1]));
            count++;
        }

        float barWidth = 0.45f;

        BarDataSet set1 = new BarDataSet(positiveEntries, "Positive Tweets");
        set1.setColor(getResources().getColor(R.color.colorPrimaryDark));
        set1.setValueTextColor(Color.WHITE);
        BarDataSet set2 = new BarDataSet(negativeEntries, "Negative Tweets");
        set2.setColor(getResources().getColor(R.color.colorPrimaryMiddle));
        set2.setValueTextColor(Color.WHITE);

        barChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(keys));
        barChart.getXAxis().setTextSize(8f);
        barChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        barChart.getXAxis().setTextColor(Color.WHITE);
        barChart.getXAxis().setLabelRotationAngle(-45f);
        barChart.getXAxis().setCenterAxisLabels(false);
        barChart.getRendererXAxis().getPaintAxisLabels().setTextAlign(Paint.Align.LEFT);

        Legend legend = barChart.getLegend();
        legend.setTextColor(Color.WHITE);
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);

        BarData data = new BarData(set1, set2);
        data.setBarWidth(barWidth);
        data.setValueTextColor(Color.WHITE);
        barChart.setData(data);
        barChart.invalidate();
    }

    private int[] getSentiments(FindIterable<Document> categoryDocs) {
        int numPositives = 0;
        int numNegatives = 0;

        for (Document doc : categoryDocs) {
            Integer sentiment = (Integer) doc.get("sentiment");
            assert sentiment != null;
            if (sentiment == 1) {
                numPositives += 1;
            } else {
                numNegatives += 1;
            }
        }

        return new int[] {numPositives, numNegatives};
    }

    private void configureGraphTypeSpinner() {
        List<String> graphTypeList = new ArrayList<>();
        graphTypeList.add("National Student Survey: Pie Charts");
        graphTypeList.add("National Student Survey: Stacked Bar Chart");
        ArrayAdapter<String> arrAdapter = new ArrayAdapter<>(this, R.layout.spinner_items, graphTypeList);
        arrAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        typeGraphSpinner.setAdapter(arrAdapter);
        typeGraphSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onItemSelected(AdapterView<?> parentAdapter, View view, int position, long id) {
                LinearLayout filterLinearLayout = findViewById(R.id.filterLinearLayout);
                LinearLayout pieChartLayout = findViewById(R.id.pieChartLayout);
                LinearLayout barChartLayout = findViewById(R.id.barChartLayout);

                if (typeGraphSpinner.getSelectedItem().equals("National Student Survey: Pie Charts") && viewSwitcher.getCurrentView() != pieChartLayout) {
                    viewSwitcher.showPrevious();
                    TextView desc = findViewById(R.id.textViewGraphDescription);
                    desc.setText("This graph shows the positive and negative tweet ratio for the 'Overall teaching' National Student Survey category.");
                    filterLinearLayout.setVisibility(View.VISIBLE);
                } else if (typeGraphSpinner.getSelectedItem().equals("National Student Survey: Stacked Bar Chart") && viewSwitcher.getCurrentView() != barChartLayout){
                    viewSwitcher.showNext();
                    TextView title = findViewById(R.id.textViewGraphTitle);
                    TextView desc = findViewById(R.id.textViewGraphDescription);
                    desc.setText("Each bar represents a National Student Survey category." +
                                 "The bars are stacked into two sections: positive sentiment and negative sentiment.");
                    title.setText("Category Sentiment Breakdown");
                    filterLinearLayout.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void configureQuestionsSpinner() {
        List<String> nssQuestionList = new ArrayList<>();
        nssQuestionList.add("Overall teaching");
        nssQuestionList.add("Learning opportunities");
        nssQuestionList.add("Assessment and feedback");
        nssQuestionList.add("Academic support");
        nssQuestionList.add("Organisation and management");
        nssQuestionList.add("Learning resources");
        nssQuestionList.add("Learning community");
        nssQuestionList.add("Student voice");
        nssQuestionList.add("Overall satisfaction");
        ArrayAdapter<String> arrAdapter = new ArrayAdapter<>(this, R.layout.spinner_items, nssQuestionList);
        arrAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        filterSpinner.setAdapter(arrAdapter);
        filterSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @SuppressLint("SetTextI18n")



            @Override
            public void onItemSelected(AdapterView<?> parentAdapter, View view, int position, long id) {
                String category = (String) filterSpinner.getSelectedItem();
                GraphCollection collection = charts.get(category);

                TextView title = findViewById(R.id.textViewGraphTitle);
                TextView desc = findViewById(R.id.textViewGraphDescription);
                title.setText("Graph of NSS Category: "+ category);

                switch (category) {
                    case "Overall teaching":
                        desc.setText("This graph shows the positive and negative tweet ratio for the 'Overall teaching' National Student Survey category.");
                        break;
                    case "Learning opportunities":
                        desc.setText("This graph shows the positive and negative tweet ratio for the 'Learning opportunities' National Student Survey category.");
                        break;
                    case "Assessment and feedback":
                        desc.setText("This graph shows the positive and negative tweet ratio for the 'Assessment and feedback' National Student Survey category.");
                        break;
                    case "Academic support":
                        desc.setText("This graph shows the positive and negative tweet ratio for the 'Academic support' National Student Survey category.");
                        break;
                    case "Organisation and management":
                        desc.setText("This graph shows the positive and negative tweet ratio for the 'Organisation and management' National Student Survey category.");
                        break;
                    case "Learning resources":
                        desc.setText("This graph shows the positive and negative tweet ratio for the 'Learning resources' National Student Survey category.");
                        break;
                    case "Learning community":
                        desc.setText("This graph shows the positive and negative tweet ratio for the 'Learning community' National Student Survey category.");
                        break;
                    case "Student voice":
                        desc.setText("This graph shows the positive and negative tweet ratio for the 'Student voice' National Student Survey category.");
                        break;
                    case "Overall satisfaction":
                        desc.setText("This graph shows the positive and negative tweet ratio for the 'Overall satisfaction' National Student Survey category. " +
                                     "This graph contains all tweets from the other categories and any un-categorized tweets.");
                        break;
                }

                if(collection != null) {
                    Chart chart = collection.current();
                    if(chart != null) {
                        PieChart pieChart = findViewById(R.id.pieChart);
                        ViewGroup parent = (ViewGroup) pieChart.getParent();

                        if(parent == null) {
                            return;
                        }
                        final int index = parent.indexOfChild(pieChart);

                        removeView(pieChart);
                        removeView(chart);
                        chart.setId(pieChart.getId());
                        parent.addView(chart, index);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }

            public void removeView(View view) {
                ViewGroup parent = (ViewGroup) view.getParent();
                if(parent != null) {
                    parent.removeView(view);
                }
            }
        });
    }

    void studentSatisfaction(String email, String name, String university) {
        Intent intent = new Intent(getApplicationContext(), StudentSatisfactionActivity.class);
        Bundle b = new Bundle();
        b.putString("email", email);
        b.putString("name", name);
        b.putString("university", university);
        intent.putExtras(b);
        startActivity(intent);
    }

    PieChart createPieChart(String category, int height, int width) {
        if(tweetDocs.containsKey(category)) {
            PieChart pieChart;
            FindIterable<Document> categoryDocs = tweetDocs.get(category);

            if (categoryDocs != null) {
                int[] sentimentCounts = getSentiments(categoryDocs);
                int numPositives = sentimentCounts[0];
                int numNegatives = sentimentCounts[1];

                pieChart = new PieChart(this);
                pieChart.setCenterTextColor(Color.WHITE);
                pieChart.setEntryLabelTextSize(12f);
                pieChart.setCenterTextSize(12f);
                pieChart.getDescription().setEnabled(false);

                pieChart.setMinimumHeight(height);
                pieChart.setMinimumWidth(width);

                Legend legend = pieChart.getLegend();
                legend.setTextColor(Color.WHITE);
                legend.setTextSize(12f);
                legend.setForm(Legend.LegendForm.CIRCLE);

                ArrayList<PieEntry> entries = new ArrayList<>();
                entries.add(new PieEntry(numPositives,"Positive Tweets"));
                entries.add(new PieEntry(numNegatives, "Negative Tweets"));

                PieDataSet set = new PieDataSet(entries, "");
                set.setColors(ColorTemplate.COLORFUL_COLORS);

                PieData data = new PieData(set);
                data.setValueTextSize(11f);
                data.setValueTextColor(Color.WHITE);
                pieChart.setData(data);
                return pieChart;
            }
        }
        return null;
    }
}
