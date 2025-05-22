package main;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.util.ArrayList;

public class QuizAddDialog extends JDialog {

    private JTextField quizNameField; 
    private JLabel quizQuestionsNumLabel;
    private Quiz currentWorkingQuiz;
    private ArrayList<Quiz> quizesListRef;
    private Runnable updateHomePageQuizesCallback;
    private Color primaryColor, accentColor, textColorWhite, bgColor, textColorDark, borderColor;


    public QuizAddDialog(Frame owner, String title, ArrayList<Quiz> quizes, Runnable updateHomePageQuizes,
                         Color primary, Color accent, Color textWhite, Color bg, Color textDark, Color border) {
        super(owner, title, true);
        this.quizesListRef = quizes;
        this.updateHomePageQuizesCallback = updateHomePageQuizes;
        this.primaryColor = primary;
        this.accentColor = accent;
        this.textColorWhite = textWhite;
        this.bgColor = bg;
        this.textColorDark = textDark;
        this.borderColor = border;

        setSize(550, 350);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(owner);
        getContentPane().setBackground(bgColor);
        setLayout(new BorderLayout());

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(bgColor);
        mainPanel.setBorder(new EmptyBorder(25, 25, 25, 25));

        JLabel nameLabel = new JLabel("Quiz Title:");
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        nameLabel.setForeground(textColorDark);
        nameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        mainPanel.add(nameLabel);

        quizNameField = createStyledTextField("Enter Quiz Name");
        quizNameField.setAlignmentX(Component.LEFT_ALIGNMENT);
        mainPanel.add(quizNameField);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        JLabel numQuestionsDescLabel = new JLabel("Number of Questions:");
        numQuestionsDescLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        numQuestionsDescLabel.setForeground(textColorDark);
        numQuestionsDescLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        mainPanel.add(numQuestionsDescLabel);
        
        quizQuestionsNumLabel = new JLabel("0 (Add questions using 'Manage Questions')");
        quizQuestionsNumLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        quizQuestionsNumLabel.setForeground(textColorDark.darker());
        quizQuestionsNumLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        mainPanel.add(quizQuestionsNumLabel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 30)));

        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonsPanel.setBackground(bgColor);

        JButton addQuestionsButton = createStyledButton("Manage Questions", primaryColor, textColorWhite);
        addQuestionsButton.addActionListener(e -> openQuestionEditor());

        JButton saveQuizButton = createStyledButton("Save Quiz", accentColor, textColorWhite);
        saveQuizButton.addActionListener(e -> saveQuiz());

        buttonsPanel.add(addQuestionsButton);
        buttonsPanel.add(saveQuizButton);
        buttonsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        mainPanel.add(buttonsPanel);

        add(mainPanel, BorderLayout.CENTER);
    }

    private JTextField createStyledTextField(String placeholder) { // to add the place holder
        JTextField textField = new JTextField(placeholder);
        textField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        textField.setForeground(Color.GRAY);
        textField.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(borderColor, 1, true),
            new EmptyBorder(8, 10, 8, 10)
        ));
        textField.setMaximumSize(new Dimension(Integer.MAX_VALUE, textField.getPreferredSize().height));
        textField.addFocusListener(new FocusAdapter() {
           
        	@Override
            public void focusGained(FocusEvent e) {
                if (textField.getText().equals(placeholder)) {
                    textField.setText("");
                    textField.setForeground(textColorDark);
                }
            }
            @Override
            public void focusLost(FocusEvent e) {
                if (textField.getText().isEmpty()) {
                    textField.setText(placeholder);
                    textField.setForeground(Color.GRAY);
                } else {
                     if (currentWorkingQuiz != null) {
                        currentWorkingQuiz.quizName = textField.getText();
                    }
                }
            }
        });
        return textField;
    }
    
    private JButton createStyledButton(String text, Color bg, Color fg) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 13));
        button.setBackground(bg);
        button.setForeground(fg);
        button.setFocusPainted(false);
        button.setBorder(new EmptyBorder(10, 20, 10, 20));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }


    private void initializeCurrentWorkingQuiz() {
        String name = quizNameField.getText();
        if (name.isEmpty() || name.equals("Enter Quiz Name")) {
            showStyledErrorDialog("Please enter a valid quiz name first.");
            quizNameField.requestFocus();
            return;
        }
        if (currentWorkingQuiz == null) {
        	int newId = quizesListRef.isEmpty() ? 0 : quizesListRef.size();
        	currentWorkingQuiz = new Quiz(name, newId);
            quizQuestionsNumLabel.setText(String.valueOf(currentWorkingQuiz.getNumOfQuestions()) + " (Add questions using 'Manage Questions')");
        } else {
            currentWorkingQuiz.quizName = name;
        }
    }

    private void openQuestionEditor() {
        initializeCurrentWorkingQuiz();
        if (currentWorkingQuiz == null) return;

        QuestionEditorDialog qed = new QuestionEditorDialog(this, "Edit Questions for: " + currentWorkingQuiz.getQuizName(), currentWorkingQuiz, primaryColor, accentColor, textColorWhite, bgColor, textColorDark, borderColor);
        qed.setVisible(true);
        quizQuestionsNumLabel.setText(String.valueOf(currentWorkingQuiz.getNumOfQuestions()) + (currentWorkingQuiz.getNumOfQuestions() == 1 ? " question" : " questions"));
    }

    private void saveQuiz() {
        initializeCurrentWorkingQuiz();
       
        if (currentWorkingQuiz == null) return; 

        if (currentWorkingQuiz.getQuizName().isEmpty() || currentWorkingQuiz.getQuizName().equals("Enter Quiz Name")) {
            showStyledErrorDialog("Quiz name cannot be empty.");
            return;
        }
        if (currentWorkingQuiz.getQuestions().isEmpty()) {
            showStyledErrorDialog("A quiz must have at least one question. Please use 'Manage Questions'.");
            return;
        }

        quizesListRef.add(currentWorkingQuiz);
        updateHomePageQuizesCallback.run();
        dispose();
    }
    
    private void showStyledErrorDialog(String message) {
        JOptionPane.showMessageDialog(this, message, "Validation Error", JOptionPane.ERROR_MESSAGE);
    }
}



