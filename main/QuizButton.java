package main;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.geom.RoundRectangle2D;

public class QuizButton extends JPanel {

    private final JLabel quizNameLabel, quizNumQuestionsLabel, quizScoreLabel;
    private final JButton takeQuizButton, deleteQuizButton;
    private final Quiz quiz;

    private Color primaryColor, accentDeleteColor, textColorWhite, textColorDark, bgColor, borderColor;

    public QuizButton(Quiz q, ActionListener deleteListener,
                      Color primaryColor, Color accentDeleteColor, Color textColorWhite, Color textColorDark,
                      Color bgColor, Color borderColor) {
        this.quiz = q;
        this.primaryColor = primaryColor;
        this.accentDeleteColor = accentDeleteColor;
        this.textColorWhite = textColorWhite;
        this.textColorDark = textColorDark;
        this.bgColor = bgColor;
        this.borderColor = borderColor;

        setOpaque(false); 
        setLayout(new BorderLayout());

        JPanel contentPanel = new JPanel() {
        };
        
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEtchedBorder(), new EmptyBorder(20, 20, 15, 20)));
        contentPanel.setOpaque(false);

        quizNameLabel = new JLabel(quiz.getQuizName());
        quizNameLabel.setFont(new Font("Segoe UI Semibold", Font.BOLD, 18));
        quizNameLabel.setForeground(textColorDark);
        quizNameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        quizNumQuestionsLabel = new JLabel("Questions: " + quiz.getNumOfQuestions());
        quizNumQuestionsLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        quizNumQuestionsLabel.setForeground(textColorDark.darker());
        quizNumQuestionsLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        quizScoreLabel = new JLabel(getScoreText());
        quizScoreLabel.setFont(new Font("Segoe UI", Font.ITALIC, 14));
        quizScoreLabel.setForeground(primaryColor);
        quizScoreLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel buttonsContainer = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        buttonsContainer.setOpaque(false);

        takeQuizButton = createStyledButton(quiz.isTaken() ? "Retake Quiz" : "Take Quiz", primaryColor, textColorWhite);
        takeQuizButton.addActionListener(e -> {
            Frame parentFrame = (Frame) SwingUtilities.getWindowAncestor(this);
            if (quiz.getQuestions().isEmpty()) {
                 showStyledErrorDialog(parentFrame, "This quiz has no questions. Please add questions first.", "No Questions");
                 return;
            }
            QuizTakingDialog quizTakingDialog = new QuizTakingDialog(parentFrame, "Taking: " + quiz.getQuizName(), quiz, primaryColor, textColorWhite, bgColor, textColorDark, borderColor);
            quizTakingDialog.setVisible(true);
            updateDisplay();
            if (parentFrame instanceof QuizApp) {
                ((QuizApp)parentFrame).refreshQuizesDisplay();
            }
        });

        deleteQuizButton = createStyledButton("Delete", accentDeleteColor, textColorWhite);
        if (deleteListener != null) {
            deleteQuizButton.addActionListener(deleteListener);
        }

        buttonsContainer.add(takeQuizButton);
        buttonsContainer.add(deleteQuizButton);

        contentPanel.add(quizNameLabel);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 8)));
        contentPanel.add(quizNumQuestionsLabel);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 8)));
        contentPanel.add(quizScoreLabel);
        contentPanel.add(Box.createVerticalStrut(20));
        contentPanel.add(buttonsContainer);
        
        add(contentPanel, BorderLayout.CENTER);

        Dimension preferredSize = new Dimension(280, 230);
        setPreferredSize(preferredSize);
        setMinimumSize(preferredSize);
        setMaximumSize(preferredSize);
    }
    
    private String getScoreText(){
        if (!quiz.isTaken()) return "Not taken yet";
        if (quiz.getNumOfQuestions() == 0) return "Score: N/A (No questions)";
        return "Score: " + quiz.getUserScore() + "/" + quiz.getNumOfQuestions();
    }


    private JButton createStyledButton(String text, Color bgColor, Color fgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 13));
        button.setBackground(bgColor);
        button.setForeground(fgColor);
        button.setFocusPainted(false);
        button.setBorder(new EmptyBorder(8, 18, 8, 18));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setOpaque(true); 
        return button;
    }
    
    public void updateDisplay() {
        quizNumQuestionsLabel.setText("Questions: " + quiz.getNumOfQuestions());
        quizScoreLabel.setText(getScoreText());
        takeQuizButton.setText(quiz.isTaken() ? "Retake Quiz" : "Take Quiz");
        quizNameLabel.setText(quiz.getQuizName());
        revalidate();
        repaint();
    }

    private void showStyledErrorDialog(Frame owner, String message, String title) {
        JOptionPane.showMessageDialog(owner, message, title, JOptionPane.WARNING_MESSAGE);
    }
}

