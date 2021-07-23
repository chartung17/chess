package chess.piece;

import java.util.ArrayList;
import java.util.List;

/**
 * This class represents a knight.
 */
public class Knight extends ChessPiece {

	/**
	 * This method creates a knight with the given location and color.
	 * 
	 * @param row     the row on which the piece is located
	 * @param col     the col on which the piece is located
	 * @param isBlack true if the piece is black and false if white
	 */
	public Knight(int row, int col, boolean isBlack) {
		super(row, col, isBlack);
	}

	@Override
	protected int move(int row, int col, byte[][] board, boolean isOccupied) {
		// any move either one square horizontally and two vertically or one square
		// vertically and two horizontally is valid
		if ((row != this.row) && (col != this.col) && ((Math.abs(row - this.row) + Math.abs(col - this.col)) == 3)) {
			this.row = row;
			this.col = col;
			return 1;
		}
		// if the move is invalid, return false
		return 0;
	}

	@Override
	public byte toByte() {
		return (byte) (isBlack ? 'N' : 'n');
	}

	@Override
	public String pieceType() {
		return "Knight";
	}

	@Override
	public List<int[]> legalMoves(byte[][] board) {
		List<int[]> moves = new ArrayList<>();
		int[][] deltas = { { 1, 2 }, { 1, -2 }, { -1, 2 }, { -1, -2 }, { 2, 1 }, { 2, -1 }, { -2, 1 }, { -2, -1 } };
		for (int[] delta : deltas) {
			if (!isInBounds(row + delta[0], col + delta[1]))
				continue;
			char target = (char) board[row + delta[0]][col + delta[1]];
			if ((target == 0) || (Character.isLowerCase(target) == isBlack)) {
				int[] move = { row + delta[0], col + delta[1] };
				moves.add(move);
			}
		}
		return moves;
	}

}
