package chess.piece;

import java.util.List;

/**
 * This class represents a queen.
 */
public class Queen extends ChessPiece {

	/**
	 * This method creates a queen with the given location and color.
	 * 
	 * @param row     the row on which the piece is located
	 * @param col     the col on which the piece is located
	 * @param isBlack true if the piece is black and false if white
	 */
	public Queen(int row, int col, boolean isBlack) {
		super(row, col, isBlack);
	}

	@Override
	protected int move(int row, int col, byte[][] board, boolean isOccupied) {
		// any valid move for a queen is a straight or diagonal move
		// if the move is valid, execute the move and return 1
		if ((checkStraightMove(row, col, board)) || (checkDiagonalMove(row, col, board))) {
			this.row = row;
			this.col = col;
			return 1;
		}
		// if the move is invalid, return 0
		return 0;
	}

	@Override
	public byte toByte() {
		return (byte) (isBlack ? 'Q' : 'q');
	}

	@Override
	public String pieceType() {
		return "Queen";
	}

	@Override
	public List<int[]> legalMoves(byte[][] board) {
		List<int[]> moves = legalDiagonalMoves(board);
		moves.addAll(legalStraightMoves(board));
		return moves;
	}

}
