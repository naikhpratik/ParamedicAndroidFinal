package com.code3apps.para;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ListView;

import com.code3apps.para.beans.CardiologyBean;
import com.code3apps.para.beans.FlashcardRecorderBean;
import com.code3apps.para.utils.Const;
import com.code3apps.para.utils.QuizContentProvider;
import com.code3apps.para.utils.QuizDatabaseHelper;

/**
 * Created by Pratik on 5/23/2016.
 */
public class CardiologyActivity extends Activity implements View.OnClickListener {
    private SharedPreferences preference;
    private static final String TAG = "CardiologyActivity";
    private static final boolean DEBUG_ENABLED = true;

    private QuizDatabaseHelper mDbHelper;
    private SQLiteDatabase mDb;
    private FlashcardChapterAdapter mAdapter;
    private ListView mListView;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        log("onCreate");

        // disable default title bar
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // load view
        setContentView(R.layout.cardiology);

        findViewById(R.id.bookmarks).setOnClickListener(this);
        findViewById(R.id.comprehensive_fcards).setOnClickListener(this);
        //prepareData();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        log("onDestroy");

        if (mDb != null)
            mDb.close();
        if (mDbHelper != null)
            mDbHelper.close();
        if (mAdapter != null)
            mAdapter.close();
    }

   /* private void prepareData() {
        mDbHelper = QuizContentProvider.createDatabaseHelper(this);
        mDb = mDbHelper.getWritableDatabase();

        mAdapter = new FlashcardChapterAdapter(this,
                QuizDatabaseHelper.getFlashcardChapterCursor(mDb));
        if (mListView != null)
            mListView.setAdapter(mAdapter);
        else
            log("mListView is null!");
    }*/

    public void onClick(View v) {
        int id = v.getId();
        Intent intent = null;
        switch (id) {
            case R.id.bookmarks:
                intent = new Intent(this, File.class);
                intent.putExtra(Const.KEY_CARDIOLOGY, Const.KEY_BOOKMARK);
                startActivity(intent);
                break;
            case R.id.comprehensive_fcards:
                final int tempIndex = getCardioCardsHistory();
                System.out.print("Temp Index: "+tempIndex);
                if(tempIndex >= 0){
                    //display popup
                    new AlertDialog.Builder(CardiologyActivity.this)
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setTitle(R.string.continue_cardio_cards)
                            .setMessage(R.string.cardio_continue_message)
                            .setPositiveButton(R.string.continue_btn, new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent intent = new Intent(CardiologyActivity.this, File.class);
                                    intent.putExtra(Const.KEY_CARDIOLOGY, Const.KEY_COMPREHENSIVE);
                                    intent.putExtra("index", tempIndex);
                                    startActivity(intent);
                                }
                            })
                            .setNegativeButton(R.string.delete_btn, new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // delete history record
                                    Intent intent = new Intent(CardiologyActivity.this, File.class);
                                    intent.putExtra(Const.KEY_CARDIOLOGY, Const.KEY_COMPREHENSIVE);
                                    intent.putExtra("index", 0);
                                    startActivity(intent);
                                }
                            })
                            .create().show();
                }else{
                    //don't display popup
                    intent = new Intent(this, File.class);
                    intent.putExtra(Const.KEY_CARDIOLOGY, Const.KEY_COMPREHENSIVE);
                    intent.putExtra("index", 0);
                    startActivity(intent);
                }

                break;

            default:
                break;
        }
    }
    private static void log(String msg) {
        if (Const.DEBUG_PHASE && DEBUG_ENABLED) {
            Log.d(TAG, Const.TAG_PREFIX + msg);
        }
    }
    private int getCardioCardsHistory(){
        preference = getSharedPreferences("CardioCardsHistory", Context.MODE_PRIVATE);
        if (preference.contains("index")) {
            int tempIndex;
            tempIndex  = preference.getInt("index", -1);
            return tempIndex;
        }else{
            return -1;
        }
    }

}

