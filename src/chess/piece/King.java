package chess.piece;

import java.util.ArrayList;
import java.util.List;

/**
 * This class represents a king.
 */
public class King extends ChessPiece {
	private boolean canCastleKingside = true;
	private boolean canCastleQueenside = true;

	/**
	 * This method creates a king with the given location and color.
	 * 
	 * @param row     the row on which the piece is located
	 * @param col     the col on which the piece is located
	 * @param isBlack true if the piece is black and false if white
	 */
	public King(int row, int col, boolean isBlack) {
		super(row, col, isBlack);
	}

	/**
	 * @return canCastleKingside
	 */
	public boolean canCastleKingside() {
		return canCastleKingside;
	}

	/**
	 * @return canCastleQueenside
	 */
	public boolean canCastleQueenside() {
		return canCastleQueenside;
	}

	/**
	 * This method indicates that the king cannot castle on the specified side.
	 * 
	 * @param side 'q' for Queenside, 'k' for Kingside, or 'b' for both
	 */
	public void markCastle(char side) {
		if (side == 'q') {
			this.canCastleQueenside = false;
		} else if (side == 'k') {
			this.canCastleKingside = false;
		} else if (side == 'b') {
			this.canCastleKingside = false;
			this.canCastleQueenside = false;
		}
	}

	/**
	 * This method indicates that the king cannot castle on either side.
	 */
	public void markCastle() {
		markCastle('b');
	}

	@Override
	protected int move(int row, int col, byte[][] board, boolean isOccupied) {
		// any move at most one square horizontally and at most one vertically is valid
		if ((Math.abs(row - this.row) <= 1) && (Math.abs(col - this.col) <= 1)) {
			this.row = row;
			this.col = col;
			// the king can no longer castle after any valid move
			markCastle();
			return 1;
		}
		// check if the move may be castling
		if ((this.canCastleKingside && (row == this.row) && (col == this.col + 2))
				|| (this.canCastleQueenside && (row == this.row) && (col == this.col - 2))) {
			return CASTLE;
		}
		// if the move is invalid, return false
		return 0;
	}

	@Override
	public void setPos(int row, int col) {
		this.row = row;
		this.col = col;
		markCastle();
	}

	@Override
	public byte toByte() {
		return (byte) (isBlack ? 'K' : 'k');
	}

	@Override
	public String pieceType() {
		return "King";
	}

	@Override
	public List<int[]> legalMoves(byte[][] board) {
		List<int[]> moves = new ArrayList<>();
		for (int i = -1; i <= 1; i++) {
			for (int j = -1; j <= 1; j++) {
				if (((i != 0) || (j != 0)) && isInBounds(row + i, col + j)) {
					char target = (char) board[row + i][col + j];
					if ((target == 0) || (Character.isLowerCase(target) == isBlack)) {
						int[] move = { row + i, col + j };
						moves.add(move);
					}
				}
			}
		}
		return moves;
	}

}
