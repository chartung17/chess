package chess.piece;

import java.util.ArrayList;
import java.util.List;

/**
 * This class represents a pawn.
 */
public class Pawn extends ChessPiece {
	private int direction;

	/**
	 * This method creates a pawn with the given location and color.
	 * 
	 * @param row     the row on which the piece is located
	 * @param col     the col on which the piece is located
	 * @param isBlack true if the piece is black and false if white
	 */
	public Pawn(int row, int col, boolean isBlack) {
		super(row, col, isBlack);
		// vertical movement is in the positive direction for black pawns and the
		// negative direction
		// for white pawns
		this.direction = isBlack ? 1 : -1;
	}

	@Override
	protected int move(int row, int col, byte[][] board, boolean isOccupied) {
		if (isOccupied) {
			// capturing must be done diagonally
			if ((row - this.row == this.direction) && (Math.abs(col - this.col) == 1)) {
				this.row = row;
				this.col = col;
				// if the move ends with the pawn in the top or bottom row, it needs to be
				// promoted,
				// otherwise it is a normal move
				return ((row == 0) || (row == 7)) ? PROMOTE : 1;
			} else {
				return 0;
			}
		}
		// moving one space forward to an empty square is valid
		if ((row - this.row == this.direction) && (col == this.col)) {
			this.row = row;
			this.col = col;
			// if the move ends with the pawn in the top or bottom row, it needs to be
			// promoted,
			// otherwise it is a normal move
			return ((row == 0) || (row == 7)) ? PROMOTE : 1;
		}
		// moving two spaces forward from starting position to an empty square is valid
		// if the square in between is empty
		if ((row - this.row == 2 * this.direction) && (col == this.col)
				&& ((this.isBlack && (this.row == 1)) || (!this.isBlack && (this.row == 6)))
				&& (board[this.row + direction][this.col] == 0)) {
			this.row = row;
			this.col = col;
			return PAWN_MOVE_2;
		}
		// check if the move may be en passant
		if ((row - this.row == this.direction) && (Math.abs(col - this.col) == 1)
				&& ((this.isBlack && (row == 5)) || (!this.isBlack && (row == 2)))) {
			return EN_PASSANT;
		}
		// otherwise the move is invalid
		return 0;
	}

	@Override
	public byte toByte() {
		return (byte) (isBlack ? 'P' : 'p');
	}

	@Override
	public String pieceType() {
		return "Pawn";
	}

	@Override
	public List<int[]> legalMoves(byte[][] board) {
		List<int[]> moves = new ArrayList<>();
		if (isInBounds(row + direction, col)) {
			char target = (char) board[row + direction][col];
			if (target == 0) {
				int[] move = { row + direction, col };
				moves.add(move);
			}
		}
		if (isInBounds(row + direction, col + 1)) {
			char target = (char) board[row + direction][col + 1];
			if ((target != 0) && (Character.isLowerCase(target) == isBlack)) {
				int[] move = { row + direction, col + 1 };
				moves.add(move);
			}
		}
		if (isInBounds(row + direction, col - 1)) {
			char target = (char) board[row + direction][col - 1];
			if ((target != 0) && (Character.isLowerCase(target) == isBlack)) {
				int[] move = { row + direction, col - 1 };
				moves.add(move);
			}
		}
		if ((isBlack && row == 1) || (!isBlack && row == 6)) {
			if ((board[row + (2 * direction)][col] == 0) && (board[row + direction][col] == 0)) {
				int[] move = { row + (2 * direction), col };
				moves.add(move);
			}
		}
		return moves;
	}

}
