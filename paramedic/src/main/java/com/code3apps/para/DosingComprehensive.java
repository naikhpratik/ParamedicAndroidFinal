package com.code3apps.para;

import java.io.InputStream;
import java.util.ArrayList;

import com.code3apps.para.R.id;
import com.code3apps.para.beans.DosingBean;
import com.code3apps.para.utils.Const;
import com.code3apps.para.utils.QuizContentProvider;
import com.code3apps.para.utils.QuizDatabaseHelper;
import com.google.analytics.tracking.android.Log;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class DosingComprehensive extends Activity implements OnClickListener {

	private QuizDatabaseHelper mDbHelper;
	private SQLiteDatabase mDb;
	private SharedPreferences preference;

	private static final int ANIMATION_DURATION = 500;
	private Animation slide;

	private ViewGroup mContainer;
	private View mQuestion, mAnswer, mEquation;

	private boolean showAnswer;
	private boolean showEquation;
	private ArrayList<DosingBean> dosingList;
	private String dosingType;
	private static final int DIALOG_SAVE_SCORE = 1; // save score to db

	private TextView tvQuestionStatics;
	private TextView tvQuestionTotalStatics;
	private TextView tvQuestion;

	private ImageView imgAnswer;
	private TextView tvEquation;
	private TextView tvNoBookmared;

	private RelativeLayout rlFooter;
	private RelativeLayout rlAnswerBookmark;
	private RelativeLayout rlEquationBookmark;
	private TextView tvAddToBookmarkAnswer;
	private TextView tvAddToBookmarkEquation;
	
	private int index = 0;
	private boolean isFromBookmark;
	

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.dosing_comprehensive);
//		int tempIndex = getDosingCardsHistory();
//		if(tempIndex >= 0){
//			index = tempIndex;
//		}else{
//			index = 0;
//		}
		getDosingDataList();
		prepareViews();
		if(dosingList.size() > 0){
			mContainer.setVisibility(View.VISIBLE);
			tvNoBookmared.setVisibility(View.GONE);
			rlFooter.setVisibility(View.VISIBLE);
			setViewsData();
		}else {
			mContainer.setVisibility(View.GONE);
			rlFooter.setVisibility(View.GONE);
			tvNoBookmared.setVisibility(View.VISIBLE);
		}
	}

	/*
	 * 
	 */
	private void getDosingDataList() {
		mDbHelper = QuizContentProvider
				.createDatabaseHelper(getApplicationContext());
		mDb = mDbHelper.getWritableDatabase();
		dosingType = getIntent().getExtras().getString(Const.KEY_DOSING);
		System.out.println("Dosing type :"+dosingType);
		System.out.println("Const type :"+Const.KEY_BOOKMARK);

		index = getIntent().getExtras().getInt("index");

		if (dosingType.equalsIgnoreCase(Const.KEY_BOOKMARK)) {
			isFromBookmark = true;
			dosingList = QuizDatabaseHelper.getDosingBookmarkedList(mDb);
		} else {
			isFromBookmark = false;
			dosingList = QuizDatabaseHelper.getDosingList(mDb);
		}
	}
	
	/*
	 * 
	 */
	private void prepareViews() {
		mContainer = (ViewGroup) findViewById(R.id.container_dosing_com);
		tvNoBookmared = (TextView) findViewById(R.id.tv_nobookmared_dosing_comp);
		getDosingDataList();
		mQuestion = findViewById(R.id.rl_question_dosing_comp);
		mAnswer = findViewById(R.id.rl_answer_dosing_comp);
		mEquation = findViewById(R.id.rl_equation_dosing_comp);

		tvQuestionStatics = (TextView) findViewById(R.id.tv_statics_dosing_comp);
		tvQuestionTotalStatics = (TextView) findViewById(id.tv_statics_total_dosing_comp);
		tvQuestion = (TextView) findViewById(R.id.tv_question_dosing_comp);
		imgAnswer = (ImageView) findViewById(R.id.img_answer_dosing_comp);
		tvEquation = (TextView) findViewById(R.id.tv_equation_dosing_comp);

		rlFooter = (RelativeLayout) findViewById(R.id.rl_footer_prev_next_dosing_comp);
		rlAnswerBookmark = (RelativeLayout) findViewById(R.id.rl_bookmark_dosing_comp);
		rlEquationBookmark = (RelativeLayout) findViewById(R.id.rl_bookmark_equation_dosing_comp);
		rlAnswerBookmark.setOnClickListener(this);
		rlEquationBookmark.setOnClickListener(this);
		

		findViewById(R.id.img_footernext_dosing_comp).setOnClickListener(this);
		findViewById(R.id.img_footerprev_dosing_comp).setOnClickListener(this);

		// For Question
		findViewById(R.id.btn_answer_dosin_comp).setOnClickListener(this);
		findViewById(R.id.img_goback_answer_dosing_comp).setOnClickListener(
				this);
		findViewById(R.id.btn_equation_dosin_comp).setOnClickListener(this);

		// For Equation
		findViewById(R.id.btn_question_equation_dosing_comp)
				.setOnClickListener(this);
		findViewById(R.id.btn_answer_equation_dosing_comp).setOnClickListener(
				this);

		// For Answer
		findViewById(R.id.btn_back_answer_dosing_comp).setOnClickListener(this);
		findViewById(R.id.btn_next_answer_dosing_comp).setOnClickListener(this);
		tvAddToBookmarkAnswer = (TextView) findViewById(R.id.tv_addToBookmarks_dosing_comp);
		tvAddToBookmarkEquation = (TextView) findViewById(R.id.tv_addToBookmarks_equation_dosing_comp);

		mQuestion.setOnClickListener(this);
		mAnswer.setOnClickListener(this);
		mEquation.setOnClickListener(this);
	}

	/*
	 * 
	 */
	private void setViewsData() {
		int id = getResources().getIdentifier(
				dosingList.get(index).getAnswer().toString().split("\\.")[0],
				"drawable", this.getPackageName());
		imgAnswer.setImageResource(id);
		tvQuestion.setText(dosingList.get(index).getQuestion().toString());
		tvQuestionStatics.setText(index+1 +"");
		tvQuestionTotalStatics.setText( " of " + dosingList.size());
		tvEquation.setText(dosingList.get(index).getEquation().toString());

		if (dosingList.get(index).isBookmark()) {
			tvAddToBookmarkAnswer.setText("Unbookmark");
			tvAddToBookmarkEquation.setText("unbookmark");
		} else {
			tvAddToBookmarkAnswer.setText("Add to Bookmark");
			tvAddToBookmarkEquation.setText("Add to Bookmark");
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onBackPressed()
	 */
	@SuppressWarnings("deprecation")
	@Override
	public void onBackPressed() {
		if(isFromBookmark){
			finish();
		}else{
			showDialog(DIALOG_SAVE_SCORE);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreateDialog(int)
	 */
	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case DIALOG_SAVE_SCORE:
			return new AlertDialog.Builder(this)
					.setIcon(android.R.drawable.ic_dialog_alert)
					.setTitle(R.string.quit_dosing_cards)
					.setMessage(R.string.quit_dosing_message)
					.setPositiveButton("YES",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton) {
									saveDosingCardsHistory();
									finish();
								}
							})
					.setNegativeButton("NO",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton) {
									index = -1;
									saveDosingCardsHistory();
									finish();
								}
							}).create();
		}
		return null;
	}
	
	/*
	 * 
	 */
	private void saveDosingCardsHistory(){	
		preference = getSharedPreferences("DosingCardsHistory", Context.MODE_PRIVATE);
		Editor editor = preference.edit();
		editor.putInt("index", index);
		editor.apply();
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 */
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		int id = v.getId();
		switch (id) {

		// For Question
		case R.id.btn_answer_dosin_comp: // Go to Answer
			showEquation = false;
			showAnswer = true;
			applyRotation(1, 0, 90);// Show question
			break;

		case R.id.btn_equation_dosin_comp: // Go to Equation
			showAnswer = false;
			showEquation = true;
			applyRotation(1, 0, 90);
			break;

		// For Answer
		case R.id.img_goback_answer_dosing_comp: // Go to Question
			showAnswer = false;
			showEquation = false;
			applyRotation(-1, 360, 270);// show answer or equation
			break;

		case R.id.img_footerprev_dosing_comp:
		case R.id.btn_back_answer_dosing_comp:
			if (index > 0) {
				index--;
				mQuestion.setVisibility(View.VISIBLE);
				mAnswer.setVisibility(View.GONE);
				slide = AnimationUtils.loadAnimation(this, R.anim.slide_right);
				slide.setDuration(ANIMATION_DURATION);
				slide.setInterpolator(new AccelerateInterpolator());
				mContainer.startAnimation(slide);
				setViewsData();
			}
			break;

		case R.id.img_footernext_dosing_comp:
		case R.id.btn_next_answer_dosing_comp:
			if (index + 1 < dosingList.size()) {
				index++;
				mQuestion.setVisibility(View.VISIBLE);
				mAnswer.setVisibility(View.GONE);
				slide = AnimationUtils.loadAnimation(this, R.anim.slide_left);
				slide.setDuration(ANIMATION_DURATION);
				slide.setInterpolator(new AccelerateInterpolator());
				mContainer.startAnimation(slide);
				setViewsData();
			}
			break;

		// For Equation
		case R.id.btn_question_equation_dosing_comp:// Go to Question
			showAnswer = false;
			showEquation = false;
			applyRotation(-1, 360, 270);
			break;

		case R.id.btn_answer_equation_dosing_comp: // Go to answer
			showAnswer = true;
			showEquation = false;
			applyRotation(1, 0, 90);
			break;

		case R.id.rl_bookmark_dosing_comp:
		case R.id.rl_bookmark_equation_dosing_comp:
			if (dosingList.get(index).isBookmark()) {// true
				tvAddToBookmarkAnswer.setText("Add to Bookmark");
				tvAddToBookmarkEquation.setText("Add to Bookmark");
				dosingList.get(index).setBookmark(false);
			} else {
				tvAddToBookmarkAnswer.setText("Unbookmark");
				tvAddToBookmarkEquation.setText("Unbookmark");
				dosingList.get(index).setBookmark(true);
			}
			QuizDatabaseHelper.setDosingBookmark(mDbHelper.getWritableDatabase(),index+1, dosingList.get(index).isBookmark());
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
				// Display Answer or Eqation
				mQuestion.setVisibility(View.GONE);

				if (showAnswer) { // answer
					mAnswer.setVisibility(View.VISIBLE);
					mEquation.setVisibility(View.GONE);
				} else if (showEquation) {
					mEquation.setVisibility(View.VISIBLE);
					mAnswer.setVisibility(View.GONE);
				}
				rotation = new Rotate3dAnimation(270, 360, centerX, centerY,
						310.0f, false);
			} else {
				if (!showAnswer && !showEquation) { // answer
					mAnswer.setVisibility(View.GONE);
					// }else if(showEquation){
					mEquation.setVisibility(View.GONE);
				}
				// mAnswer.setVisibility(View.GONE);
				mQuestion.setVisibility(View.VISIBLE);

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

}
