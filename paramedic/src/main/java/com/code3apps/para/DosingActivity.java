package com.code3apps.para;

import java.util.ArrayList;

import com.code3apps.para.beans.DosingBean;
import com.code3apps.para.utils.Const;
import com.code3apps.para.utils.QuizContentProvider;
import com.code3apps.para.utils.QuizDatabaseHelper;
import com.google.analytics.tracking.android.Log;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;

public class DosingActivity extends Activity implements OnClickListener {

	private SharedPreferences preference;
	
	/*
	 * (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		 // disable default title bar
        requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.dosing);
		
		findViewById(R.id.img_comprehensive_quiz_dosing).setOnClickListener(this);
		findViewById(R.id.img_bookmark_dosing).setOnClickListener(this);
		
		
	}

	@Override
	public void onClick(View v) {
		Intent intent = null;
		switch (v.getId()) {
		
		case R.id.img_comprehensive_quiz_dosing:
			final int tempIndex = getDosingCardsHistory();
			if(tempIndex >= 0){
				//display popup
				new AlertDialog.Builder(DosingActivity.this)
    			.setIcon(android.R.drawable.ic_dialog_alert)
    			.setTitle(R.string.continue_dosing_cards)
    			.setMessage(R.string.dosing_continue_message)
    			.setPositiveButton(R.string.continue_btn, new DialogInterface.OnClickListener() {

    				@Override
    				public void onClick(DialogInterface dialog, int which) {
    					Intent intent = new Intent(DosingActivity.this, DosingComprehensive.class);
    		        	intent.putExtra(Const.KEY_DOSING, Const.KEY_COMPREHENSIVE);
    		        	intent.putExtra("index", tempIndex); 
    		        	startActivity(intent);
    				}
    			})
    			.setNegativeButton(R.string.delete_btn, new DialogInterface.OnClickListener() {

    				@Override
    				public void onClick(DialogInterface dialog, int which) {
    					// delete history record
    					Intent intent = new Intent(DosingActivity.this, DosingComprehensive.class);
    		        	intent.putExtra(Const.KEY_DOSING, Const.KEY_COMPREHENSIVE);
    		        	intent.putExtra("index", 0);
    		        	startActivity(intent);
    				}
    			})
    			.create().show();
			}else{
				//don't display popup
				intent = new Intent(this, DosingComprehensive.class);
	        	intent.putExtra(Const.KEY_DOSING, Const.KEY_COMPREHENSIVE);
	        	intent.putExtra("index", 0);
	        	startActivity(intent);
			}
			
			break;
			
		case R.id.img_bookmark_dosing:
			 intent = new Intent(this, DosingComprehensive.class);
	         intent.putExtra(Const.KEY_DOSING, Const.KEY_BOOKMARK);
	         startActivity(intent);
			break;

		default:
			break;
		}
		
	}
	/*
	 * 
	 */
	private int getDosingCardsHistory(){
		preference = getSharedPreferences("DosingCardsHistory", Context.MODE_PRIVATE);
		if (preference.contains("index")) {
			int tempIndex;
			tempIndex  = preference.getInt("index", -1);
			return tempIndex;
		}else{
			return -1;
		}
	}
}
