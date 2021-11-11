package chess.piece;

import java.util.List;

/**
 * This class represents a rook.
 */
public class Rook extends ChessPiece {
	// the first time the rook moves, the move method will return the value of
	// firstMoveRetVal
	private boolean hasMoved = false;
	private int firstMoveRetVal;

	/**
	 * This method creates a rook with the given location and color.
	 * 
	 * @param row     the row on which the piece is located
	 * @param col     the col on which the piece is located
	 * @param isBlack true if the piece is black and false if white
	 */
	public Rook(int row, int col, boolean isBlack) {
		super(row, col, isBlack);
		// set firstMoveRetVal to -3 for the kingside rook, -4 for the queenside rook,
		// and 1 for a rook created by promotion
		if ((isBlack && (row == 0)) || (!isBlack && (row == 7))) {
			this.firstMoveRetVal = (col == 7) ? K_ROOK : Q_ROOK;
		} else {
			this.firstMoveRetVal = 1;
		}
	}

	@Override
	protected int move(int row, int col, byte[][] board, boolean isOccupied) {
		// any valid move for a rook is a straight move
		// if the move is valid, execute the move and return 1
		if (checkStraightMove(row, col, board)) {
			this.row = row;
			this.col = col;
			// check whether the rook has moved before, and return 1 if it has
			if (hasMoved) {
				return 1;
			}
			hasMoved = true;
			// if this is the rook's first move, return firstMoveRetVal
			return this.firstMoveRetVal;
		}
		// if the move is invalid, return 0
		return 0;
	}

	@Override
	public byte toByte() {
		return (byte) (isBlack ? 'R' : 'r');
	}

	@Override
	public String pieceType() {
		return "Rook";
	}

	@Override
	public List<int[]> legalMoves(byte[][] board) {
		return legalStraightMoves(board);
	}

}
