package com.code3apps.para.utils;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;

public class QuizContentProvider extends ContentProvider {
    public static final String DB_NAME = "users_quiz.sqlite";
    public static final String PACKAGE_NAME = "com.code3apps.para";
    public static final String DB_PATH = "/data"
            + Environment.getDataDirectory().getAbsolutePath() + "/"
            + PACKAGE_NAME + "/" + "databases";
    public final static String AUTHORIZATION = "quiz-emutator";
    QuizDatabaseHelper mOpenHelper;

    // protected final UriMatcher sURIMatcher = new
    // UriMatcher(UriMatcher.NO_MATCH);
    // protected final static int ALL_ACTIONS = 1;

    public QuizContentProvider(String authorization) {
        // sURIMatcher.addURI(authorization, "actions", ALL_ACTIONS);
    }

    public static QuizDatabaseHelper createDatabaseHelper(Context context) {
        return new QuizDatabaseHelper(context, DB_NAME);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public String getType(Uri uri) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = createDatabaseHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
            String[] selectionArgs, String sortOrder) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
            String[] selectionArgs) {
        // TODO Auto-generated method stub
        return 0;
    }

}