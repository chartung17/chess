package ui;

import java.awt.*;
import javax.swing.*;

import main.Mediator;
import ui.ButtonPanel.ButtonState;

/**
 * This class is a graphical user interface.
 */
public class GUI extends UserInterface {
	private JFrame frame = new JFrame("Chess");
	private Container pane = frame.getContentPane();
	private BoardPanel board = new BoardPanel(mediator);
	private ButtonPanel buttons = new ButtonPanel(mediator);
	private JPanel sidePanel = new JPanel();
	private MessageArea text = new MessageArea();
	public static final Color BACKGROUND_COLOR = new Color(225, 225, 225); // light gray
	private boolean handlingButton = false;

	/**
	 * This method initializes the GUI.
	 */
	public GUI() {
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		pane.setLayout(new BorderLayout());
		pane.add(board, BorderLayout.CENTER);
		pane.add(sidePanel, BorderLayout.LINE_END);
		sidePanel.setLayout(new BoxLayout(sidePanel, BoxLayout.Y_AXIS));
		sidePanel.add(text);
		sidePanel.add(buttons);
		sidePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		frame.setSize(BoardPanel.BOARD_SIZE + MessageArea.MESSAGE_WIDTH + 2 * BoardPanel.BORDER_SIZE + 50,
				BoardPanel.BOARD_SIZE + 2 * BoardPanel.BORDER_SIZE + 50);
		pane.setBackground(BACKGROUND_COLOR);
		sidePanel.setBackground(BACKGROUND_COLOR);
		frame.setVisible(true);
	}

	@Override
	protected void updateBoard(byte[][] board) {
		this.board.setBoard(board);
		handlingButton = false;
		setButtonState();
	}

	/**
	 * This method updates the state of the button panel.
	 */
	private void setButtonState() {
		if (isPromotion) {
			buttons.setState(ButtonState.PROMOTION, isGameOver);
		} else if (isGameOver || handlingButton) {
			buttons.setState(ButtonState.YES_NO, isGameOver);
		} else {
			buttons.setState(ButtonState.DEFAULT, isGameOver);
		}
	}

	@Override
	protected void display() {
		board.drawBoard();
		if (!handlingButton)
			text.setMessage(messageLine1, messageLine2, isGameOver);
		pane.repaint();
	}

	/**
	 * This method is run when the user selects an option that can be handled by the
	 * GUI.
	 * 
	 * @param option an integer indicating which option the user selected, either
	 *               one of the Mediator constants or a negative number to indicate
	 *               that option handling is complete
	 */
	public void handleButton(int option) {
		switch (option) {
		case Mediator.DRAW:
			String currentPlayer = blackToMove ? "Black" : "White";
			String otherPlayer = blackToMove ? "White" : "Black";
			handlingButton = true;
			board.setHandlingButton(handlingButton);
			setButtonState();
			text.setMessage("",
					currentPlayer + " has offered to end the game in a draw. " + otherPlayer + ", do you accept?",
					isGameOver);
			break;
		case Mediator.RESIGN:
			handlingButton = true;
			board.setHandlingButton(handlingButton);
			setButtonState();
			text.setMessage("", (blackToMove ? "Black" : "White") + ", are you sure you want to resign?", isGameOver);
			break;
		case Mediator.QUEEN:
		case Mediator.ROOK:
		case Mediator.KNIGHT:
		case Mediator.BISHOP:
			board.unselectSquare();
		default:
			handlingButton = false;
			board.setHandlingButton(handlingButton);
			setButtonState();
			text.setMessage(messageLine1, messageLine2, isGameOver);
		}
	}

}
