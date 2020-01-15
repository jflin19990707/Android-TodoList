package com.example.mytodolist.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class TagDBOpenHelper  extends SQLiteOpenHelper {

    private static final String DB_NAME = "TAG.db";
    private static final int DB_VERSION = 2;

    public TagDBOpenHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TagContract.SQL_CREATE_TAG);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        for (int i = oldVersion; i < newVersion; i++) {
            switch (i) {
                case 1:
                    db.execSQL(TagContract.SQL_ADD_TAG_COLUMN);
                    break;
            }
        }
    }
}