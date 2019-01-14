package com.oxfordbrookes.max.studentsatisfactionapp.utils;

import android.content.Context;

import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Properties;

public class DBClient {
    private MongoClient client;

    public DBClient(Context context) throws IOException {
        MongoCredential credential = MongoCredential.createCredential(
                Config.getProperty(context, "username"),
                Config.getProperty(context, "dbAuth"),
                Config.getProperty(context, "password").toCharArray());
        client = new MongoClient(
                new ServerAddress(Config.getProperty(context, "address"),
                Integer.valueOf(Config.getProperty(context, "port"))),
                Arrays.asList(credential));
    }

    public MongoClient getClient() {
        return client;
    }
}
