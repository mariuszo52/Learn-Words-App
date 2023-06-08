package pl.languagelearn.application.answer;

import java.time.LocalTime;

public class Answer {
    private String polishName;
    private String userTranslation;
    private String translation;
    boolean isGoodAnswer;
    LocalTime time;

    public String getPolishName() {
        return polishName;
    }

    public void setPolishName(String polishName) {
        this.polishName = polishName;
    }

    public String getTranslation() {
        return translation;
    }

    public void setTranslation(String translation) {
        this.translation = translation;
    }

    public boolean isGoodAnswer() {
        return isGoodAnswer;
    }

    public void setGoodAnswer(boolean goodAnswer) {
        isGoodAnswer = goodAnswer;
    }

    public LocalTime getTime() {
        return time;
    }

    @Override
    public String toString() {
        return "Answer{" +
                "polishName='" + polishName + '\'' +
                ", userTranslation='" + userTranslation + '\'' +
                ", translation='" + translation + '\'' +
                '}';
    }

    public String getUserTranslation() {
        return userTranslation;
    }

    public void setUserTranslation(String userTranslation) {
        this.userTranslation = userTranslation;
    }

    public Answer(String polishName, String userTranslation, boolean isGoodAnswer, String translation) {
        this.polishName = polishName;
        this.userTranslation = userTranslation;
        this.translation = translation;
        this.isGoodAnswer = isGoodAnswer;
        this.time = LocalTime.now();
    }
}
