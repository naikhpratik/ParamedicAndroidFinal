package com.code3apps.para;

import java.util.ArrayList;
import java.util.Random;

import com.code3apps.para.beans.FlashcardBean;
import com.code3apps.para.beans.FlashcardRecorderBean;
import com.code3apps.para.beans.QuizRecorderBean;
import com.code3apps.para.utils.Const;
import com.code3apps.para.utils.QuizContentProvider;
import com.code3apps.para.utils.QuizDatabaseHelper;
import com.code3apps.para.utils.Util;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

public class AllFlashcard extends Activity implements OnClickListener {
    private static final String TAG = "AllFlashcard";
    private static final boolean DEBUG_ENABLED = true;
    private static final int ANIMATION_DURATION = 500;
    private static final Random RANDOM = new Random();

    private ArrayList<Integer> mIndexUsedList;
    private int[] mIndexUsedArray;
    private int mTotalCount = 0;
    private int mTotalLeftNo = 0;
    private String mChapterId = "";
    private String mChapterName = "";
    private QuizDatabaseHelper mDbHelper;
    private SQLiteDatabase mDb;
    private int mCurFlashCount = 0;
    private int mCurFlashIndex = 0;
    private ViewGroup mContainer;
    private View mFlashcard, mFlashcardNext;
    private TextView mStatics;
    private TextView mQuestion;
    private TextView mAnswer;
    private TextView mBookmark;
    private ImageView mBookmarkIcon;
    private ImageView mBigNext;
    private ImageView mPrev;
    private ImageView mNext;
    private boolean mBookmarked;
    private String mId = "";
    private int[] mIds = null;
    private static final int DIALOG_SAVE_SCORE = 1;	//save score to db
    private FlashcardRecorderBean flashRecordbean = null;
    private Animation slide;
    
    private boolean mBackNotShowDialog = false;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        mChapterId = intent.getStringExtra(Const.KEY_CHAPTER_ID);
        if (!"0".equals(mChapterId)) {
            mChapterName = intent.getStringExtra(Const.KEY_CHAPTER_NAME);
        } else {
            mChapterName = getString(R.string.all_chapters);
        }
        log("mChapterId is " + mChapterId);
        log("mChapterName is " + mChapterName);
        mId = intent.getStringExtra(Const.KEY_ID);
        mIds = intent.getIntArrayExtra(Const.KEY_IDS);
        log("mId is " + mId + ", mIds is " + mIds);

        // disable default title bar
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // load view
        setContentView(R.layout.all_flashcard_new);
        ((TextView) findViewById(R.id.header)).setText(mChapterName);

        prepareViews();
        
		if(intent!=null){
			Bundle b = intent.getBundleExtra("bundle");
			if(b!=null){
				flashRecordbean = (FlashcardRecorderBean) b.get("fr");
			}
		}
		if(flashRecordbean != null){
			mDbHelper = QuizContentProvider.createDatabaseHelper(this);
			mDb = mDbHelper.getWritableDatabase();
			mTotalCount = flashRecordbean.getTotalAmount();
			mCurFlashCount = flashRecordbean.getCurrentIndex()-1;
			String[] fr_sequence = flashRecordbean.getChapterSequence().split(",");
			mIndexUsedArray = new int[mTotalCount];
			for(int m=0; m<fr_sequence.length;m++){
				mIndexUsedArray[m] = Integer.valueOf(fr_sequence[m]);
			}
		}else{
			prepareData();
		}
        loadNextFlashcard(true, true);
    }

    // @Override
    // public void onResume() {
    // super.onResume();
    // log("onResume");
    // }

    @Override
    public void onDestroy() {
        super.onDestroy();
        log("onDestroy");

        if (mDb != null)
            mDb.close();
        if (mDbHelper != null)
            mDbHelper.close();
    }

    public void onClick(View v) {
        log("onClick");
        int id = v.getId();
        
        switch (id) {
        case R.id.flashcard:
            log("mFlashcard");
            applyRotation(1, 0, 90);
            break;
        case R.id.flip_btn_img:
            log("mFlashcardNext");
            applyRotation(-1, 360, 270);
            break;
        case R.id.back_btn:
        case R.id.prev:
        	if(mCurFlashCount>1){
        		log("prev");
        		loadNextFlashcard(false, false);
        		mFlashcard.setVisibility(View.VISIBLE);
        		mFlashcardNext.setVisibility(View.GONE);
        		slide = AnimationUtils.loadAnimation(this, R.anim.slide_right);
        		slide.setDuration(ANIMATION_DURATION);
        		slide.setInterpolator(new AccelerateInterpolator());
        		mContainer.startAnimation(slide);
        	}
            break;
        case R.id.next_btn:
        case R.id.bigNext:
        case R.id.next:
        	if (mCurFlashCount >= mTotalCount){
        		new AlertDialog.Builder(AllFlashcard.this)
        		.setMessage(R.string.end_fcards_tips)
        		.setPositiveButton("YES", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						log("bigNext or next");
		        		loadNextFlashcard(false, true);
		        		mFlashcard.setVisibility(View.VISIBLE);
		        		mFlashcardNext.setVisibility(View.GONE);
		        		slide = AnimationUtils.loadAnimation(AllFlashcard.this, R.anim.slide_left);
		        		slide.setDuration(ANIMATION_DURATION);
		        		slide.setInterpolator(new AccelerateInterpolator());
		        		mContainer.startAnimation(slide);
					}
				})
				.setNegativeButton("NO", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						mBackNotShowDialog = true;
						onBackPressed();
					}
				})
				.create().show();
        	}else{
        		log("bigNext or next");
        		loadNextFlashcard(false, true);
        		mFlashcard.setVisibility(View.VISIBLE);
        		mFlashcardNext.setVisibility(View.GONE);
        		slide = AnimationUtils.loadAnimation(this, R.anim.slide_left);
        		slide.setDuration(ANIMATION_DURATION);
        		slide.setInterpolator(new AccelerateInterpolator());
        		mContainer.startAnimation(slide);
        	}
            break;
        case R.id.bookmarkIcon:
        case R.id.addToBookmarks:
            log("bookmarkIcon or addToBookmarks");
            if (mBookmarked) {
                setFlashcardBookmark(false);
                mBookmarked = false;
            } else {
                setFlashcardBookmark(true);
                mBookmarked = true;
            }
            break;
        default:
            break;
        }
    }

    private void setFlashcardBookmark(boolean bookmark) {
        if (bookmark) {
            mBookmarkIcon.setImageResource(R.drawable.bookmarked_icon);
            mBookmark.setText(R.string.bookmarked);
        } else {
            mBookmarkIcon.setImageResource(R.drawable.bookmark_icon);
            mBookmark.setText(R.string.addToBookmarks);
        }
        QuizDatabaseHelper.setFlashcardBookmark(mDb, mChapterId,
                mCurFlashIndex, bookmark);
    }

    /**
     * Setup a new 3D rotation on the container view.
     * 
     * @param position
     *            the item that was clicked to show a picture, or -1 to show the
     *            list
     * @param start
     *            the start angle at which the rotation must begin
     * @param end
     *            the end angle of the rotation
     */
    private void applyRotation(int position, float start, float end) {
        // log("applyRotation begin");
        // Find the center of the container
    	mAnswer.scrollTo(0, 0);
        final float centerX = mContainer.getWidth() / 2.0f;
        final float centerY = mContainer.getHeight() / 2.0f;

        // Create a new 3D rotation with the supplied parameter
        // The animation listener is used to trigger the next animation
        final Rotate3dAnimation rotation = new Rotate3dAnimation(start, end,
                centerX, centerY, 310.0f, true);
        rotation.setDuration(ANIMATION_DURATION);
        rotation.setFillAfter(true);
        rotation.setInterpolator(new AccelerateInterpolator());
        rotation.setAnimationListener(new DisplayNextView(position));

        mContainer.startAnimation(rotation);
        // log("applyRotation end");
    }

    /**
     * This class listens for the end of the first half of the animation. It
     * then posts a new action that effectively swaps the views when the
     * container is rotated 90 degrees and thus invisible.
     */
    private final class DisplayNextView implements Animation.AnimationListener {
        private final int mPosition;

        private DisplayNextView(int position) {
            mPosition = position;
        }

        public void onAnimationStart(Animation animation) {
            // log("onAnimationStart");
        }

        public void onAnimationEnd(Animation animation) {
            // log("onAnimationEnd");
            mContainer.post(new SwapViews(mPosition));
        }

        public void onAnimationRepeat(Animation animation) {
            // log("onAnimationRepeat");
        }
    }

    /**
     * This class is responsible for swapping the views and start the second
     * half of the animation.
     */
    private final class SwapViews implements Runnable {
        private final int mPosition;

        public SwapViews(int position) {
            mPosition = position;
        }

        public void run() {
            // log("SwapViews run start, mPosition is " + mPosition);
            final float centerX = mContainer.getWidth() / 2.0f;
            final float centerY = mContainer.getHeight() / 2.0f;
            Rotate3dAnimation rotation;

            if (mPosition > -1) {
                mFlashcard.setVisibility(View.GONE);
                mFlashcardNext.setVisibility(View.VISIBLE);

                rotation = new Rotate3dAnimation(270, 360, centerX, centerY,
                        310.0f, false);
            } else {
                mFlashcardNext.setVisibility(View.GONE);
                mFlashcard.setVisibility(View.VISIBLE);

                rotation = new Rotate3dAnimation(90, 0, centerX, centerY,
                        310.0f, false);
            }

            rotation.setDuration(ANIMATION_DURATION);
            rotation.setFillAfter(true);
            rotation.setInterpolator(new DecelerateInterpolator());

            mContainer.startAnimation(rotation);
            // log("SwapViews run end");
        }
    }

    @Override
    public void onBackPressed() {
        if (mId != null) {
            // Come from bookmark
            finish();
            Intent intent = new Intent(this, BookmarkList.class);
            intent.putExtra(Const.KEY_BOOKMARK_TYPE, Const.VALUE_TYPE_FLASHCARD);
            intent.putExtra(Const.KEY_CHAPTER_ID, mChapterId);
            intent.putExtra(Const.KEY_CHAPTER_NAME, mChapterName);
            startActivity(intent);
        } else {
        	if(mBackNotShowDialog){
        		finish();
        	}else{
        		showDialog(DIALOG_SAVE_SCORE);
        	}
        }
    }
    @Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case DIALOG_SAVE_SCORE:
			return new AlertDialog.Builder(this).setIcon(
					android.R.drawable.ic_dialog_alert).setTitle(
							R.string.quit_cards).setMessage(R.string.quit_cards_question)
							.setPositiveButton("YES",
									new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton) {
									/* User clicked OK so do some stuff */
									saveScore(false);
									finish();
								}
							}).setNegativeButton("NO",
									new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton) {
									/* User clicked Cancel so do some stuff */
									QuizDatabaseHelper.deletFlashRecorder(mDb, mChapterId);
									finish();
								}
							}).create();
		}
		return null;
	}
    
    private void saveScore(boolean finishFlag) {
    	String sequence = "";
		for(int i=0; i<mIndexUsedArray.length; i++){
			sequence += mIndexUsedArray[i]+",";
		}
		sequence.subSequence(0, mIndexUsedArray.length-1);
		FlashcardRecorderBean fr = new FlashcardRecorderBean();
		fr.setChapterId(Integer.valueOf(mChapterId));
		fr.setChapterSequence(sequence);
		fr.setCurrentIndex(mCurFlashCount);
		fr.setTotalAmount(mTotalCount);
		fr.setFinished(0);
    	QuizDatabaseHelper.saveFlashChaperHistoryRecorder(mDb, fr);
    }
    
    private void prepareViews() {
        mContainer = (ViewGroup) findViewById(R.id.container);
        mFlashcard = findViewById(R.id.flashcard);
        mFlashcardNext = findViewById(R.id.flashcardnext);

        mStatics = (TextView) findViewById(R.id.statics);
        mQuestion = (TextView) findViewById(R.id.question);
        mAnswer = (TextView) findViewById(R.id.answer);
        mBookmark = (TextView) findViewById(R.id.addToBookmarks);
        mBookmarkIcon = (ImageView) findViewById(R.id.bookmarkIcon);
        mPrev = (ImageView) findViewById(R.id.prev);
        mNext = (ImageView) findViewById(R.id.next);

        mFlashcard.setOnClickListener(this);
//        mFlashcardNext.setOnClickListener(this);
        mBookmark.setOnClickListener(this);
        mBookmarkIcon.setOnClickListener(this);
        mPrev.setOnClickListener(this);
        mNext.setOnClickListener(this);
        findViewById(R.id.back_btn).setOnClickListener(this);
        findViewById(R.id.next_btn).setOnClickListener(this);
        findViewById(R.id.flip_btn_img).setOnClickListener(this);
        mPrev.setClickable(false);
        mAnswer.setMovementMethod(ScrollingMovementMethod.getInstance());
    }

    private void loadNextFlashcard(boolean bFirsttime, boolean bNext) {
        log("loadNextFlashcard, bFirsttime is " + bFirsttime + ", bNext is "
                + bNext);
        log("mCurFlashCount is " + mCurFlashCount);
        if (bNext) {
            // Get Next
//            if (mId != null && bFirsttime) {
//                mCurFlashIndex = Integer.valueOf(mId);
//                mIndexUsedArray[0] = mCurFlashIndex;
//                mIndexUsedList.remove(Integer.valueOf(mId));
////                 log("mIndexUsedArray[0] is " + mIndexUsedArray[0]);
//            } else {
//                if (mCurFlashCount >= mTotalCount)
//                    mCurFlashCount = 0;
//                if (mIndexUsedArray[mCurFlashCount] == 0) {
//                    mCurFlashIndex = getNextInt();
//                    mIndexUsedArray[mCurFlashCount] = mCurFlashIndex;
////                     log("mIndexUsedArray[" + mCurFlashCount + "] is "
////                     + mIndexUsedArray[mCurFlashCount]);
//                } else {
//                    mCurFlashIndex = mIndexUsedArray[mCurFlashCount];
////                     log("mIndexUsedArray[" + mCurFlashCount + "] is "+ mIndexUsedArray[mCurFlashCount]);
//                }
//            }
//            mCurFlashCount++;
        	if (mCurFlashCount >= mTotalCount)
        		mCurFlashCount = 0;
        	mCurFlashIndex = getNextFlashId();
        	mCurFlashCount++;
        } else {
            // Get Previous
            mCurFlashCount = mCurFlashCount - 2;
            if (mCurFlashCount < 0)
                mCurFlashCount = mTotalCount - 1;
            mCurFlashIndex = getNextFlashId();
//            mCurFlashIndex = mIndexUsedArray[mCurFlashCount];
//            log("mIndexUsedArray[" + mCurFlashCount + "] is "+ mIndexUsedArray[mCurFlashCount]);
            mCurFlashCount++;
        }
        FlashcardBean flashcard = QuizDatabaseHelper.getFlashcard(mDb,
                mChapterId, mCurFlashIndex);

        String strQuestion = flashcard.getQuestion();
        String strAnswer = flashcard.getAnswer();
        if (mChapterId.equals("0")) {
            mBookmarked = flashcard.isBookinAll();
        } else {
            mBookmarked = flashcard.isBookmark();
        }
        mQuestion.setText(strQuestion);
        mAnswer.setText(strAnswer);

        log("mCurQuizIndex is " + mCurFlashIndex + ", mBookmarked is "
                + mBookmarked);
        if (mBookmarked) {
            mBookmarkIcon.setImageResource(R.drawable.bookmarked_icon);
            mBookmark.setText(R.string.unbookmark);
        } else {
            mBookmarkIcon.setImageResource(R.drawable.bookmark_icon);
            mBookmark.setText(R.string.addToBookmarks);
        }
        mStatics.setText(mCurFlashCount + " of " + mTotalCount);

        int intPrev = mCurFlashCount - 2;
        if (intPrev < 0)
            intPrev = mTotalCount - 1;
//        if (mIndexUsedArray[intPrev] == 0)
//            mPrev.setClickable(false);
//        else
//            mPrev.setClickable(true);
        if (intPrev == -1)
          mPrev.setClickable(false);
        else
          mPrev.setClickable(true);
    }

    private void prepareData() {
        log("prepareData begin");
        mDbHelper = QuizContentProvider.createDatabaseHelper(this);
        mDb = mDbHelper.getWritableDatabase();
        if (mId != null) {
            // Come from bookmark
        	System.out.println("prepareData mId != null");
            mTotalCount = mIds.length;
            mIndexUsedList = new ArrayList<Integer>();
            mIndexUsedArray = new int[mTotalCount];
            for (int i = 0; i < mTotalCount; i++) {
                mIndexUsedArray[i] = 0;
                mIndexUsedList.add(mIds[i]);
                log("mIds[" + i + "] is " + mIds[i]);
            }
        } else {
        	System.out.println("prepareData mId == null");
        	//mIds = Util.getArrRandomSequence(mIds);
            mTotalCount = QuizDatabaseHelper.getFlashcardCount(mDb, mChapterId);
            mIndexUsedList = new ArrayList<Integer>();
            mIndexUsedArray = new int[mTotalCount];
            Cursor cursor = QuizDatabaseHelper.getFlashcardCursorById(mDb,
                    mChapterId);
            cursor.moveToFirst();
            for (int i = 0; i < mTotalCount; i++) {
//                mIndexUsedArray[i] = 0;
                mIndexUsedList.add(Integer.valueOf(cursor.getString(0)));
                cursor.moveToNext();
            }
            cursor.close();
            mIndexUsedArray = Util.getArrRandomSequence(mIds);
        }
        log("prepareData end");
    }

    private int getNextInt() {
        mTotalLeftNo = mTotalCount - mCurFlashCount;
        int nextRandom = RANDOM.nextInt(mTotalLeftNo);
        int nextInt = mIndexUsedList.get(nextRandom);
        mIndexUsedList.remove(nextRandom);
        log("mTotalLeftNo is " + mTotalLeftNo + ", nextRandom is " + nextRandom
                + ", nextInt is " + nextInt);
        return nextInt;
    }

    private int getNextFlashId() {
    	int nextInt = -1;
		if (mIndexUsedArray != null && mIndexUsedArray.length > 0) {
			nextInt = mIndexUsedArray[mCurFlashCount];
		}
		return nextInt;
    }
    
    private static void log(String msg) {
        if (Const.DEBUG_PHASE && DEBUG_ENABLED) {
            Log.d(TAG, Const.TAG_PREFIX + msg);
        }
    }

}