package com.code3apps.para;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.code3apps.para.beans.BookmarkBean;
import com.code3apps.para.beans.ChapterBean;
import com.code3apps.para.beans.HtmlFileBean;
import com.code3apps.para.utils.Const;
import com.code3apps.para.utils.QuizContentProvider;
import com.code3apps.para.utils.QuizDatabaseHelper;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextPaint;
import android.text.TextUtils.TruncateAt;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class BookmarksActivity extends Activity implements OnItemClickListener {
	private static final String TAG = "BookmarksActivity";
	private static final boolean DEBUG_ENABLED = true;
	private static final String LIST_COLUMN_NAME = "name";
	private static final String LIST_COLUMN_ID = "id";

	private Context mContext;
	private QuizDatabaseHelper mDbHelper;
	private SQLiteDatabase mDb;
	// private BaseAdapter mAdapter;
	private ListView mListView;
	private ExpandableListView mExListView;
	private ImageView mTitleBelowBar;
	private TextView mNoData;
	private String mType;
	private List<ChapterBean> mChapters;
	private List<HtmlFileBean> mHtmls;
	private List<BookmarkBean> mBookmarks;
	private int[] mIds; 

	private ArrayList<ChapterBean> mGroupArray;
	private ArrayList<List<BookmarkBean>> mChildArray;
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		log("onCreate");
		mType = getIntent().getStringExtra(Const.KEY_BOOKMARK_TYPE);
		mContext = this;

		// disable default title bar
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		// load view
		setContentView(R.layout.bookmarks);

		mGroupArray = new ArrayList<ChapterBean>();
		mChildArray = new ArrayList<List<BookmarkBean>>();
		prepareViews();
//		prepareData();
	}

	@Override
	protected void onStart() {
		super.onStart();
		if(mChapters!=null){
			mChapters.clear();
		}
		if(mHtmls!=null){
			mHtmls.clear();
		}
		if(mBookmarks!=null){
			mBookmarks.clear();
		}
		if(mGroupArray!=null){
			mGroupArray.clear();
		}
		if(mChildArray!=null){
			mChildArray.clear();
		}
		
		prepareData();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		log("onDestroy");

		if (mDb != null)
			mDb.close();
		if (mDbHelper != null)
			mDbHelper.close();
	}

	private void prepareViews() {
		//        mBackground = findViewById(R.id.background);
		//        int height = this.getWindow().getWindowManager().getDefaultDisplay().getHeight();
		//        mBackground.setMinimumHeight(height/2);
		mNoData = (TextView) findViewById(R.id.no_data);
		mTitleBelowBar = (ImageView) findViewById(R.id.quiz_bar);
		mListView = (ListView) findViewById(R.id.chapter_list);
		mListView.setOnItemClickListener(this);

		mExListView = (ExpandableListView) findViewById(R.id.ex_list);
		mExListView.setOnChildClickListener(new OnChildClickListener(){

			@Override
			public boolean onChildClick(ExpandableListView parent, View v,
					int groupPosition, int childPosition, long id) {
				// TODO Auto-generated method stub
				int childSize = mChildArray.get(groupPosition).size();
				mIds = new int[childSize];
				for(int i=0; i<childSize; i++){
					mIds[i] = mChildArray.get(groupPosition).get(i).getId();
				}
				Intent intent = null;
				if (mType.equals(Const.VALUE_TYPE_QUIZ)) {
					intent = new Intent(BookmarksActivity.this, AllQuiz.class);
				} else if (mType.equals(Const.VALUE_TYPE_FLASHCARD)) {
					intent = new Intent(BookmarksActivity.this, FlashcardBookMarked.class);
				}
				intent.putExtra(Const.KEY_CHAPTER_ID, ""+mGroupArray.get(groupPosition).getId());
				intent.putExtra(Const.KEY_CHAPTER_NAME, mGroupArray.get(groupPosition).getName());
				intent.putExtra(Const.KEY_ID, ""+mChildArray.get(groupPosition).get(childPosition).getId());
				intent.putExtra(Const.KEY_IDS, mIds);
				intent.putExtra(Const.KEY_POSITION, childPosition);
				startActivity(intent);
				return false;
			}

		});
	}

	private void prepareData() {
		mDbHelper = QuizContentProvider.createDatabaseHelper(this);
		mDb = mDbHelper.getWritableDatabase();

		if (mType.equals(Const.VALUE_TYPE_QUIZ)) {
			//            mBackground.setBackgroundResource(R.drawable.quizzes_bookmarks_new);
			mTitleBelowBar.setImageResource(R.drawable.quiz_bar);
			mChapters = QuizDatabaseHelper.getBookmarkedQuizChapters(mDb);
			for(int i=0; i<mChapters.size(); i++){
				mGroupArray.add(mChapters.get(i));
				mBookmarks = QuizDatabaseHelper.getBookmarks(mDb, mType, ""+mChapters.get(i).getId());
				mChildArray.add(mBookmarks);
			}
			ExpandableAdapter exAdapter = new ExpandableAdapter(this);
			mExListView.setAdapter(exAdapter);
//			for(int i = 1; i < exAdapter.getGroupCount(); i++){
//				mExListView.expandGroup(i);
//			}
			mListView.setVisibility(View.GONE);
			mExListView.setVisibility(View.VISIBLE);
		} else if (mType.equals(Const.VALUE_TYPE_FLASHCARD)) {
			//            mBackground.setBackgroundResource(R.drawable.flashcards_bookmarks_new);
			mTitleBelowBar.setImageResource(R.drawable.fcards_bar);
			mChapters = QuizDatabaseHelper.getBookmarkedFlashChapters(mDb);
			for(int i=0; i<mChapters.size(); i++){
				mGroupArray.add(mChapters.get(i));
				mBookmarks = QuizDatabaseHelper.getBookmarks(mDb, mType, ""+mChapters.get(i).getId());
				mChildArray.add(mBookmarks);
			}
			ExpandableAdapter exAdapter = new ExpandableAdapter(this);
			mExListView.setAdapter(exAdapter);
//			for(int i = 1; i < exAdapter.getGroupCount(); i++){
//				mExListView.expandGroup(i);
//			}
			mListView.setVisibility(View.GONE);
			mExListView.setVisibility(View.VISIBLE);
		} else if (mType.equals(Const.VALUE_TYPE_SKILL_SHEET)) {
			//            mBackground.setBackgroundResource(R.drawable.skillsheet_bookmarks_new);
			mTitleBelowBar.setImageResource(R.drawable.toolbox_bar);
			mHtmls = QuizDatabaseHelper.getBookmarkedHtmls(mDb, mType);
			mListView.setAdapter(new SimpleAdapter(mContext, getData(),
					R.layout.chapter_item, new String[] { LIST_COLUMN_NAME },
					new int[] { R.id.name }));
			mExListView.setVisibility(View.GONE);
			mListView.setVisibility(View.VISIBLE);
		}else if (mType.equals(Const.VALUE_TYPE_Cardiology))
		{
			mTitleBelowBar.setImageResource(R.drawable.cardio_bar);
		} else {
			//            mBackground.setBackgroundResource(R.drawable.scenarios_bookmarks_new);
			mTitleBelowBar.setImageResource(R.drawable.scenarios_bar);
			mHtmls = QuizDatabaseHelper.getBookmarkedHtmls(mDb, mType);
			mListView.setAdapter(new SimpleAdapter(mContext, getData(),
					R.layout.chapter_item, new String[] { LIST_COLUMN_NAME },
					new int[] { R.id.name }));
			mExListView.setVisibility(View.GONE);
			mListView.setVisibility(View.VISIBLE);
		}
		
		if(mType.equals(Const.VALUE_TYPE_QUIZ) || mType.equals(Const.VALUE_TYPE_FLASHCARD)){
			if(mChapters!=null && mChapters.size()==0){	//no bookmark data
				mNoData.setVisibility(View.VISIBLE);
				mExListView.setVisibility(View.GONE);
				mListView.setVisibility(View.GONE);
			}else{
				mNoData.setVisibility(View.GONE);
			}
		}else{
			if(mHtmls!=null && mHtmls.size()==0){ //no bookmark data
				mNoData.setVisibility(View.VISIBLE);
				mExListView.setVisibility(View.GONE);
				mListView.setVisibility(View.GONE);
			}else{
				mNoData.setVisibility(View.GONE);
			}
		}
		
	}

	private List<? extends Map<String, ?>> getData() {
		List<HashMap<String, String>> myData = new ArrayList<HashMap<String, String>>();
		HashMap<String, String> temp = new HashMap<String, String>();
		if (mChapters != null) {
			ChapterBean chapter = null;
			int size = mChapters.size();
			for (int i = 0; i < size; i++) {
				temp = new HashMap<String, String>();
				chapter = mChapters.get(i);
				temp.put(LIST_COLUMN_NAME, chapter.getName());
				temp.put(LIST_COLUMN_ID, String.valueOf(chapter.getId()));
				myData.add(temp);
			}
		} else {
			HtmlFileBean html = null;
			int size = mHtmls.size();
			int id;
			mIds = new int[size];
			for (int i = 0; i < size; i++) {
				temp = new HashMap<String, String>();
				html = mHtmls.get(i);
				id = html.getId();
				mIds[i] = id;
				temp.put(LIST_COLUMN_NAME, html.getName());
				temp.put(LIST_COLUMN_ID, String.valueOf(id));
				myData.add(temp);
			}
		}
		return myData;
	}

	private static void log(String msg) {
		if (Const.DEBUG_PHASE && DEBUG_ENABLED) {
			Log.d(TAG, Const.TAG_PREFIX + msg);
		}
	}

	class ChapterTypeAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return mChapters.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return mChapters.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ChapterBean bean = mChapters.get(position);
			convertView = LayoutInflater.from(BookmarksActivity.this).inflate(R.layout.bookmark_items_new, null);
			TextView chapterTitle = (TextView) convertView.findViewById(R.id.chapter_name);
			TextView quizTitle = (TextView) convertView.findViewById(R.id.quiz_title);

			chapterTitle.setText(bean.getName());
			quizTitle.setText(bean.getQuizName());
			if(position==0){
				chapterTitle.setVisibility(View.VISIBLE);
			}else{
				ChapterBean beanBefore = mChapters.get(position-1);

				if(beanBefore.getId() == bean.getId()){	//same chapter
					chapterTitle.setVisibility(View.GONE);
				}else{
					chapterTitle.setVisibility(View.VISIBLE);
				}
			}
			return convertView;
		}

	}

	public  class  ExpandableAdapter extends  BaseExpandableListAdapter  
	{  
		Activity activity;  

		public  ExpandableAdapter(Activity a)  
		{  
			activity = a;  
		}  
		public  Object getChild(int  groupPosition, int  childPosition)  
		{  
			return  mChildArray.get(groupPosition).get(childPosition);
		}  
		public  long  getChildId(int  groupPosition, int  childPosition)  
		{  
			return  childPosition;  
		}  
		public  int  getChildrenCount(int  groupPosition)  
		{
			return  mChildArray.get(groupPosition).size();  
		}
		public  View getChildView(int  groupPosition, int  childPosition,  
				boolean  isLastChild, View convertView, ViewGroup parent)  
		{
			String string = mChildArray.get(groupPosition).get(childPosition).getName();  
			return  getGenericView(string, false);  
		}
		// group method stub   
		public  Object getGroup(int  groupPosition)  
		{
			return  mGroupArray.get(groupPosition);  
		}
		public  int  getGroupCount()  
		{
			return  mGroupArray.size();  
		}
		public  long  getGroupId(int  groupPosition)  
		{
			return  groupPosition;  
		}
		public  View getGroupView(int  groupPosition, boolean  isExpanded,  
				View convertView, ViewGroup parent)  
		{
			String string = mGroupArray.get(groupPosition).getName();  
			return  getGenericView(string, true);  
		}
		// View stub to create Group/Children 's View
		public  TextView getGenericView(String string, boolean bolid)
		{
			// Layout parameters for the ExpandableListView
			AbsListView.LayoutParams layoutParams = new  AbsListView.LayoutParams(
					ViewGroup.LayoutParams.FILL_PARENT, 64 );
			TextView text = new  TextView(activity);
			text.setLayoutParams(layoutParams);
			// Center the text vertically
			text.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
			// Set the text starting position  
			float leftPadding = BookmarksActivity.this.getResources().getDimension(R.dimen.left_padding);
			int lp = (int)leftPadding;
			System.out.println("lp:"+lp+" leftPadding:"+leftPadding);
			text.setPadding(lp , 0 , 0 , 0 );
			text.setSingleLine(true);
			text.setEllipsize(TruncateAt.END);
			text.setTextSize(18.0f);
			text.setTextColor(Color.BLACK);
			if(bolid){
				TextPaint tp = text .getPaint();
				tp.setFakeBoldText(true);
				text.setTextSize(20.0f);
			}
			text.setText(string);
			return text;
		}
		public  boolean  hasStableIds()  
		{
			return  false ;  
		}
		public  boolean  isChildSelectable(int  groupPosition, int  childPosition)  
		{
			return  true ;  
		}
	}  
	// @Override
	public void onItemClick(AdapterView<?> arg0, View v, int position, long id) {
        log("onItemClick(), position is " + position + ", id is " + id);
        Intent intent = null;
        @SuppressWarnings("unchecked")
        HashMap<String, String> temp = (HashMap<String, String>) arg0
                .getItemAtPosition(position);
        String strId = temp.get(LIST_COLUMN_ID);
        String name = temp.get(LIST_COLUMN_NAME);
        if (mChapters != null) {
            intent = new Intent(this, BookmarkList.class);
            intent.putExtra(Const.KEY_BOOKMARK_TYPE, mType);
            intent.putExtra(Const.KEY_CHAPTER_ID, strId);
            intent.putExtra(Const.KEY_CHAPTER_NAME, name);
        } else {
            intent = new Intent(this, AllHtmlFileActivity.class);
            intent.putExtra(Const.KEY_BOOKMARK_TYPE, mType);
            intent.putExtra(Const.KEY_ID, strId);
            intent.putExtra(Const.KEY_IDS, mIds);
            finish();
        }
        startActivity(intent);
    }
}