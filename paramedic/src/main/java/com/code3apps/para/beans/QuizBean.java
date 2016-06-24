package com.code3apps.para.beans;

public class QuizBean {
    private String id = "";
    private String question = "";
    private int chapter = -1;
    private String rightanswer = "";
    private String wronganswer1 = "";
    private String wronganswer2 = "";
    private String wronganswer3 = "";
    private String wronganswer4 = "";
    private String explanation = "";
    private boolean bookmark = false;
    private boolean bookinComp = false;
    private int bookTime = 0;
    private String rese = "";

    public String getId() {
        return id;
    }

    public void setId(String id) {
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

    public String getRightanswer() {
        return rightanswer;
    }

    public void setRightanswer(String rightanswer) {
        this.rightanswer = rightanswer;
    }

    public String getWronganswer1() {
        return wronganswer1;
    }

    public void setWronganswer1(String wronganswer1) {
        this.wronganswer1 = wronganswer1;
    }

    public String getWronganswer2() {
        return wronganswer2;
    }

    public void setWronganswer2(String wronganswer2) {
        this.wronganswer2 = wronganswer2;
    }

    public String getWronganswer3() {
        return wronganswer3;
    }

    public void setWronganswer3(String wronganswer3) {
        this.wronganswer3 = wronganswer3;
    }

    public String getWronganswer4() {
        return wronganswer4;
    }

    public void setWronganswer4(String wronganswer4) {
        this.wronganswer4 = wronganswer4;
    }

    public String getExplanation() {
        return explanation;
    }

    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }

    public boolean isBookmark() {
        return bookmark;
    }

    public void setBookmark(boolean bookmark) {
        this.bookmark = bookmark;
    }

    public boolean isBookinComp() {
        return bookinComp;
    }

    public void setBookinComp(boolean bookinComp) {
        this.bookinComp = bookinComp;
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