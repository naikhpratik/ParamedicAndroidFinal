package com.code3apps.para.beans;

import java.io.Serializable;

public class QuizRecorderBean implements Serializable{
	private int id;
	private int chapterId;
	private String chapterName;
	private String chapterSequence;
	private int rightAmount;
	private int doneAmount;
	private int currentIndex;
	private int totalAmount;
	private int firstTouch;
	private int finished;
	public QuizRecorderBean(){}

	public QuizRecorderBean(int id, int chapterId, String chapterName,
			String chapterSequence, int rightAmount, int doneAmount,
			int firstTouch, int finished) {
		super();
		this.id = id;
		this.chapterId = chapterId;
		this.chapterName = chapterName;
		this.chapterSequence = chapterSequence;
		this.rightAmount = rightAmount;
		this.doneAmount = doneAmount;
		this.firstTouch = firstTouch;
		this.finished = finished;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getChapterId() {
		return chapterId;
	}

	public void setChapterId(int chapterId) {
		this.chapterId = chapterId;
	}

	public String getChapterName() {
		return chapterName;
	}

	public void setChapterName(String chapterName) {
		this.chapterName = chapterName;
	}

	public String getChapterSequence() {
		return chapterSequence;
	}

	public void setChapterSequence(String chapterSequence) {
		this.chapterSequence = chapterSequence;
	}

	public int getRightAmount() {
		return rightAmount;
	}

	public void setRightAmount(int rightAmount) {
		this.rightAmount = rightAmount;
	}

	public int getDoneAmount() {
		return doneAmount;
	}

	public void setDoneAmount(int doneAmount) {
		this.doneAmount = doneAmount;
	}

	public int getCurrentIndex() {
		return currentIndex;
	}

	public void setCurrentIndex(int currentIndex) {
		this.currentIndex = currentIndex;
	}

	public int getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(int totalAmount) {
		this.totalAmount = totalAmount;
	}

	public int getFirstTouch() {
		return firstTouch;
	}

	public void setFirstTouch(int firstTouch) {
		this.firstTouch = firstTouch;
	}

	public int getFinished() {
		return finished;
	}

	public void setFinished(int finished) {
		this.finished = finished;
	}

}
