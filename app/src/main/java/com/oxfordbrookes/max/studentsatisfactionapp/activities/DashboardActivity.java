package com.oxfordbrookes.max.studentsatisfactionapp.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.oxfordbrookes.max.studentsatisfactionapp.R;

public class DashboardActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_dashboard);

        View vAccountInfo = findViewById(R.id.viewAccountInfo);
        View vStudentSatisfaction = findViewById(R.id.viewStudentSatisfaction);
        View vNSSComparisons = findViewById(R.id.viewNSSComparisons);
        View vLogOut = findViewById(R.id.viewLogOut);
        TextView tvWelcomeName = findViewById(R.id.welcomeTextView);
        final String email = getIntent().getStringExtra("email");
        final String name = getIntent().getStringExtra("name");
        final String university = getIntent().getStringExtra("university");

        String welcomeMessage = "Welcome, " + name + "!";
        tvWelcomeName.setText(welcomeMessage);
        tvWelcomeName.setTextColor(Color.WHITE);
        tvWelcomeName.setTextSize(36);

        vAccountInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                accountInfo(name, email, university);
            }
        });

        vStudentSatisfaction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                studentSatisfaction(name, email, university);
            }
        });

        vNSSComparisons.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nssComparisons(name, email, university);
            }
        });

        vLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logOut(v);
            }
        });
    }

    public void nssComparisons(String name, String email, String university) {
        Intent intent = new Intent(getApplicationContext(), NSSComparisonsActivity.class);
        Bundle b = new Bundle();
        b.putString("email", email);
        b.putString("name", name);
        b.putString("university", university);
        intent.putExtras(b);
        startActivity(intent);
    }

    public void accountInfo(String name, String email, String university) {
        Intent intent = new Intent(getApplicationContext(), AccountInfoActivity.class);
        Bundle b = new Bundle();
        b.putString("email", email);
        b.putString("name", name);
        b.putString("university", university);
        intent.putExtras(b);
        startActivity(intent);
    }

    public void logOut(View v) {
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(intent);
    }

    public void studentSatisfaction(String name, String email, String university) {
        Intent intent = new Intent(getApplicationContext(), StudentSatisfactionActivity.class);
        Bundle b = new Bundle();
        b.putString("email", email);
        b.putString("name", name);
        b.putString("university", university);
        intent.putExtras(b);
        startActivity(intent);
    }
}
