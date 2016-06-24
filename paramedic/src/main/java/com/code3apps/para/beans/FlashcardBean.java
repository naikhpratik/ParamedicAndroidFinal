package com.code3apps.para.beans;

public class FlashcardBean {
    private int id = 0;
    private int chapter = -1;
    private String question = "";
    private String answer = "";
    private boolean bookmark = false;
    private boolean bookinAll = false;
    private int bookTime = 0;
    private String rese = "";

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public int getChapter() {
        return chapter;
    }

    public void setChapter(int chapter) {
        this.chapter = chapter;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public boolean isBookmark() {
        return bookmark;
    }

    public void setBookmark(boolean bookmark) {
        this.bookmark = bookmark;
    }

    public boolean isBookinAll() {
        return bookinAll;
    }

    public void setBookinAll(boolean bookinAll) {
        this.bookinAll = bookinAll;
    }

    public int getBookTime() {
        return bookTime;
    }

    public void setBookTime(int bookTime) {
        this.bookTime = bookTime;
    }

    public String getRese() {
        return rese;
    }

    public void setRese(String rese) {
        this.rese = rese;
    }

}