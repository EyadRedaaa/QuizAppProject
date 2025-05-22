package main; // Added package declaration

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class QuizApp extends JFrame {

    // List to store all quizzes
    ArrayList<Quiz> quizes = new ArrayList<>();
    private int nextQuizId = 0; // Used to generate unique quiz IDs

    // UI components
    private JButton addQuizButtonComponent;
    private JPanel northPanel;
    private JLabel homePageHeroLabel;
    private JPanel quizesContainer;

    // Color constants for UI styling
    private static final Color PRIMARY_COLOR_DARK = new Color(25, 42, 86);
    private static final Color PRIMARY_COLOR_LIGHT = new Color(60, 99, 130);
    private static final Color BACKGROUND_COLOR = new Color(245, 246, 250);
    private static final Color TEXT_COLOR_DARK = new Color(47, 54, 64);
    private static final Color TEXT_COLOR_WHITE = Color.WHITE;
    private static final Color ACCENT_DELETE = new Color(205, 97, 85);
    private static final Color ACCENT_ADD = new Color(39, 174, 96);
    private static final Color BORDER_COLOR = new Color(220, 221, 225);

    public QuizApp() {
        super("Quiz Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 750);
        setLocationRelativeTo(null); // Owner
        getContentPane().setBackground(BACKGROUND_COLOR);

        // Add some sample quizzes to start with
        initializeSampleQuizzes();

        // Create and style the top panel (header)
        northPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        northPanel.setBackground(PRIMARY_COLOR_DARK);
        northPanel.setBorder(new EmptyBorder(25, 0, 25, 0));

        // Add the main title label to the header
        homePageHeroLabel = new JLabel("Quiz Dashboard");
        homePageHeroLabel.setFont(new Font("Segoe UI Semibold", Font.BOLD, 32));
        homePageHeroLabel.setForeground(TEXT_COLOR_WHITE);
        northPanel.add(homePageHeroLabel);
        add(northPanel, BorderLayout.NORTH);

        // Container for all quiz buttons/cards
        quizesContainer = new JPanel(new WrapLayout(FlowLayout.LEFT, 25, 25));
        quizesContainer.setBackground(BACKGROUND_COLOR);
        quizesContainer.setBorder(new EmptyBorder(25, 25, 25, 25));

        // Add quiz buttons/cards to the container
        initializeQuizesContainerGUI();

        // Make the quiz container scrollable
        JScrollPane quizesScrollPane = new JScrollPane(quizesContainer);

        // Style the scroll pane
        quizesScrollPane.setBorder(BorderFactory.createEmptyBorder());
        quizesScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        quizesScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        quizesScrollPane.getVerticalScrollBar().setUnitIncrement(16);
        quizesScrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(10, 0));
        quizesScrollPane.getVerticalScrollBar().setUI(new javax.swing.plaf.basic.BasicScrollBarUI() {
        	
        	@Override
            protected void configureScrollBarColors() {
                this.thumbColor = PRIMARY_COLOR_LIGHT;
                this.trackColor = BACKGROUND_COLOR;
            }
            @Override
            protected JButton createDecreaseButton(int orientation) {
                return createZeroButton();
            }
            @Override
            protected JButton createIncreaseButton(int orientation) {
                return createZeroButton();
            }
            // Helper to create invisible scroll buttons
            private JButton createZeroButton() {
                JButton jbutton = new JButton();
                jbutton.setPreferredSize(new Dimension(0, 0));
                jbutton.setMinimumSize(new Dimension(0, 0));
                jbutton.setMaximumSize(new Dimension(0, 0));
                return jbutton;
            }
        });

        // Add a KeyListener to the frame for the 'A' key (add quiz)
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent k) {
                if (k.getKeyCode() == KeyEvent.VK_A) {
                    // Show the dialog to add a new quiz
                    QuizAddDialog addQuizDialog = new QuizAddDialog(
                        QuizApp.this, "Create New Quiz", quizes,
                        QuizApp.this::handleQuizAddedOrUpdated,
                        PRIMARY_COLOR_DARK, ACCENT_ADD, TEXT_COLOR_WHITE,
                        BACKGROUND_COLOR, TEXT_COLOR_DARK, BORDER_COLOR
                    );
                    addQuizDialog.setVisible(true);
                }
            }
        });

        // Add the scrollable quiz container to the main window
        add(quizesScrollPane, BorderLayout.CENTER);

        // Make sure the frame can receive key events
        setFocusable(true);
        requestFocusInWindow();

        // Show the window
        setVisible(true);
    
    }

    // Generate a new unique quiz ID
    private int generateNewQuizId() {
        return nextQuizId++;
    }

    // Add some sample quizzes for demonstration
    private void initializeSampleQuizzes() {
        List<Question> vpQuestions = new ArrayList<>();
        vpQuestions.add(new Question("What does IDE stand for?",
                Arrays.asList("Integrated Development Environment", "Internal Drive Error", "Ideal Design Example", "Interface Docker Engine"), 0));
        vpQuestions.add(new Question("Which keyword is used to define a constant in Java?",
                Arrays.asList("const", "static", "final", "let"), 2));

        quizes.add(new Quiz("Java Basics", generateNewQuizId(), vpQuestions));

        List<Question> mathQuestions = new ArrayList<>();
        mathQuestions.add(new Question("What is 7 * 8?",
                Arrays.asList("54", "56", "63", "49"), 1));
        mathQuestions.add(new Question("What is the value of Pi (approx)?",
                Arrays.asList("3.14", "2.71", "1.61", "4.00"), 0));

        quizes.add(new Quiz("Simple Math", generateNewQuizId(), mathQuestions));
    
    
    }

    // Remove a quiz by its ID and refresh the display
    public void deleteHandler(int idToDelete) {
        quizes.removeIf(q -> q.getQuizId() == idToDelete);
        refreshQuizesDisplay();
    }

    // Refresh the quiz display (after add, delete, or update)
    public void refreshQuizesDisplay() {

        // Remove all and re-add to update UI
        quizesContainer.removeAll();
        initializeQuizesContainerGUI();
        quizesContainer.revalidate();
        quizesContainer.repaint();
    }

    // Called after a quiz is added or updated
    public void handleQuizAddedOrUpdated() {
        refreshQuizesDisplay();
    }

    // Add all quiz buttons/cards and the "Add New Quiz" button to the container
    public void initializeQuizesContainerGUI() {
        for (Quiz q : quizes) {
            QuizButton quizButton = new QuizButton(
                q, new DeleteActionListener(q.getQuizId()),
                PRIMARY_COLOR_LIGHT, ACCENT_DELETE, TEXT_COLOR_WHITE,
                TEXT_COLOR_DARK, BACKGROUND_COLOR, BORDER_COLOR
            );
            quizesContainer.add(quizButton);
        }

        // Create the "+ Add New Quiz" button
        addQuizButtonComponent = new JButton("+ Add New Quiz");
        addQuizButtonComponent.setFocusPainted(false);
        addQuizButtonComponent.setForeground(PRIMARY_COLOR_DARK);
        addQuizButtonComponent.setBackground(BACKGROUND_COLOR);
        addQuizButtonComponent.setCursor(new Cursor(Cursor.HAND_CURSOR));
        Dimension buttonSize = new Dimension(280, 230);
        addQuizButtonComponent.setPreferredSize(buttonSize);
        addQuizButtonComponent.setMinimumSize(buttonSize);
        addQuizButtonComponent.setMaximumSize(buttonSize);
        addQuizButtonComponent.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        addQuizButtonComponent.setBorder(BorderFactory.createDashedBorder(
            PRIMARY_COLOR_LIGHT, 1.5f, 5.0f, 2.0f, false
        ));

        // Show the add quiz dialog when button is clicked
        addQuizButtonComponent.addActionListener(e -> {
            QuizAddDialog addQuizDialog = new QuizAddDialog(
                QuizApp.this, "Create New Quiz", quizes,
                this::handleQuizAddedOrUpdated,
                PRIMARY_COLOR_DARK, ACCENT_ADD, TEXT_COLOR_WHITE,
                BACKGROUND_COLOR, TEXT_COLOR_DARK, BORDER_COLOR
            );
            addQuizDialog.setVisible(true);
            requestFocusInWindow();
        });
        quizesContainer.add(addQuizButtonComponent);
    }

    // Handles delete button clicks for quizzes
    private class DeleteActionListener implements ActionListener {
        private final int quizIdToDelete;

        public DeleteActionListener(int quizId) {
            this.quizIdToDelete = quizId;
        }

        @Override
        public void actionPerformed(ActionEvent e) {

            // Show confirmation dialog before deleting
            int confirmation = JOptionPane.showConfirmDialog(
                QuizApp.this,
                "Are you sure you want to delete this quiz?",
                "Confirm Deletion", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE
            );
            if (confirmation == JOptionPane.YES_OPTION) {
                deleteHandler(quizIdToDelete);
            }
        }
    }

    // Main entry point: start the application
    public static void main(String[] args) {
        new QuizApp();
    }
}

