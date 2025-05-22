package main;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.util.Arrays;
import java.util.List;

public class QuizTakingDialog extends JDialog {
    private Quiz quiz;
    private List<Question> questions;
    private int currentQuestionIndex;
    private int[] userAnswers;

    private JLabel questionNumberLabel;
    private JTextArea questionTextDisplay;
    private JRadioButton[] optionRadioButtons;
    private ButtonGroup optionsGroup;
    private JButton prevButton, nextButton, submitButton;
    private Color primaryColor, textColorWhite, bgColor, textColorDark, borderColor;

    public QuizTakingDialog(Frame owner, String title, Quiz quizToTake,
                            Color primary, Color textWhite, Color bg, Color textDark, Color border) {
        super(owner, title, true);
        this.quiz = quizToTake;
        this.questions = quiz.getQuestions();
        this.userAnswers = new int[questions.size()];
        Arrays.fill(userAnswers, -1);
        this.primaryColor = primary;
        this.textColorWhite = textWhite;
        this.bgColor = bg;
        this.textColorDark = textDark;
        this.borderColor = border;

        if (questions.isEmpty()) {
            SwingUtilities.invokeLater(() -> {
                showStyledErrorDialog("This quiz has no questions!", "Error");
                dispose();
            });
            return;
        }
        currentQuestionIndex = 0;

        setSize(700, 550);
        setLocationRelativeTo(owner);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        getContentPane().setBackground(bgColor);
        setLayout(new BorderLayout(10, 10));
        ((JPanel)getContentPane()).setBorder(new EmptyBorder(20,20,20,20));

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        topPanel.setBackground(bgColor);
        questionNumberLabel = new JLabel();
        questionNumberLabel.setFont(new Font("Segoe UI Semibold", Font.BOLD, 18));
        questionNumberLabel.setForeground(textColorDark);
        topPanel.add(questionNumberLabel);
        add(topPanel, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBackground(bgColor);

        questionTextDisplay = new JTextArea(6, 30);
        questionTextDisplay.setWrapStyleWord(true);
        questionTextDisplay.setLineWrap(true);
        questionTextDisplay.setEditable(false);
        questionTextDisplay.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        questionTextDisplay.setForeground(textColorDark);
        questionTextDisplay.setBackground(bgColor);
        questionTextDisplay.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(borderColor, 1, true),
            new EmptyBorder(10,10,10,10)
        ));
        JScrollPane questionScrollPane = new JScrollPane(questionTextDisplay);
        questionScrollPane.setBorder(BorderFactory.createEmptyBorder());
        centerPanel.add(questionScrollPane);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        optionsGroup = new ButtonGroup();
        optionRadioButtons = new JRadioButton[4];
        JPanel optionsPanel = new JPanel(new GridLayout(0, 1, 0, 10));
        optionsPanel.setBackground(bgColor);
        for (int i = 0; i < 4; i++) {
            optionRadioButtons[i] = new JRadioButton();
            optionRadioButtons[i].setFont(new Font("Segoe UI", Font.PLAIN, 14));
            optionRadioButtons[i].setBackground(bgColor);
            optionRadioButtons[i].setForeground(textColorDark);
            optionRadioButtons[i].setCursor(new Cursor(Cursor.HAND_CURSOR));
            optionsGroup.add(optionRadioButtons[i]);
            optionsPanel.add(optionRadioButtons[i]);
        }
        centerPanel.add(optionsPanel);
        add(centerPanel, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        bottomPanel.setBackground(bgColor);
        prevButton = createStyledNavButton("Previous");
        prevButton.addActionListener(e -> navigate(-1));

        nextButton = createStyledNavButton("Next");
        nextButton.addActionListener(e -> navigate(1));
        
        submitButton = createStyledNavButton("Submit Quiz");
        submitButton.setBackground(new Color(39, 174, 96)); 
        submitButton.addActionListener(e -> submitQuiz());

        bottomPanel.add(prevButton);
        bottomPanel.add(nextButton);
        bottomPanel.add(submitButton);
        add(bottomPanel, BorderLayout.SOUTH);

        loadQuestion(currentQuestionIndex);
    }
    
    private JButton createStyledNavButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 13));
        button.setBackground(primaryColor);
        button.setForeground(textColorWhite);
        button.setFocusPainted(false);
        button.setBorder(new EmptyBorder(10, 22, 10, 22));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    private void loadQuestion(int index) {
        Question q = questions.get(index);
        questionNumberLabel.setText("Question " + (index + 1) + " of " + questions.size());
        questionTextDisplay.setText(q.getQuestionText());

        List<String> options = q.getOptions();
        optionsGroup.clearSelection();
        for (int i = 0; i < optionRadioButtons.length; i++) {
            if (i < options.size()) {
                optionRadioButtons[i].setText(options.get(i));
                optionRadioButtons[i].setVisible(true);
                if (userAnswers[index] == i) {
                    optionRadioButtons[i].setSelected(true);
                }
            } else {
                optionRadioButtons[i].setVisible(false);
            }
        }

        prevButton.setEnabled(index > 0);
        nextButton.setEnabled(index < questions.size() - 1);
        submitButton.setVisible(index == questions.size() - 1);
    }

    private void saveCurrentAnswer() {
        for (int i = 0; i < optionRadioButtons.length; i++) {
            if (optionRadioButtons[i].isSelected()) {
                userAnswers[currentQuestionIndex] = i;
                return;
            }
        }
    }

    private void navigate(int direction) {
        saveCurrentAnswer();
        currentQuestionIndex += direction;
        loadQuestion(currentQuestionIndex);
    }

    private void submitQuiz() {
        saveCurrentAnswer();

        int score = 0;
        for (int i = 0; i < questions.size(); i++) {
            if (userAnswers[i] == questions.get(i).getCorrectAnswerIndex()) {
                score++;
            }
        }
        quiz.setUserScore(score);
        quiz.setTaken(true);

        QuizResultDialog resultDialog = new QuizResultDialog((Frame) getOwner(), "Quiz Result", quiz, score, primaryColor, textColorWhite, bgColor, textColorDark);
        resultDialog.setVisible(true);
        dispose();
    }
    
    private void showStyledErrorDialog(String message, String title) {
        JOptionPane.showMessageDialog(this, message, title, JOptionPane.ERROR_MESSAGE);
    }
}