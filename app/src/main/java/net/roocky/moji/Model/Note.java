package net.roocky.moji.Model;

/**
 * Created by roocky on 03/29.
 * 便箋
 */
public class Note extends Base {
    private String remind;
    public Note() {

    }

    public Note(int id, String date, String content, String remind) {
        super(id, date, content);
        this.remind = remind;
    }

    public String getRemind() {
        return remind;
    }

    public void setRemind(String remind) {
        this.remind = remind;
    }
}
