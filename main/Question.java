package main;

import java.util.ArrayList;
import java.util.List;

public class Question {
    public String questionText;
    public List<String> options;
    public int correctAnswerIndex;

    public Question(String questionText, List<String> options, int correctAnswerIndex) {
        this.questionText = questionText;
        this.options = new ArrayList<>(options); 
        this.correctAnswerIndex = correctAnswerIndex;
    }

    public String getQuestionText() {
        return questionText;
    }

    public List<String> getOptions() {
        return options;
    }

    public int getCorrectAnswerIndex() {
        return correctAnswerIndex;
    }
}