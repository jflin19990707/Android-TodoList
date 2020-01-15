package com.example.mytodolist.db;

import android.provider.BaseColumns;

public class TodoContract {
    public static final String SQL_CREATE_NOTES =
            "CREATE TABLE " + TodoNote.TABLE_NAME
                    + "(" + TodoNote._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + TodoNote.COLUMN_COUNTSTAR + " INTEGER,"
                    + TodoNote.COLUMN_HEADLINE + " TEXT, "
                    + TodoNote.COLUMN_FILES + " TEXT, "
                    + TodoNote.COLUMN_STATE + " INTEGER, "
                    + TodoNote.COLUMN_PRIORITY + " INTEGER,"
                    + TodoNote.COLUMN_TAG + " TEXT, "

                    + TodoNote.COLUMN_SCHEDULE + " TEXT, "
                    + TodoNote.COLUMN_DEADLINE + " TEXT, "
                    + TodoNote.COLUMN_SHOW + " TEXT, "
                    + TodoNote.COLUMN_REPEAT_SHOW_NUM + " TEXT, "
                    + TodoNote.COLUMN_REPEAT_SHOW + " TEXT, "

                    + TodoNote.COLUMN_CONTENT + " TEXT) ";

    public static final String SQL_ADD_PRIORITY_COLUMN =
            "ALTER TABLE " + TodoNote.TABLE_NAME + " ADD " + TodoNote.COLUMN_PRIORITY + " INTEGER";

    private TodoContract() {
    }

    public static class TodoNote implements BaseColumns {
        public static final String TABLE_NAME = "note";

        public static final String _ID = "_id";
        public static final String COLUMN_COUNTSTAR = "countstar";
        public static final String COLUMN_HEADLINE = "headline";
        public static final String COLUMN_FILES = "files";
        public static final String COLUMN_STATE = "state";
        public static final String COLUMN_PRIORITY = "priority";
        public static final String COLUMN_TAG = "tag";

        public static final String COLUMN_SCHEDULE = "schedule";
        public static final String COLUMN_DEADLINE = "deadline";
        public static final String COLUMN_SHOW = "show";
        public static final String COLUMN_REPEAT_SHOW = "repeat_show";
        public static final String COLUMN_REPEAT_SHOW_NUM = "repeat_show_num";

        public static final String COLUMN_CONTENT = "content";
    }
}
