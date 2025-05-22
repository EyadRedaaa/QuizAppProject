package main;

import java.util.ArrayList;
import java.util.List;

public class Quiz {
    public String quizName;
    private int numOfQuestions;
    public final int quizId; // final -> no Inheritance , Must send the values to constructor , can't make override to this method
    public List<Question> questions;
    public int userScore = -1;
    public boolean taken = false;

        public Quiz(String quizName, int quizId, List<Question> initialQuestions){
        this.quizName = quizName;
        this.quizId = quizId;
        this.questions = initialQuestions != null ? new ArrayList<>(initialQuestions) : new ArrayList<>();
        this.numOfQuestions = this.questions.size();
    }
    
        public Quiz(String quizName, int quizId) {
        this.quizName = quizName;
        this.quizId = quizId;
        this.questions = new ArrayList<>();
        this.numOfQuestions = 0;
  
    }

    public void addQuestion(Question q) {
        if (q != null) {
            this.questions.add(q);
            this.numOfQuestions = this.questions.size();
        }
    }

    public List<Question> getQuestions() {
        return questions;
    }

    public int getNumOfQuestions() {
        return questions.size();
    }
    
    public String getQuizName() {
        return quizName;
    }

    public int getQuizId() {
        return quizId;
    }

    public int getUserScore() {
        return userScore;
    }

    public void setUserScore(int userScore) {
        this.userScore = userScore;
    }

    public boolean isTaken() {
        return taken;
    }

    public void setTaken(boolean taken) {
        this.taken = taken;
    }

}