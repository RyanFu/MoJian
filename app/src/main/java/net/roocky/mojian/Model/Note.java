package net.roocky.mojian.Model;

/**
 * Created by roocky on 03/29.
 * 便箋
 */
public class Note extends Base {
    private String remind;

    public Note() {

    }

    public Note(int id, int year, int month, int day, String content, int background, String remind) {
        super(id, year, month, day, content, background);
        this.remind = remind;
    }

    public String getRemind() {
        return remind;
    }

    public void setRemind(String remind) {
        this.remind = remind;
    }
}
