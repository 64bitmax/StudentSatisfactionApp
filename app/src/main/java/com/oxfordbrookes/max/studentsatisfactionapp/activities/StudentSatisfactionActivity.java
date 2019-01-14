package com.oxfordbrookes.max.studentsatisfactionapp.activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.oxfordbrookes.max.studentsatisfactionapp.R;
import com.oxfordbrookes.max.studentsatisfactionapp.utils.DBClient;
import com.oxfordbrookes.max.studentsatisfactionapp.utils.TweetSentiment;
import com.oxfordbrookes.max.studentsatisfactionapp.utils.TweetSentimentAdapter;

import org.bson.Document;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class StudentSatisfactionActivity extends Activity {
    private DBClient client;
    private ListView tweetsList;
    private Button graphButton;
    private Button backButton;
    private SearchView searchBar;
    private TweetSentimentAdapter adapter;
    private Spinner nssQuestions;
    private FindIterable<Document> tweetDocs;

    @SuppressLint({"ClickableViewAccessibility", "ResourceType"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_student_satisfaction);

        final String name = getIntent().getStringExtra("name");
        final String email = getIntent().getStringExtra("email");
        final String university = getIntent().getStringExtra("university");

        /** Instantiate activity components **/
        graphButton = findViewById(R.id.buttonGraphs);
        backButton = findViewById(R.id.buttonSatisfactionBack);
        searchBar = findViewById(R.id.searchTweets);
        tweetsList = findViewById(R.id.listViewTweets);
        nssQuestions = findViewById(R.id.spinnerNssCategory);
        try {
            client = new DBClient(getApplicationContext());
        } catch (IOException e) {
            e.printStackTrace();
        }
        tweetDocs = getTweets(client);
        adapter = loadTweets();

        /** Configure all components **/
        configureSearchBar();
        configureQuestions();
        configureListeners(email, name, university);
    }

    private void configureQuestions() {
        List<String> nssQuestionList = new ArrayList<>();
        nssQuestionList.add("Quality of teaching");
        nssQuestionList.add("Learning opportunities");
        nssQuestionList.add("Assessment and feedback");
        nssQuestionList.add("Academic support");
        nssQuestionList.add("Organisation and management");
        nssQuestionList.add("Learning resources");
        nssQuestionList.add("Learning community");
        nssQuestionList.add("Student voice");
        nssQuestionList.add("Overall satisfaction");
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.spinner_items, nssQuestionList);
        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        nssQuestions.setAdapter(adapter);
    }

    TweetSentimentAdapter loadTweets() {
        List<TweetSentiment> tweetSentiments = new ArrayList<>();

        for(Document doc: tweetDocs) {
            String tweet = (String) doc.get("tweet");
            int sentiment = (int) doc.get("sentiment");
            tweetSentiments.add(new TweetSentiment(tweet, sentiment));
        }

        return new TweetSentimentAdapter(this, tweetSentiments);
    }

    @SuppressLint("ClickableViewAccessibility")
    void configureListeners(final String email, final String name, final String university) {
        setListViewHeightBasedOnChildren(tweetsList);
        tweetsList.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                v.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });

        graphButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                statisticsGraphs(email, name, university);
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dashboard(email, name, university);
            }
        });

        tweetsList.setAdapter(adapter);
    }

    void configureSearchBar() {
        int id = searchBar.getContext().getResources().getIdentifier("android:id/search_src_text", null, null);
        TextView textView = searchBar.findViewById(id);
        textView.setTextColor(Color.WHITE);

        searchBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchBar.setIconified(false);
            }
        });

        searchBar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return false;
            }
        });
    }

    FindIterable<Document> getTweets(DBClient client) {
        MongoDatabase db = client.getClient().getDatabase("predictions");
        MongoCollection<Document> collection = db.getCollection("tweet_sentiments_bayes");
        return collection.find();
    }

    public void statisticsGraphs(String email, String name, String university) {
        Intent intent = new Intent(getApplicationContext(), SatisfactionStatisticsActivity.class);
        Bundle b = new Bundle();
        b.putString("email", email);
        b.putString("name", name);
        b.putString("university", university);
        intent.putExtras(b);
        startActivity(intent);
    }

    public void dashboard(String email, String name, String university) {
        Intent intent = new Intent(getApplicationContext(), DashboardActivity.class);
        Bundle b = new Bundle();
        b.putString("email", email);
        b.putString("name", name);
        b.putString("university", university);
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
