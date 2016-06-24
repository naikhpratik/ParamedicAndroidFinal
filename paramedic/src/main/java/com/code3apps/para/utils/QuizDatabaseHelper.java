package com.code3apps.para.utils;

import java.util.ArrayList;
import java.util.List;

import com.code3apps.para.beans.BookmarkBean;
import com.code3apps.para.beans.CardiologyBean;
import com.code3apps.para.beans.ChapterBean;
import com.code3apps.para.beans.CheckListDetailBean;
import com.code3apps.para.beans.DosingBean;
import com.code3apps.para.beans.FlashcardBean;
import com.code3apps.para.beans.FlashcardRecorderBean;
import com.code3apps.para.beans.HtmlFileBean;
import com.code3apps.para.beans.QuizBean;
import com.code3apps.para.beans.QuizRecorderBean;
import com.code3apps.para.beans.SkillSheetBean;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class QuizDatabaseHelper extends SQLiteOpenHelper {
	private static final String TAG = "QuizDatabaseHelper";
	private static final boolean DEBUG_ENABLED = true;
	public static final int DB_VERSION = 1;
	private static final String TABLE_QUIZ = "quiz";
	private static final String TABLE_QUIZ_SCORE = "quizScore";

	private static final String TABLE_CARDIOLOGY = "Cardiology_Questions";
	private static final String TABLE_DOSING = "Dosing_Questions";
	
	private static final String TABLE_FLASHCARD = "flashcard";
	private static final String TABLE_QUIZ_CHAPTER = "quizChapter";
	private static final String TABLE_FLASHCARD_CHAPTER = "chapterFcard";
	private static final String TABLE_SKILL_SHEET = "skillsheet";
	private static final String TABLE_SCENARIOS = "scenarios";
	private static final String COLUMN_ID = "id";
	private static final String COLUMN_QUESTION = "question";
	private static final String COLUMN_ANSWER = "answer";
	private static final String COLUMN_CHAPTER = "chapter";
	private static final String COLUMN_RESULT = "result";
	private static final String COLUMN_CORRECT_QUIZ = "correctqiz";
	private static final String COLUMN_DONE_QUIZ = "doneqiz";
	private static final String COLUMN_NAME = "name";
	private static final String COLUMN_BOOKMARK = "bookmark";
	private static final String COLUMN_BOOKINCOMP = "bookinComp";
	private static final String COLUMN_BOOKINALL = "bookinall";
	private static final String COLUMN_BOOK = "book";

	//v1.4 from FireFighter
	private static final String TABLE_CHAPTER_CHECKLIST = "chaptersChecklist";
	private static final String COLUMN_CHECKED = "checked";
	private static final String TABLE_CHAPTER_CHECKLISTDETAIL = "checklist";
	private static final String COLUMN_CHAPTER_ID = "chapter_id";

	public QuizDatabaseHelper(Context context, String name) {
		super(context, name, null, DB_VERSION);
		log("QuizDatabaseHelper");
	}

	/** Called when the activity is first created. */
	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		log("onCreate");

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		log("onUpgrade");

	}

	public static int getQuizCount(SQLiteDatabase db, String chapterId) {
		log("getQuizCount");
		int result = 0;
		String sql;
		if (chapterId.equals("0"))
			sql = "SELECT count(*) FROM " + TABLE_QUIZ;
		else
			sql = "SELECT count(*) FROM " + TABLE_QUIZ + " WHERE chapter = "
					+ chapterId;
		log("sql is " + sql);
		Cursor cursor = db.rawQuery(sql, null);
		if (null != cursor) {
			cursor.moveToFirst();
			result = cursor.getInt(0);
			cursor.close();
		}
		return result;
	}

	public static int getFlashcardCount(SQLiteDatabase db, String chapterId) {
		log("getFlashcardCount");
		int result = 0;
		String sql;
		if ("0".equals(chapterId))
			sql = "SELECT count(*) FROM " + TABLE_FLASHCARD;
		else
			sql = "SELECT count(*) FROM " + TABLE_FLASHCARD
			+ " WHERE chapter = " + chapterId;
		log("sql is " + sql);
		Cursor cursor = db.rawQuery(sql, null);
		if (null != cursor) {
			cursor.moveToFirst();
			result = cursor.getInt(0);
			cursor.close();
		}
		return result;
	}

	public static Cursor getQuizCursorById(SQLiteDatabase db, String chapterId) {
		log("getQuizCursorForId");
		String sql;
		if (chapterId.equals("0"))
			sql = "SELECT " + COLUMN_ID + " FROM " + TABLE_QUIZ;
		else
			sql = "SELECT " + COLUMN_ID + " FROM " + TABLE_QUIZ
			+ " WHERE chapter = " + chapterId;
		log("sql is " + sql);
		Cursor cursor = db.rawQuery(sql, null);
		return cursor;
	}

	public static Cursor getFlashcardCursorById(SQLiteDatabase db,
			String chapterId) {
		log("getFlashcardCursorById");
		String sql;
		if (chapterId.equals("0"))
			sql = "SELECT _id" + " FROM " + TABLE_FLASHCARD;
		else
			sql = "SELECT _id"+ " FROM " + TABLE_FLASHCARD
			+ " WHERE chapter = " + chapterId;
		log("sql is " + sql);
		Cursor cursor = db.rawQuery(sql, null);
		return cursor;
	}
	
	/**
	 * added by Anum Tariq Butt
	 * @param db
	 * @return
	 */
	public static ArrayList<DosingBean> getDosingList(SQLiteDatabase db){
		
		ArrayList<DosingBean> dosingList = new ArrayList<DosingBean>();
		DosingBean dosingBean;
		
		String sql = "SELECT *" + " FROM " + TABLE_DOSING;
		Cursor cursor = db.rawQuery(sql, null);
		int bookmark;
		if(cursor!=null && cursor.getCount()>0){
			cursor.moveToFirst();
			do{				
				bookmark = cursor.getInt(3);
				if (bookmark == 0)
					dosingBean = new DosingBean(cursor.getString(0), cursor.getString(1), cursor.getString(2), false);
				else
					dosingBean = new DosingBean(cursor.getString(0), cursor.getString(1), cursor.getString(2), true);

				dosingList.add(dosingBean);
			}while(cursor.moveToNext());
		}
		if(cursor!=null){
			cursor.close();
		}
		return dosingList;
		
	}
	
	/**
	 * added by Anum Tariq Butt
	 * @param db
	 * @return
	 */
	public static ArrayList<DosingBean> getDosingBookmarkedList(SQLiteDatabase db){
		
		ArrayList<DosingBean> dosingList = new ArrayList<DosingBean>();
		DosingBean dosingBean;
		
		String sql = "SELECT *" + " FROM " + TABLE_DOSING;
		Cursor cursor = db.rawQuery(sql, null);
		if(cursor!=null && cursor.getCount()>0){
			cursor.moveToFirst();
			do{
				if (cursor.getInt(3) != 0){
					dosingBean = new DosingBean(cursor.getString(0), cursor.getString(1), cursor.getString(2), true);
					dosingList.add(dosingBean);
				}
			}while(cursor.moveToNext());
		}
		if(cursor!=null){
			cursor.close();
		}
		return dosingList;
		
	}
	
	
	/**
	 * added by Anum Tariq Butt
	 * @param db
	 * @return
	 */
	public static ArrayList<CardiologyBean> getCardiologyList(SQLiteDatabase db){
		
		ArrayList<CardiologyBean> cardiologyList = new ArrayList<CardiologyBean>();
		CardiologyBean cardiologyBean;
		
		String sql = "SELECT *" + " FROM " + TABLE_CARDIOLOGY;
		Cursor cursor = db.rawQuery(sql, null);
		if(cursor!=null && cursor.getCount()>0){
			cursor.moveToFirst();
			do{
				if (cursor.getInt(3) == 0){
					cardiologyBean = new CardiologyBean(cursor.getString(0), cursor.getString(1), cursor.getString(2), false);
				}else{
					cardiologyBean = new CardiologyBean(cursor.getString(0), cursor.getString(1), cursor.getString(2), true);
				}
				cardiologyList.add(cardiologyBean);
			}while(cursor.moveToNext());
		}
		if(cursor!=null){
			cursor.close();
		}
		return cardiologyList;
		
	}

	public static ArrayList<CardiologyBean> getCardiologyBookmarkedList(SQLiteDatabase db){

		ArrayList<CardiologyBean> CardiologyList = new ArrayList<CardiologyBean>();
		CardiologyBean CardiologyBean;

		String sql = "SELECT *" + " FROM " + TABLE_CARDIOLOGY;
		Cursor cursor = db.rawQuery(sql, null);
		if(cursor!=null && cursor.getCount()>0){
			cursor.moveToFirst();
			do{
				if (cursor.getInt(3) != 0){
					CardiologyBean = new CardiologyBean(cursor.getString(0), cursor.getString(1), cursor.getString(2), true);
					CardiologyList.add(CardiologyBean);
				}
			}while(cursor.moveToNext());
		}
		if(cursor!=null){
			cursor.close();
		}
		return CardiologyList;

	}
	/***ADD HERE*** /

	 */
	/**
	 * added by Amy on 02/28
	 * @param db
	 * @param chapterId
	 * @return
	 */
	public static int[] getFlashCardIdByChapter(SQLiteDatabase db,int chapterId)
	{
		String sql = "";
		Cursor cursor = null;
		int[] ids = null;

		if(chapterId == 0){
			sql = "select _id from " + TABLE_FLASHCARD ;
		}else{
			sql = "select _id from " + TABLE_FLASHCARD + " where chapter = "  + chapterId;
		}
		cursor = db.rawQuery(sql, null);
		if(cursor!=null && cursor.getCount()>0){
			log("ids count is " + cursor.getCount());
			ids = new int[cursor.getCount()];
			cursor.moveToFirst();
			int i = 0;
			do{
				ids[i] = cursor.getInt(0) ;
				i++;
			}while(cursor.moveToNext());
		}
		if(cursor!=null){
			cursor.close();
		}
		return ids;
	}

	public static QuizBean getQuiz(SQLiteDatabase db, String chapterId,
			int curQuestionIndex) {
		QuizBean quiz = new QuizBean();
		// String sql =
		// "SELECT id, question, chapter, rightanswer, wronganswer1,"
		// +
		// " wronganswer2, wronganswer3, wronganswer4, wronganswer5, explanation,"
		// + " bookmark, bookinComp, bookTime, rese FROM "
		// + QUIZ_TABLE_NAME + " WHERE quiz.id = " + curQuestionIndex;
		String sql;
		sql = "SELECT id,question,chapter,rightanswer,wronganswer1," +
				"wronganswer2,wronganswer3,wronganswer4,explanation,bookmark,bookinComp,bookTime,rese FROM ";
		if (chapterId.equals("0"))
			sql = sql + TABLE_QUIZ + " WHERE id = " + curQuestionIndex;
		else
			sql = sql +  TABLE_QUIZ + " WHERE (chapter = "
					+ chapterId + " AND id = " + curQuestionIndex + ")";
		log("sql is " + sql);
		Cursor cursor = db.rawQuery(sql, null);
		int bookmark, bookinComp;
		if (null != cursor) {
			cursor.moveToFirst();
			quiz.setId(cursor.getString(0));
			quiz.setQuestion(cursor.getString(1));
			quiz.setChapter(cursor.getInt(2));
			quiz.setRightanswer(cursor.getString(3));
			quiz.setWronganswer1(cursor.getString(4));
			quiz.setWronganswer2(cursor.getString(5));
			quiz.setWronganswer3(cursor.getString(6));
			quiz.setWronganswer4(cursor.getString(7));
			quiz.setExplanation(cursor.getString(8));
			bookmark = cursor.getInt(9);
			bookinComp = cursor.getInt(10);
			if (bookmark == 0)
				quiz.setBookmark(false);
			else
				quiz.setBookmark(true);
			if (bookinComp == 0)
				quiz.setBookinComp(false);
			else
				quiz.setBookinComp(true);
			quiz.setBookTime(cursor.getInt(11));
			quiz.setRese(cursor.getString(12));
			log("Id is " + quiz.getId());
			log("Question is " + quiz.getQuestion());
			log("Chapter is " + quiz.getChapter());
			log("Rightanswer is " + quiz.getRightanswer());
			// log("Wronganswer1 is " + quiz.getWronganswer1());
			// log("Wronganswer2 is " + quiz.getWronganswer2());
			// log("Wronganswer3 is " + quiz.getWronganswer3());
			// log("Wronganswer4 is " + quiz.getWronganswer4());
			// log("Explanation is " + quiz.getExplanation());
			// log("Bookmark is " + quiz.getBookmark());
			// log("BookinComp is " + quiz.getBookinComp());
			// log("BookTime is " + quiz.getBookTime());
			// log("Rese is " + quiz.getRese());
			cursor.close();
		}
		return quiz;
	}

	public static FlashcardBean getFlashcard(SQLiteDatabase db,
			String chapterId, int curQuestionIndex) {
		FlashcardBean flashcard = new FlashcardBean();
		String sql;
		if (chapterId.equals("0"))
			sql = "SELECT * FROM " + TABLE_FLASHCARD + " WHERE _id = "
					+ curQuestionIndex;
		else
			sql = "SELECT * FROM " + TABLE_FLASHCARD + " WHERE (chapter = "
					+ chapterId + " AND _id = " + curQuestionIndex + ")";
		log("sql is " + sql);
		int bookmark, bookinAll;
		Cursor cursor = db.rawQuery(sql, null);
		if (null != cursor) {
			cursor.moveToFirst();
			flashcard.setId(cursor.getInt(0));
			flashcard.setChapter(cursor.getInt(1));
			flashcard.setQuestion(cursor.getString(2));
			flashcard.setAnswer(cursor.getString(3));
			bookmark = cursor.getInt(4);
			bookinAll = cursor.getInt(5);
			if (bookmark == 0)
				flashcard.setBookmark(false);
			else
				flashcard.setBookmark(true);
			if (bookinAll == 0)
				flashcard.setBookinAll(false);
			else
				flashcard.setBookinAll(true);
			flashcard.setBookTime(cursor.getInt(6));
			flashcard.setRese(cursor.getString(7));
			log("Id is " + flashcard.getId());
			log("Question is " + flashcard.getQuestion());
			log("Chapter is " + flashcard.getChapter());
			log("answer is " + flashcard.getAnswer());
			cursor.close();
		}
		return flashcard;
	}

	public static HtmlFileBean getHtmlFile(SQLiteDatabase db, int id,
			String type) {
		HtmlFileBean html = new HtmlFileBean();
		String table;
		if (type.equals(Const.VALUE_TYPE_SKILL_SHEET))
			table = TABLE_SKILL_SHEET;
		else
			table = TABLE_SCENARIOS;
		String sql = "SELECT * FROM " + table + " WHERE id = " + id;
		log("sql is " + sql);
		int book;
		Cursor cursor = db.rawQuery(sql, null);
		if (null != cursor) {
			cursor.moveToFirst();
			html.setId(cursor.getInt(0));
			html.setName(cursor.getString(1));
			html.setFile(cursor.getString(2));
			book = cursor.getInt(3);
			if (book == 0)
				html.setBook(false);
			else
				html.setBook(true);
			// log("Id is " + html.getId());
			// log("file is " + html.getName());
			// log("name is " + html.getFile());
			// log("book is " + html.isBook());
			cursor.close();
		}
		return html;
	}
	
	public static void setQuizScoreDone(SQLiteDatabase db,String chapter){
		Cursor cursor = null;
		String sql = "SELECT * FROM " + TABLE_QUIZ_SCORE + " WHERE chapter = '" + chapter+"' and done=0";
		cursor = db.rawQuery(sql, null);
		if(cursor != null && cursor.moveToNext()){	//exist
			db.execSQL("update quizScore set done=1 where chapter='"+chapter+"' and done=0");
		}
		if(cursor != null){
			cursor.close();
		}
	}
	
	public static void setQuizScore(SQLiteDatabase db, String chapter,
			String result, int current_quiz_index, int continueOrSave) {
		Cursor cursor = null;
		ContentValues values = new ContentValues();
		values.put(COLUMN_CHAPTER, chapter);
		values.put(COLUMN_RESULT, result);
		values.put("current_quiz_index", current_quiz_index);
		values.put("done", continueOrSave);
		//		String whereClause = "chapter = ?";
		//		String[] whereArgs = new String[] { chapter };

		String sql = "SELECT * FROM " + TABLE_QUIZ_SCORE + " WHERE chapter = '" + chapter+"' and done=0";
		if(continueOrSave == 0){

			cursor = db.rawQuery(sql, null);
			if(cursor != null && cursor.moveToNext()){	//exist
				//			db.update(TABLE_QUIZ, values, whereClause, whereArgs);
				db.execSQL("update quizScore set result='"+result+"', done="+continueOrSave+" where chapter='"+chapter+"'");
			}else{
				db.insert(TABLE_QUIZ_SCORE, null, values);
			}
		}else{
			cursor = db.rawQuery(sql, null);
			if(cursor != null && cursor.moveToNext()){	//exist
				db.execSQL("update quizScore set result='"+result+"', done="+continueOrSave+" where chapter='"+chapter+"'");
			}else{
				db.insert(TABLE_QUIZ_SCORE, null, values);
			}
		}
		if(cursor != null){
			cursor.close();
		}

	}

	//	public static void setQuizScoreState(SQLiteDatabase db, String chapter,
	//			String result, int corr, int done) {
	//		ContentValues values = new ContentValues();
	//		values.put(COLUMN_CHAPTER, chapter);
	//		values.put(COLUMN_RESULT, result);
	//		values.put(COLUMN_CORRECT_QUIZ, corr);
	//		values.put(COLUMN_DONE_QUIZ, done);
	//		
	//		String whereClause = "chapter = ?";
	//		String[] whereArgs = new String[] { chapter };
	//		
	//		String sql = "SELECT * FROM " + TABLE_QUIZ_SCORE + " WHERE chapter = " + chapter;
	//		Cursor cursor = db.rawQuery(sql, null);
	//		if(cursor != null && cursor.moveToNext()){	//exist
	//			db.update(TABLE_QUIZ, values, whereClause, whereArgs);
	//		}else{
	//			db.insert(TABLE_QUIZ_SCORE, null, values);
	//		}
	//		if(cursor != null){
	//			cursor.close();
	//		}
	//	}

	public static void setQuizChapterOrder(SQLiteDatabase db, String chapter, int[] original, int[] random) {
		int length = original.length;

		for(int i=0; i<length; i++){
			int id = original[i];
			int sort_chapter = random[i];
			String whereClause = COLUMN_ID + " = ?";
			String[] whereArgs = new String[] { String.valueOf(id) };

			ContentValues values = new ContentValues();
			values.put("sort_chapter", sort_chapter);
			db.update(TABLE_QUIZ, values, whereClause, whereArgs);
		}

	}

	public static Cursor getScoreHistoryCursor(SQLiteDatabase db) {
		String sql = "SELECT " + COLUMN_CHAPTER + ", " + COLUMN_RESULT + ", "
				+ COLUMN_ID + " FROM " + TABLE_QUIZ_SCORE;
		log("sql is " + sql);
		Cursor cursor = db.rawQuery(sql, null);
		return cursor;
	}

	public static int deleteScoreHistory(SQLiteDatabase db, String recordId) {
		return db.delete(TABLE_QUIZ_SCORE, "id = " + recordId, null);
	}

	public static Cursor getQuizChapterCursor(SQLiteDatabase db) {
		String sql = "SELECT " + COLUMN_NAME + " FROM " + TABLE_QUIZ_CHAPTER;
		log("sql is " + sql);
		Cursor cursor = db.rawQuery(sql, null);
		return cursor;
	}

	public static List<ChapterBean> getBookmarkedQuizChapters(SQLiteDatabase db) {
		final List<ChapterBean> chapters = new ArrayList<ChapterBean>();
		ChapterBean chapter = null;
		String sql = "SELECT DISTINCT quizChapter.id, quizChapter.name FROM"
				+ " quiz, quizChapter WHERE quizChapter.id = quiz.chapter AND"
				+ " quiz.bookmark = 1";
		log("sql is " + sql);
		Cursor cursor = db.rawQuery(sql, null);
		if (null != cursor) {
			int id, count;
			String name;
			count = cursor.getCount();
			if (count > 0) {
				cursor.moveToFirst();
				for (int i = 0; i < count; i++) {
					chapter = new ChapterBean();
					id = cursor.getInt(0);
					name = cursor.getString(1);
					log("i is " + i + ", id is " + id + ", name is " + name);
					chapter.setId(id);
					chapter.setName(name);
					chapters.add(chapter);
					cursor.moveToNext();
				}
			}
			cursor.close();
			if (count == 0) {
				// check all
				sql = "SELECT count(*) FROM quiz WHERE bookinComp = 1";
				log("sql is " + sql);
				cursor = db.rawQuery(sql, null);
				if (null != cursor) {
					cursor.moveToFirst();
					if (cursor.getInt(0) > 0) {
						chapter = new ChapterBean();
						chapter.setId(0);
						chapter.setName("Comprehensive Quiz");
						chapters.add(chapter);
					}
					cursor.close();
				}
			} else {
				chapter = new ChapterBean();
				chapter.setId(0);
				chapter.setName("Comprehensive Quiz");
				chapters.add(0, chapter);
			}
		}
		return chapters;
	}

	public static List<ChapterBean> getBookmarkedQuizChaptersNew(SQLiteDatabase db) {
		final List<ChapterBean> chapters = new ArrayList<ChapterBean>();
		ChapterBean chapter = null;
		String sql = "SELECT DISTINCT quizChapter.id, quizChapter.name,quiz.id,quiz.question FROM"
				+ " quiz, quizChapter WHERE quizChapter.id = quiz.chapter AND"
				+ " quiz.bookmark = 1 group by quiz.chapter";
		log("sql is " + sql);
		Cursor cursor = db.rawQuery(sql, null);
		if (null != cursor) {
			int id, count, quizId;
			String name,quizName;
			count = cursor.getCount();
			if (count > 0) {
				cursor.moveToFirst();
				for (int i = 0; i < count; i++) {
					chapter = new ChapterBean();
					id = cursor.getInt(0);
					name = cursor.getString(1);
					quizId = cursor.getInt(2);
					quizName = cursor.getString(3);
					log("i is " + i + ", id is " + id + ", name is " + name);
					chapter.setId(id);
					chapter.setName(name);
					chapter.setQuizId(quizId);
					chapter.setQuizName(quizName);
					chapters.add(chapter);
					cursor.moveToNext();
				}
			}
			cursor.close();
			if (count == 0) {
				// check all
				sql = "SELECT count(*) FROM quiz WHERE bookinComp = 1";
				log("sql is " + sql);
				cursor = db.rawQuery(sql, null);
				if (null != cursor) {
					cursor.moveToFirst();
					if (cursor.getInt(0) > 0) {
						chapter = new ChapterBean();
						chapter.setId(0);
						chapter.setName("Comprehensive Quiz");
						chapters.add(chapter);
					}
					cursor.close();
				}
			} else {
				chapter = new ChapterBean();
				chapter.setId(0);
				chapter.setName("Comprehensive Quiz");
				chapters.add(0, chapter);
			}
		}
		return chapters;
	}

	public static Cursor getFlashcardChapterCursor(SQLiteDatabase db) {
		String sql = "SELECT " + COLUMN_NAME + " FROM "
				+ TABLE_FLASHCARD_CHAPTER;
		log("sql is " + sql);
		Cursor cursor = db.rawQuery(sql, null);
		return cursor;
	}

	public static Cursor getHtmlFileCursor(SQLiteDatabase db, String type) {
		String table;
		if (type.equals(Const.VALUE_TYPE_SKILL_SHEET))
			table = TABLE_SKILL_SHEET;
		else
			table = TABLE_SCENARIOS;
		String sql = "SELECT name FROM " + table;
		log("sql is " + sql);
		Cursor cursor = db.rawQuery(sql, null);
		return cursor;
	}

	public static List<ChapterBean> getBookmarkedFlashChapters(SQLiteDatabase db) {
		final List<ChapterBean> chapters = new ArrayList<ChapterBean>();
		ChapterBean chapter = null;
		String sql = "SELECT DISTINCT chapterFcard.id, chapterFcard.name FROM"
				+ " flashcard, chapterFcard WHERE chapterFcard.id = "
				+ "flashcard.chapter AND flashcard.bookmark = 1";
		log("sql is " + sql);
		Cursor cursor = db.rawQuery(sql, null);
		if (null != cursor) {
			int id, count;
			String name;
			count = cursor.getCount();
			if (count > 0) {
				cursor.moveToFirst();
				for (int i = 0; i < count; i++) {
					chapter = new ChapterBean();
					id = cursor.getInt(0);
					name = cursor.getString(1);
					log("i is " + i + ", id is " + id + ", name is " + name);
					chapter.setId(id);
					chapter.setName(name);
					chapters.add(chapter);
					cursor.moveToNext();
				}
			}
			cursor.close();
			if (count == 0) {
				// check all
				sql = "SELECT count(*) FROM flashcard WHERE bookinAll = 1";
				log("sql is " + sql);
				cursor = db.rawQuery(sql, null);
				if (null != cursor) {
					cursor.moveToFirst();
					if (cursor.getInt(0) > 0) {
						chapter = new ChapterBean();
						chapter.setId(0);
						chapter.setName("All Chapters");
						chapters.add(chapter);
					}
					cursor.close();
				}
			} else {
				chapter = new ChapterBean();
				chapter.setId(0);
				chapter.setName("All Chapters");
				chapters.add(0, chapter);
			}
		}
		return chapters;
	}

	public static List<HtmlFileBean> getBookmarkedHtmls(SQLiteDatabase db,
			String type) {
		final List<HtmlFileBean> htmls = new ArrayList<HtmlFileBean>();
		HtmlFileBean html = null;
		String table;
		if (type.equals(Const.VALUE_TYPE_SKILL_SHEET))
			table = TABLE_SKILL_SHEET;
		else
			table = TABLE_SCENARIOS;
		String sql = "SELECT * FROM " + table + " WHERE book = 1";
		log("sql is " + sql);
		int book;
		Cursor cursor = db.rawQuery(sql, null);
		if (null != cursor) {
			int count = cursor.getCount();
			if (count > 0) {
				cursor.moveToFirst();
				for (int i = 0; i < count; i++) {
					html = new HtmlFileBean();
					html.setId(cursor.getInt(0));
					html.setName(cursor.getString(1));
					html.setFile(cursor.getString(2));
					book = cursor.getInt(3);
					if (book == 0)
						html.setBook(false);
					else
						html.setBook(true);
					// log("Id is " + html.getId());
					// log("file is " + html.getName());
					// log("name is " + html.getFile());
					// log("book is " + html.isBook());
					htmls.add(html);
					cursor.moveToNext();
				}
			}
			cursor.close();
		}
		return htmls;
	}

	public static void setQuizBookmark(SQLiteDatabase db, String chapterId,
			int id, boolean bookmark) {
		log("setQuizBookmark, id is " + id + ", bookmark is " + bookmark);
		ContentValues values = new ContentValues();
		String whereClause = COLUMN_ID + " = ?";
		String[] whereArgs = new String[] { String.valueOf(id) };
		if (chapterId.equals("0")) {
			if (bookmark) {
				// Just bookmark it for Comp
				values.put(COLUMN_BOOKINCOMP, bookmark);
			} else {
				values.put(COLUMN_BOOKMARK, bookmark);
				values.put(COLUMN_BOOKINCOMP, bookmark);
			}
		} else {
			values.put(COLUMN_BOOKMARK, bookmark);
			values.put(COLUMN_BOOKINCOMP, bookmark);
		}
		int result = db.update(TABLE_QUIZ, values, whereClause, whereArgs);
		log("result is " + result);
	}

	public static void setFlashcardBookmark(SQLiteDatabase db,
			String chapterId, int id, boolean bookmark) {
		log("setFlashcardBookmark, id is " + id);
		ContentValues values = new ContentValues();
		String whereClause = "_id = ?";
		String[] whereArgs = new String[] { String.valueOf(id) };
		if (chapterId.equals("0")) {
			if (bookmark) {
				// Just bookmark it for All
				values.put(COLUMN_BOOKINALL, bookmark);
			} else {
				values.put(COLUMN_BOOKMARK, bookmark);
				values.put(COLUMN_BOOKINALL, bookmark);
			}
		} else {
			values.put(COLUMN_BOOKMARK, bookmark);
			values.put(COLUMN_BOOKINALL, bookmark);
		}
		db.update(TABLE_FLASHCARD, values, whereClause, whereArgs);
	}
	
	public static void setDosingBookmark(SQLiteDatabase db, int id, boolean bookmark) {
		ContentValues values = new ContentValues();
		String whereClause = "id = ?";
		String[] whereArgs = new String[] { String.valueOf(id) };
		values.put(COLUMN_BOOKMARK, bookmark);
		db.update(TABLE_DOSING, values, whereClause, whereArgs);
	}
	public static void setCardiologyBookmark(SQLiteDatabase db, int id, boolean bookmark) {
		ContentValues values = new ContentValues();
		String whereClause = "id = ?";
		String[] whereArgs = new String[] { String.valueOf(id) };
		values.put(COLUMN_BOOKMARK, bookmark);
		db.update(TABLE_CARDIOLOGY, values, whereClause, whereArgs);
	}

	public static void setHtmlFileBookmark(SQLiteDatabase db, String type,
			int id, boolean bookmark) {
		ContentValues values = new ContentValues();
		String whereClause = COLUMN_ID + " = ?";
		String[] whereArgs = new String[] { String.valueOf(id) };
		values.put(COLUMN_BOOK, bookmark);
		if (type.equals(Const.VALUE_TYPE_SKILL_SHEET))
			db.update(TABLE_SKILL_SHEET, values, whereClause, whereArgs);
		else
			db.update(TABLE_SCENARIOS, values, whereClause, whereArgs);
	}

	public static List<BookmarkBean> getBookmarks(SQLiteDatabase db,
			String type, String chapterId) {
		final List<BookmarkBean> bookmarks = new ArrayList<BookmarkBean>();
		BookmarkBean bookmark = null;
		String sql = null;
		if (type.equals(Const.VALUE_TYPE_QUIZ)) {
			if (chapterId.equals("0"))
				sql = "SELECT id, question FROM quiz WHERE bookinComp = 1";
			else
				sql = "SELECT id, question FROM quiz WHERE chapter = "
						+ chapterId + " AND bookmark = 1";
		} else if (type.equals(Const.VALUE_TYPE_FLASHCARD)) {
			if (chapterId.equals("0"))
				sql = "SELECT _id, question FROM flashcard WHERE bookinAll = 1";
			else
				sql = "SELECT _id, question FROM flashcard WHERE chapter = "
						+ chapterId + " AND bookmark = 1";
		}
		log("sql is " + sql);
		Cursor cursor = db.rawQuery(sql, null);
		if (null != cursor) {
			int id, count;
			String name;

			count = cursor.getCount();
			cursor.moveToFirst();

			for (int i = 0; i < count; i++) {
				bookmark = new BookmarkBean();
				id = cursor.getInt(0);
				name = cursor.getString(1);
				log("i is " + i + ", id is " + id + ", name is " + name);
				bookmark.setId(id);
				bookmark.setName(name);
				bookmarks.add(bookmark);
				cursor.moveToNext();
			}
			cursor.close();
		}
		return bookmarks;
	}

	//	public static List<BookmarkBean> getBookmarksByChapterName(SQLiteDatabase db,
	//			String type, String chapterId) {
	//		final List<BookmarkBean> bookmarks = new ArrayList<BookmarkBean>();
	//		BookmarkBean bookmark = null;
	//		String sql = null;
	//		if (type.equals(Const.VALUE_TYPE_QUIZ)) {
	//			if (chapterId.equals("0"))
	//				sql = "SELECT id, question FROM quiz WHERE bookinComp = 1";
	//			else
	//				sql = "SELECT quiz.id, quiz.question quizChapter.name FROM quiz,quizChapter WHERE quiz.chapter = "
	//						+ chapterId + " AND quiz.bookmark = 1 and quizChapter.id=quiz.chapter group by quiz.chapter";
	//		} else if (type.equals(Const.VALUE_TYPE_FLASHCARD)) {
	//			if (chapterId.equals("0"))
	//				sql = "SELECT _id, question FROM flashcard WHERE bookinAll = 1";
	//			else
	//				sql = "SELECT _id, question FROM flashcard WHERE chapter = "
	//						+ chapterId + " AND bookmark = 1 group by chapter";
	//		}
	//		log("sql is " + sql);
	//		Cursor cursor = db.rawQuery(sql, null);
	//		if (null != cursor) {
	//			int id, count;
	//			String name,chapter;
	//
	//			count = cursor.getCount();
	//			cursor.moveToFirst();
	//
	//			for (int i = 0; i < count; i++) {
	//				bookmark = new BookmarkBean();
	//				id = cursor.getInt(0);
	//				name = cursor.getString(1);
	//				chapter =cursor.getString(2);
	//				log("i is " + i + ", id is " + id + ", name is " + name);
	//				bookmark.setId(id);
	//				bookmark.setName(name);
	//				bookmark.setChapter(chapter);
	//				bookmarks.add(bookmark);
	//				cursor.moveToNext();
	//			}
	//			cursor.close();
	//		}
	//		return bookmarks;
	//	}

	/**
	 * 
	 * @param db
	 * @return
	 */
	public static ArrayList<SkillSheetBean> getScenriosBeans(SQLiteDatabase db) {
		String sql = "SELECT id,name,file from " + TABLE_SCENARIOS;
		Cursor cursor = db.rawQuery(sql, null);
		ArrayList<SkillSheetBean> skillsheetBeans = new ArrayList<SkillSheetBean>();
		SkillSheetBean skill_temp = null;
		if (cursor != null) {
			// cursor.moveToFirst();
			while (cursor.moveToNext()) {
				skill_temp = new SkillSheetBean();
				skill_temp.set_id(cursor.getInt(0));
				skill_temp.setSkillName(cursor.getString(1));
				skill_temp.setFileName(cursor.getString(2));
				skillsheetBeans.add(skill_temp);
			}
			cursor.close();
		}
		return skillsheetBeans;
	}

	/**
	 * 
	 * @param db
	 * @return
	 */
	public static ArrayList<SkillSheetBean> getSkillSheetBean(SQLiteDatabase db) {
		String sql = "SELECT id,name,s_order,file from " + TABLE_SKILL_SHEET
				+ " order by s_order asc";
		Cursor cursor = db.rawQuery(sql, null);
		ArrayList<SkillSheetBean> skillsheetBeans = new ArrayList<SkillSheetBean>();
		SkillSheetBean skill_temp = null;
		if (cursor != null) {
			// cursor.moveToFirst();
			while (cursor.moveToNext()) {
				skill_temp = new SkillSheetBean();
				skill_temp.set_id(cursor.getInt(0));
				skill_temp.setSkillName(cursor.getString(1));
				skill_temp.setS_order(cursor.getInt(2));
				skill_temp.setFileName(cursor.getString(3));
				skillsheetBeans.add(skill_temp);
			}
			cursor.close();
		}
		return skillsheetBeans;
	}

	//v1.4: from FireFighter
	public static Cursor getSubCheckListsCursor(SQLiteDatabase db, String catId) {
		String sql = "SELECT _id," + COLUMN_NAME + " FROM "
				+ TABLE_CHAPTER_CHECKLIST + " where parent_ID = " + catId;
		log("sql is " + sql);
		Cursor cursor = db.rawQuery(sql, null);
		return cursor;
	}

	public static void setCheckListCheckedByParentId(SQLiteDatabase db,
			String parentId, boolean checked) {
		log("setCheckListCheckedByParentId, parentId is " + parentId
				+ ", checked is " + checked);
		ContentValues values = new ContentValues();
		values.put(COLUMN_CHECKED, checked);
		int result;
		if (parentId.equals("0")) {
			// for all chapters
			result = db.update(TABLE_CHAPTER_CHECKLISTDETAIL, values, null,
					null);
		} else {
			String whereClause = COLUMN_CHAPTER_ID
					+ " in (SELECT _id FROM chaptersChecklist WHERE parent_ID is ?)";
			String[] whereArgs = new String[] { String.valueOf(parentId) };
			result = db.update(TABLE_CHAPTER_CHECKLISTDETAIL, values,
					whereClause, whereArgs);
			log("whereClause is " + whereClause);
		}
		log("result is " + result);
	}


	public static int setCheckListCheckedById(SQLiteDatabase db, int id,
			boolean checked) {
		log("setCheckListCheckedById, id is " + id + ", checked is " + checked);
		ContentValues values = new ContentValues();
		String whereClause = "_id = ?";
		String[] whereArgs = new String[] { String.valueOf(id) };
		values.put(COLUMN_CHECKED, checked);
		int result = db.update(TABLE_CHAPTER_CHECKLISTDETAIL, values,
				whereClause, whereArgs);
		log("whereClause is " + whereClause);
		log("result is " + result);
		return result;
	}

	public static void setCheckListCheckedByChapterId(SQLiteDatabase db,
			String chapterId, boolean checked) {
		log("setCheckListCheckedByChapterId, chapterId is " + chapterId
				+ ", checked is " + checked);
		ContentValues values = new ContentValues();
		String whereClause = COLUMN_CHAPTER_ID + " = ?";
		String[] whereArgs = new String[] { String.valueOf(chapterId) };
		values.put(COLUMN_CHECKED, checked);
		int result = db.update(TABLE_CHAPTER_CHECKLISTDETAIL, values,
				whereClause, whereArgs);
		log("whereClause is " + whereClause);
		log("result is " + result);
	}


	public static List<CheckListDetailBean> getCheckListDetailBeans(
			SQLiteDatabase db, String chapterId) {
		List<CheckListDetailBean> checkListDetailBeans = null;
		String sql = "SELECT _id,precheck," + COLUMN_NAME
				+ ",checked,information1,information2,information3  FROM "
				+ TABLE_CHAPTER_CHECKLISTDETAIL + " where chapter_id = "
				+ chapterId;
		log("sql is " + sql);
		Cursor cursor = db.rawQuery(sql, null);
		if (cursor != null) {
			if (cursor.moveToFirst()) {
				CheckListDetailBean temp = null;
				checkListDetailBeans = new ArrayList<CheckListDetailBean>();
				do {
					temp = new CheckListDetailBean();
					temp.set_id(cursor.getInt(0));
					temp.setPrecheck(Util.trimString(cursor.getString(1)));
					temp.setName(Util.trimString(cursor.getString(2)));
					temp.setChecked(cursor.getInt(3));
					temp.setInformation1(Util.trimString(cursor.getString(4)));
					temp.setInformation2(Util.trimString(cursor.getString(5)));
					temp.setInformation3(Util.trimString(cursor.getString(6)));
					checkListDetailBeans.add(temp);
				} while (cursor.moveToNext());
			}
			cursor.close();
		}
		return checkListDetailBeans;
	}
	public static void saveChaperHistoryRecorder(SQLiteDatabase db, QuizRecorderBean rb) {
		Cursor cursor = null;
		int chapId;
		try{
			if(rb!=null){
				chapId = rb.getChapterId();
				ContentValues values = new ContentValues();
				values.put("chapter_id", chapId);
				values.put("chapter_sequence", rb.getChapterSequence());
				values.put("right_amount", rb.getRightAmount());
				values.put("done_amount", rb.getDoneAmount());
				values.put("current_index", rb.getCurrentIndex());
				values.put("total_amount", rb.getTotalAmount());
				values.put("first_touch", rb.getFirstTouch());
				values.put("finished", rb.getFinished());
				int idNum = -1;
				cursor = db.rawQuery("select id from quizHistoryrecoder where chapter_id="+chapId, null);
				if(cursor!=null && cursor.moveToNext()){
					idNum = cursor.getInt(0);
				}
				if(idNum!=-1){
					String whereClause = "chapter_id = ?";
					String[] whereArgs = new String[] { String.valueOf(chapId) };
					db.update("quizHistoryrecoder", values, whereClause, whereArgs);
				}else{
					db.insert("quizHistoryrecoder", null, values);
				}
			}
		}finally{
			if(cursor != null)
				cursor.close();
		}
	}
	public static void saveChaperHistoryRecorder(SQLiteDatabase db,
			String mChapterId, String sequence, int right, int done, int current, int total, int ftouch, int finished) {
		Cursor cursor = null;
		try{
			ContentValues values = new ContentValues();
			values.put("chapter_id", mChapterId);
			values.put("chapter_sequence", sequence);
			values.put("right_amount", right);
			values.put("done_amount", done);
			values.put("current_index", current);
			values.put("total_amount", total);
			values.put("first_touch", ftouch);
			values.put("finished", finished);
			int idNum = -1;
			cursor = db.rawQuery("select id from quizHistoryrecoder where chapter_id="+mChapterId, null);
			if(cursor!=null && cursor.moveToNext()){
				idNum = cursor.getInt(0);
			}
			if(idNum!=-1){
				String whereClause = "chapter_id = ?";
				String[] whereArgs = new String[] { String.valueOf(mChapterId) };
				db.update("quizHistoryrecoder", values, whereClause, whereArgs);
			}else{
				db.insert("quizHistoryrecoder", null, values);
			}
		}finally{
			if(cursor != null)
				cursor.close();
		}
	}

	public static QuizRecorderBean getChaperHistoryRecorder(SQLiteDatabase db, int mChapterId) {
		Cursor cursor = null;
		QuizRecorderBean recordbean = null;
		try{
			int idNum = -1;
			cursor = db.rawQuery("select chapter_id,chapter_name,chapter_sequence,right_amount,done_amount,total_amount,first_touch,finished,current_index from quizHistoryrecoder where chapter_id="+mChapterId, null);
			if(cursor!=null && cursor.moveToNext()){
				recordbean = new QuizRecorderBean();
				recordbean.setChapterId(cursor.getInt(0));
				recordbean.setChapterName(cursor.getString(1));
				recordbean.setChapterSequence(cursor.getString(2));
				recordbean.setRightAmount(cursor.getInt(3));
				recordbean.setDoneAmount(cursor.getInt(4));
				recordbean.setTotalAmount(cursor.getInt(5));
				recordbean.setFirstTouch(cursor.getInt(6));
				recordbean.setFinished(cursor.getInt(7));
				recordbean.setCurrentIndex(cursor.getInt(8));
				return recordbean;
			}
		}finally{
			if(cursor != null)
				cursor.close();
		}
		return recordbean;
	}

	public static void deletRecorder(SQLiteDatabase db, String mChapterId) {
		db.delete("quizHistoryrecoder", "chapter_id = " + String.valueOf(mChapterId), null);
	}

	public static void saveFlashChaperHistoryRecorder(SQLiteDatabase db,FlashcardRecorderBean fr) {
		Cursor cursor = null;
		try{
			if(fr!=null){
				int chaId = fr.getChapterId();
				ContentValues values = new ContentValues();
				values.put("chapter_id", chaId);
				values.put("chapter_sequence", fr.getChapterSequence());
				values.put("current_index", fr.getCurrentIndex());
				values.put("total_amount", fr.getTotalAmount());
				values.put("finished", fr.getFinished());
				int idNum = -1;
				cursor = db.rawQuery("select id from flashcardHistoryrecorder where chapter_id="+chaId, null);
				if(cursor!=null && cursor.moveToNext()){
					idNum = cursor.getInt(0);
				}
				if(idNum!=-1){
					String whereClause = "chapter_id = ?";
					String[] whereArgs = new String[] { String.valueOf(chaId) };
					db.update("flashcardHistoryrecorder", values, whereClause, whereArgs);
				}else{
					db.insert("flashcardHistoryrecorder", null, values);
				}
			}
		}finally{
			if(cursor != null)
				cursor.close();
		}
	}

	public static FlashcardRecorderBean getFlashChaperHistoryRecorder(SQLiteDatabase db, int mChapterId) {
		Cursor cursor = null;
		FlashcardRecorderBean fr = null;
		try{
			int idNum = -1;
			cursor = db.rawQuery("select chapter_id,chapter_sequence,current_index,total_amount,finished from flashcardHistoryrecorder where chapter_id="+mChapterId, null);
			if(cursor!=null && cursor.moveToNext()){
				fr = new FlashcardRecorderBean();
				fr.setChapterId(cursor.getInt(0));
				fr.setChapterSequence(cursor.getString(1));
				fr.setCurrentIndex(cursor.getInt(2));
				fr.setTotalAmount(cursor.getInt(3));
				fr.setFinished(cursor.getInt(4));
				return fr;
			}
		}finally{
			if(cursor != null)
				cursor.close();
		}
		return fr;
	}

	public static void deletFlashRecorder(SQLiteDatabase db, String mChapterId) {
		db.delete("flashcardHistoryrecorder", "chapter_id = " + String.valueOf(mChapterId), null);
	}

	public static Cursor getReferenceCursor(SQLiteDatabase db,
			CharSequence constraint) {
		String sql = null;
		if(constraint != null){
			sql = "SELECT "+"_id, " + COLUMN_QUESTION + " FROM "
					+ TABLE_FLASHCARD + " WHERE " + COLUMN_QUESTION + " LIKE '%"
					+ constraint + "%'" + " ORDER BY " + COLUMN_QUESTION;
		}else{
			sql = "SELECT "+"_id, " + COLUMN_QUESTION + " FROM "
					+ TABLE_FLASHCARD + " ORDER BY " + COLUMN_QUESTION;
		}

		log("sql is " + sql);
		Cursor cursor = db.rawQuery(sql, null);
		log("getCount is " + cursor.getCount());
		return cursor;
	}

	public static FlashcardBean getFlashcardDetailById(SQLiteDatabase db,
			String _id) {
		log("getFlashcardCursorById");
		String sql;
		FlashcardBean flashcard = null;
		sql = "SELECT " + COLUMN_QUESTION + ", " + COLUMN_ANSWER + " FROM "
				+ TABLE_FLASHCARD + " WHERE _id = " + _id;
		log("sql is " + sql);
		Cursor cursor = db.rawQuery(sql, null);
		if (cursor != null) {
			if (cursor.moveToFirst()) {
				flashcard = new FlashcardBean();
				flashcard.setQuestion(cursor.getString(0));
				flashcard.setAnswer(cursor.getString(1));
			}
			cursor.close();
		}
		return flashcard;
	}

	public static Cursor getReferenceCursor(SQLiteDatabase db) {
		String sql = "SELECT "+"_id," + COLUMN_QUESTION + " FROM "
				+ TABLE_FLASHCARD + " ORDER BY " + COLUMN_QUESTION;
		log("sql is " + sql);
		Cursor cursor = db.rawQuery(sql, null);
		log("getCount is " + cursor.getCount());
		return cursor;
	}

	private static void log(String msg) {
		if (Const.DEBUG_PHASE && DEBUG_ENABLED) {
			Log.d(TAG, Const.TAG_PREFIX + msg);
		}
	}


}