package net.roocky.mojian.Model;

/**
 * Created by roocky on 04/11.
 * Diary & Note 的基类
 */
public class Base {
    private int id;
    private int year;
    private int month;
    private int day;
    private String content;

    public Base() {

    }

    public Base(int id, int year, int month, int day, String content) {
        this.id = id;
        this.year = year;
        this.month = month;
        this.day = day;
        this.content = content;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
