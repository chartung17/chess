package test;

import main.Mediator;
import ui.UserInterface;

/**
 * This class is used in place of a UserInterface for testing purposes.
 */
public class UserInterfaceTester extends UserInterface {
	protected byte[][] board;

	@Override
	protected void updateBoard(byte[][] board) {
		this.board = board;
	}

	@Override
	protected void display() {
		// do nothing
	}

	/**
	 * @return the board
	 */
	public byte[][] getBoard() {
		return board;
	}

	/**
	 * @return the mediator
	 */
	public Mediator getMediator() {
		return mediator;
	}

	/**
	 * @return the message
	 */
	public String getMessage() {
		return messageLine1 + '\n' + messageLine2;
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
