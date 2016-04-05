package net.roocky.moji.Model;

/**
 * Created by roocky on 03/29.
 * Note的基类
 */
public class Diary {
    private int id;
    private String date;
    private String content;

    public Diary() {

    }

    public Diary(int id, String date, String content) {
        this.id = id;
        this.date = date;
        this.content = content;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
