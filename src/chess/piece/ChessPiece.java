package chess.piece;

import java.util.ArrayList;
import java.util.List;

/**
 * This class represents a chess piece.
 */
public abstract class ChessPiece {
	protected int row, col;
	protected boolean isBlack;

	// move constants
	public static final int EN_PASSANT = -1; // indicates that the move may be en passant
	public static final int CASTLE = -2; // indicates that the move may be castling
	public static final int K_ROOK = -3; // indicates the first move of the kingside rook
	public static final int Q_ROOK = -4; // indicates the first move of the queenside rook
	public static final int PAWN_MOVE_2 = -5; // indicates that a pawn moved two spaces
	public static final int PROMOTE = -6; // indicates that a pawn needs to be promoted

	/**
	 * This method creates a chess piece with the given location and color.
	 * 
	 * @param row     the row on which the piece is located
	 * @param col     the col on which the piece is located
	 * @param isBlack true if the piece is black and false if white
	 */
	protected ChessPiece(int row, int col, boolean isBlack) {
		this.row = row;
		this.col = col;
		this.isBlack = isBlack;
	}

	/**
	 * @return the row
	 */
	public int getRow() {
		return row;
	}

	/**
	 * @return the col
	 */
	public int getCol() {
		return col;
	}

	/**
	 * @return isBlack
	 */
	public boolean isBlack() {
		return isBlack;
	}

	/**
	 * @param row the row to set
	 * @param col the col to set
	 */
	public void setPos(int row, int col) {
		this.row = row;
		this.col = col;
	}

	/**
	 * This method is used to check whether the specified square can be reached by a
	 * valid vertical or horizontal move.
	 * 
	 * @param row   the row to which this piece is attempting to move
	 * @param col   the col to which this piece is attempting to move
	 * @param board an 8x8 matrix with a nonzero value in each occupied square and
	 *              zero in each unoccupied square
	 * @return true if the move is legal and false if illegal
	 */
	protected boolean checkStraightMove(int row, int col, byte[][] board) {
		// any move which changes both the row and the column is not a valid straight
		// move
		if ((row != this.row) && (col != this.col)) {
			return false;
		}

		// if there are any occupied squares between the current square and the target
		// square, the
		// move is invalid
		if (row != this.row) {
			int start = Math.min(row, this.row) + 1;
			int stop = Math.max(row, this.row);
			for (int i = start; i < stop; i++) {
				if (board[i][col] != 0) {
					return false;
				}
			}
		} else {
			int start = Math.min(col, this.col) + 1;
			int stop = Math.max(col, this.col);
			for (int i = start; i < stop; i++) {
				if (board[row][i] != 0) {
					return false;
				}
			}
		}
		// if this point is reached, the move is valid
		return true;
	}

	/**
	 * This method is used to check whether the specified square can be reached by a
	 * valid diagonal move.
	 * 
	 * @param row   the row to which this piece is attempting to move
	 * @param col   the col to which this piece is attempting to move
	 * @param board an 8x8 matrix with a nonzero value in each occupied square and
	 *              zero in each unoccupied square
	 * @return true if the move is legal and false if illegal
	 */
	protected boolean checkDiagonalMove(int row, int col, byte[][] board) {
		// a valid diagonal move must change the row and column by the same amount
		if (Math.abs(row - this.row) != Math.abs(col - this.col)) {
			return false;
		}

		// if there are any occupied squares between the current square and the target
		// square, the
		// move is invalid
		int rowDelta = Integer.signum(row - this.row);
		int colDelta = Integer.signum(col - this.col);
		int x = this.row + rowDelta;
		int y = this.col + colDelta;
		while (x != row) {
			if (board[x][y] != 0) {
				return false;
			}
			x += rowDelta;
			y += colDelta;
		}

		// if this point is reached, the move is valid
		return true;
	}

	/**
	 * This method is used to move to the specified square. If the square is
	 * occupied, this method also captures the target piece. The board must verify
	 * that the given move would not put or leave the player's own king in check
	 * before calling this method.
	 * 
	 * @param row        the row to which this piece is attempting to move
	 * @param col        the col to which this piece is attempting to move
	 * @param board      an 8x8 matrix with a nonzero value in each occupied square
	 *                   and zero in each unoccupied square
	 * @param isOccupied true if the target square contains a piece of the opposite
	 *                   color or false if it is empty
	 * @return 1 if the move is successful, 0 if unsuccessful, or one of the
	 *         following constants if additional action is needed: EN_PASSANT if the
	 *         move may be en passant, CASTLE if the move may be castling, K_ROOK if
	 *         the move is the first move by the kingside rook, Q_ROOK if the move
	 *         is the first move by the queenside rook, PAWN_MOVE_2 for a pawn
	 *         moving two spaces, or PROMOTE for a pawn needing to be promoted
	 */
	protected abstract int move(int row, int col, byte[][] board, boolean isOccupied);

	/**
	 * This method returns a list of moves this piece can legally make. Each move is
	 * represented as a two-element array, where the first element indicates the row
	 * and the second element indicates the col of the piece's position at the end
	 * of the move. Castling and en passant are not included in this list. This
	 * method does not consider check, so the resulting list may include moves that
	 * are actually illegal because they put or leave the player's king in check.
	 * 
	 * @param board an 8x8 matrix representing the current state of the board, with
	 *              empty squares represented by zeros and occupied squares
	 *              represented by the return value of the occupying piece's
	 *              toByte() method
	 * @return a list of legal moves, each represented as a two-element array
	 *         containing the row and col of the piece's position at the end of the
	 *         move
	 */
	public abstract List<int[]> legalMoves(byte[][] board);

	/**
	 * This method returns a list of moves in the specified directions that can be
	 * legal made from this piece's position. Each move is represented as a
	 * two-element array, where the first element indicates the row and the second
	 * element indicates the col of the piece's position at the end of the move.
	 * This method does not consider check, so the resulting list may include moves
	 * that are actually illegal because they put or leave the player's king in
	 * check.
	 * 
	 * @param board      an 8x8 matrix representing the current state of the board,
	 *                   with empty squares represented by zeros and occupied
	 *                   squares represented by the return value of the occupying
	 *                   piece's toByte() method
	 * @param directions an array of two-element int arrays; each element in an
	 *                   inner array is either 0, 1, or -1 (at least one must be
	 *                   nonzero), with the first element representing the row
	 *                   direction and the second representing the col direction
	 * @return a list of legal moves in the specified direction, each represented as
	 *         a two-element array containing the row and col of the piece's
	 *         position at the end of the move
	 */
	private List<int[]> legalDirectionalMoves(byte[][] board, int[][] directions) {
		List<int[]> moves = new ArrayList<>();
		char target;
		for (int[] direction : directions) {
			for (int i = 1; i < 8; i++) {
				if (!isInBounds(row + (i * direction[0]), col + (i * direction[1])))
					break;
				target = (char) board[row + (i * direction[0])][col + (i * direction[1])];
				if ((target == 0) || (Character.isLowerCase(target) == isBlack)) {
					int[] move = { row + (i * direction[0]), col + (i * direction[1]) };
					moves.add(move);
				}
				if (target != 0)
					break;
			}
		}
		return moves;
	}

	/**
	 * This method returns a list of horizontal and vertical moves that can be legal
	 * made from this piece's position. Each move is represented as a two-element
	 * array, where the first element indicates the row and the second element
	 * indicates the col of the piece's position at the end of the move. This method
	 * does not consider check, so the resulting list may include moves that are
	 * actually illegal because they put or leave the player's king in check.
	 * 
	 * @param board an 8x8 matrix representing the current state of the board, with
	 *              empty squares represented by zeros and occupied squares
	 *              represented by the return value of the occupying piece's
	 *              toByte() method
	 * @return a list of legal horizontal and vertical moves, each represented as a
	 *         two-element array containing the row and col of the piece's position
	 *         at the end of the move
	 */
	protected List<int[]> legalStraightMoves(byte[][] board) {
		int[][] directions = { { 1, 0 }, { -1, 0 }, { 0, 1 }, { 0, -1 } };
		return legalDirectionalMoves(board, directions);
	}

	/**
	 * This method returns a list of diagonal moves that can be legal made from this
	 * piece's position. Each move is represented as a two-element array, where the
	 * first element indicates the row and the second element indicates the col of
	 * the piece's position at the end of the move. This method does not consider
	 * check, so the resulting list may include moves that are actually illegal
	 * because they put or leave the player's king in check.
	 * 
	 * @param board an 8x8 matrix representing the current state of the board, with
	 *              empty squares represented by zeros and occupied squares
	 *              represented by the return value of the occupying piece's
	 *              toByte() method
	 * @return a list of legal diagonal moves, each represented as a two-element
	 *         array containing the row and col of the piece's position at the end
	 *         of the move
	 */
	protected List<int[]> legalDiagonalMoves(byte[][] board) {
		int[][] directions = { { 1, 1 }, { 1, -1 }, { -1, 1 }, { -1, -1 } };
		return legalDirectionalMoves(board, directions);
	}

	/**
	 * This method is used to move to the specified square. The board must verify
	 * that the given move would not put or leave the player's own king in check
	 * before calling this method.
	 * 
	 * @param row   the row to which this piece is attempting to move
	 * @param col   the col to which this piece is attempting to move
	 * @param board an 8x8 matrix with a nonzero value in each occupied square and
	 *              zero in each unoccupied square
	 * @return 1 if the move is successful, 0 if unsuccessful, or one of the
	 *         following constants if additional action is needed: EN_PASSANT if the
	 *         move may be en passant, CASTLE if the move may be castling, K_ROOK if
	 *         the move is the first move by the kingside rook, Q_ROOK if the move
	 *         is the first move by the queenside rook, PAWN_MOVE_2 for a pawn
	 *         moving two spaces, or PROMOTE for a pawn needing to be promoted
	 */
	public int move(int row, int col, byte[][] board) {
		// any move which changes neither the row nor the column is invalid
		if ((row == this.row) && (col == this.col)) {
			return 0;
		}
		// otherwise pass info to subclass for evaluation
		return move(row, col, board, false);
	}

	/**
	 * This method is used to capture the specified target piece. The board must
	 * verify that the given move would not put or leave the player's own king in
	 * check before calling this method.
	 * 
	 * @param target the piece which this piece is attempting to capture
	 * @param board  an 8x8 matrix with a nonzero value in each occupied square and
	 *               zero in each unoccupied square
	 * @return 1 if the move is successful, 0 if unsuccessful, or one of the
	 *         following constants if additional action is needed: EN_PASSANT if the
	 *         move may be en passant, CASTLE if the move may be castling, K_ROOK if
	 *         the move is the first move by the kingside rook, Q_ROOK if the move
	 *         is the first move by the queenside rook, PAWN_MOVE_2 for a pawn
	 *         moving two spaces, or PROMOTE for a pawn needing to be promoted
	 */
	public int capture(ChessPiece target, byte[][] board) {
		// any move which captures a piece of the same color is invalid
		if (this.isBlack == target.isBlack) {
			return 0;
		}
		// otherwise pass info to subclass for evaluation
		return move(target.row, target.col, board, true);
	}

	/**
	 * This method represents the piece as a byte. Empty squares are represented by
	 * zeros. Occupied squares are represented by the ASCII value of a letter
	 * corresponding to the piece, with capital letters for black pieces and
	 * lowercase letters for white pieces. The letters representing each piece are
	 * as follows: 'K' or 'k' for kings, 'Q' or 'q' for queens, 'R' or 'r' for
	 * rooks, 'N' or 'n' for knights, 'B' or 'b' for bishops, and 'P' or 'p' for
	 * pawns.
	 * 
	 * @return a byte representing the piece
	 */
	public abstract byte toByte();

	/**
	 * This method returns a String representing the type of chess piece.
	 * 
	 * @return "King", "Queen", "Rook", "Knight", "Bishop", or "Pawn", depending on
	 *         the type of the chess piece
	 */
	public abstract String pieceType();

	/**
	 * This method checks whether the specified row and col are within the bounds of
	 * the board.
	 * 
	 * @param row the row to check
	 * @param col the col to check
	 * @return true if the specified location is in bounds and false otherwise
	 */
	protected boolean isInBounds(int row, int col) {
		return (row >= 0) && (row < 8) && (col >= 0) && (col < 8);
	}
}
