import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;

public class TicTacToeGame extends JFrame implements ActionListener {
    private static final int BOARD_SIZE = 3;
    private static final int CELL_SIZE = 100;

    private JButton[][] buttons;
    private int[][] board;
    private int currentPlayer;
    private boolean playWithComputer;
    private boolean computerStarts;
    private JLabel statusLabel;
    private Random random;

    public TicTacToeGame() {
        setTitle("Tic Tac Toe");
        setSize(BOARD_SIZE * CELL_SIZE, BOARD_SIZE * CELL_SIZE + 30); // Adjusted to fit status label
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Center the window on the screen
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(BOARD_SIZE, BOARD_SIZE));

        buttons = new JButton[BOARD_SIZE][BOARD_SIZE];
        board = new int[BOARD_SIZE][BOARD_SIZE];
        currentPlayer = 1; // Player 1 starts
        playWithComputer = false; // Default: play with friend
        computerStarts = false; // Default: user starts
        random = new Random();

        // Status label to show whose turn it is
        statusLabel = new JLabel("Player 1's turn");
        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);
        statusLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 16));
        add(statusLabel, BorderLayout.NORTH);

        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                JButton button = new JButton();
                button.setPreferredSize(new Dimension(CELL_SIZE, CELL_SIZE));
                button.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 40));
                button.addActionListener(this);
                buttons[row][col] = button;
                panel.add(button);
            }
        }

        add(panel);
        setVisible(true);

        // Start the game with mode selection
        chooseGameMode();

        // If playWithComputer is true and computerStarts is true, make the computer's move
        if (playWithComputer && computerStarts) {
            currentPlayer = 2;
            makeComputerMove();
        }
    }

    private void chooseGameMode() {
        // Dialog to choose game mode
        int choice = JOptionPane.showOptionDialog(this,
                "Choose game mode:", "Game Mode", JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE, null, new String[]{"Play with Friend", "Play with Computer"}, null);

        if (choice == JOptionPane.CLOSED_OPTION) {
            // User closed the dialog, exit the program
            System.exit(0);
        } else if (choice == JOptionPane.NO_OPTION) {
            playWithComputer = true;
            chooseFirstMove();
        } else {
            playWithComputer = false;
        }
    }

    private void chooseFirstMove() {
        // Dialog to choose who starts first
        int choice = JOptionPane.showOptionDialog(this,
                "Who should make the first move?", "First Move", JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE, null, new String[]{"User", "Computer"}, null);

        if (choice == JOptionPane.CLOSED_OPTION) {
            // User closed the dialog, exit the program
            System.exit(0);
        } else if (choice == JOptionPane.NO_OPTION) {
            computerStarts = true;
        } else {
            computerStarts = false;
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        JButton clickedButton = (JButton) e.getSource();

        // Find the clicked button's position
        int clickedRow = -1, clickedCol = -1;
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                if (buttons[row][col] == clickedButton) {
                    clickedRow = row;
                    clickedCol = col;
                    break;
                }
            }
        }

        if (clickedRow != -1 && clickedCol != -1 && board[clickedRow][clickedCol] == 0) {
            board[clickedRow][clickedCol] = currentPlayer;
            updateButton(clickedRow, clickedCol);

            if (hasWon(currentPlayer)) {
                JOptionPane.showMessageDialog(this, "Player " + currentPlayer + " wins!");
                resetGame();
            } else if (isBoardFull()) {
                JOptionPane.showMessageDialog(this, "Draw!");
                resetGame();
            } else {
                currentPlayer = (currentPlayer == 1) ? 2 : 1;
                updateStatusLabel();

                if (playWithComputer && currentPlayer == 2) {
                    makeComputerMove();
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Invalid move!");
        }
    }

    private void updateButton(int row, int col) {
        buttons[row][col].setText((currentPlayer == 1) ? "X" : "O");
        buttons[row][col].setEnabled(false);
    }

    private boolean hasWon(int player) {
        // Check rows and columns
        for (int i = 0; i < BOARD_SIZE; i++) {
            if (board[i][0] == player && board[i][1] == player && board[i][2] == player) {
                return true; // Row win
            }
            if (board[0][i] == player && board[1][i] == player && board[2][i] == player) {
                return true; // Column win
            }
        }

        // Check diagonals
        if (board[0][0] == player && board[1][1] == player && board[2][2] == player) {
            return true; // Diagonal (top-left to bottom-right) win
        }
        if (board[0][2] == player && board[1][1] == player && board[2][0] == player) {
            return true; // Diagonal (top-right to bottom-left) win
        }

        return false;
    }

    private boolean isBoardFull() {
        for (int[] row : board) {
            for (int cell : row) {
                if (cell == 0) {
                    return false; // Board is not full
                }
            }
        }
        return true; // Board is full (draw)
    }

    private void resetGame() {
        // Clear board
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                board[row][col] = 0;
                buttons[row][col].setText("");
                buttons[row][col].setEnabled(true);
            }
        }

        // Ask for game mode again after each win
        chooseGameMode();
        currentPlayer = 1; // Player 1 starts again
        updateStatusLabel();

        if (playWithComputer) {
            if (computerStarts) {
                currentPlayer = 2;
                makeComputerMove();
            }
        }
    }

    private void makeComputerMove() {
        // Simple random move for the computer
        int row, col;
        do {
            row = random.nextInt(BOARD_SIZE);
            col = random.nextInt(BOARD_SIZE);
        } while (board[row][col] != 0);

        board[row][col] = currentPlayer;
        updateButton(row, col);

        if (hasWon(currentPlayer)) {
            JOptionPane.showMessageDialog(this, "Computer wins!");
            resetGame();
        } else if (isBoardFull()) {
            JOptionPane.showMessageDialog(this, "Draw!");
            resetGame();
        } else {
            currentPlayer = 1; // Player's turn
            updateStatusLabel();
        }
    }

    private void updateStatusLabel() {
        statusLabel.setText("Player " + currentPlayer + "'s turn");
    }

    public static void main(String[] args) {
        new TicTacToeGame();
    }
}
