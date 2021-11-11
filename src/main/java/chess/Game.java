package chess;

import java.util.HashMap;
import java.util.List;

import main.Mediator;

/**
 * This class represents a game of chess.
 */
public class Game extends AbstractGame {
	private HashMap<BoardState, Integer> boardStates = new HashMap<>();
	private boolean gameOverByThreefoldRepetition = false;
	private int moveCount = 0; // counts the number of moves since the last capture or pawn move

	/**
	 * This method creates a game with the specified mediator.
	 * 
	 * @param mediator the mediator needed to communicate with the UserInterface
	 */
	public Game(Mediator mediator) {
		super(mediator);
		board = new Board();
		blackToMove = false;
		board.getBoardState(blackToMove).insertInto(boardStates);
	}

	@Override
	public int handleSelectedSquare(int row, int col) {
		// send an error message if a square is clicked while waiting for a pawn to be
		// promoted
		if (isPromotion) {
			mediator.updateUI(generateData("Error: you must promote your pawn first", null));
			return Board.FAILURE;
		}

		// otherwise let the board handle it
		int result = board.handleSelectedSquare(row, col, blackToMove);

		// update the UserInterface based on the result of the board's action
		switch (result) {
		case Board.FAILURE:
			mediator.updateUI(generateData("Error: invalid move",
					(blackToMove ? "Black" : "White") + ", please select a piece to move."));
			break;
		case Board.CAPTURE:
		case Board.PAWN_MOVE:
			moveCount = -1; // execution will continue into the Board.MOVE case and update to 0
		case Board.MOVE:
			moveCount++;
			blackToMove = !blackToMove;
			if (board.getBoardState(blackToMove).insertInto(boardStates) >= 3)
				gameOverByThreefoldRepetition = true;
			if (!gameOver())
				mediator.updateUI(generateData(
						(blackToMove ? "Black" : "White") + ", please select a piece to move."));
			break;
		case Board.SELECT:
			String pieceType = board.getPieceType(row, col);
			String squareName = board.getSquareName(row, col);
			mediator.updateUI(generateData(
					"You selected the " + pieceType + " on square " + squareName,
					(blackToMove ? "Black" : "White") + ", please select a square to move to."));
			break;
		case Board.PROMOTE:
			moveCount = 0;
			isPromotion = true;
			mediator.updateUI(generateData("Congratulations! Your pawn is being promoted.",
					(blackToMove ? "Black" : "White")
							+ ", please select a piece to promote your pawn to."));
		}
		return result;
	}

	@Override
	public void handleSelectedOption(int option) {
		if (isPromotion) {
			char optionChar = 0;
			switch (option) {
			case Mediator.QUEEN:
				optionChar = 'q';
				break;
			case Mediator.ROOK:
				optionChar = 'r';
				break;
			case Mediator.KNIGHT:
				optionChar = 'n';
				break;
			case Mediator.BISHOP:
				optionChar = 'b';
				break;
			default:
				mediator.updateUI(
						generateData("Error: invalid selection. Please try again.", null));
				return;
			}
			board.promote(optionChar);
			blackToMove = !blackToMove;
			if (board.getBoardState(blackToMove).insertInto(boardStates) >= 3)
				gameOverByThreefoldRepetition = true;
			isPromotion = false;
			if (!gameOver())
				mediator.updateUI(generateData(
						(blackToMove ? "Black" : "White") + ", please select a piece to move."));
		} else {
			switch (option) {
			case Mediator.DRAW:
				mediator.updateUI(
						generateData("Game Over!", "The game ended in a draw.", false, true));
				break;
			case Mediator.RESIGN:
				mediator.updateUI(generateData("Game Over!",
						(blackToMove ? "Black" : "White") + " has resigned.", false, true));
				break;
			default:
				mediator.updateUI(
						generateData("Error: the selected option is not yet supported.", null));
			}
		}
	}

	@Override
	public void start() {
		updateUI(generateData("New Game", "White: Select a piece to move"));
	}

	/**
	 * This method checks whether the game has ended by checkmate, stalemate, or a forced draw.
	 * Then, if the game is over, this method updates the UserInterface and returns true, otherwise
	 * it returns false
	 * 
	 * @return true if the game is over and false otherwise
	 */
	protected boolean gameOver() {
		String message;
		if (board.anyLegalMoves(blackToMove)) {
			if (gameOverByThreefoldRepetition) {
				message = "The game ended in a draw by the threefold repetition rule.";
			} else if (moveCount >= 100) {
				message = "The game ended in a draw by the fifty-move rule.";
			} else {
				return false;
			}
		} else if (board.isCheck(blackToMove)) {
			message = (blackToMove ? "White" : "Black") + " wins by checkmate.";
		} else {
			message = "The game ended in a stalemate.";
		}
		mediator.updateUI(generateData("Game Over!", message, false, true));
		return true;
	}

	@Override
	public List<int[]> getLegalMoves(int row, int col) {
		return board.getLegalMoves(row, col, blackToMove);
	}

}
