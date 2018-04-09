package com.example.pinor.inf8405runner.db;


/**
 * Created by Pinor on 2018-04-08.
 */

public class MongoDBHandler {
    private String dbName = "results";
    private String apiKey = "20oR4y6FRSMjOBjEr35Avm-v1y8c7zcK";
    private String URLPrefix = "https://api.mlab.com/api/1/databases/results/collections/results";

    public String getDbName() {
        return dbName;
    }

    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getURLPrefix() {
        return URLPrefix;
    }

    public void setURLPrefix(String URLPrefix) {
        this.URLPrefix = URLPrefix;
    }
}
