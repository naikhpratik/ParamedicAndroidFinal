package com.code3apps.para;

import com.code3apps.para.utils.Const;
import com.code3apps.para.utils.DBManager;
import com.code3apps.para.utils.Util;
import com.google.analytics.tracking.android.GAServiceManager;
import com.google.analytics.tracking.android.GoogleAnalytics;
import com.google.analytics.tracking.android.Tracker;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Toast;

public class HomeActivity extends Activity implements OnClickListener {
    private static final String TAG = "HomeActivity";
    private static final boolean DEBUG_ENABLED = true;

    public DBManager dbHelper;
    private Tracker mGaTracker;
	private GoogleAnalytics mGaInstance;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        log("onCreate");

        // disable default title bar
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // load view
        setContentView(R.layout.home);

        findViewById(R.id.follow).setOnClickListener(this);
        findViewById(R.id.about).setOnClickListener(this);
        findViewById(R.id.quizzes).setOnClickListener(this);
        findViewById(R.id.flashcards).setOnClickListener(this);
        findViewById(R.id.toolbox).setOnClickListener(this);
        findViewById(R.id.scenarios).setOnClickListener(this);
        
        //Anum Added Code
        findViewById(R.id.img_dosing_home).setOnClickListener(this);
        findViewById(R.id.img_cardiology_home).setOnClickListener(this);

        // Import original database from raw resource users_quiz.db
        dbHelper = new DBManager(this);
  
        dbHelper.openDatabase();
        dbHelper.closeDatabase();
        
        if(Util.getFirstCompleteFlag(this) == Util.DEFAULT_VALUE){
        	Util.putFirstCompleteFlag(this, 0);
        }
        int times = Util.getStartAppTimes(this);
        if(times <= 3){
        	times++;
        	Util.putStartAppTimes(this, times);
        }
        if(Util.getStartAppTimes(this) == 3){
        	showRateDialog();
        }
        // for track
     	mGaInstance = GoogleAnalytics.getInstance(this);
     	mGaTracker = mGaInstance.getTracker(Const.UA_CODE);
     	GAServiceManager.getInstance().setDispatchPeriod(30);
    }

    private void showRateDialog() {
		new AlertDialog.Builder(this)
		.setMessage(R.string.rating_txt)
		.setPositiveButton("Rate", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				Intent i = new Intent(Intent.ACTION_VIEW);
				i.setData(Uri.parse("market://details?id=" + getPackageName()));
				startActivity(i);
			}
		})
		.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		})
		.create().show();
	}

	public void onClick(View v) {
        // TODO Auto-generated method stub
        int id = v.getId();
        Intent intent = null;
        switch (id) {
        case R.id.follow:
            intent = new Intent(this, FollowActivity.class);
            startActivity(intent);
            break;
        case R.id.about:
            intent = new Intent(this, AboutActivity.class);
            startActivity(intent);
            break;
        case R.id.quizzes:
        	mGaTracker.sendEvent("quizzes", "quizzes_clicked", "quizzes", (long) 1);
            intent = new Intent(this, QuizzesActivity.class);
            startActivity(intent);
            break;
        case R.id.flashcards:
        	mGaTracker.sendEvent("flashcards", "flashcards_clicked", "flashcards", (long) 1);
            intent = new Intent(this, FlashcardsActivity.class);
            startActivity(intent);
            break;
        case R.id.toolbox:
        	mGaTracker.sendEvent("toolbox", "toolbox_clicked", "toolbox", (long) 1);
            intent = new Intent(this, ToolboxActivity.class);
            startActivity(intent);
            break;
        case R.id.scenarios:
        	mGaTracker.sendEvent("scenarios", "scenarios_clicked", "scenarios", (long) 1);
            intent = new Intent(this, ScenariosActivity.class);
            startActivity(intent);
            break;
            
        case R.id.img_dosing_home:
        	intent = new Intent(this, DosingActivity.class);
        	startActivity(intent);
        	break;
        	
        case R.id.img_cardiology_home:
       	intent = new Intent(this, CardiologyActivity.class);
        	startActivity(intent);
        	//Toast.makeText(getApplicationContext(), "Cardiology in progress", Toast.LENGTH_LONG).show();
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

}