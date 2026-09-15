import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

/**
 * A simple GUI + text viewer for the GameOfLife model.
 *
 * Students should not need to modify this file for the core project.
 */
public class GameOfLifeApp extends JFrame {

    private static final int ROWS = 40;
    private static final int COLS = 60;
    private static final int DELAY = 350;

    private final GameOfLife game;
    private final JButton[][] cells;

    private final JButton startButton;
    private final JLabel generationLabel;
    private final JLabel statusLabel;
    private final JTextArea textView;
    private final JSlider speedSlider;
    private final JLabel speedLabel;

    private Timer timer;
    private int generation;

    public GameOfLifeApp() {
        super("Conway's Game of Life - 2D Arrays");

        game = new GameOfLife(ROWS, COLS);
        cells = new JButton[ROWS][COLS];

        startButton = new JButton("Start");
        generationLabel = new JLabel("Generation: 0");
        statusLabel = new JLabel("Click cells to create a pattern, or add a glider.");
        textView = new JTextArea();
        speedSlider = new JSlider(1, 50, 10);
        speedLabel = new JLabel("Speed: 0");

        generation = 0;

        timer = new Timer((int)(DELAY*((double)(speedSlider.getValue())/3)), new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                stepGeneration();
            }
        });
        updateSpeedLabel();

        buildWindow();
        refreshDisplay();
    }

    private void startTimer() {
        timer = new Timer((int)(DELAY/((double)(speedSlider.getValue())/3)), new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                stepGeneration();
            }
        });
        timer.start();
    }

    private void buildWindow() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(8, 8));

        add(buildControlPanel(), BorderLayout.NORTH);
        add(buildMainPanel(), BorderLayout.CENTER);
        add(statusLabel, BorderLayout.SOUTH);

        setSize(1050, 610);
        setLocationRelativeTo(null);
    }

    private JPanel buildControlPanel() {
        JPanel controls = new JPanel();

        JButton stepButton = new JButton("Step");
        JButton clearButton = new JButton("Clear");
        JButton gliderButton = new JButton("Add Glider");

        stepButton.addActionListener(e -> stepGeneration());
        startButton.addActionListener(e -> toggleAnimation());
        clearButton.addActionListener(e -> clearBoard());
        gliderButton.addActionListener(e -> addGlider());
        speedSlider.addChangeListener(e -> updateSpeedLabel());

        controls.add(stepButton);
        controls.add(startButton);
        controls.add(clearButton);
        controls.add(gliderButton);
        controls.add(speedSlider);
        controls.add(speedLabel);
        controls.add(generationLabel);

        return controls;
    }

    private void updateSpeedLabel() {
        speedLabel.setText("Speed: " + speedSlider.getValue());
        if (timer.isRunning()) {
            timer.stop();
            startTimer();
        }
    }

    private JSplitPane buildMainPanel() {
        JPanel gridPanel = new JPanel(new GridLayout(ROWS, COLS, 1, 1));
        gridPanel.setBackground(Color.GRAY);
        gridPanel.setBorder(BorderFactory.createTitledBorder("GUI View"));

        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                JButton cellButton = new JButton();
                cellButton.setPreferredSize(new Dimension(22, 22));
                cellButton.setFocusPainted(false);
                cellButton.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
                cellButton.setOpaque(true);

                final int r = row;
                final int c = col;
                cellButton.addActionListener(e -> toggleCell(r, c));

                cells[row][col] = cellButton;
                gridPanel.add(cellButton);
            }
        }

        textView.setEditable(false);
        textView.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 16));
        textView.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        JScrollPane textScroll = new JScrollPane(textView);
        textScroll.setBorder(BorderFactory.createTitledBorder("Text View (toString)"));

        JSplitPane splitPane = new JSplitPane(
                JSplitPane.HORIZONTAL_SPLIT,
                gridPanel,
                textScroll
        );

        splitPane.setResizeWeight(0.68);
        splitPane.setDividerLocation(700);

        return splitPane;
    }

    private void toggleCell(int row, int col) {
        if (timer.isRunning()) {
            statusLabel.setText("Stop the animation before editing individual cells.");
            return;
        }

        if (game.cellAt(row, col)) {
            game.killCellAt(row, col);
        }
        else {
            game.growCellAt(row, col);
        }

        refreshDisplay();
    }

    private void stepGeneration() {


        game.update();
        generation++;
        refreshDisplay();

        if (touchesWall()) {
            stopAnimation();
            statusLabel.setText("A live cell reached the wall. Animation stopped.");
        }
        else {
            statusLabel.setText("Generation advanced.");
        }
    }

    private void toggleAnimation() {
        if (timer.isRunning()) {
            stopAnimation();
            statusLabel.setText("Animation stopped.");
        }
        else {
            if (touchesWall()) {
                statusLabel.setText("Move the pattern away from the wall before starting.");
                return;
            }

            startTimer();
            startButton.setText("Stop");
            statusLabel.setText("Animation running...");
        }
    }

    private void stopAnimation() {
        timer.stop();
        startButton.setText("Start");
    }

    private void clearBoard() {
        stopAnimation();
        game.clear();
        generation = 0;
        refreshDisplay();
        statusLabel.setText("Board cleared.");
    }

    private void addGlider() {
        stopAnimation();
        game.clear();
        generation = 0;

        // Standard five-cell glider, placed away from the walls.
        int row = 2;
        int col = 2;

        game.growCellAt(row, col + 1);
        game.growCellAt(row + 1, col + 2);
        game.growCellAt(row + 2, col);
        game.growCellAt(row + 2, col + 1);
        game.growCellAt(row + 2, col + 2);

        refreshDisplay();
        statusLabel.setText("Glider added. Complete update() to make it move.");
    }

    private boolean touchesWall() {
        return false;
    }

    private void refreshDisplay() {
        for (int row = 0; row < game.numberOfRows(); row++) {
            for (int col = 0; col < game.numberOfColumns(); col++) {
                if (game.cellAt(row, col)) {
                    cells[row][col].setBackground(Color.BLACK);
                }
                else {
                    cells[row][col].setBackground(Color.WHITE);
                }
            }
        }

        generationLabel.setText("Generation: " + generation);
        textView.setText(game.toString());
        textView.setCaretPosition(0);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            GameOfLifeApp app = new GameOfLifeApp();
            app.setVisible(true);
        });
    }
}
