package example.dkamphake.moveapp;

public class SaveState {
    private int score;
    private String date;
    private int thumbnail;

    public SaveState(int score, String date, int thumbnail) {
        this.score = score;
        this.date = date;
        this.thumbnail = thumbnail;
    }

    public int getScore() {
        return this.score;
    }

    public void setScore(int score) {
        this.score = score;
    }
}
