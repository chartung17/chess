package chess;

import chess.piece.ChessPiece;
import chess.piece.King;
import chess.piece.Pawn;

/**
 * This class is used by the board class to check the validity of any move that cannot be checked by
 * the piece making the move, including checking whether castling and en passant are legal and
 * checking whether a given move would result in the player's own king being in check.
 */
public class MoveValidator {
	private ChessPiece[][] pieces;
	private King blackKing;
	private King whiteKing;

	/**
	 * This method creates a MoveValidator for the board with the specified pieces.
	 * 
	 * @param pieces    the array of pieces on the board
	 * @param blackKing the black king
	 * @param whiteKing the white king
	 */
	public MoveValidator(ChessPiece[][] pieces, King blackKing, King whiteKing) {
		this.pieces = pieces;
		this.blackKing = blackKing;
		this.whiteKing = whiteKing;
	}

	/**
	 * This method check whether the given piece is a Pawn of the given color.
	 * 
	 * @param piece   the piece to check
	 * @param isBlack the color to check, true for black or false for white
	 * @return true if the piece is a Pawn of the specified color and false otherwise
	 */
	private boolean isPawn(ChessPiece piece, boolean isBlack) {
		return (piece != null) && (piece instanceof Pawn) && (piece.isBlack() == isBlack);
	}

	/**
	 * This method checks whether en passant is legal.
	 * 
	 * @param enPassantPawn the pawn that can potentially be captured by en passant
	 * @param board         an 8x8 matrix representing the current state of the board, with empty
	 *                      squares represented by zeros and occupied squares represented by the
	 *                      return value of the occupying piece's toByte() method
	 * @return true if en passant is legal and false otherwise
	 */
	protected boolean isEnPassantLegal(Pawn enPassantPawn, byte[][] board) {
		// if the last move was not a pawn moving two spaces, return false
		if (enPassantPawn == null)
			return false;

		// if a pawn moved two spaces, the two pieces immediately next to that pawn
		// could
		// potentially capture it by en passant if they are pawns of the correct color
		int row = enPassantPawn.getRow();
		int col = enPassantPawn.getCol();
		ChessPiece piece1 = (col + 1 < 8) ? pieces[row][col + 1] : null;
		ChessPiece piece2 = (col - 1 >= 0) ? pieces[row][col - 1] : null;

		// return true if at least one of the two pieces can capture by en passant and
		// false
		// otherwise
		return (isEnPassantLegal(enPassantPawn, piece1, board)
				|| (isEnPassantLegal(enPassantPawn, piece2, board)));
	}

	/**
	 * This method checks whether the given piece can legally capture by en passant
	 * 
	 * @param enPassantPawn the pawn that can potentially be captured by en passant
	 * @param piece         the piece to check
	 * @param board         an 8x8 matrix representing the current state of the board, with empty
	 *                      squares represented by zeros and occupied squares represented by the
	 *                      return value of the occupying piece's toByte() method
	 * @return true if the piece can capture by en passant and false otherwise
	 */
	protected boolean isEnPassantLegal(Pawn enPassantPawn, ChessPiece piece, byte[][] board) {
		// if the last move was not a pawn moving two spaces or if the given piece is
		// null, return
		// false
		if ((enPassantPawn == null) || (piece == null))
			return false;

		// if a pawn moved two spaces, the given piece could potentially capture it if
		// it is right
		// next to it and is a pawn of the correct color
		int row = enPassantPawn.getRow();
		int col = enPassantPawn.getCol();
		int pieceCol = piece.getCol();
		if ((row != piece.getRow()) || ((col != pieceCol + 1) && (col != pieceCol - 1)))
			return false;
		boolean blackToMove = !enPassantPawn.isBlack();
		int direction = blackToMove ? 1 : -1;

		// if either of the pieces next to the pawn that just moved is a pawn of the
		// opposite color,
		// it can capture en passant as long as that would not result in the player's
		// own king being
		// in check
		if (isPawn(piece, blackToMove)) {
			board[row + direction][col] = board[row][pieceCol];
			board[row][col] = 0;
			board[row][pieceCol] = 0;
			return !isCheck(blackToMove, board);
		}
		return false;
	}

	/**
	 * This method checks whether the given piece can legally capture by en passant by moving to the
	 * specified location
	 * 
	 * @param row           the row to check
	 * @param col           the col to check
	 * @param enPassantPawn the pawn that can potentially be captured by en passant
	 * @param piece         the piece to check
	 * @param board         an 8x8 matrix representing the current state of the board, with empty
	 *                      squares represented by zeros and occupied squares represented by the
	 *                      return value of the occupying piece's toByte() method
	 * @return true if the piece can capture by en passant and false otherwise
	 */
	protected boolean isEnPassantLegal(int row, int col, Pawn enPassantPawn, ChessPiece piece,
			byte[][] board) {
		// if the last move was not a pawn moving two spaces, return false
		if (enPassantPawn == null)
			return false;

		// if the given location is the wrong square for an en passant capture of the
		// pawn that just
		// moved, return false
		if ((row != enPassantPawn.getRow() + (enPassantPawn.isBlack() ? -1 : 1))
				|| (col != enPassantPawn.getCol()))
			return false;

		// if the given location is correct, check whether the given piece can legally
		// capture by en
		// passant
		return isEnPassantLegal(enPassantPawn, piece, board);
	}

	/**
	 * This method checks whether moving the given piece to the specified location would result in
	 * the player's own king being in check.
	 * 
	 * @param row         the row to move to
	 * @param col         the col to move to
	 * @param blackToMove true if it is black's turn and false if white's turn
	 * @param piece       the piece being moved
	 * @param board       an 8x8 matrix representing the current state of the board, with empty
	 *                    squares represented by zeros and occupied squares represented by the
	 *                    return value of the occupying piece's toByte() method
	 * @return true if the move results in the player's own king being in check and false otherwise
	 */
	protected boolean doesMoveEndInCheck(int row, int col, boolean blackToMove, ChessPiece piece,
			byte[][] board) {
		int startRow = piece.getRow();
		int startCol = piece.getCol();
		board[row][col] = board[startRow][startCol];
		board[startRow][startCol] = 0;
		King king = blackToMove ? blackKing : whiteKing;
		int kingRow, kingCol;
		if (king == piece) {
			kingRow = row;
			kingCol = col;
		} else {
			kingRow = king.getRow();
			kingCol = king.getCol();
		}
		return isCheck(kingRow, kingCol, blackToMove, board);
	}

	/**
	 * This method checks whether the king of the specified color is in check in the given board
	 * setup.
	 * 
	 * @param isBlackKing true if the king is black and false if white
	 * @param board       an 8x8 matrix representing the current state of the board, with empty
	 *                    squares represented by zeros and occupied squares represented by the
	 *                    return value of the occupying piece's toByte() method
	 * @return true if the king is in check and false if not
	 */
	protected boolean isCheck(boolean blackToMove, byte[][] board) {
		King king = blackToMove ? blackKing : whiteKing;
		return isCheck(king.getRow(), king.getCol(), blackToMove, board);
	}

	/**
	 * This method checks whether the king of the specified color at the specified location is in
	 * check in the given board setup.
	 * 
	 * @param kingRow     the row at which the king is located
	 * @param kingCol     the col at which the king is located
	 * @param isBlackKing true if the king is black and false if white
	 * @param board       an 8x8 matrix representing the current state of the board, with empty
	 *                    squares represented by zeros and occupied squares represented by the
	 *                    return value of the occupying piece's toByte() method
	 * @return true if the king is in check and false if not
	 */
	protected boolean isCheck(int kingRow, int kingCol, boolean isBlackKing, byte[][] board) {
		char piece;

		// check if the king's square is reachable by a pawn
		int direction = isBlackKing ? 1 : -1;
		piece = pieceAt(kingRow + direction, kingCol - 1, board);
		if (matches(piece, 'p', !isBlackKing))
			return true;
		piece = pieceAt(kingRow + direction, kingCol + 1, board);
		if (matches(piece, 'p', !isBlackKing))
			return true;

		// check if the king's square is reachable by a knight
		int[][] deltas = { { 1, 2 }, { 1, -2 }, { -1, 2 }, { -1, -2 }, { 2, 1 }, { 2, -1 },
				{ -2, 1 }, { -2, -1 } };
		for (int[] delta : deltas) {
			piece = pieceAt(kingRow + delta[0], kingCol + delta[1], board);
			if (matches(piece, 'n', !isBlackKing))
				return true;
		}

		// check if the king's square is reachable by a king
		for (int i = -1; i <= 1; i++) {
			for (int j = -1; j <= 1; j++) {
				piece = pieceAt(kingRow + i, kingCol + j, board);
				if (matches(piece, 'k', !isBlackKing))
					return true;
			}
		}

		// check if the king's square is reachable by a rook or queen moving
		// horizontally or
		// vertically
		int[][] rookDirections = { { 1, 0 }, { -1, 0 }, { 0, 1 }, { 0, -1 } };
		for (int[] rookDirection : rookDirections) {
			for (int i = 1; i < 8; i++) {
				piece = pieceAt(kingRow + (i * rookDirection[0]), kingCol + (i * rookDirection[1]),
						board);
				if ((matches(piece, 'r', !isBlackKing)) || matches(piece, 'q', !isBlackKing))
					return true;
				if (piece != 0)
					break;
			}
		}

		// check if the king's square is reachable by a bishop or queen moving
		// diagonally
		int[][] bishopDirections = { { 1, 1 }, { 1, -1 }, { -1, 1 }, { -1, -1 } };
		for (int[] bishopDirection : bishopDirections) {
			for (int i = 1; i < 8; i++) {
				piece = pieceAt(kingRow + (i * bishopDirection[0]),
						kingCol + (i * bishopDirection[1]), board);
				if ((matches(piece, 'b', !isBlackKing)) || matches(piece, 'q', !isBlackKing))
					return true;
				if (piece != 0)
					break;
			}
		}

		// if this point is reached, the king is not in check
		return false;
	}

	/**
	 * This method determines whether the specified piece is a piece of the specified type and
	 * color.
	 * 
	 * @param piece     the piece to check
	 * @param pieceType the piece type to check
	 * @param isBlack   true to check if piece is black and false to check if white
	 * @return true if the given piece matches the given piece type and color
	 */
	private boolean matches(char piece, char pieceType, boolean isBlack) {
		// if the piece is the null char, there is no match
		// if the piece is the wrong color (uppercase characters are black), there is no
		// match
		// if the piece is not null and is the right color, check whether it matches the
		// piece type
		return (piece != 0) && (Character.isUpperCase(piece) == isBlack)
				&& (Character.toUpperCase(piece) == Character.toUpperCase(pieceType));
	}

	/**
	 * This method returns the char representing the piece at the given location in the given board,
	 * or the null char if the specified location is outside of the bounds of the board
	 * 
	 * @param row   the row to check
	 * @param col   the col to check
	 * @param board the board to check
	 * @return the char representing the piece at the given location in the given board, or the null
	 *         char if the specified location is outside of the bounds of the board
	 */
	private char pieceAt(int row, int col, byte[][] board) {
		if ((row >= 0) && (row < 8) && (col >= 0) && (col < 8)) {
			return (char) board[row][col];
		}
		return 0;
	}

	/**
	 * This method checks whether the specified king can castle on the specified side
	 * 
	 * @param king        the king to check
	 * @param side        the side to which the king is moving, either 'q' for queenside or 'k' for
	 *                    kingside
	 * @param blackToMove true if it is black's turn to move and false if it is white's turn
	 * @param board       an 8x8 matrix representing the current state of the board, with empty
	 *                    squares represented by zeros and occupied squares represented by the
	 *                    return value of the occupying piece's toByte() method
	 * @return 1 if successful or 0 if unsuccessful
	 */
	protected boolean isCastleLegal(King king, char side, boolean blackToMove, byte[][] board) {
		// if the king can't castle on the selected side due to either it or the rook on
		// that side
		// having moved, return 0, otherwise determine the direction the king must move
		// and the
		// column in which the rook is located
		int direction;
		int rookCol;
		if (side == 'q') {
			if (!king.canCastleQueenside())
				return false;
			direction = -1;
			rookCol = 0;
		} else if (side == 'k') {
			if (!king.canCastleKingside())
				return false;
			direction = 1;
			rookCol = 7;
		} else {
			return false;
		}

		// if the rook of the correct color is not located in the correct position,
		// return 0
		int row = king.getRow();
		byte rook = board[row][rookCol];
		if (!(Character.toUpperCase(rook) == 'R') || !(Character.isUpperCase(rook) == blackToMove))
			return false;

		// if there are any pieces between the king and the rook on the selected side,
		// return 0
		int kingCol = king.getCol();
		for (int i = kingCol + direction; i != rookCol; i += direction) {
			if (board[row][i] != 0)
				return false;
		}

		// if the king is in check, return 0
		if (isCheck(blackToMove, board))
			return false;

		// if the square the king would pass through is under attack, return 0
		byte kingByte = board[row][kingCol];
		board[row][kingCol] = 0;
		board[row][kingCol + direction] = kingByte;
		if (isCheck(row, kingCol + direction, blackToMove, board))
			return false;

		// if the square the king would land on is under attack, return 0
		board[row][kingCol + direction] = 0;
		board[row][kingCol + (2 * direction)] = kingByte;
		if (isCheck(row, kingCol + (2 * direction), blackToMove, board))
			return false;

		// if all of the above checks pass, return true
		return true;
	}

}
