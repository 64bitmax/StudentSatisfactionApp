package com.oxfordbrookes.max.studentsatisfactionapp.database;

import android.content.Context;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.oxfordbrookes.max.studentsatisfactionapp.utils.Config;

import org.bson.Document;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Properties;

import static com.mongodb.client.model.Filters.eq;

public class DBClient {
    private MongoClient client;

    public DBClient(Context context) throws IOException {
        client = new MongoClient(new MongoClientURI(Config.getProperty(context, "uri")));
    }

    public MongoClient getClient() {
        return client;
    }

    public HashMap<String, FindIterable<Document>> getPredictedTweets() {
        MongoDatabase db = client.getDatabase("predictions");
        MongoCollection<Document> collection = db.getCollection("tweet_sentiments_bayes");

        FindIterable<Document> academicSupport = collection.find(eq("category", "academic_support"));
        FindIterable<Document> assessmentAndFeedback =  collection.find(eq("category", "assessment_and_feedback"));
        FindIterable<Document> learningCommunity = collection.find(eq("category", "learning_community"));
        FindIterable<Document> learningOpportunities =  collection.find(eq("category", "learning_opportunities"));
        FindIterable<Document> learningResources = collection.find(eq("category", "learning_resources"));
        FindIterable<Document> organisationAndManagement =  collection.find(eq("category", "organisation_and_management"));
        FindIterable<Document> overallTeaching = collection.find(eq("category", "overall_teaching"));
        FindIterable<Document> studentVoice =  collection.find(eq("category", "student_voice"));
        FindIterable<Document> overall =  collection.find(eq("category", "overall"));

        HashMap<String, FindIterable<Document>> categories = new HashMap<>();
        categories.put("Academic support", academicSupport);
        categories.put("Assessment and feedback", assessmentAndFeedback);
        categories.put("Learning community", learningCommunity);
        categories.put("Learning opportunities", learningOpportunities);
        categories.put("Learning resources", learningResources);
        categories.put("Organisation and management", organisationAndManagement);
        categories.put("Overall teaching", overallTeaching);
        categories.put("Student voice", studentVoice);
        categories.put("Overall satisfaction", overall);

        return categories;
    }
}
