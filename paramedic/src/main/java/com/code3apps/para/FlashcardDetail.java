package com.code3apps.para;

import com.code3apps.para.beans.FlashcardBean;
import com.code3apps.para.utils.Const;
import com.code3apps.para.utils.QuizContentProvider;
import com.code3apps.para.utils.QuizDatabaseHelper;

import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.Window;
import android.widget.TextView;

public class FlashcardDetail extends Activity implements OnClickListener {
    private static final String TAG = "AllFlashcard";
    private static final boolean DEBUG_ENABLED = true;
    private static final int ANIMATION_DURATION = 500;
    private QuizDatabaseHelper mDbHelper;
    private SQLiteDatabase mDb;
    private ViewGroup mContainer;
    private View mFlashcard, mFlashcardNext;
    private TextView mQuestion;
    private TextView mQuizz;
    private TextView mAnswer;
    private View mReturnTerm;
    private String mId = "";

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        log("onCreate");
        // disable default title bar
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // load view
        setContentView(R.layout.flashcard_detail);

        Intent intent = getIntent();
        mId = intent.getStringExtra(Const.KEY_ID);
        log("mId is " + mId);

        prepareViews();
        loadFlashcard(mId);
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

    public void onClick(View v) {
        log("onClick");
        int id = v.getId();
        switch (id) {
        case R.id.flashcard:
            log("mFlashcard");
            applyRotation(1, 0, 90);
            break;
        case R.id.flashcardnext:
        case R.id.answer:
        case R.id.quizz:
            log("mFlashcardNext");
            applyRotation(-1, 360, 270);
            break;
        case R.id.back_btn:
            this.finish();
            break;
        default:
            break;
        }
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

    private void prepareViews() {
        mContainer = (ViewGroup) findViewById(R.id.container);
        mFlashcard = findViewById(R.id.flashcard);
        mFlashcardNext = findViewById(R.id.flashcardnext);
//        mReturnTerm = findViewById(R.id.imgReturnTerm);
        mAnswer = (TextView) findViewById(R.id.answer);
        mQuestion = (TextView) findViewById(R.id.question);
        mQuizz = (TextView) findViewById(R.id.quizz);

        findViewById(R.id.back_btn).setOnClickListener(this);
        mFlashcard.setOnClickListener(this);
//        mReturnTerm.setOnClickListener(this);
        mFlashcardNext.setOnClickListener(this);
        mAnswer.setOnClickListener(this);
        mQuizz.setOnClickListener(this);
        mQuizz.setMovementMethod(ScrollingMovementMethod.getInstance());
        mAnswer.setMovementMethod(ScrollingMovementMethod.getInstance());
    }

    private void loadFlashcard(String _id) {
        mDbHelper = QuizContentProvider.createDatabaseHelper(this);
        mDb = mDbHelper.getWritableDatabase();
        FlashcardBean flashcard = QuizDatabaseHelper.getFlashcardDetailById(
                mDb, _id);
        String strQuestion = flashcard.getQuestion();
        String strAnswer = flashcard.getAnswer();
        mQuestion.setText(strQuestion);
        mQuizz.setText(strQuestion);
        mAnswer.setText(strAnswer);
    }

    private static void log(String msg) {
        if (Const.DEBUG_PHASE && DEBUG_ENABLED) {
            Log.d(TAG, Const.TAG_PREFIX + msg);
        }
    }

}