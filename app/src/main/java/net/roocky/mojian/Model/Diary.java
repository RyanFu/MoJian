package net.roocky.mojian.Model;

/**
 * Created by roocky on 03/29.
 * Note的基类
 */
public class Diary extends Base {
    private int weather;

    public Diary() {

    }

    public Diary(int id, int year, int month, int day, String content, int background, int paper, int weather) {
        super(id, year, month, day, content, background, paper);
        this.weather = weather;
    }

    public int getWeather() {
        return weather;
    }

    public void setWeather(int weather) {
        this.weather = weather;
    }
}
