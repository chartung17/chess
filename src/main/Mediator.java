package main;

import java.util.List;

import chess.AbstractGame;
import chess.Game;
import chess.GameData;
import ui.GUI;
import ui.UserInterface;

/**
 * This class is used to communicate between a UserInterface and an
 * AbstractGame. It is also used by the components of a GUI to communicate with
 * the GUI.
 * 
 */
public class Mediator {
	private UserInterface ui;
	private AbstractGame game;

	// constants for use with handleSelectedOption()
	public static final int RESIGN = 1;
	public static final int DRAW = 2;
	public static final int QUEEN = 5;
	public static final int ROOK = 6;
	public static final int KNIGHT = 7;
	public static final int BISHOP = 8;

	/**
	 * This method creates a Mediator for the given UserInterface.
	 * 
	 * @param ui the UserInterface to use
	 */
	public Mediator(UserInterface ui) {
		this.ui = ui;
	}

	/**
	 * @return the ui
	 */
	public UserInterface getUi() {
		return ui;
	}

	/**
	 * @return the game
	 */
	public AbstractGame getGame() {
		return game;
	}

	/**
	 * This method starts a new game.
	 */
	public void initializeGame() {
		this.game = new Game(this);
	}

	/**
	 * This method is run when the user selects a square on the chessboard.
	 * 
	 * @param row the row of the selected square
	 * @param col the col of the selected square
	 * @return one of the following constants: Board.SELECT for successfully
	 *         selecting a piece, Board.PROMOTE for a successful move resulting in a
	 *         pawn needing to be promoted, Board.CAPTURE for a move resulting in a
	 *         piece being captured, Board.PAWN_MOVE for any other successful move
	 *         of a pawn, Board.MOVE for any other successful move, or Board.FAILURE
	 *         if unsuccessful
	 */
	public int handleSelectedSquare(int row, int col) {
		return game.handleSelectedSquare(row, col);
	}

	/**
	 * This method is run when the user selects an option.
	 * 
	 * @param option an integer indicating which option the user selected,
	 *               corresponding to one of the option constants
	 */
	public void handleSelectedOption(int option) {
		game.handleSelectedOption(option);
	}

	/**
	 * This method refreshes the user interface to display updated game data.
	 * 
	 * @param data the data to be displayed
	 */
	public void updateUI(GameData data) {
		ui.update(data);
	}

	/**
	 * This method starts the game.
	 */
	public void start() {
		game.start();
	}

	/**
	 * This method returns a list of moves the piece at the specified location can
	 * legally make; if the specified location is empty or if the piece is not the
	 * current player's color, this method returns an empty list. Each move is
	 * represented as a two-element array, where the first element indicates the row
	 * and the second element indicates the col of the piece's position at the end
	 * of the move.
	 * 
	 * @param row the row of the selected piece
	 * @param col the col of the selected piece
	 * @return a list of legal moves, each represented as a two-element array
	 *         containing the row and col of the piece's position at the end of the
	 *         move
	 */
	public List<int[]> getLegalMoves(int row, int col) {
		return game.getLegalMoves(row, col);
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
		if (ui instanceof GUI) {
			((GUI) ui).handleButton(option);
		}
	}
}
