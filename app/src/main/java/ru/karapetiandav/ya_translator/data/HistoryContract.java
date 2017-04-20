package ru.karapetiandav.ya_translator.data;

import android.provider.BaseColumns;

public final class HistoryContract {

    private HistoryContract() {
    }

    public static final class HistoryTable implements BaseColumns {
        public final static String TABLE_NAME = "history";

        public final static String _ID = BaseColumns._ID;
        public final static String COLUMN_TEXT = "text";
        public final static String COLUMN_TRANS = "trans";
        public final static String COLUMN_LANGS = "langs";
        public final static String COLUMN_FAV = "fav";

        public static final int IN_FAV = 1;
        public static final int NOT_FAV = 0;
    }
}
