package com.code3apps.para;

import java.util.ArrayList;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;

import com.code3apps.para.AllQuiz.VerfierRecivier;
import com.code3apps.para.AllQuiz.MainHandler;
import com.code3apps.para.beans.QuizBean;
import com.code3apps.para.beans.QuizRecorderBean;
import com.code3apps.para.utils.Const;
import com.code3apps.para.utils.QuizContentProvider;
import com.code3apps.para.utils.QuizDatabaseHelper;
/*
import com.facebook.FacebookException;
import com.facebook.FacebookOperationCanceledException;
import com.facebook.LoggingBehavior;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.Settings;
import com.facebook.Session.StatusCallback;
import com.facebook.widget.WebDialog;
import com.facebook.widget.WebDialog.OnCompleteListener;
*/

import com.code3apps.para.utils.Util;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.format.Time;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class AllQuiz extends Activity implements OnClickListener {
	private static final String TAG = "AllQuiz";
	private static final boolean DEBUG_ENABLED = true;
	private static final int MAX_CHOICES = 5;
	private static final String CHOICES[] = { "A. ", "B. ", "C. ", "D. ", "E. " };
	private static final int PICK_QUESTION = 0;
	private static final int DIALOG_SAVE_SCORE = 1;	//save score to db
	private static final int DIALOG_COMPLETE_TEST = 2;	//finish test

	private ArrayList<Integer> mIndexUsedList;
	private int[] mIndexUsedArray;
	private int mTotalCount = 0;
	private int mTotalCorrectNo = 0;
	private int mTotalNo = 0;
	private int mTotalRightPercent = 0;
	private String mChapterId = "";
	private String mChapterName = "";
	private boolean mNotAnswered = true;
	private QuizDatabaseHelper mDbHelper;
	private static SQLiteDatabase mDb = null;
	private int mCurQuizCount = 0;
	private static int mCurQuizIndex = 0;
	private int mRightAnswerPos = 0;
	private int mAnswerIndexes[] = new int[MAX_CHOICES];
	private boolean mAnswerStatuses[] = new boolean[MAX_CHOICES];
	private ImageView mWrongImages[] = new ImageView[MAX_CHOICES];
	private ImageView mNormalImages[] = new ImageView[MAX_CHOICES];
	private ImageView mRightImages[] = new ImageView[MAX_CHOICES];
	private String mExplanation = "";
	private TextView mStatics;
	private TextView mStaticsPercent;
	private TextView mQuestion;
	private TextView mAnswersViews[];
	private View mChoicesViews[];
	private boolean mBookmarked;
	// private String mType;
	private String mId = "";
	private int[] mIds = null;
	private QuizRecorderBean recordbean = null;
	private int mFirstTouch;
//	private Session session;
	private String platform = "twitter";
	private VerfierRecivier reciver;
	private Twitter twitter;
	private RequestToken requestToken;
	private AccessToken accessTokenTwitter;
	private String authUrl;
	private Handler mHandler = new MainHandler();
	private static final int TWITTER_SUCCESS_FLAG = 111;
	private static final int TWITTER_FAILED_FLAG = 222;
	private TextView mCurrent_quiz_num;
	private TextView mTotalQuizCount;
	private boolean mRateFlag = false;
	private int mGetFirstInQuizindex;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		log("onCreate");
		Intent intent = getIntent();
		// mType = intent.getStringExtra(Const.KEY_BOOKMARK_TYPE);
		mChapterId = intent.getStringExtra(Const.KEY_CHAPTER_ID);
		
		if (!mChapterId.equals("0")) {
			mChapterName = intent.getStringExtra(Const.KEY_CHAPTER_NAME);
		} else {
			mChapterName = getString(R.string.comprehensive_quiz);
		}
		
		log("mChapterId is " + mChapterId);
		log("mChapterName is " + mChapterName);
		mId = intent.getStringExtra(Const.KEY_ID);
		mIds = intent.getIntArrayExtra(Const.KEY_IDS);
		mCurQuizCount =  intent.getIntExtra(Const.KEY_POSITION, 0);

		log("mId is " + mId + ", mIds is " + mIds);

		// disable default title bar
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		// load view
		setContentView(R.layout.all_quiz_new);
		((TextView) findViewById(R.id.header)).setText(mChapterName);

		prepareViews();

		if(intent!=null){
			Bundle b = intent.getBundleExtra("bundle");
			if(b!=null){
				recordbean = (QuizRecorderBean) b.get("rb");
			}
		}
		if(recordbean != null){
			mDbHelper = QuizContentProvider.createDatabaseHelper(this);
			mDb = mDbHelper.getWritableDatabase();
			mCurQuizCount = recordbean.getCurrentIndex()-1;
			mTotalCorrectNo = recordbean.getRightAmount();
			mTotalNo = recordbean.getDoneAmount();
			String[] rb_sequence = recordbean.getChapterSequence().split(",");
			mTotalCount = recordbean.getTotalAmount();
			mIds = new int[mTotalCount];
			for(int k=0; k<rb_sequence.length; k++){
				mIds[k] = Integer.valueOf(rb_sequence[k]);
			}
		}else{
			prepareData();
		}
		mGetFirstInQuizindex = mCurQuizCount;
		loadNextQuestion(true);
/*
		this.session = createSession();
		Settings.addLoggingBehavior(LoggingBehavior.INCLUDE_ACCESS_TOKENS);
*/
	}
/*
	private Session createSession() {
		Session activeSession = Session.getActiveSession();
		if (activeSession == null || activeSession.getState().isClosed()) {
			activeSession = new Session.Builder(this).setApplicationId(
					getString(R.string.facebook_app_id)).build();
			Session.setActiveSession(activeSession);
		}
		return activeSession;
	}
*/
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
				Intent i = new Intent(AllQuiz.this, TwitterPostActivity.class);
				String con = "Great app, study for your NREMT-B. Check it out."+" http://bit.ly/14THppk";
				i.putExtra("content", con);
				startActivity(i);
				break;
			/*case TWITTER_FAILED_FLAG:
				Toast.makeText(AllQuiz.this, "Oauth Fialed!", 0).show();
				break;*/
			}
		}
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

	@Override
	public void onBackPressed() {
		if (mId != null) {
			// Come from bookmark
			finish();
			//			Intent intent = new Intent(this, BookmarkList.class);
			//			intent.putExtra(Const.KEY_BOOKMARK_TYPE, Const.VALUE_TYPE_QUIZ);
			//			intent.putExtra(Const.KEY_CHAPTER_ID, mChapterId);
			//			intent.putExtra(Const.KEY_CHAPTER_NAME, mChapterName);
			//			startActivity(intent);
		} else {
			if(!mRateFlag){
				showDialog(DIALOG_SAVE_SCORE);
			}else{
				finish();
			}
		}
	}

	//	private void deletTest(int testId) {
	//		// delete test by id
	//		// delete test details by id
	//		QuizDatabaseHelper.deletTest(mDb, mTestBean);
	//	}

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case DIALOG_SAVE_SCORE:
			return new AlertDialog.Builder(this).setIcon(
					android.R.drawable.ic_dialog_alert).setTitle(
							R.string.quit_quiz).setMessage(R.string.save_test)
							.setPositiveButton("YES",
									new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton) {
									/* User clicked OK so do some stuff */
									saveScore(false);
									saveHistoryScore(false);
									finish();
								}
							}).setNegativeButton("NO",
									new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton) {
									/* User clicked Cancel so do some stuff */
									QuizDatabaseHelper.deletRecorder(mDb, mChapterId);
									saveHistoryScore(true);
									finish();
								}
							}).create();
		case DIALOG_COMPLETE_TEST:
			return new AlertDialog.Builder(this).setIcon(
					android.R.drawable.ic_dialog_alert).setTitle(
							R.string.quit_quiz).setMessage(R.string.quit_quiz_question)
							.setPositiveButton(R.string.save_score,
									new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton) {
									/* User clicked OK so do some stuff */
									saveScore(true);
									saveHistoryScore(true);
									dialog.dismiss();
									rateApp();
									//									int firstFlag = Util.getFirstCompleteFlag(AllQuiz.this);
									//									if(firstFlag == 0){	//rate app after first complete test 
									//										dialog.dismiss();
									//										rateApp();
									//										Util.putFirstCompleteFlag(AllQuiz.this, 1);
									//									}else{
									//										finish();
									//									}
								}
							}).setNegativeButton(R.string.do_not_save_score,
									new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton) {
									/* User clicked Cancel so do some stuff */
									rateApp();
									//									finish();
								}
							}).create();
		}
		return null;
	}
	private void publishFeedDialog() {
		Bundle params = new Bundle();
		params.putString("description", "Great app, study for your NREMT-B. Check it out.");
		params.putString("link", "http://bit.ly/14THppk");
		params.putString("picture", "http://www.emsstudyguides.com/EMT_Study_guide_app_NREMT__EMS_ambulance_Paramedic_iPhone_Android_EMTutor.png");
		/*WebDialog feedDialog = (new WebDialog.FeedDialogBuilder(
				AllQuiz.this, Session.getActiveSession(), params))
				.setOnCompleteListener(new OnCompleteListener() {

					@Override
					public void onComplete(Bundle values,
							FacebookException error) {
						if (error == null) {
							// When the story is posted, echo the success
							// and the post Id.
							final String postId = values.getString("post_id");
							if (postId != null) {
								//								Toast.makeText(AllQuiz.this,
								//										"Posted story, id: " + postId,
								//										Toast.LENGTH_SHORT).show();
								Toast.makeText(AllQuiz.this,"Success",Toast.LENGTH_SHORT).show();
								finish();
							} else {
								// User clicked the Cancel button
								Toast.makeText(AllQuiz.this,
										"Publish cancelled", Toast.LENGTH_SHORT)
										.show();
							}
						} else if (error instanceof FacebookOperationCanceledException) {
							// User clicked the "x" button
							Toast.makeText(AllQuiz.this,
									"Publish cancelled", Toast.LENGTH_SHORT)
									.show();
						} else {
							// Generic, ex: network error
							Toast.makeText(AllQuiz.this,
									"Error posting story", Toast.LENGTH_SHORT)
									.show();
						}
					}

				}).build();
		feedDialog.show();*/
	}
	/*private void onClickRequest() {
		if (this.session.isOpened()) {
			publishFeedDialog();
		} else {
			StatusCallback callback = new StatusCallback() {
				public void call(Session session, SessionState state,
						Exception exception) {
					if (exception != null) {
						new AlertDialog.Builder(AllQuiz.this)
						.setTitle(
								R.string.facebook_app_login_failed_dialog_title)
								.setMessage(exception.getMessage())
								.setPositiveButton(
										R.string.facebook_app_ok_button, null)
										.show();
						AllQuiz.this.session = createSession();
					}
				}
			};
			//			pendingRequest = true;
			this.session.openForRead(new Session.OpenRequest(this)
			.setCallback(callback));
		}
	}*/

	private void connectToTwitter(){
		String accesstoken = Util.getTwitterAccessToken(AllQuiz.this);
		String tokensecret = Util.getTwitterTokenSectet(AllQuiz.this);
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
						intent.putExtra("from", "allquiz");
						Bundle extras = new Bundle();
						extras.putString("url", authUrl);
						extras.putString("callback", Const.TWITTER_CALLBACK_URL);
						intent.setClass(AllQuiz.this, WebViewActivity.class);
						intent.putExtras(extras);
						AllQuiz.this.startActivity(intent);
						IntentFilter filter = new IntentFilter();
						filter.addAction("oauth_verifier");
						reciver = new VerfierRecivier();
						AllQuiz.this.registerReceiver(reciver, filter);
					} catch (TwitterException e) {
						e.printStackTrace();
					}
				}
			}).start();
		}
	}

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
								Util.putTwitterAccessToken(AllQuiz.this, accessToken);
								Util.putTwitterTokenSectet(AllQuiz.this, tokenSecret);
								Message m = new Message();
								m.what = TWITTER_SUCCESS_FLAG;
								mHandler.sendMessage(m);
							} else {
								Message m = new Message();
								m.what = TWITTER_FAILED_FLAG;
								mHandler.sendMessage(m);
							}
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

	private void rateApp(){
		mRateFlag = true;
//		String[] shares = new String[]{"Post to Facebook", "Post to Twitter"};
		String[] shares = new String[]{"Post to Twitter"};
		new AlertDialog.Builder(this)
		.setTitle(R.string.rate_title)
		.setItems(shares, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				if(which==0){	//facebook
//					onClickRequest();
					connectToTwitter();
				}else{	//twitter
					connectToTwitter();
				}
			}
		})
		.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				finish();
			}
		})
		.create().show();
	}

	private void saveHistoryScore(boolean saveOrContinue){
		int done;
		if(saveOrContinue){
			done = 1;
		}else{
			done = 0;
		}
		QuizDatabaseHelper.setQuizScore(mDb, mChapterName, mTotalCorrectNo
				+ "/" + mTotalNo + " (" + mTotalRightPercent + "%)" + " on "
				+ getCurTime(), mTotalNo, done);
	}
	
	private void saveScore(boolean finishFlag) {
		
//		if(++mGetFirstInQuizindex != mCurQuizCount){
//			QuizDatabaseHelper.setQuizScore(mDb, mChapterName, mTotalCorrectNo
//					+ "/" + mTotalNo + " (" + mTotalRightPercent + "%)" + " on "
//					+ getCurTime());
//		}
		
		//save chapter info(name,correct,total)  to quizScore
		//		QuizDatabaseHelper.setQuizScoreState(mDb, mChapterName, mTotalCorrectNo
		//				+ "/" + mTotalNo + " (" + mTotalRightPercent + "%)" + " on "
		//				+ getCurTime(), mTotalCorrectNo, mTotalNo);
		//update quiz's order by current random order
		//		QuizDatabaseHelper.setQuizChapterOrder(mDb, mChapterName, mIdsBefore, mIds);
		//save chapter quiz recorder to table chapterHistoryrecorder

		if (!finishFlag) {
			String sequence = "";
			for(int i=0; i<mIds.length; i++){
				sequence += mIds[i]+",";
			}
			sequence.subSequence(0, mIds.length-1);
			recordbean = new QuizRecorderBean();
			recordbean.setChapterId(Integer.valueOf(mChapterId));
			recordbean.setChapterSequence(sequence);
			recordbean.setRightAmount(mTotalCorrectNo);
			recordbean.setDoneAmount(mTotalNo);
			recordbean.setCurrentIndex(mCurQuizCount);
			recordbean.setTotalAmount(mTotalCount);
			recordbean.setFirstTouch(mFirstTouch);
			recordbean.setFinished(0);
			QuizDatabaseHelper.saveChaperHistoryRecorder(mDb, recordbean);
		}

	}

	private String getCurTime() {
		Time t = new Time();
		t.setToNow();
		int year = t.year;
		int month = t.month + 1;
		int date = t.monthDay;
		int hour = t.hour;
		int minute = t.minute;

		String curTime = month + "-" + date + "-" + year + " " + hour + ":"
				+ minute;
		// log("curTime is " + curTime);

		// long time=System.currentTimeMillis();
		// final Calendar mCalendar=Calendar.getInstance();
		// mCalendar.setTimeInMillis(time);
		// int mHour=mCalendar.get(Calendar.HOUR);
		// int mMinuts=mCalendar.get(Calendar.MINUTE);

		return curTime;
	}

	public void onClick(View v) {
		log("onClick");
		int id = v.getId();
		int diff = -1;
		switch (id) {
		case R.id.answerContentA:
		case R.id.a_chapter_pannel:
			diff = 0;
			break;
		case R.id.answerContentB:
		case R.id.b_chapter_pannel:
			diff = 1;
			break;
		case R.id.answerContentC:
		case R.id.c_chapter_pannel:
			diff = 2;
			break;
		case R.id.answerContentD:
		case R.id.d_chapter_pannel:
			diff = 3;
			break;
		case R.id.answerContentE:
		case R.id.e_chapter_pannel:
			diff = 4;
			break;
		default:
			break;
		}
		log("diff is " + diff);
		if (diff != -1) {
			mFirstTouch = 1;
			if (mRightAnswerPos == diff) {
				// right answer
				if (mNotAnswered) {
					//					if(recordbean!=null && recordbean.getFirstTouch() == 1){	//choose correct answer before save the quiz
					//						recordbean.setFirstTouch(0);
					//					}else{
					mTotalCorrectNo++;
					//					}
				}
				mNormalImages[diff].setVisibility(View.GONE);
				mRightImages[diff].setVisibility(View.VISIBLE);
				showCorrectAnswer();
			} else {
				// wrong answer
				mNormalImages[diff].setVisibility(View.GONE);
				mWrongImages[diff].setVisibility(View.VISIBLE);

			}
			if (mNotAnswered) {
				mNotAnswered = false;
				mTotalNo++;
			}
		}
	}

	public static void setQuizBookmark(String chapterId, boolean bookmark) {
		if (mDb != null) {
			QuizDatabaseHelper.setQuizBookmark(mDb, chapterId, mCurQuizIndex,
					bookmark);
		}
	}

	private void prepareViews() {
		// prepare views about statics and question
		mTotalQuizCount = (TextView) findViewById(R.id.total_quiz_num);
		mStatics = (TextView) findViewById(R.id.statics);
		mStaticsPercent = (TextView) findViewById(R.id.staticsper);
		mQuestion = (TextView) findViewById(R.id.question);
		mCurrent_quiz_num = (TextView) findViewById(R.id.current_quiz_num);

		// prepare those views about the answers
		int i = 0;
		mAnswersViews = new TextView[MAX_CHOICES];
		mWrongImages[i] = (ImageView) findViewById(R.id.wrong_a);
		mNormalImages[i] = (ImageView) findViewById(R.id.normal_a);
		mRightImages[i] = (ImageView) findViewById(R.id.right_a);
		mAnswersViews[i] = (TextView) findViewById(R.id.answerContentA);
		mAnswersViews[i++].setOnClickListener(this);

		mWrongImages[i] = (ImageView) findViewById(R.id.wrong_b);
		mNormalImages[i] = (ImageView) findViewById(R.id.normal_b);
		mRightImages[i] = (ImageView) findViewById(R.id.right_b);
		mAnswersViews[i] = (TextView) findViewById(R.id.answerContentB);
		mAnswersViews[i++].setOnClickListener(this);

		mWrongImages[i] = (ImageView) findViewById(R.id.wrong_c);
		mNormalImages[i] = (ImageView) findViewById(R.id.normal_c);
		mRightImages[i] = (ImageView) findViewById(R.id.right_c);
		mAnswersViews[i] = (TextView) findViewById(R.id.answerContentC);
		mAnswersViews[i++].setOnClickListener(this);

		mWrongImages[i] = (ImageView) findViewById(R.id.wrong_d);
		mNormalImages[i] = (ImageView) findViewById(R.id.normal_d);
		mRightImages[i] = (ImageView) findViewById(R.id.right_d);
		mAnswersViews[i] = (TextView) findViewById(R.id.answerContentD);
		mAnswersViews[i++].setOnClickListener(this);

		mWrongImages[i] = (ImageView) findViewById(R.id.wrong_e);
		mNormalImages[i] = (ImageView) findViewById(R.id.normal_e);
		mRightImages[i] = (ImageView) findViewById(R.id.right_e);
		mAnswersViews[i] = (TextView) findViewById(R.id.answerContentE);
		mAnswersViews[i++].setOnClickListener(this);

		// prepare those views about choices
		i = 0;
		mChoicesViews = new View[MAX_CHOICES];
		mChoicesViews[i] = (View) findViewById(R.id.a_chapter_pannel);
		mChoicesViews[i++].setOnClickListener(this);
		mChoicesViews[i] = (View) findViewById(R.id.b_chapter_pannel);
		mChoicesViews[i++].setOnClickListener(this);
		mChoicesViews[i] = (View) findViewById(R.id.c_chapter_pannel);
		mChoicesViews[i++].setOnClickListener(this);
		mChoicesViews[i] = (View) findViewById(R.id.d_chapter_pannel);
		mChoicesViews[i++].setOnClickListener(this);
		mChoicesViews[i] = (View) findViewById(R.id.e_chapter_pannel);
		mChoicesViews[i++].setOnClickListener(this);

		//		mChoicesViews = new TextView[MAX_CHOICES];
		//		mChoicesViews[i] = (TextView) findViewById(R.id.choiceA);
		//		mChoicesViews[i++].setOnClickListener(this);
		//		mChoicesViews[i] = (TextView) findViewById(R.id.choiceB);
		//		mChoicesViews[i++].setOnClickListener(this);
		//		mChoicesViews[i] = (TextView) findViewById(R.id.choiceC);
		//		mChoicesViews[i++].setOnClickListener(this);
		//		mChoicesViews[i] = (TextView) findViewById(R.id.choiceD);
		//		mChoicesViews[i++].setOnClickListener(this);
		//		mChoicesViews[i] = (TextView) findViewById(R.id.choiceE);
		//		mChoicesViews[i++].setOnClickListener(this);

	}

	private void showCorrectAnswer() {
		log("Congratulations!");
		Intent intent = null;
		intent = new Intent(this, CorrectAnswerActivity.class);
		intent.putExtra(Const.KEY_EXPLANATION, mExplanation);
		intent.putExtra(Const.KEY_CHAPTER_ID, mChapterId);
		intent.putExtra(Const.KEY_BOOKMARK, mBookmarked);
		startActivityForResult(intent, PICK_QUESTION);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == PICK_QUESTION) {
			if (resultCode == RESULT_CANCELED) {
				// do nothing
			} else {
				mBookmarked = data.getBooleanExtra(Const.KEY_BOOKMARK, false);
				String action = data.getAction();
				if (action.equals(Const.ACTION_RELOAD)) {
					log("Reload this question");
					loadNextQuestion(false);
				} else if (action.equals(Const.ACTION_NEXT)) {
					log("Load next question");
					if(recordbean != null && recordbean.getFirstTouch() == 1){
						mTotalNo--;
						recordbean.setFirstTouch(0);
					}else{
						mFirstTouch = 0;
					}
					loadNextQuestion(true);
				}
			}
		}
	}

	//private void loadNextQuestion(boolean bFirsttime, boolean bNext) {
	private void loadNextQuestion(boolean bNext) {
		// initial view
		if (mCurQuizCount < mTotalCount)
			for (int i = 0; i < MAX_CHOICES; i++) {
				mWrongImages[i].setVisibility(View.INVISIBLE);
				mRightImages[i].setVisibility(View.INVISIBLE);
				mNormalImages[i].setVisibility(View.VISIBLE);
			}
		if (bNext) {
			mNotAnswered = true;
			// log("mCurQuizCount is " + mCurQuizCount + ", mTotalCount is "
			// + mTotalCount);
			if (mCurQuizCount < mTotalCount)
				for (int i = 0; i < MAX_CHOICES; i++) {
					mWrongImages[i].setVisibility(View.INVISIBLE);
					mAnswersViews[i].setVisibility(View.GONE);
					mChoicesViews[i].setVisibility(View.GONE);
				}
			// if the quiz stop
			if (mCurQuizCount >= mTotalCount) {
				// Exit
				if (mId != null) {
					// Come from bookmark
					finish();
					/*Intent intent = new Intent(this, BookmarkList.class);
					intent.putExtra(Const.KEY_BOOKMARK_TYPE,
							Const.VALUE_TYPE_QUIZ);
					intent.putExtra(Const.KEY_CHAPTER_ID, mChapterId);
					intent.putExtra(Const.KEY_CHAPTER_NAME, mChapterName);
					startActivity(intent);*/
					return;
				} else {
					mTotalRightPercent = mTotalCorrectNo * 100 / mTotalNo;

					mStatics.setText( mTotalCorrectNo + "/" + mTotalNo);
					mStaticsPercent.setText(mTotalRightPercent + "%");
					mCurrent_quiz_num.setText(mCurQuizCount+".");
					mTotalQuizCount.setText(" of "+mTotalCount);
					showDialog(DIALOG_COMPLETE_TEST);
					return;
				}
			}

			// Get QuizBean
			mCurQuizIndex = getNextQuizID();
			QuizBean quiz = QuizDatabaseHelper.getQuiz(mDb, mChapterId,
					mCurQuizIndex);
			mCurQuizCount++;
			if (mChapterId.equals("0")) {
				mBookmarked = quiz.isBookinComp();
			} else {
				mBookmarked = quiz.isBookmark();
			}

			String strQuestion = quiz.getQuestion();
			//			mQuestion.setText(mCurQuizCount + ". " + strQuestion);
			mQuestion.setText(strQuestion);

			// Set answers
			String[] answers = new String[MAX_CHOICES];
			int totalAnswer = 0;
			answers[0] = quiz.getRightanswer();
			answers[1] = quiz.getWronganswer1();
			answers[2] = quiz.getWronganswer2();
			answers[3] = quiz.getWronganswer3();
			answers[4] = quiz.getWronganswer4();
			int i = 0;
			for (i = 0; i < MAX_CHOICES; i++) {
				mAnswerStatuses[i] = false;
				if (answers[i] == null || answers[i].trim() == "")
					break;
				else {
					totalAnswer++;
					// possible answers
					mAnswerStatuses[i] = true;
				}
			}
			log("totalAnswer is " + totalAnswer);

			int[] temp_arry = new int[totalAnswer];
			for (int j = 0; j < totalAnswer; j++) {
				temp_arry[j] = j;
			}
			mAnswerIndexes = Util.getArrRandomSequence(temp_arry);

			String strAnswers = "";
			for (i = 0; i < totalAnswer; i++) {
				if (mAnswerIndexes[i] == 0) {
					mRightAnswerPos = i;
					log("mRightAnswerPos is " + mRightAnswerPos);
				}
				strAnswers = "";
				//				strAnswers = strAnswers.concat(CHOICES[i]);
				strAnswers = strAnswers.concat(answers[mAnswerIndexes[i]]);
				// log("strAnswers is " + strAnswers);
				mAnswersViews[i].setText(strAnswers);
				mAnswersViews[i].setVisibility(View.VISIBLE);
				mChoicesViews[i].setVisibility(View.VISIBLE);
			}

			mExplanation = CHOICES[mRightAnswerPos] + quiz.getExplanation();
			log("mExplanation: " + mExplanation);
		}
		if (mId != null) {
			// Come from bookmark
			mStatics.setText(mCurQuizCount + " of " + mTotalCount);
			mStatics.setVisibility(View.GONE);
			mStaticsPercent.setVisibility(View.GONE);
			mCurrent_quiz_num.setText(mCurQuizCount+".");
			mTotalQuizCount.setText(" of "+mTotalCount);
		} else {
			if (mTotalNo == 0){
				mTotalRightPercent = 0;
				mTotalQuizCount.setText(" of "+mTotalCount);
			}
			else{
				mTotalRightPercent = mTotalCorrectNo * 100 / mTotalNo;
				//				mStatics.setText(mCurQuizCount + " of " + mTotalCount + ", "
				//						+ mTotalCorrectNo + "/" + mTotalNo + " ("
				//						+ mTotalRightPercent + "%)");

				mStatics.setText( mTotalCorrectNo + "/" + mTotalNo);
				mStaticsPercent.setText(mTotalRightPercent + "%");
				mCurrent_quiz_num.setText(mCurQuizCount+".");
				mTotalQuizCount.setText(" of "+mTotalCount);
			}
		}
	}

	private void prepareData() {
		mStatics.setText( 0 + "/" + 0);
		mStaticsPercent.setText(0 + "%");
		mCurrent_quiz_num.setText("1.");

		mDbHelper = QuizContentProvider.createDatabaseHelper(this);
		mDb = mDbHelper.getWritableDatabase();
		if (mId != null) {
			// Come from bookmark
			mTotalCount = mIds.length;
			mIndexUsedList = new ArrayList<Integer>();
			mIndexUsedArray = new int[mTotalCount];
			for (int i = 0; i < mTotalCount; i++) {
				mIndexUsedArray[i] = 0;
				mIndexUsedList.add(mIds[i]);
				log("mIds[" + i + "] is " + mIds[i]);
			}
		} else {
			mTotalCount = QuizDatabaseHelper.getQuizCount(mDb, mChapterId);
			mIds = new int[mTotalCount];
			Cursor cursor = QuizDatabaseHelper.getQuizCursorById(mDb,
					mChapterId);
			cursor.moveToFirst();
			for (int i = 0; i < mTotalCount; i++) {
				mIds[i] = Integer.valueOf(cursor.getString(0));
				cursor.moveToNext();
			}
			cursor.close();
			mIds = Util.getArrRandomSequence(mIds);
		}

	}

	private int getNextQuizID() {
		int nextInt = -1;
		if (mIds != null && mIds.length > 0) {
			nextInt = mIds[mCurQuizCount];
		}
		return nextInt;
	}

	private static void log(String msg) {
		if (Const.DEBUG_PHASE && DEBUG_ENABLED) {
			Log.d(TAG, Const.TAG_PREFIX + msg);
		}
	}
}