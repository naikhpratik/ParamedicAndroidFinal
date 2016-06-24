package com.code3apps.para;

import java.io.ByteArrayOutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.Signature;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.code3apps.para.utils.Const;
import com.code3apps.para.utils.CrashHandler;
import com.code3apps.para.utils.QuizContentProvider;
import com.code3apps.para.utils.QuizDatabaseHelper;
import com.code3apps.para.utils.Util;
/*
import com.facebook.FacebookException;
import com.facebook.FacebookOperationCanceledException;
import com.facebook.LoggingBehavior;
import com.facebook.Session;
import com.facebook.Session.StatusCallback;
import com.facebook.SessionState;
import com.facebook.Settings;
import com.facebook.widget.WebDialog;
import com.facebook.widget.WebDialog.OnCompleteListener;
*/

public class ScoreHistory extends Activity implements OnItemClickListener,
OnClickListener {
	private static final String TAG = "ScoreHistory";
	private static final boolean DEBUG_ENABLED = true;

	private QuizDatabaseHelper mDbHelper;
	private SQLiteDatabase mDb;
	private ScoreHistoryAdapter mAdapter;
	private ListView mListView;

	// added by zorro
	private Button mPreviousDeleteBtn = null;
	private Button mCurrentDeleteBtn = null;
	private TextView mTextView = null;
	private String mDeleteScoreId = "";
	//added by neal
	private float startX = 0;
	private float startY = 0;
	private View mPreviousClickView = null;
	//private Session session;
	private String mMarks;
	private String platform = "twitter";
	private VerfierRecivier reciver;
	public static Twitter twitter;
	private RequestToken requestToken;
	private AccessToken accessTokenTwitter;
	private String authUrl;
	private Handler mHandler = new MainHandler();
	private static final int TWITTER_SUCCESS_FLAG = 111;
	private static final int TWITTER_FAILED_FLAG = 222;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		log("onCreate");

		// Add code to print out the key hash
		{

			if (CrashHandler.DEBUG) {
				CrashHandler INSTANCE = CrashHandler.getInstance();
				INSTANCE.init(this);
			}
			//			try {
			//				PackageInfo info = getPackageManager().getPackageInfo(
			//						"com.code3apps.para", 
			//						PackageManager.GET_SIGNATURES);
			//				for (Signature signature : info.signatures) {
			//					System.out.println("signature.toCharsString():"+signature.toCharsString());
			//					MessageDigest md = MessageDigest.getInstance("SHA");
			//					md.update(signature.toByteArray());
			//					Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
			//					System.out.println("KeyHash:"+Base64.encodeToString(md.digest(), Base64.DEFAULT));
			//				}
			//			} catch (NameNotFoundException e) {
			//
			//			} catch (NoSuchAlgorithmException e) {
			//
			//			}
		}
		// disable default title bar
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		// load view
		setContentView(R.layout.score_history);

		mDbHelper = QuizContentProvider.createDatabaseHelper(this);
		mDb = mDbHelper.getWritableDatabase();

		prepareViews();
		prepareData();
		////this.session = createSession();
		//		Settings.addLoggingBehavior(LoggingBehavior.INCLUDE_ACCESS_TOKENS);
	}

	// @Override
	// public void onResume() {
	// super.onResume();
	// log("onResume");
	// }
	class MainHandler extends Handler{

		@Override
		public void handleMessage(Message msg) {
			switch(msg.what){
			case TWITTER_SUCCESS_FLAG:
				Intent i = new Intent(ScoreHistory.this, TwitterPostActivity.class);
				String con = "I just score "+mMarks+" on EMT Tutor practice quiz."+" http://bit.ly/14THppk";
				i.putExtra("content", con);
				startActivity(i);
				break;
			case TWITTER_FAILED_FLAG:
				Toast.makeText(ScoreHistory.this, "Oauth Fialed!", 0).show();
				break;
			}
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		log("onDestroy");
		if(reciver!=null){
			try{
				this.unregisterReceiver(reciver);
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		if (mDb != null)
			mDb.close();
		if (mDbHelper != null)
			mDbHelper.close();
		if (mAdapter != null)
			mAdapter.close();
	}

	private void prepareViews() {
		mListView = (ListView) findViewById(R.id.score_history_list);
		mListView.setOnItemClickListener(this);
	}

	private void prepareData() {

		mAdapter = new ScoreHistoryAdapter(this,
				QuizDatabaseHelper.getScoreHistoryCursor(mDb));
		if (mListView != null)
			mListView.setAdapter(mAdapter);
		else
			log("mListView is null!");
	}

	private static void log(String msg) {
		if (Const.DEBUG_PHASE && DEBUG_ENABLED) {
			Log.d(TAG, Const.TAG_PREFIX + msg);
		}
	}

	public void onItemClick(AdapterView<?> arg0, final View v, int postion, long arg3) {
		log("item is clicked");

		//		mCurrentDeleteBtn = (Button) v.findViewById(R.id.delete_btn);

		//        v.setOnClickListener(new OnClickListener() {
		//			
		//			@Override
		//			public void onClick(View v) {
		//	             
		//	             
		//			}
		//		});

		//        mPreviousDeleteBtn = mCurrentDeleteBtn;
		//        mCurrentDeleteBtn = (Button) v.findViewById(R.id.delete_btn);
		//        if (mCurrentDeleteBtn.getVisibility() == View.GONE) {
		//            log("gone before click");
		//            mTextView = (TextView) v.findViewById(R.id.scoreid);
		//            mDeleteScoreId = mTextView.getText().toString();
		//            log("mDeleteScoreId is " + mDeleteScoreId);
		//            mCurrentDeleteBtn.setVisibility(View.VISIBLE);
		//            mCurrentDeleteBtn.setOnClickListener(this);
		//            if (mPreviousDeleteBtn != null
		//                    && mCurrentDeleteBtn != mPreviousDeleteBtn) {
		//                mPreviousDeleteBtn.setVisibility(View.GONE);
		//            }
		//        }
		//        // else if(mCurrentDeleteBtn.getVisibility() == View.VISIBLE){
		//        else {
		//            log("visible before click");
		//            mCurrentDeleteBtn.setVisibility(View.GONE);
		//            if (mPreviousDeleteBtn != null) {
		//                log("set previous as gone");
		//                mPreviousDeleteBtn.setVisibility(View.GONE);
		//            }
		//        }
	}

	public void onClick(View v) {
		// TODO Auto-generated method stub
		int id = v.getId();
		switch (id) {
		case R.id.delete_btn:
			log("delete the record");
			// delete the rocord by id
			QuizDatabaseHelper.deleteScoreHistory(mDb, mDeleteScoreId);
			// reload view
			prepareData();
			break;
		}
	}

	/*private Session createSession() {
		Session activeSession = Session.getActiveSession();
		if (activeSession == null || activeSession.getState().isClosed()) {
			activeSession = new Session.Builder(this).setApplicationId(
					getString(R.string.facebook_app_id)).build();
			Session.setActiveSession(activeSession);
		}
		return activeSession;
	}*/

	public class VerfierRecivier extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			final Bundle bundle = intent.getExtras();
			if (bundle != null) {
				new Thread(new Runnable() {

					@Override
					public void run() {
						String oauth_verifier = bundle.getString("oauth_verifier");
						String oauth_token = bundle.getString("oauth_token");
						try {
							if (oauth_verifier!=null && oauth_verifier.length() > 0) {
								accessTokenTwitter = twitter.getOAuthAccessToken(requestToken, oauth_verifier);
								twitter.setOAuthAccessToken(accessTokenTwitter);
								String accessToken = accessTokenTwitter.getToken();
								String tokenSecret = accessTokenTwitter.getTokenSecret();
								Util.putTwitterAccessToken(ScoreHistory.this, accessToken);
								Util.putTwitterTokenSectet(ScoreHistory.this, tokenSecret);
								Message m = new Message();
								m.what = TWITTER_SUCCESS_FLAG;
								mHandler.sendMessage(m);
							} else {
								Message m = new Message();
								m.what = TWITTER_FAILED_FLAG;
								mHandler.sendMessage(m);
							}
							//							System.out.println("accessTokenTwitter.getScreenName:"+accessTokenTwitter.getScreenName());
							//							System.out.println("accessToken:"+accessTokenTwitter.getToken()+" accessTokenSecret:"+accessTokenTwitter.getTokenSecret());
						} catch (TwitterException te) {
							if (401 == te.getStatusCode()) {
								System.out.println("Unable to get the access token.");
								te.printStackTrace();
							} else {
								te.printStackTrace();
							}
						}
					}
				}).start();

			}
		}
	}
	private void connectToTwitter(){
		String accesstoken = Util.getTwitterAccessToken(ScoreHistory.this);
		String tokensecret = Util.getTwitterTokenSectet(ScoreHistory.this);
		if(accesstoken!=null && tokensecret != null){
			twitter = new TwitterFactory().getInstance();
			twitter.setOAuthConsumer(Const.TWITTER_CONSUMER_KEY, Const.TWITTER_CONSUMER_SECRET);
			AccessToken at = new AccessToken(accesstoken, tokensecret);
			twitter.setOAuthAccessToken(at);
			Message msg = new Message();
			msg.what = TWITTER_SUCCESS_FLAG;
			mHandler.sendMessage(msg);
		}else{
			new Thread(new Runnable() {

				@Override
				public void run() {
					try {
						ConfigurationBuilder configurationBuilder = new ConfigurationBuilder();
						configurationBuilder.setOAuthConsumerKey(Const.TWITTER_CONSUMER_KEY);
						configurationBuilder.setOAuthConsumerSecret(Const.TWITTER_CONSUMER_SECRET);
						Configuration configuration = configurationBuilder.build();
						twitter = new TwitterFactory(configuration).getInstance();
						requestToken = twitter.getOAuthRequestToken();
						authUrl = requestToken.getAuthorizationURL();
						Intent intent = new Intent();
						intent.putExtra("from", "score");
						Bundle extras = new Bundle();
						extras.putString("url", authUrl);
						extras.putString("callback", Const.TWITTER_CALLBACK_URL);
						intent.setClass(ScoreHistory.this, WebViewActivity.class);
						intent.putExtras(extras);
						ScoreHistory.this.startActivity(intent);
						IntentFilter filter = new IntentFilter();
						filter.addAction("oauth_verifier_score");
						reciver = new VerfierRecivier();
						ScoreHistory.this.registerReceiver(reciver, filter);
					} catch (TwitterException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}).start();
		}
	}

	/*private void onClickRequest() {
		if (this.session.isOpened()) {
			publishFeedDialog();
		} else {
			StatusCallback callback = new StatusCallback() {
				public void call(Session session, SessionState state,
						Exception exception) {
					if (exception != null) {
						new AlertDialog.Builder(ScoreHistory.this)
						.setTitle(
								R.string.facebook_app_login_failed_dialog_title)
								.setMessage(exception.getMessage())
								.setPositiveButton(
										R.string.facebook_app_ok_button, null)
										.show();
						ScoreHistory.this.session = createSession();
					}
				}
			};
			//			pendingRequest = true;
			this.session.openForRead(new Session.OpenRequest(this)
			.setCallback(callback));
		}
	}
*/
	private void publishFeedDialog() {
		Bundle params = new Bundle();
		//		params.putString("name", "NCLEX");
		//		params.putString("caption", "NCLEX-Reminder");
		//		{
		//			byte[] data = null;
		//            Bitmap bi = BitmapFactory.decodeResource(getResources(), R.drawable.share_img);
		//            ByteArrayOutputStream baos = new ByteArrayOutputStream();
		//            bi.compress(Bitmap.CompressFormat.PNG, 100, baos);
		//            data = baos.toByteArray();
		//		}

		params.putString("description", "I just score "+mMarks+" on EMT Tutor practice quiz.");
		params.putString("link", "http://bit.ly/14THppk");
		params.putString("picture", "http://www.emsstudyguides.com/EMT_Study_guide_app_NREMT__EMS_ambulance_Paramedic_iPhone_Android_EMTutor.png");
		/*WebDialog feedDialog = (new WebDialog.FeedDialogBuilder(
				ScoreHistory.this, Session.getActiveSession(), params))
				.setOnCompleteListener(new OnCompleteListener() {

					@Override
					public void onComplete(Bundle values,
							FacebookException error) {
						if (error == null) {
							// When the story is posted, echo the success
							// and the post Id.
							final String postId = values.getString("post_id");
							if (postId != null) {
								//								Toast.makeText(ScoreHistory.this,
								//										"Posted story, id: " + postId,
								//										Toast.LENGTH_SHORT).show();
								Toast.makeText(ScoreHistory.this,"Success",Toast.LENGTH_SHORT).show();
							} else {
								// User clicked the Cancel button
								Toast.makeText(ScoreHistory.this,
										"Publish cancelled", Toast.LENGTH_SHORT)
										.show();
							}
						} else if (error instanceof FacebookOperationCanceledException) {
							// User clicked the "x" button
							Toast.makeText(ScoreHistory.this,"Publish cancelled", Toast.LENGTH_SHORT).show();
						} else {
							// Generic, ex: network error
							Toast.makeText(ScoreHistory.this,"Error posting story", Toast.LENGTH_SHORT).show();
						}
					}

				}).build();
		feedDialog.show();
	*/}

	private void showShareDialog(){
//		String[] shares = new String[]{"Post to Facebook", "Post to Twitter"};
		String[] shares = new String[]{"Post to Twitter"};
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder
		.setTitle("Share your score")
		.setItems(shares, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				if(which==0){	//facebook
					System.out.println("facebook");
//					onClickRequest();
					connectToTwitter();
				}else{	//twitter
					System.out.println("twitter");
					connectToTwitter();
				}
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
	class ScoreHistoryAdapter extends BaseAdapter {
		private static final String TAG = "ScoreHistoryAdapter";
		private static final boolean DEBUG_ENABLED = true;

		private LayoutInflater mInflater;
		private Cursor mCursor;
		private int mCurPos = 0;
		private int mCount = 0;
		private Map<String, Integer> mSelect = new HashMap<String, Integer>();

		public ScoreHistoryAdapter(Context context, Cursor cursor) {
			mInflater = LayoutInflater.from(context);
			mCursor = cursor;
			mCount = mCursor.getCount();
			mCursor.moveToFirst();
			mSelect.put("position", -1);
		}

		public Object getItem(int position) {
			return position;
		}

		public long getItemId(int position) {
			return position;
		}

		class ViewHolder {
			TextView scoreid;
			TextView chapter;
			TextView result;
		}

		public int getCount() {
			log("getCount is " + mCount);
			return mCount;
		}

		public View getView(int position, View convertView, ViewGroup parent) {

			log("getView(), position is " + position);
			String chapter_txt, result_txt, id_txt;

			final int index = position;
			convertView = mInflater.inflate(R.layout.score_history_item, null);
			final View cv = convertView;

			TextView chapter = (TextView) cv.findViewById(R.id.chapter);
			final TextView result = (TextView) cv.findViewById(R.id.result);
			final TextView scoreid = (TextView) cv.findViewById(R.id.scoreid);
			final Button del_btn = (Button) cv.findViewById(R.id.delete_btn);
			del_btn.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					mDeleteScoreId = scoreid.getText().toString();
					// delete the rocord by id
					QuizDatabaseHelper.deleteScoreHistory(mDb, mDeleteScoreId);
					// reload view
					prepareData();
				}
			});
			if(mSelect!=null && index == mSelect.get("position")){
				cv.setBackgroundResource(R.color.blue);
			}else{
				cv.setBackgroundResource(R.color.transparent);
			}

			cv.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					cv.setBackgroundResource(R.color.blue);
				}
			});
			cv.setOnTouchListener(new OnTouchListener() {
				float endX;
				float endY;
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					switch(event.getAction()){
					case MotionEvent.ACTION_DOWN:
						startX = event.getX();
						startY = event.getY();
						System.out.println("ACTION_DOWN");
						break;
					case MotionEvent.ACTION_UP:
						System.out.println("ACTION_UP");
						endX = event.getX();
						endY = event.getY();
						float c = endX-startX;
						System.out.println("endX-startX:"+c);
						if(endX-startX>5 || endX-startX<-5){	//move
							if(del_btn.getVisibility() == View.INVISIBLE){
								del_btn.setVisibility(View.VISIBLE);
							}else{
								del_btn.setVisibility(View.INVISIBLE);
							}
						}else{	//clicked

							if(del_btn.getVisibility() == View.VISIBLE){
								del_btn.setVisibility(View.INVISIBLE);
							}else{
								cv.setBackgroundResource(R.color.blue);
								mSelect.put("position", index);
								mAdapter.notifyDataSetChanged();
								String res = result.getText().toString();
								mMarks = res.substring(0, res.indexOf(")")+1);
								showShareDialog();
							}
						}
						break;
					}
					return true;
				}
			});

			if (mCursor != null) {
				if (mCurPos != position) {
					mCurPos = position;
					mCursor.moveToPosition(mCurPos);
				}

				chapter_txt = mCursor.getString(0);
				result_txt = mCursor.getString(1);
				id_txt = String.valueOf(mCursor.getInt(2));

				chapter.setText(chapter_txt);
				result.setText(result_txt);
				scoreid.setText(id_txt);
			}

			return cv;
		}

		public void close() {
			if (mCursor != null)
				mCursor.close();
		}

	}
	//@Override
	/*protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (this.session.onActivityResult(this, requestCode, resultCode, data)
				&& this.session.getState().isOpened()) {
			publishFeedDialog();
		}
	}*/
}