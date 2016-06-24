package com.code3apps.para.beans;

public class DosingBean {
	
	private String question;
	private String answer;
	private String equation;
	private boolean bookmark;
	
	
	public DosingBean(String question, String answer, String equation, boolean bookmark) {
		super();
		this.question = question;
		this.answer = answer;
		this.equation = equation;
		this.bookmark = bookmark;
	}
	
	public String getQuestion() {
		return question;
	}
	public void setQuestion(String question) {
		this.question = question;
	}
	public String getAnswer() {
		return answer;
	}
	public void setAnswer(String answer) {
		this.answer = answer;
	}
	public String getEquation() {
		return equation;
	}
	public void setEquation(String equation) {
		this.equation = equation;
	}
	public boolean isBookmark() {
		return bookmark;
	}
	public void setBookmark(boolean bookmark) {
		this.bookmark = bookmark;
	}
	
	

}
