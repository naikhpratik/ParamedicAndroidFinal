package com.code3apps.para.ff;

import java.util.List;

import com.code3apps.para.R;
import com.code3apps.para.beans.CheckListDetailBean;
import com.code3apps.para.utils.Const;
import com.code3apps.para.utils.QuizContentProvider;
import com.code3apps.para.utils.QuizDatabaseHelper;

import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class CheckListDetail extends Activity implements OnItemClickListener,
        OnClickListener {
    // for debug
    private static final String TAG = "CheckListDetail";
    private static final boolean DEBUG_ENABLED = true;

    private QuizDatabaseHelper mDbHelper;
    private static SQLiteDatabase mDb = null;
    private List<CheckListDetailBean> mDetailBeans = null;
    private CheckDetailsListAdapter mCheckListDetailAdapter = null;
    private static final int MSG_SHOW_DETAILS_LIST = 0;

    private ListView mCheckDetailsListView = null;
    private String mChapterId = "";
    private String mChapterName = "";

    private ImageView mImgCheckBox = null;
    private CheckListDetailBean mDetailBean = null;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        log("onCreate");

        Intent intent = getIntent();
        mChapterId = intent.getStringExtra(Const.KEY_CHAPTER_ID);
        mChapterName = intent.getStringExtra(Const.KEY_CHAPTER_NAME);

        // disable default title bar
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // load view
        setContentView(R.layout.checklist_detail);

        TextView titleView = (TextView) findViewById(R.id.title);
        titleView.setText(R.string.checklists);
        TextView titleNameView = (TextView) findViewById(R.id.header);
        titleNameView.setText(mChapterName);
        
        mCheckDetailsListView = (ListView) this.findViewById(R.id.listView);
        mDbHelper = QuizContentProvider.createDatabaseHelper(this);
        mDb = mDbHelper.getWritableDatabase();

        mCheckListDetailAdapter = new CheckDetailsListAdapter(this);
        mHandler.sendEmptyMessage(MSG_SHOW_DETAILS_LIST);
        mCheckDetailsListView.setOnItemClickListener(this);

        findViewById(R.id.clear_all).setOnClickListener(this);
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case MSG_SHOW_DETAILS_LIST:
                log("show reminder list");
                mDetailBeans = QuizDatabaseHelper.getCheckListDetailBeans(mDb,
                        mChapterId);
                mCheckListDetailAdapter.setDetailBeans(mDetailBeans);
                // set adapter
                mCheckDetailsListView.setAdapter(mCheckListDetailAdapter);
                break;
            default:
                super.handleMessage(msg);
                break;
            }
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        log("onDestroy");
        if (mDb != null)
            mDb.close();
        if (mDbHelper != null)
            mDbHelper.close();
    }

    public void onItemClick(AdapterView<?> arg0, View arg1, int position,
            long arg3) {
        // TODO Auto-generated method stub
        log("item is clicked");
        mDetailBean = mDetailBeans.get(position);
        mImgCheckBox = (ImageView) arg1.findViewById(R.id.chekbox_img);
        int id = mDetailBean.get_id();
        if (mDetailBean.getChecked() == 0) {
            if (QuizDatabaseHelper.setCheckListCheckedById(mDb, id, true) == 1) {
                mDetailBean.setChecked(1);
                mImgCheckBox.setImageResource(R.drawable.box_checked);
            }
        } else {
            if (QuizDatabaseHelper.setCheckListCheckedById(mDb, id, false) == 1) {
                mDetailBean.setChecked(0);
                mImgCheckBox.setImageResource(R.drawable.box_blank);
            }
        }
    }

    public void onClick(View v) {
        // TODO Auto-generated method stub
        int id = v.getId();
        switch (id) {
        case R.id.clear_all:
            QuizDatabaseHelper.setCheckListCheckedByChapterId(mDb, mChapterId,
                    false);
            // Need update all mImgCheckBox here
            mHandler.sendEmptyMessage(MSG_SHOW_DETAILS_LIST);
            break;
        default:
            break;
        }

    }

    // ---- for test ----
    private static void log(String msg) {
        if (Const.DEBUG_PHASE && DEBUG_ENABLED) {
            Log.d(TAG, Const.TAG_PREFIX + msg);
        }
    }

}