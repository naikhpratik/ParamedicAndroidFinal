package com.code3apps.para.beans;

public class CardiologyBean {
	
	private String question;
	private String hint;
	private String explanation;
	private boolean bookmark = false;
	
	public CardiologyBean(String question, String hint, String explanation, boolean bookmark) {
		super();
		this.question = question;
		this.hint = hint;
		this.explanation = explanation;
		this.bookmark = bookmark;
	}
	
	public String getQuestion() {
		return question;
	}
	public void setQuestion(String question) {
		this.question = question;
	}
	public String getHint() {
		return hint;
	}
	public void setHint(String hint) {
		this.hint = hint;
	}
	public String getExplanation() {
		return explanation;
	}
	public void setExplanation(String explanation) {
		this.explanation = explanation;
	}
	public void setBookmark(boolean bookmark) {
		this.bookmark = bookmark;
	}
	public boolean isBookmark() {
		return bookmark;
	}
	
	

}
