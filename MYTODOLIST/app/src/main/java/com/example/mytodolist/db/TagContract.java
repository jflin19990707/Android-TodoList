package com.example.mytodolist.db;

import android.provider.BaseColumns;

public class TagContract {
    public static final String SQL_CREATE_TAG =
            "CREATE TABLE " + Tag.TABLE_NAME
                    + "(" + Tag._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + Tag.COLUMN_TAGNAME + " TEXT) ";


    public static final String SQL_ADD_TAG_COLUMN =
            "ALTER TABLE " + Tag.TABLE_NAME + " ADD " + Tag.COLUMN_TAGNAME + " TEXT";

    private TagContract() {
    }

    public static class Tag implements BaseColumns {
        public static final String TABLE_NAME = "tag";

        public static final String COLUMN_TAGNAME = "tagName";
    }
}
