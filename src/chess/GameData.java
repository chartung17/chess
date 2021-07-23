package chess;

/**
 * This class holds all of the game state data needed by the UserInterface.
 */
public class GameData {
	private String messageLine1, messageLine2;
	private byte[][] board;
	private boolean isPromotion, blackToMove, isGameOver;

	/**
	 * This method creates a GameData object with the specified parameters.
	 * 
	 * @param messageLine1 the first line of the message displayed to the user
	 * @param messageLine2 the second line of the message displayed to the user
	 * @param board        an 8x8 matrix representing the current state of the board
	 * @param isPromotion  true if a pawn is being promoted and the user needs to
	 *                     select which piece to promote it to or false otherwise
	 * @param blackToMove  true is it is black's turn to move and false if it is
	 *                     white's turn
	 * @param isGameOver   true if the game is over and false otherwise
	 */
	public GameData(String messageLine1, String messageLine2, byte[][] board, boolean isPromotion, boolean blackToMove,
			boolean isGameOver) {
		this.messageLine1 = messageLine1;
		this.messageLine2 = messageLine2;
		this.board = board;
		this.isPromotion = isPromotion;
		this.blackToMove = blackToMove;
		this.isGameOver = isGameOver;
	}

	/**
	 * @return the messageLine1
	 */
	public String getMessageLine1() {
		return messageLine1;
	}

	/**
	 * @return the messageLine2
	 */
	public String getMessageLine2() {
		return messageLine2;
	}

	/**
	 * @return the board
	 */
	public byte[][] getBoard() {
		return board;
	}

	/**
	 * @return isPromotion
	 */
	public boolean isPromotion() {
		return isPromotion;
	}

	/**
	 * @return blackToMove
	 */
	public boolean blackToMove() {
		return blackToMove;
	}

	/**
	 * @return isGameOver
	 */
	public boolean isGameOver() {
		return isGameOver;
	}
}
