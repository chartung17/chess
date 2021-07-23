package ui;

import chess.GameData;
import main.Mediator;

/**
 * This class is a user interface.
 */
public abstract class UserInterface {
	protected Mediator mediator;
	protected String messageLine1;
	protected String messageLine2;
	protected boolean isPromotion = false; // true only if user is promoting a pawn
	protected boolean blackToMove, isGameOver;

	/**
	 * This method creates a user interface.
	 */
	protected UserInterface() {
		mediator = new Mediator(this);
		initializeGame();
	}

	/**
	 * This method starts a new Game.
	 */
	protected void initializeGame() {
		mediator.initializeGame();
	}

	/**
	 * This method refreshes the user interface to display updated game data.
	 * 
	 * @param data the data to be displayed
	 */
	public void update(GameData data) {
		if (data.getMessageLine1() != null)
			messageLine1 = data.getMessageLine1();
		if (data.getMessageLine2() != null)
			messageLine2 = data.getMessageLine2();
		isPromotion = data.isPromotion();
		blackToMove = data.blackToMove();
		isGameOver = data.isGameOver();
		updateBoard(data.getBoard());
		display();
	}

	/**
	 * This method updates the user interface's internal representation of the
	 * board.
	 * 
	 * @param board the current state of the board
	 */
	protected abstract void updateBoard(byte[][] board);

	/**
	 * This method displays game data to the user.
	 */
	protected abstract void display();

	/**
	 * This method starts the user interface.
	 */
	public void start() {
		mediator.start();
	}
}
