package com.code3apps.para.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import com.code3apps.para.R;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class DBManager {
    private static final String TAG = "DBManager";
    private static final boolean DEBUG_ENABLED = true;
    private final int BUFFER_SIZE = 400000;
    private ArrayList<Integer> mHasBookmarksList;
    private SQLiteDatabase mDatabase;
    private Context mContext;

    public DBManager(Context context) {
        mContext = context;
    }

    public void openDatabase() {
       this.openDatabase(QuizContentProvider.DB_PATH + "/"
                + QuizContentProvider.DB_NAME);
    }
    
	private void openDatabase(String dbfile) {
		log("openDatabase");
		if (!(new File(dbfile).exists())) {
			log("copy new file");
			copyFile(dbfile);
		}else{
			log("existing");
			if(!getOldDBVersion(dbfile).trim().equalsIgnoreCase(Const.LAST_DB_VERSION)){
				backUpBookMarks(dbfile);
				Util.deleteFile(dbfile);
				log("existing db is deleted");
				copyFile(dbfile);

				if(mHasBookmarksList.size() > 0)
					updateNewDBBookmark(dbfile);
			}
		}
	}
    
	private void backUpBookMarks(String dbfile) {	// back up old DB's bookmarks
		mHasBookmarksList = new ArrayList<Integer>();
		mDatabase = SQLiteDatabase.openOrCreateDatabase(dbfile, null);
		if(mDatabase.isOpen()){
			String sql = "select id from quiz where bookmark = 1";
			Cursor cursor = mDatabase.rawQuery(sql, null);
			while (cursor.moveToNext()) {
				mHasBookmarksList.add(cursor.getInt(0));
			}
		}
	}

	private void updateNewDBBookmark(String dbfile){
		mDatabase = SQLiteDatabase.openOrCreateDatabase(dbfile, null);
		String sql = "update quiz set bookmark = 1 where id in (";
		for(int i=0; i<mHasBookmarksList.size(); i++){
			sql += mHasBookmarksList.get(i)+",";
		}
		sql = sql.substring(0, sql.length()-1);
		sql += ")";
		mDatabase.execSQL(sql);
	}
	
	/**
	 * 
	 * @param dbfile
	 * @return
	 */
	public String getOldDBVersion(String dbfile) {
		String sql;
		String version = "-1";
		mDatabase = SQLiteDatabase.openOrCreateDatabase(dbfile, null);
		sql = "SELECT version FROM Version";
		log("sql is " + sql);
		Cursor cursor = mDatabase.rawQuery(sql, null);
		if (null != cursor) {
			cursor.moveToFirst();
			version = cursor.getString(0);
		}
		log("version: " + String.valueOf(version));
		cursor.close();
		mDatabase.close();
		return version;
	}
	
    /**
     * 
     * @param dbfile
     */
	private void copyFile(String dbfile) {
		try {
			InputStream is = this.mContext.getResources().openRawResource(
					R.raw.users_quiz);
			File path = new File(dbfile.substring(0, dbfile.length()
					- QuizContentProvider.DB_NAME.length()));
			if (!path.exists())
				path.mkdir();
			FileOutputStream fos = new FileOutputStream(dbfile);
			byte[] buffer = new byte[BUFFER_SIZE];
			int count = 0;
			while ((count = is.read(buffer)) > 0) {
				fos.write(buffer, 0, count);
			}
			fos.close();
			is.close();
		} catch (FileNotFoundException e) {
			log("File not found");
			e.printStackTrace();
		} catch (IOException e) {
			log("IO Exception");
			e.printStackTrace();
		}
	}


    public void closeDatabase() {
        log("closeDatabase");
        if (mDatabase != null)
            mDatabase.close();
    }

    private static void log(String msg) {
        if (Const.DEBUG_PHASE && DEBUG_ENABLED) {
            Log.d(TAG, Const.TAG_PREFIX + msg);
        }
    }

}