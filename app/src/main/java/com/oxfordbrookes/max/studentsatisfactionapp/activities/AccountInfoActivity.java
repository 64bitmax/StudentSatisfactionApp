package com.oxfordbrookes.max.studentsatisfactionapp.activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.oxfordbrookes.max.studentsatisfactionapp.R;
import com.oxfordbrookes.max.studentsatisfactionapp.utils.DBClient;

import org.bson.Document;
import org.w3c.dom.Text;

import static com.mongodb.client.model.Filters.eq;

public class AccountInfoActivity extends Activity {

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_account_info);

        TextView tvName = findViewById(R.id.textViewInfoName);
        TextView tvEmail = findViewById(R.id.textViewInfoEmail);
        TextView tvUniversity = findViewById(R.id.textViewInfoUniversity);

        final String nameText = getIntent().getStringExtra("name");
        final String emailText = getIntent().getStringExtra("email");
        final String uniText = getIntent().getStringExtra("university");

        tvName.setText(nameText);
        tvName.setTextColor(Color.WHITE);
        tvName.setTextSize(24);
        tvEmail.setText(emailText);
        tvEmail.setTextColor(Color.WHITE);
        tvEmail.setTextSize(24);
        tvUniversity.setText(uniText);
        tvUniversity.setTextColor(Color.WHITE);
        tvUniversity.setTextSize(24);

        final Button backButton = findViewById(R.id.backButtonInfo);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dashboard(nameText, emailText, uniText);
            }
        });
    }

    void dashboard(String name, String email, String university) {
        Intent intent = new Intent(getApplicationContext(), DashboardActivity.class);
        Bundle b = new Bundle();
        b.putString("email", email);
        b.putString("name", name);
        b.putString("university", university);
        intent.putExtras(b);
        startActivity(intent);
    }
}
