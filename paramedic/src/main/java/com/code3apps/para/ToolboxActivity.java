package com.code3apps.para;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;

import com.code3apps.para.ff.SubCheckListsActivity;
import com.code3apps.para.utils.Const;

public class ToolboxActivity extends Activity implements OnClickListener {
	private static final String TAG = "ToolboxActivity";
	private static final boolean DEBUG_ENABLED = true;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		log("onCreate");

		// disable default title bar
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		// load view
		setContentView(R.layout.toolbox);

		findViewById(R.id.skill_sheets).setOnClickListener(this);
		findViewById(R.id.diff_rule_outs).setOnClickListener(this);
		findViewById(R.id.mdedical_scene).setOnClickListener(this);
		findViewById(R.id.reference_terms).setOnClickListener(this);
		findViewById(R.id.bookmarks).setOnClickListener(this);
	}

	public void onClick(View v) {
		int id = v.getId();
		Intent intent = null;
		switch (id) {
		case R.id.skill_sheets:
			intent = new Intent(this, SkillSheetsActivity.class);
			startActivity(intent);
			break;
		case R.id.diff_rule_outs:
			intent = new Intent(this, AllDiffRuleOutActivity.class);
			startActivity(intent);
			break;
		case R.id.mdedical_scene:
			intent = new Intent(this, SubCheckListsActivity.class);
			intent.putExtra(Const.KEY_PARENT_ID, "2");
			intent.putExtra(Const.KEY_CHAPTER_NAME, "MEDICAL");
			startActivity(intent);
			break;
		case R.id.reference_terms:
            intent = new Intent(this, ReferencesActivity.class);
            startActivity(intent);
            break;
		case R.id.bookmarks:
			intent = new Intent(this, BookmarksActivity.class);
            intent.putExtra(Const.KEY_BOOKMARK_TYPE,
                    Const.VALUE_TYPE_SKILL_SHEET);
            startActivity(intent);
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