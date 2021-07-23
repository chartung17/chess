package chess;

import java.util.List;

import main.Mediator;

/**
 * This class represents a game or a replay of a game that can be displayed on
 * the chess user interface.
 */
public abstract class AbstractGame {
	protected Board board;
	protected Mediator mediator;
	protected boolean blackToMove;
	protected boolean isPromotion = false;

	/**
	 * This method initializes a game with the given mediator.
	 * 
	 * @param mediator the mediator needed to communicate with the UserInterface
	 */
	public AbstractGame(Mediator mediator) {
		this.mediator = mediator;
	}

	/**
	 * This method refreshes the user interface to display updated game data.
	 * 
	 * @param data the data to be displayed
	 */
	protected void updateUI(GameData data) {
		mediator.updateUI(data);
	}

	/**
	 * This method generates a GameData object with the specified values.
	 * 
	 * @param messageLine1 the first line of the message to be displayed to the user
	 * @param messageLine2 the second line of the message to be displayed to the
	 *                     user
	 * @param isPromotion  true if a pawn is being promoted and the user needs to
	 *                     select which piece to promote it to or false otherwise
	 * @param isGameOver   true if the game is over and false otherwise
	 * @return a GameData object with the specified values and the current board
	 *         state
	 */
	protected GameData generateData(String messageLine1, String messageLine2, boolean isPromotion, boolean isGameOver) {
		return new GameData(messageLine1, messageLine2, board.toByteArray(), isPromotion, blackToMove, isGameOver);
	}

	/**
	 * This method generates a GameData object with the specified values.
	 * 
	 * @param messageLine1 the first line of the message to be displayed to the user
	 * @param messageLine2 the second line of the message to be displayed to the
	 *                     user
	 * @param isPromotion  true if a pawn is being promoted and the user needs to
	 *                     select which piece to promote it to or false otherwise
	 * @return a GameData object with the specified values and the current board
	 *         state
	 */
	protected GameData generateData(String messageLine1, String messageLine2, boolean isPromotion) {
		return new GameData(messageLine1, messageLine2, board.toByteArray(), isPromotion, blackToMove, false);
	}

	/**
	 * This method generates a GameData object with the specified values.
	 * 
	 * @param messageLine1 the first line of the message to be displayed to the user
	 * @param messageLine2 the second line of the message to be displayed to the
	 *                     user
	 * @return a GameData object with the specified values and the current board
	 *         state, with isPromotion set to false
	 */
	protected GameData generateData(String messageLine1, String messageLine2) {
		return new GameData(messageLine1, messageLine2, board.toByteArray(), isPromotion, blackToMove, false);
	}

	/**
	 * This method generates a GameData object with the specified values.
	 * 
	 * @param message the single-line message to be displayed to the user
	 * @return a GameData object with the specified values and the current board
	 *         state, with isPromotion set to false
	 */
	protected GameData generateData(String message) {
		return new GameData("", message, board.toByteArray(), isPromotion, blackToMove, false);
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
	public abstract int handleSelectedSquare(int row, int col);

	/**
	 * This method is run when the user selects an option.
	 * 
	 * @param option an integer indicating which option the user selected,
	 *               corresponding to one of the option constants defined in the
	 *               Mediator class
	 */
	public abstract void handleSelectedOption(int option);

	/**
	 * This method starts the game.
	 */
	public abstract void start();

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
	public abstract List<int[]> getLegalMoves(int row, int col);
}
