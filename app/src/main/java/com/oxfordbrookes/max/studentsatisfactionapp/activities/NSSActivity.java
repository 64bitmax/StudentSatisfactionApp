package com.oxfordbrookes.max.studentsatisfactionapp.activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.mongodb.client.FindIterable;
import com.oxfordbrookes.max.studentsatisfactionapp.R;
import com.oxfordbrookes.max.studentsatisfactionapp.database.DBClient;

import org.bson.Document;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class NSSActivity extends Activity {

    private DBClient client;
    private ListView questionsListView;
    private Button backButton;
    private Spinner nssQuestions;
    private TextView percentage;
    private ArrayAdapter<String> listAdapter;
    private List<Document> nssDocuments;

    @SuppressLint({"ClickableViewAccessibility", "ResourceType"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_nss);

        final String name = getIntent().getStringExtra("name");
        final String email = getIntent().getStringExtra("email");
        final String university = getIntent().getStringExtra("university");
        final String nssYear = getIntent().getStringExtra("year");

        /** Instantiate activity components **/
        backButton = findViewById(R.id.buttonBackTest);
        questionsListView = findViewById(R.id.listViewQuestionns);
        nssQuestions = findViewById(R.id.spinnerNssTest);
        percentage = findViewById(R.id.textViewTweetPercentage);
        try {
            client = new DBClient(getApplicationContext());
        } catch (IOException e) {
            e.printStackTrace();
        }

        /** Configure all components **/
        configureQuestions(nssYear, university);
        configureListeners(email, name, university, nssYear);
    }


    private void configureQuestions(String year, String university) {
        nssDocuments = client.getQuestions(year.substring(0,4) + "_nss_results", university);
        final HashMap<String, FindIterable<Document>> predictedTweets;
        if(university.equals("Oxford Brookes University")) {
            predictedTweets = client.getPredictedTweets(year.substring(0,4) + "_tweets_predictions");
        } else {
            predictedTweets = client.getPredictedTweets(university + "_" + year.substring(0,4) + "_tweets_predictions");
        }
        final List<String> results = new ArrayList<>();

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
        nssQuestions.setAdapter(arrAdapter);
        nssQuestions.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch(nssQuestions.getSelectedItem().toString()) {
                    case "Overall teaching":
                        results.clear();
                        results.add("The staff are good at explaining things: " + Integer.toString( (int) nssDocuments.get(0).get("result")) + "%");
                        results.add("Staff have made the subject interesting: " + Integer.toString((int)  nssDocuments.get(1).get("result")) + "%");
                        results.add("The course is intellectually stimulating: " + Integer.toString((int)  nssDocuments.get(2).get("result")) + "%");
                        results.add("My course has challenged me to achieve my best work: " + Integer.toString((int) nssDocuments.get(3).get("result")) + "%");
                        break;
                    case "Learning opportunities":
                        results.clear();
                        results.add("My course has provided me with opportunities to explore ideas or concepts in depth: " + Integer.toString((int) nssDocuments.get(4).get("result")) + "%");
                        results.add("My course has provided me with opportunities to bring information and ideas together from different topics: " + Integer.toString((int) nssDocuments.get(5).get("result")) + "%");
                        results.add("My course has provided me with opportunities to apply what I have learnt: " + Integer.toString((int) nssDocuments.get(6).get("result")) + "%");
                        break;
                    case "Assessment and feedback":
                        results.clear();
                        results.add("The criteria used in marking have been clear in advance: " + Integer.toString((int) nssDocuments.get(7).get("result")) + "%");
                        results.add("Marking and assessment has been fair: " + Integer.toString((int) nssDocuments.get(8).get("result")) + "%");
                        results.add("Feedback on my work has been timely: " + Integer.toString((int) nssDocuments.get(9).get("result")) + "%");
                        results.add("I have received helpful comments on my work: " + Integer.toString((int) nssDocuments.get(10).get("result")) + "%");
                        break;
                    case "Academic support":
                        results.clear();
                        results.add("I have been able to contact staff when I needed to: " + Integer.toString((int) nssDocuments.get(11).get("result")) + "%");
                        results.add("I have received sufficient advice and guidance in relation to my course: " + Integer.toString((int) nssDocuments.get(12).get("result")) + "%");
                        results.add("Good advice was available when I needed to make study choices on my course: " + Integer.toString((int) nssDocuments.get(13).get("result")) + "%");
                        break;
                    case "Organisation and management":
                        results.clear();
                        results.add("The course is well organised and running smoothly: " + Integer.toString((int) nssDocuments.get(14).get("result")) + "%");
                        results.add("The timetable works efficiently for me: " + Integer.toString((int) nssDocuments.get(15).get("result")) + "%");
                        results.add("Any changes in the course or teaching have been communicated effectively: " + Integer.toString((int) nssDocuments.get(16).get("result")) + "%");
                        break;
                    case "Learning resources":
                        results.clear();
                        results.add("The IT resources and facilities provided have supported my learning well: " + Integer.toString((int) nssDocuments.get(17).get("result")) + "%");
                        results.add("The library resources (e.g. books, online services and learning spaces) have supported my learning well: " + Integer.toString((int) nssDocuments.get(18).get("result")) + "%");
                        results.add("I have been able to access course-specific resources (e.g. equipment, facilities, software, collections) when I needed to: " + Integer.toString((int) nssDocuments.get(19).get("result")) + "%");
                        break;
                    case "Learning community":
                        results.clear();
                        results.add("I feel part of a community of staff and students: " + Integer.toString((int) nssDocuments.get(20).get("result")) + "%");
                        results.add("I have had the right opportunities to work with other students as part of my course: " + Integer.toString((int) nssDocuments.get(21).get("result")) + "%");
                        break;
                    case "Student voice":
                        results.clear();
                        results.add("I have had the right opportunities to provide feedback on my course: " + Integer.toString((int) nssDocuments.get(22).get("result")) + "%");
                        results.add("Staff value students' views and opinions about the course: " + Integer.toString((int) nssDocuments.get(23).get("result")) + "%");
                        results.add("It is clear how students' feedback on the course has been acted on: " + Integer.toString((int) nssDocuments.get(24).get("result")) + "%");
                        results.add("The students' union (association or guild) effectively represents students' academic interests " + Integer.toString((int) nssDocuments.get(25).get("result")) + "%");
                        break;
                    case "Overall satisfaction":
                        results.clear();
                        results.add("Overall, I am satisfied with the quality of the course: " + Integer.toString((int) nssDocuments.get(26).get("result")) + "%");
                        break;
                }

                ArrayAdapter<String> adapter = new ArrayAdapter(NSSActivity.this, R.layout.custom_text_view, results);
                questionsListView.setAdapter(adapter);
                adapter.notifyDataSetChanged();

                if(predictedTweets != null) {
                    FindIterable<Document> documents = predictedTweets.get(nssQuestions.getSelectedItem().toString());

                    int total = 0;
                    int pos = 0;
                    assert documents != null;
                    for(Document d : documents) {
                        int sentiment = (int) d.get("sentiment");
                        if(sentiment == 1) pos++;
                        total++;
                    }
                    if(total > 0) {
                        float p = (((float) pos) / total) * 100;
                        DecimalFormat REAL_FORMATTER = new DecimalFormat("0.###");
                        percentage.setText("Tweet Positivity: " + REAL_FORMATTER.format(p) + "%");
                    } else {
                        percentage.setText("N/A");
                    }
                } else {
                    percentage.setText("No tweet data available");
                }


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });
    }

    @SuppressLint("ClickableViewAccessibility")
    void configureListeners(final String email, final String name, final String university, final String year) {
        setListViewHeightBasedOnChildren(questionsListView);
        questionsListView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                v.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                comparisonTweets(year, email, name, university);
            }
        });
    }

    public void comparisonTweets(String year, String email, String name, String university) {
        Intent intent = new Intent(getApplicationContext(), NSSComparisonsActivity.class);
        Bundle b = new Bundle();
        b.putString("email", email);
        b.putString("name", name);
        b.putString("university", university);
        b.putString("year", year);
        intent.putExtras(b);
        startActivity(intent);
    }

    /** Credit to user 'arshu' from StackOverflow **/
    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null)
            return;

        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.UNSPECIFIED);
        int totalHeight = 0;
        View view = null;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            view = listAdapter.getView(i, view, listView);
            if (i == 0)
                view.setLayoutParams(new ViewGroup.LayoutParams(desiredWidth, ViewGroup.LayoutParams.WRAP_CONTENT));

            view.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += view.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }
}
