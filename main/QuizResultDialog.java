package main;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class QuizResultDialog extends JDialog {

    public QuizResultDialog(Frame owner, String title, Quiz quiz, int score,
                            Color primaryColor, Color textColorWhite, Color bgColor, Color textColorDark) {
        super(owner, title, true);
        setSize(450, 280);
        setLocationRelativeTo(owner);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        getContentPane().setBackground(bgColor);
        setLayout(new BorderLayout(10, 10));

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBorder(new EmptyBorder(25, 25, 25, 25));
        contentPanel.setBackground(bgColor);

        JLabel quizNameLabel = new JLabel(quiz.getQuizName());
        quizNameLabel.setFont(new Font("Segoe UI Semibold", Font.BOLD, 20));
        quizNameLabel.setForeground(textColorDark);
        quizNameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel scoreInfoLabel = new JLabel("Your Score:");
        scoreInfoLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        scoreInfoLabel.setForeground(textColorDark.darker());
        scoreInfoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel scoreLabel = new JLabel(score + " / " + quiz.getNumOfQuestions());
        scoreLabel.setFont(new Font("Segoe UI Black", Font.BOLD, 36));
        scoreLabel.setForeground(primaryColor);
        scoreLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        float percentage = 0;
        if (quiz.getNumOfQuestions() > 0) {
            percentage = ((float)score / quiz.getNumOfQuestions()) * 100; // to Get the percentage
        }
        JLabel percentageLabel = new JLabel(String.format("Percentage: %.1f%%", percentage)); // one Fraction after Point
        percentageLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        percentageLabel.setForeground(textColorDark.darker());
        percentageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton closeButton = new JButton("Close");
        closeButton.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 13));
        closeButton.setBackground(primaryColor);
        closeButton.setForeground(textColorWhite);
        closeButton.setFocusPainted(false);
        closeButton.setBorder(new EmptyBorder(10, 25, 10, 25));
        closeButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        closeButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        closeButton.addActionListener(e -> dispose());

        contentPanel.add(quizNameLabel);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        contentPanel.add(scoreInfoLabel);
        contentPanel.add(scoreLabel);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        contentPanel.add(percentageLabel);
        contentPanel.add(Box.createVerticalStrut(25));
        contentPanel.add(closeButton);

        add(contentPanel, BorderLayout.CENTER);
    }
}