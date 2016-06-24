package com.code3apps.para.beans;

public class HtmlFileBean {
    private int id = 0;
    private String name = "";
    private String file = "";
    private boolean book = false;
    private int bookTime = 0;
    private String rese = "";

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public boolean isBook() {
        return book;
    }

    public void setBook(boolean book) {
        this.book = book;
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