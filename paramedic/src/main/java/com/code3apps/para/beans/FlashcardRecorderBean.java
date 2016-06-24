package com.code3apps.para.beans;

import java.io.Serializable;

public class FlashcardRecorderBean implements Serializable {

	private int id;
	private int chapterId;
	private String chapterSequence;
	private int currentIndex;
	private int totalAmount;
	private int finished;
	public FlashcardRecorderBean(){}
	public FlashcardRecorderBean(int id, int chapterId, String chapterSequence,
			int currentIndex, int totalAmount, int finished) {
		super();
		this.id = id;
		this.chapterId = chapterId;
		this.chapterSequence = chapterSequence;
		this.currentIndex = currentIndex;
		this.totalAmount = totalAmount;
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
	public String getChapterSequence() {
		return chapterSequence;
	}
	public void setChapterSequence(String chapterSequence) {
		this.chapterSequence = chapterSequence;
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
	public int getFinished() {
		return finished;
	}
	public void setFinished(int finished) {
		this.finished = finished;
	}
	
}
