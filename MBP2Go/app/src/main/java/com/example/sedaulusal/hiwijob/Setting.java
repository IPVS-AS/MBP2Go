package com.example.sedaulusal.hiwijob;

public class Setting {

    public static final String TABLE_NAME_URL = "url";

    public static final String COLUMN_ID_URL = "id";
    public static final String COLUMN_URL = "urladress";
    //public static final String COLUMN_TIMESTAMP = "timestamp";

    private int id;
    private String url;
    private String timestamp;


    // Create table SQL query
    public static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME_URL + "("
                    + COLUMN_ID_URL + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + COLUMN_URL + " TEXT"
                   // + COLUMN_TIMESTAMP + " DATETIME DEFAULT CURRENT_TIMESTAMP"
                    + ")";

    public Setting() {
    }

    public Setting(int id, String url) {
        this.id = id;
        this.url = this.url;
    }

    public Setting(String url) {
        this.url = this.url;
    }

    public int getId() {
        return id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String note) {
        this.url = note;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}

