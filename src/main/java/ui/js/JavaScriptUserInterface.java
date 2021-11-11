package ui.js;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.*;

import chess.Board;
import main.Mediator;
import ui.UserInterface;

import java.util.List;

import javax.servlet.http.HttpSession;

@RestController
@SpringBootApplication
public class JavaScriptUserInterface extends UserInterface {
	private byte[][] board = new byte[8][8];
	private int[] selectedSquare = null;
	private boolean handlingButton = false;
	private String oldMessageLine1, oldMessageLine2;
	private static final String ILLEGAL = "{\"status\":405,\"message\":\"Illegal move\"}";
	private static final String DEFAULT = "[\"Resign\",\"Offer Draw\"]";
	private static final String YES_NO = "[\"Yes\",\"No\"]";
	private static final String PLAY_AGAIN = "[\"Play Again\"]";
	private static final String PROMOTION = "[\"Queen\",\"Rook\",\"Knight\",\"Bishop\"";

	@Override
	protected void updateBoard(byte[][] board) {
		this.board = board;
	}

	@Override
	protected void display() {
		// do nothing
	}

	/**
	 * This method returns the user interface associated with the given session. If the session does
	 * not yet have an associated user interface, a new one is created and assigned to the session.
	 * 
	 * @param session the session to use
	 * @return the user interface associated with the session
	 */
	private static JavaScriptUserInterface getUI(HttpSession session) {
		JavaScriptUserInterface ui = (JavaScriptUserInterface) session.getAttribute("ui");
		if (ui == null) {
			ui = new JavaScriptUserInterface();
			session.setAttribute("ui", ui);
		}
		return ui;
	}

	/**
	 * This method handles a button click.
	 * 
	 * @param buttontext the label of the button, in lowercase letters, with spaces replaced by
	 *                   underscores
	 * @param session    the current session
	 * @return the state of the board after handling the button click
	 */
	@RequestMapping(value = "/button/{buttontext}", method = RequestMethod.GET)
	@CrossOrigin
	public static String handleButton(@PathVariable String buttontext, HttpSession session) {
		return getUI(session).handleButton(buttontext);
	}

	private String setButtonState() {
		if (isPromotion) {
			return PROMOTION;
		} else if (handlingButton) {
			return YES_NO;
		} else if (isGameOver) {
			return PLAY_AGAIN;
		} else {
			return DEFAULT;
		}
	}

	/**
	 * This method handles a button click.
	 * 
	 * @param buttontext the label of the button, in lowercase letters, with spaces replaced by
	 *                   underscores
	 * @return the state of the board after handling the button click
	 */
	private String handleButton(String buttontext) {
		String buttons = setButtonState();
		switch (buttontext) {
		case "offer_draw":
			if (buttons != DEFAULT)
				return ILLEGAL;
			String currentPlayer = blackToMove ? "Black" : "White";
			String otherPlayer = blackToMove ? "White" : "Black";
			handlingButton = true;
			oldMessageLine1 = messageLine1;
			oldMessageLine2 = messageLine2;
			messageLine1 = "";
			messageLine2 = currentPlayer + " has offered to end the game in a draw. " + otherPlayer
					+ ", do you accept?";
			break;
		case "resign":
			if (buttons != DEFAULT)
				return ILLEGAL;
			handlingButton = true;
			oldMessageLine1 = messageLine1;
			oldMessageLine2 = messageLine2;
			messageLine1 = "";
			messageLine2 = (blackToMove ? "Black" : "White") + ", are you sure you want to resign?";
			break;
		case "queen":
			if (buttons != PROMOTION)
				return ILLEGAL;
			handlingButton = false;
			selectedSquare = null;
			mediator.handleSelectedOption(Mediator.QUEEN);
			break;
		case "rook":
			if (buttons != PROMOTION)
				return ILLEGAL;
			handlingButton = false;
			selectedSquare = null;
			mediator.handleSelectedOption(Mediator.ROOK);
			break;
		case "knight":
			if (buttons != PROMOTION)
				return ILLEGAL;
			handlingButton = false;
			selectedSquare = null;
			mediator.handleSelectedOption(Mediator.KNIGHT);
			break;
		case "bishop":
			if (buttons != PROMOTION)
				return ILLEGAL;
			handlingButton = false;
			selectedSquare = null;
			mediator.handleSelectedOption(Mediator.BISHOP);
			break;
		case "yes":
			if (buttons != YES_NO)
				return ILLEGAL;
			handlingButton = false;
			if (messageLine2.contains("resign"))
				mediator.handleSelectedOption(Mediator.RESIGN);
			else
				mediator.handleSelectedOption(Mediator.DRAW);
			break;
		case "no":
			if (buttons != YES_NO)
				return ILLEGAL;
			handlingButton = false;
			messageLine1 = oldMessageLine1;
			messageLine2 = oldMessageLine2;
			break;
		case "play_again":
			if (buttons != PLAY_AGAIN)
				return ILLEGAL;
			mediator.initializeGame();
			mediator.start();
			break;
		default:
			return ILLEGAL;
		}
		return returnJson();
	}

	/**
	 * This method handles a click on a board square.
	 * 
	 * @param row     the row of the square
	 * @param col     the column of the square
	 * @param session the current session
	 * @return the state of the board after handling the click
	 */
	@RequestMapping(value = "/square/{row:[\\d]+}/{col:[\\d]+}", method = RequestMethod.GET)
	@CrossOrigin
	public static String handleSelectedSquare(@PathVariable int row, @PathVariable int col,
			HttpSession session) {
		JavaScriptUserInterface ui = getUI(session);
		if (ui.handlingButton || (row < 0) || (row >= 8) || (col < 0) || (col >= 8))
			return ILLEGAL;
		int result = ui.mediator.handleSelectedSquare(row, col);
		if ((result == Board.SELECT) || (result == Board.PROMOTE)) {
			ui.selectedSquare = new int[] { row, col };
		} else {
			ui.selectedSquare = null;
		}
		return ui.returnJson();
	}

	/**
	 * This method returns a JSON-formatted string indicating the current board state.
	 * 
	 * @return a JSON-formatted string indicating the current board state
	 */
	private String returnJson() {
		StringBuilder boardStr = new StringBuilder();
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {
				char c = board[i][j] == 0 ? '0' : (char) board[i][j];
				boardStr.append(c);
			}
		}
		StringBuilder movesStr = new StringBuilder();
		for (int i = 0; i < 64; i++) {
			movesStr.append('0');
		}
		if (selectedSquare != null) {
			List<int[]> moves = mediator.getLegalMoves(selectedSquare[0], selectedSquare[1]);
			moves.add(new int[] { selectedSquare[0], selectedSquare[1] });
			for (int[] move : moves) {
				movesStr.setCharAt(8 * move[0] + move[1], 'X');
			}
		}
		String buttons = setButtonState();
		return "{\"status\":200,\"board\":\"" + boardStr.toString() + "\",\"moves\":\""
				+ movesStr.toString() + "\",\"message1\":\"" + messageLine1 + "\",\"message2\":\""
				+ messageLine2 + "\",\"buttons\":" + buttons + "}";
	}

	/**
	 * This method runs the app.
	 * 
	 * @param args not used
	 */
	public static void main(String[] args) {
		SpringApplication.run(JavaScriptUserInterface.class, args);
	}

}
