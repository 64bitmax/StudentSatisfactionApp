package com.oxfordbrookes.max.studentsatisfactionapp.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.StrictMode;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.oxfordbrookes.max.studentsatisfactionapp.R;
import com.oxfordbrookes.max.studentsatisfactionapp.database.DBClient;
import com.oxfordbrookes.max.studentsatisfactionapp.utils.PasswordEncryption;

import org.bson.Document;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.exists;

public class RegisterActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_register);

        DBClient client = null;
        try {
            client = new DBClient(getApplicationContext());
        } catch (IOException e) {
            e.printStackTrace();
        }

        /** Get all the components in the activity **/
        final EditText etName = findViewById(R.id.registerNameEditText);
        final EditText etEmail = findViewById(R.id.registerEmailEditText);
        final EditText etPassword = findViewById(R.id.registerPasswordEditText);
        final Button btRegister = findViewById(R.id.registerButton);
        final TextView tvLogin = findViewById(R.id.backToLoginTextView);
        final Spinner spUniversities = findViewById(R.id.universitySpinner);

        /** Add listener to go back to login activity **/
        tvLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openLogin(v);
            }
        });

        /** Add listener to register **/
        final DBClient finalClient = client;
        btRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                register(
                        v,
                        finalClient,
                        etName.getText().toString(),
                        etEmail.getText().toString(),
                        etPassword.getText().toString(),
                        spUniversities.getSelectedItem().toString()
                );
            }
        });


        /** Load universities into the drop-down spinner menu **/
        List<String> universityNames = loadUniversities(client);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.spinner_items, universityNames);
        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        spUniversities.setAdapter(adapter);
    }

    public void register(View view, DBClient client, String name, String email, String password, String university) {
        if(validateEmail(email, client)) {
            MongoDatabase db = client.getClient().getDatabase("user-info");
            MongoCollection<Document> collection = db.getCollection("accounts");

            PasswordEncryption encryption = new PasswordEncryption();

            try {
                byte[] salt = encryption.generateSalt();
                byte[] hash = encryption.getEncryptedPassword(password, salt);

                Document doc = new Document("name", name).append("email", email).append("hash", hash).append("salt", salt).append("university", university);
                collection.insertOne(doc);
            } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
                e.printStackTrace();
            }

            /** Open the login page **/
            openLogin(view);
        } else {
            EditText etEmail = findViewById(R.id.registerEmailEditText);
            etEmail.setText("");
            etEmail.setHint("Email");
            AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
            builder.setCancelable(true);
            builder.setTitle("ERROR: Email Verification");
            builder.setMessage("The email provided is already in use or is not a valid email, please use another.");

            builder.setPositiveButton("Okay.", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });

            builder.show();
        }
    }

    public void openLogin(View view) {
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(intent);
    }

    boolean validateEmail(String email, DBClient client) {
        /** Check regex for email structure **/
        String emailRegex = "^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$";
        Pattern emailPattern = Pattern.compile(emailRegex, Pattern.CASE_INSENSITIVE);
        Matcher matcher = emailPattern.matcher(email);

        /** Check email does not exist **/
        MongoDatabase db = client.getClient().getDatabase("user-info");
        MongoCollection<Document> collection = db.getCollection("accounts");

        return matcher.find() && collection.count(eq("email",email)) == 0;
    }

    List<String> loadUniversities(DBClient client) {
        MongoDatabase db = client.getClient().getDatabase("university_info");
        MongoCollection<Document> collection = db.getCollection("universities_2018");

        List<Document> documents = collection.find().into(new ArrayList<Document>());
        List<String> universityNames = new ArrayList<>();
        for (Document doc : documents) {
            universityNames.add((String) doc.get("university"));
        }

        return universityNames;
    }
}
