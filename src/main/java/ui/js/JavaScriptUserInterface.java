package ui.js;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.*;

import chess.Board;
import ui.UserInterface;

import java.util.List;

import javax.servlet.http.HttpSession;

@RestController
@SpringBootApplication
public class JavaScriptUserInterface extends UserInterface {
	private byte[][] board = new byte[8][8];
	private int[] selectedSquare = null;
	private boolean handlingButton = false;
	private static final String ILLEGAL = "{\"status\":405,\"message\":\"Illegal move\"}";
	private static final String DEFAULT = "[\"Resign\",\"Offer Draw\"]";
	private static final String YES_NO = "[\"Yes\",\"No\"]";
	private static final String PROMOTION = "[\"Queen\",\"Rook\",\"Knight\",\"Bishop\"";

	@Override
	protected void updateBoard(byte[][] board) {
		this.board = board;
	}

	@Override
	protected void display() {
		// do nothing
	}

	private static JavaScriptUserInterface getUI(HttpSession session) {
		JavaScriptUserInterface ui = (JavaScriptUserInterface) session.getAttribute("ui");
		if (ui == null) {
			ui = new JavaScriptUserInterface();
			session.setAttribute("ui", ui);
		}
		return ui;
	}

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
		StringBuilder boardStr = new StringBuilder();
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {
				char c = ui.board[i][j] == 0 ? '0' : (char) ui.board[i][j];
				boardStr.append(c);
			}
		}
		StringBuilder movesStr = new StringBuilder();
		for (int i = 0; i < 64; i++) {
			movesStr.append('0');
		}
		if (ui.selectedSquare != null) {
			List<int[]> moves = ui.mediator.getLegalMoves(ui.selectedSquare[0],
					ui.selectedSquare[1]);
			moves.add(new int[] { ui.selectedSquare[0], ui.selectedSquare[1] });
			for (int[] move : moves) {
				movesStr.setCharAt(8 * move[0] + move[1], 'X');
			}
		}
		String buttons;
		if (ui.isPromotion) {
			buttons = PROMOTION;
		} else if (ui.isGameOver || ui.handlingButton) {
			buttons = YES_NO;
		} else {
			buttons = DEFAULT;
		}
		return "{\"status\":200,\"board\":\"" + boardStr.toString() + "\",\"moves\":\""
				+ movesStr.toString() + "\",\"message1\":" + ui.messageLine1 + "\",\"message2\":"
				+ ui.messageLine2 + "\",\"buttons\":" + buttons + "}";
	}

	public static void main(String[] args) {
		SpringApplication.run(JavaScriptUserInterface.class, args);
	}

}
