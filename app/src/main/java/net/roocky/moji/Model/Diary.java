package net.roocky.moji.Model;

/**
 * Created by roocky on 03/29.
 * Note的基类
 */
public class Diary extends Base {
    private int weather;

    public Diary() {

    }

    public Diary(int id, int year, int month, int day, int weather, String content) {
        super(id, year, month, day, content);
        this.weather = weather;
    }

    public int getWeather() {
        return weather;
    }

    public void setWeather(int weather) {
        this.weather = weather;
    }
}
