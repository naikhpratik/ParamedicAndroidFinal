package com.code3apps.para;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.code3apps.para.beans.CardiologyBean;
import com.code3apps.para.utils.Const;
import com.code3apps.para.utils.QuizContentProvider;
import com.code3apps.para.utils.QuizDatabaseHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class File extends Activity implements OnClickListener{

    private int hint;
    private QuizDatabaseHelper mDbHelper;
    private SQLiteDatabase mDb;
    private ArrayList<CardiologyBean> cardioList;
    private String cardioType;
	private ViewGroup mContainer;
	private static final int ANIMATION_DURATION = 500;
	 private View mFlashcard, mFlashcardNext;
    private TextView tvNoBookmared;
    private RelativeLayout rlFooter;
    private TextView namechange;
    private SharedPreferences preference;

    private TextView tvQuestionStatics;
    private TextView tvQuestionTotalStatics;

    private int index = 0;
    private int index1 = 0;
    private boolean isFromBookmark;

    private TextView tvAddToBookmarkAnswer;
    private TextView tvAddToBookmarkHint;

    private ImageView imgAnswer;

    private static final int DIALOG_SAVE_SCORE = 1;
    private TextView tvHint;
    private TextView tvExplanation;
    private Button tvbck;
    private Button tvfrd;

    private Button tvnextquestion;
    private ImageView tvfooternext;
    private Button tvprevquestion;
    private ImageView tvfooterprev;
    boolean flag=true;
    //ArrayList<Integer> random = new ArrayList<Integer>();
   // Set<Integer> generated = new LinkedHashSet<Integer>();
    //Random rng = new Random();
    List<Integer> numbers = new ArrayList<Integer>();
	
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
		setContentView(R.layout.file);

        getCardioDataList();
        for (int i = 0; i < cardioList.size(); i++)
            numbers.add(i);
        Collections.shuffle(numbers);
        if(cardioList.size()>0)
        index1=numbers.get(0);
        //System.out.println("Sizebagh : "+cardioList.size());
        prepareViews();
        if(cardioList.size() > 0){
            System.out.println("Size : "+cardioList.size());
            mContainer.setVisibility(View.VISIBLE);
            tvNoBookmared.setVisibility(View.GONE);
            rlFooter.setVisibility(View.VISIBLE);
            setViewsData();
        }else {
            mContainer.setVisibility(View.GONE);
            rlFooter.setVisibility(View.GONE);
            tvNoBookmared.setVisibility(View.VISIBLE);
           // setViewsData();
        }



	}
    private void prepareViews() {

        mContainer = (ViewGroup) findViewById(R.id.fl_container_cardiology);
        mFlashcard = (View)findViewById(R.id.ll_cardiology_next);
        mFlashcardNext =(View) findViewById(R.id.ll_cardiology_previous);
        tvNoBookmared = (TextView) findViewById(R.id.tv_nobookmared_cardio_comp);

        findViewById(R.id.btn_ans_cardioLogy).setOnClickListener(this);
        findViewById(R.id.btn_hint_cardiology).setOnClickListener(this);
        findViewById(R.id.btn_nextquestion_cardiology).setOnClickListener(this);
        findViewById(R.id.btn_previousquestion_cardiology).setOnClickListener(this);

        findViewById(R.id.img_footernext_cardio_comp).setOnClickListener(this);
        findViewById(R.id.img_footerprev_cardio_comp).setOnClickListener(this);

        findViewById(R.id.btn_question_cardiology).setOnClickListener(this);
        findViewById(R.id.tv_addToBookmarks_cardio_comp).setOnClickListener(this);

        namechange = (TextView) findViewById(R.id.title_change_Question_Explanation_Hint);

        //tvAddToBookmarkAnswer = (TextView) findViewById(R.id.tv_addToBookmarks_cardio_comp);
        tvAddToBookmarkHint = (TextView) findViewById(R.id.tv_addToBookmarks_cardio_comp);

        imgAnswer = (ImageView) findViewById(R.id.img_cardio_comp);
        rlFooter = (RelativeLayout) findViewById(R.id.rl_footer_prev_next_cardio_comp);
        tvHint = (TextView) findViewById(R.id.display_hint);
        tvExplanation = (TextView) findViewById(R.id.display_explanation);
        tvbck = (Button) findViewById(R.id.btn_previousquestion_cardiology);
        tvfrd = (Button) findViewById(R.id.btn_nextquestion_cardiology);

        tvQuestionStatics = (TextView) findViewById(R.id.textView1);
        tvQuestionTotalStatics = (TextView) findViewById(R.id.textView2);

        tvnextquestion = (Button) findViewById(R.id.btn_nextquestion_cardiology);
        tvfooternext = (ImageView) findViewById(R.id.img_footernext_cardio_comp);

        tvprevquestion = (Button) findViewById(R.id.btn_previousquestion_cardiology);
        tvfooterprev = (ImageView) findViewById(R.id.img_footerprev_cardio_comp);
    }

    private void setViewsData() {
    //System.out.println("List : "+cardioList);
      //  int id= Integer.valueOf(cardioList.get(index).getQuestion());
        int id = getResources().getIdentifier(
                cardioList.get(index1).getQuestion().toString().split("\\.")[0],
                "drawable", this.getPackageName());
        //System.out.println(index+": Id : "+id);
         imgAnswer.setImageResource(id);
        //imgAnswer.setScaleType(ImageView.ScaleType.FIT_XY);
        //System.out.println("Hint :"+cardioList.get(index).getHint());
        //String set = cardioList.get(index).getHint();

        tvQuestionStatics.setText(index+1 +"");
        tvQuestionTotalStatics.setText( " of " + cardioList.size());
        //int a= R.id.btn_hint_cardiology;
       // System.out.println(cardioList.get(index).getExplanation()+" : "+cardioList.get(index).getHint());
        //tvHint.setText(cardioList.get(index).getHint());
        tvExplanation.setText(cardioList.get(index1).getExplanation());
        tvHint.setText(cardioList.get(index1).getHint());

        if (cardioList.get(index1).isBookmark()) {
            //tvAddToBookmarkAnswer.setText("Unbookmark");
            tvAddToBookmarkHint.setText("unbookmark");
        } else {
           // tvAddToBookmarkAnswer.setText("Add to Bookmark");
            tvAddToBookmarkHint.setText("Add to Bookmark");
        }
    }
	/*
     * (non-Javadoc)
     * @see android.view.View.OnClickListener#onClick(android.view.View)
     */
	@Override
	public void onClick(View v) {
       // ArrayList<Integer> Randnum=new ArrayList<Integer>();


        switch (v.getId()) {
		
		case R.id.btn_ans_cardioLogy:

            tvbck.setVisibility(View.VISIBLE);
            tvfrd.setVisibility(View.VISIBLE);
            tvHint.setVisibility(View.GONE);
            tvExplanation.setVisibility(View.VISIBLE);
            applyRotation(1, 0, 90);
            namechange.setText("Explanation :");
            hint = 0;
            setViewsData();
            break;

        case R.id.img_footernext_cardio_comp:
		case R.id.btn_nextquestion_cardiology:
            System.out.println("Index = "+index+" Cardiolist Size = "+cardioList.size());
            if(index<cardioList.size()-1){
            index++;
            index1 = numbers.get(index);}
            System.out.println("Index = "+index+"Index1 = "+index1);


            if(index < cardioList.size())
            {
            applyRotation(-1,0,90);
             boolean A =true;
                while(A){
                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                        setViewsData();
                        }
                    }, 600);
                    A=false;
                }

            //System.out.println("Hi : "+index);
            }else
            {
                tvfooternext.setVisibility(View.GONE);
                tvnextquestion.setVisibility(View.GONE);
            }
			break;


        case R.id.btn_hint_cardiology:
            hint = 1;
            tvHint.setVisibility(View.VISIBLE);
            tvExplanation.setVisibility(View.GONE);
            tvbck.setVisibility(View.GONE);
            tvfrd.setVisibility(View.GONE);
            applyRotation(1,0,90);
            namechange.setText("Hint :");
            break;

        case R.id.btn_question_cardiology:
            applyRotation(-1, 360, 270);
            break;


        case R.id.img_footerprev_cardio_comp:
        case R.id.btn_previousquestion_cardiology:
            if(index <= 0)
            {}else{index--;
                index1 = numbers.get(index);

            System.out.println("Index = "+index+"Index1 = "+index1);
            if(index>=0)
            {
            //
            applyRotation(-1, 360, 270);
                boolean A =true;
                while(A){
                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            setViewsData();
                        }
                    }, 600);
                    A=false;
                }

            }else
            {
                tvfooterprev.setVisibility(View.GONE);
                tvprevquestion.setVisibility(View.GONE);
            }
        }
            break;

            case R.id.tv_addToBookmarks_cardio_comp:
                if (cardioList.get(index1).isBookmark()) {// true
                   // tvAddToBookmarkAnswer.setText("Add to Bookmark");
                    tvAddToBookmarkHint.setText("Add to Bookmark");
                    cardioList.get(index1).setBookmark(false);
                } else {
                    //tvAddToBookmarkAnswer.setText("Unbookmark");
                    tvAddToBookmarkHint.setText("Unbookmark");
                    cardioList.get(index1).setBookmark(true);
                }
                QuizDatabaseHelper.setCardiologyBookmark(mDbHelper.getWritableDatabase(),index1+1, cardioList.get(index1).isBookmark());
                //getCardioDataList();
                break;
		default:
			break;
		}
		
	}

    private void getCardioDataList() {
        mDbHelper = QuizContentProvider
                .createDatabaseHelper(getApplicationContext());
        mDb = mDbHelper.getWritableDatabase();
        cardioType = getIntent().getExtras().getString(Const.KEY_CARDIOLOGY);
        //cardioType = Const.KEY_COMPREHENSIVE;
        System.out.println("cardio type :"+cardioType);
        System.out.println("Const type :"+Const.KEY_BOOKMARK);

        index = getIntent().getExtras().getInt("index");
       // namechange.setText("Explanation :");
        if (cardioType.equalsIgnoreCase(Const.KEY_BOOKMARK)) {
            isFromBookmark = true;
            cardioList = QuizDatabaseHelper.getCardiologyBookmarkedList(mDb);
        } else {
            isFromBookmark = false;
            cardioList = QuizDatabaseHelper.getCardiologyList(mDb);
        }
    }

    public void onBackPressed() {
        if(isFromBookmark){
            getCardioDataList();
            finish();
        }else{
            showDialog(DIALOG_SAVE_SCORE);
        }
    }

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
                                        saveCardioCardsHistory();
                                        finish();
                                    }
                                })
                        .setNegativeButton("NO",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                                        int whichButton) {
                                        index = -1;
                                        saveCardioCardsHistory();
                                        finish();
                                    }
                                }).create();
        }
        return null;
    }

    private void saveCardioCardsHistory(){
        preference = getSharedPreferences("CardioCardsHistory", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preference.edit();
        editor.putInt("index", index);
        editor.apply();
    }
	/**
	 * Animation Code 
	 */
	
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

    

}
