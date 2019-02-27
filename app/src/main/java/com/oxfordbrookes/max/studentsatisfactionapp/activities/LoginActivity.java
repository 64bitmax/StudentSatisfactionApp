package com.oxfordbrookes.max.studentsatisfactionapp.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.StrictMode;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.oxfordbrookes.max.studentsatisfactionapp.R;
import com.oxfordbrookes.max.studentsatisfactionapp.database.DBClient;
import com.oxfordbrookes.max.studentsatisfactionapp.utils.PasswordEncryption;

import org.bson.Document;
import org.bson.types.Binary;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import static com.mongodb.client.model.Filters.eq;

public class LoginActivity extends Activity {
    static final boolean DEBUG_MODE = false;
    AlertDialog.Builder alertDialogBuilder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_login);

        /** Get all the components in the activity **/
        final EditText etEmail = findViewById(R.id.emailEditText);
        final EditText etPassword = findViewById(R.id.passwordEditText);
        final Button btLogin = findViewById(R.id.loginButton);

        alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("You have entered incorrect credentials.");
        alertDialogBuilder.setPositiveButton("Okay.", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                arg0.dismiss();
            }
        });

        DBClient client = null;
        try {
            client = new DBClient(getApplicationContext());
        } catch (IOException e) {
            e.printStackTrace();
        }

        final DBClient finalClient = client;
        btLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login(finalClient, etEmail.getText().toString(), etPassword.getText().toString());
            }
        });
    }

    public void login(DBClient client, String email, String password) {
        if(!DEBUG_MODE) {
            MongoDatabase db = client.getClient().getDatabase("user-info");

            if(checkForDb(client)) {
                MongoCollection<Document> collection = db.getCollection("accounts");
                if(checkForCollection(db)) {
                    Document doc = collection.find(eq("email", email)).first();
                    if(doc != null) {
                        Binary dbHash = (Binary) doc.get("hash");
                        Binary dbSalt = (Binary) doc.get("salt");
                        String nameText = (String) doc.get("name");
                        String uniText = (String) doc.get("university");

                        PasswordEncryption encryption = new PasswordEncryption();

                        try {
                            if(encryption.authenticate(password, dbHash.getData(), dbSalt.getData())) {
                                openDashboard(nameText, email, uniText);
                            } else {
                                alertDialogBuilder.show();
                            }
                        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
                            e.printStackTrace();
                        }
                    } else {
                        alertDialogBuilder.show();
                    }
                } else {
                    alertDialogBuilder.show();
                }
            }
            else {
                alertDialogBuilder.show();
            }
        } else {
            openDashboard("Debugger", "University of Debug", "debug@debugging.com");
        }
    }

    public boolean checkForDb (DBClient client) {
        for(String dbName : client.getClient().listDatabaseNames()) {
            if(dbName.equals("user-info")) {
                return true;
            }
        }
        return false;
    }

    public boolean checkForCollection (MongoDatabase db) {
        for(String collectionName : db.listCollectionNames()) {
            if(collectionName.equals("accounts")) {
                return true;
            }
        }
        return false;
    }

    public void openRegistration(View view) {
        Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
        startActivity(intent);
    }

    public void openDashboard(String name, String university, String email) {
        Intent intent = new Intent(getApplicationContext(), DashboardActivity.class);
        Bundle b = new Bundle();
        b.putString("email", email);
        b.putString("name", name);
        b.putString("university", university);
        intent.putExtras(b);
        startActivity(intent);
    }
}
