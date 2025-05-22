package main;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.util.ArrayList;
import java.util.List;

public class QuestionEditorDialog extends JDialog {
    private Quiz quiz;
    private JTextArea questionTextArea;
    private JTextField[] optionFields;
    private JRadioButton[] correctAnswerRadioButtons;
    private ButtonGroup correctAnswerGroup;
    private JLabel questionsAddedLabel;
    private Color primaryColor, accentColor, textColorWhite, bgColor, textColorDark, borderColor;

    public QuestionEditorDialog(Dialog owner, String title, Quiz quizToEdit,
                                Color primary, Color accent, Color textWhite, Color bg, Color textDark, Color border) {
        super(owner, title, true);
        this.quiz = quizToEdit;
        this.primaryColor = primary;
        this.accentColor = accent;
        this.textColorWhite = textWhite;
        this.bgColor = bg;
        this.textColorDark = textDark;
        this.borderColor = border;

        setSize(650, 550);
        setLocationRelativeTo(owner);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        getContentPane().setBackground(bgColor);
        setLayout(new BorderLayout(10, 10));

        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        formPanel.setBackground(bgColor);

        formPanel.add(createStyledLabel("Question Text:"));
        questionTextArea = new JTextArea(4, 30);
        questionTextArea.setLineWrap(true);
        questionTextArea.setWrapStyleWord(true);
        questionTextArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        questionTextArea.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(borderColor, 1 , true),
            new EmptyBorder(8, 8, 8, 8)
        ));
       
        JScrollPane questionScrollPane = new JScrollPane(questionTextArea);
        questionScrollPane.setBorder(BorderFactory.createEmptyBorder());
        formPanel.add(questionScrollPane);
        formPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        formPanel.add(createStyledLabel("Options (Mark correct answer):"));
        optionFields = new JTextField[4];
        correctAnswerRadioButtons = new JRadioButton[4];
        correctAnswerGroup = new ButtonGroup();
        JPanel optionsGridPanel = new JPanel(new GridLayout(4, 1, 0, 8)); 
        optionsGridPanel.setBackground(bgColor);

        for (int i = 0; i < 4; i++) {
            JPanel optionEntryPanel = new JPanel(new BorderLayout(10,0));
            optionEntryPanel.setBackground(bgColor);
            optionFields[i] = createStyledTextField("Option " + (i + 1));
            correctAnswerRadioButtons[i] = new JRadioButton();
            correctAnswerRadioButtons[i].setBackground(bgColor);
            correctAnswerRadioButtons[i].setCursor(new Cursor(Cursor.HAND_CURSOR));
            correctAnswerGroup.add(correctAnswerRadioButtons[i]);
            optionEntryPanel.add(optionFields[i], BorderLayout.CENTER);
            optionEntryPanel.add(correctAnswerRadioButtons[i], BorderLayout.EAST);
            optionsGridPanel.add(optionEntryPanel);
        }
        formPanel.add(optionsGridPanel);
        formPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        JButton addQuestionButton = createStyledButton("Add This Question", accentColor, textColorWhite);
        addQuestionButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        addQuestionButton.addActionListener(e -> addQuestionToQuiz());
        formPanel.add(addQuestionButton);
        formPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        questionsAddedLabel = createStyledLabel("Questions added: " + quiz.getNumOfQuestions());
        questionsAddedLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        formPanel.add(questionsAddedLabel);
        
        add(formPanel, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.setBackground(bgColor);
        bottomPanel.setBorder(new EmptyBorder(0,0,10,10));
        JButton finishButton = createStyledButton("Done", primaryColor, textColorWhite);
        finishButton.addActionListener(e -> dispose());
        bottomPanel.add(finishButton);
        add(bottomPanel, BorderLayout.SOUTH);
    }
    
    private JLabel createStyledLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 14));
        label.setForeground(textColorDark);
        return label;
    }

    private JTextField createStyledTextField(String placeholder) {
        JTextField textField = new JTextField(placeholder);
        textField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        textField.setForeground(Color.GRAY);
        textField.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(borderColor, 1, true),
            new EmptyBorder(8, 10, 8, 10)
        ));
        textField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (textField.getText().equals(placeholder) || (textField.getText().startsWith("Option ") && textField.getForeground().equals(Color.GRAY)) ) {
                    textField.setText("");
                    textField.setForeground(textColorDark);
                }
            }
            @Override
            public void focusLost(FocusEvent e) {
                if (textField.getText().isEmpty()) {
                    textField.setText(placeholder);
                    textField.setForeground(Color.GRAY);
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
    
    
    
//===================================================================================================
    private void addQuestionToQuiz() {
        String qText = questionTextArea.getText().trim();
        if (qText.isEmpty()) {
            showStyledErrorDialog("Question text cannot be empty.");
            return;
        }

        List<String> options = new ArrayList<>();
        for (int i = 0; i < optionFields.length; i++) {
            String optText = optionFields[i].getText().trim();
            if (optText.isEmpty() || optText.equals("Option " + (i+1))) {
                showStyledErrorDialog("Option " + (i + 1) + " cannot be empty.");
                return;
            }
            options.add(optText);
        }

        int correctIndex = -1;
        for (int i = 0; i < correctAnswerRadioButtons.length; i++) {
            if (correctAnswerRadioButtons[i].isSelected()) {
                correctIndex = i;
                break;
            }
        }

        if (correctIndex == -1) {
            showStyledErrorDialog("Please select the correct answer.");
            return;
        }

        Question newQuestion = new Question(qText, options, correctIndex);
        quiz.addQuestion(newQuestion);
        questionsAddedLabel.setText("Questions added: " + quiz.getNumOfQuestions());

        questionTextArea.setText("");
        for (int i=0; i<optionFields.length; i++) {
            optionFields[i].setText("Option " + (i+1));
            optionFields[i].setForeground(Color.GRAY);
        }
        correctAnswerGroup.clearSelection();
        questionTextArea.requestFocus();
        showStyledInfoDialog("Question added successfully!");
    }
    
    private void showStyledErrorDialog(String message) {
        JOptionPane.showMessageDialog(this, message, "Validation Error", JOptionPane.ERROR_MESSAGE);
    }
    
    private void showStyledInfoDialog(String message) {
        JOptionPane.showMessageDialog(this, message, "Success", JOptionPane.INFORMATION_MESSAGE);
    }
}