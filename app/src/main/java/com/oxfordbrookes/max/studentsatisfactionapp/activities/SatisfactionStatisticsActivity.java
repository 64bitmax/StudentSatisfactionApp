package com.oxfordbrookes.max.studentsatisfactionapp.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.oxfordbrookes.max.studentsatisfactionapp.R;
import com.oxfordbrookes.max.studentsatisfactionapp.utils.DBClient;

import org.bson.Document;
import org.bson.types.Binary;

import java.io.IOException;
import java.util.ArrayList;

import static com.mongodb.client.model.Filters.eq;

public class SatisfactionStatisticsActivity extends Activity {
    PieChart pieChart;
    Button backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_satisfaction_statistics);

        DBClient client = null;
        try {
            client = new DBClient(getApplicationContext());
        } catch (IOException e) {
            e.printStackTrace();
        }

        final String name = getIntent().getStringExtra("name");
        final String email = getIntent().getStringExtra("email");
        final String university = getIntent().getStringExtra("university");

        backButton = findViewById(R.id.buttonBackStudentSatisfaction);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                studentSatisfaction(email, name, university);
            }
        });

        createChart(client);
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

    void createChart(DBClient client) {
        MongoDatabase db = client.getClient().getDatabase("predictions");
        MongoCollection<Document> collection = db.getCollection("tweet_sentiments_bayes");

        long numDocs = collection.count();
        FindIterable<Document> tweetDocs = collection.find();

        int numPositives = 0;
        int numNegatives = 0;

        for(Document doc : tweetDocs) {
            Integer sentiment = (Integer) doc.get("sentiment");
            assert sentiment != null;
            if(sentiment == 1) {
                numPositives += 1;
            } else {
                numNegatives += 1;
            }
        }


        pieChart = findViewById(R.id.chart);
        pieChart.setCenterTextColor(Color.WHITE);
        pieChart.setEntryLabelTextSize(12f);
        pieChart.setCenterTextSize(12f);

        ArrayList<PieEntry> entries = new ArrayList<>();
        entries.add(new PieEntry(numPositives,"Positive Tweets"));
        entries.add(new PieEntry(numNegatives, "Negative Tweets"));

        PieDataSet set = new PieDataSet(entries, "Tweet Sentiment Statistics");
        set.setColors(ColorTemplate.COLORFUL_COLORS);

        PieData data = new PieData(set);
        data.setValueTextSize(11f);
        data.setValueTextColor(Color.WHITE);
        pieChart.setData(data);
    }
}
