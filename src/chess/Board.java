package chess;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import chess.piece.*;

/**
 * This class represents a chessboard.
 */
public class Board {
	private ChessPiece[][] pieces;
	private King blackKing;
	private King whiteKing;
	private ChessPiece selectedPiece = null;
	private HashSet<ChessPiece> blackPieces = new HashSet<>();
	private HashSet<ChessPiece> whitePieces = new HashSet<>();
	private Pawn enPassantPawn = null; // if en passant is legal, this is the pawn that can be
										// captured by en passant
	private MoveValidator validator;

	// return values for handleSelectedSquare()
	public static final int FAILURE = 0;
	public static final int MOVE = 1;
	public static final int SELECT = 2;
	public static final int PROMOTE = 3;
	public static final int CAPTURE = 4;
	public static final int PAWN_MOVE = 5;

	/**
	 * This method initializes a chessboard with all pieces in their normal starting
	 * positions.
	 */
	public Board() {
		// initialize empty board
		pieces = new ChessPiece[8][8];

		// create pawns
		for (int i = 0; i < 8; i++) {
			pieces[1][i] = new Pawn(1, i, true);
			pieces[6][i] = new Pawn(6, i, false);
		}

		// create other black pieces
		pieces[0][0] = new Rook(0, 0, true);
		pieces[0][1] = new Knight(0, 1, true);
		pieces[0][2] = new Bishop(0, 2, true);
		pieces[0][3] = new Queen(0, 3, true);
		blackKing = new King(0, 4, true);
		pieces[0][4] = blackKing;
		pieces[0][5] = new Bishop(0, 5, true);
		pieces[0][6] = new Knight(0, 6, true);
		pieces[0][7] = new Rook(0, 7, true);

		// create other white pieces
		pieces[7][0] = new Rook(7, 0, false);
		pieces[7][1] = new Knight(7, 1, false);
		pieces[7][2] = new Bishop(7, 2, false);
		pieces[7][3] = new Queen(7, 3, false);
		whiteKing = new King(7, 4, false);
		pieces[7][4] = whiteKing;
		pieces[7][5] = new Bishop(7, 5, false);
		pieces[7][6] = new Knight(7, 6, false);
		pieces[7][7] = new Rook(7, 7, false);

		// add pieces to lists
		for (int i = 0; i < 8; i++) {
			blackPieces.add(pieces[0][i]);
			blackPieces.add(pieces[1][i]);
			whitePieces.add(pieces[6][i]);
			whitePieces.add(pieces[7][i]);
		}

		// initialize move validator
		validator = new MoveValidator(pieces, blackKing, whiteKing);
	}

	/**
	 * This method initializes a chessboard with the specified array of pieces.
	 * 
	 * @param pieces    the pieces to use
	 * @param blackKing the black king
	 * @param whiteKing the white king
	 */
	public Board(ChessPiece[][] pieces, King blackKing, King whiteKing) {
		this.pieces = pieces;
		this.blackKing = blackKing;
		this.whiteKing = whiteKing;
		validator = new MoveValidator(pieces, blackKing, whiteKing);
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {
				ChessPiece piece = pieces[i][j];
				if (piece != null) {
					if (piece.isBlack()) {
						blackPieces.add(piece);
					} else {
						whitePieces.add(piece);
					}
				}
			}
		}
	}

	/**
	 * This method returns an 8x8 matrix representing the current state of the
	 * board. Empty squares are represented by zeros, and occupied squares are
	 * represented by the return value of the occupying piece's toByte() method.
	 * 
	 * @return an 8x8 matrix representing the current state of the board
	 */
	public byte[][] toByteArray() {
		byte[][] array = new byte[8][8];
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {
				ChessPiece piece = pieces[i][j];
				// if the square is occupied, fill in the byte representation of the occupying
				// piece
				// no action needed for empty squares, since they were already initialized to
				// zero
				if (piece != null) {
					array[i][j] = piece.toByte();
				}
			}
		}
		return array;
	}

	/**
	 * If there is a piece at the specified location, this method removes it from
	 * the sets of remaining pieces. If the captured piece is a rook at its starting
	 * position, this method also indicates that the king cannot castle on the side
	 * of that rook.
	 * 
	 * @param row the row of the captured piece
	 * @param col the col of the captured piece
	 */
	private void capturePiece(int row, int col) {
		ChessPiece capturedPiece = pieces[row][col];
		if (capturedPiece != null) {
			whitePieces.remove(capturedPiece);
			blackPieces.remove(capturedPiece);
			if ((capturedPiece instanceof Rook) && ((col == 0) || (col == 7))
					&& (row == (capturedPiece.isBlack() ? 0 : 7))) {
				King king = capturedPiece.isBlack() ? blackKing : whiteKing;
				char side = (col == 0) ? 'q' : 'k';
				king.markCastle(side);
			}
		}
	}

	/**
	 * This method is run when the user selects a square on the chessboard.
	 * 
	 * @param row         the row of the selected square
	 * @param col         the col of the selected square
	 * @param blackToMove true if it is black's turn to move and false if it is
	 *                    white's turn
	 * @return one of the following constants: SELECT for successfully selecting a
	 *         piece, PROMOTE for a successful move resulting in a pawn needing to
	 *         be promoted, CAPTURE for a move resulting in a piece being captured,
	 *         PAWN_MOVE for any other successful move of a pawn, MOVE for any other
	 *         successful move, or FAILURE if unsuccessful
	 */
	public int handleSelectedSquare(int row, int col, boolean blackToMove) {
		// if a piece has already been selected, attempt to move the selected piece to
		// the selected
		// square
		if (selectedPiece != null) {
			int result;
			// save the current position of the selected piece
			int currentRow = selectedPiece.getRow();
			int currentCol = selectedPiece.getCol();
			ChessPiece target = pieces[row][col];
			// if the specified move would end with the player's own king in check, it is
			// illegal
			// in that case unselect the selected piece and return 0
			if (validator.doesMoveEndInCheck(row, col, blackToMove, selectedPiece, toByteArray())) {
				selectedPiece = null;
				return FAILURE;
			}
			// attempt to move to the selected square if empty or to capture if occupied
			boolean isCapture;
			boolean isPawnMove = selectedPiece instanceof Pawn;
			if (target == null) {
				result = selectedPiece.move(row, col, toByteArray());
				isCapture = false;
			} else {
				result = selectedPiece.capture(target, toByteArray());
				isCapture = true;
			}
			switch (result) {
			// if the move is successful, update the board, unselect the selected piece, and
			// return
			case 1:
				capturePiece(row, col);
				move(currentRow, currentCol, row, col);
				return isCapture ? CAPTURE : (isPawnMove ? PAWN_MOVE : MOVE);
			// if the move is unsuccessful, unselect the selected piece and return 0z
			case 0:
				selectedPiece = null;
				return FAILURE;
			// handle other situations
			case ChessPiece.EN_PASSANT:
				ChessPiece piece = selectedPiece;
				selectedPiece = null;
				return enPassant(row, col, piece);
			case ChessPiece.CASTLE:
				selectedPiece = null;
				return castle(row, col, blackToMove);
			case ChessPiece.K_ROOK:
				King kingK = blackToMove ? blackKing : whiteKing;
				kingK.markCastle('k');
				capturePiece(row, col);
				move(currentRow, currentCol, row, col);
				return isCapture ? CAPTURE : MOVE;
			case ChessPiece.Q_ROOK:
				King kingQ = blackToMove ? blackKing : whiteKing;
				kingQ.markCastle('q');
				capturePiece(row, col);
				move(currentRow, currentCol, row, col);
				return isCapture ? CAPTURE : MOVE;
			case ChessPiece.PAWN_MOVE_2:
				Pawn newEnPassantPawn = (Pawn) selectedPiece;
				capturePiece(row, col);
				move(currentRow, currentCol, row, col);
				enPassantPawn = newEnPassantPawn;
				return PAWN_MOVE;
			case ChessPiece.PROMOTE:
				capturePiece(row, col);
				pieces[currentRow][currentCol] = null;
				pieces[row][col] = selectedPiece;
				enPassantPawn = null;
				return PROMOTE;
			default:
				return -10; // indicating an error
			}
		}
		// if no piece has been selected, select the piece at the chosen square
		selectedPiece = pieces[row][col];
		// if the selected piece does not exist or is the wrong color, unselect it and
		// return
		if ((selectedPiece == null) || (selectedPiece.isBlack() != blackToMove)) {
			selectedPiece = null;
			return FAILURE;
		}
		// if the selected piece is the correct color, return SELECT
		return SELECT;
	}

	/**
	 * This method returns a String representing the type of chess piece at the
	 * selected square, or null if the square is empty.
	 * 
	 * @param row the row of the selected square
	 * @param col the col of the selected square
	 * @return "King", "Queen", "Rook", "Knight", "Bishop", or "Pawn", depending on
	 *         the type of the chess piece at the selected square, or null if the
	 *         square is empty
	 */
	public String getPieceType(int row, int col) {
		ChessPiece piece = pieces[row][col];
		if (piece == null)
			return null;
		return piece.pieceType();
	}

	/**
	 * This method returns a two-character String identifying the selected square.
	 * The first character is a lowercase letter between 'a' (left) and 'h' (right)
	 * identifying the column, and the second character is a digit between '1'
	 * (bottom) and '8' (top) identifying the row.
	 * 
	 * @param row the row of the selected square
	 * @param col the col of the selected square
	 * @return a two-character String identifying the column and row of the selected
	 *         square
	 */
	public String getSquareName(int row, int col) {
		char colChar = (char) (97 + col);
		char rowChar = (char) (56 - row);
		return "" + colChar + rowChar;
	}

	/**
	 * This method moves the selected piece from the given current position to the
	 * given new position, and unselects.
	 * 
	 * @param currentRow the row to move from
	 * @param currentCol the col to move from
	 * @param newRow     the row to move to
	 * @param newCol     the col to move to
	 */
	private void move(int currentRow, int currentCol, int newRow, int newCol) {
		pieces[currentRow][currentCol] = null;
		pieces[newRow][newCol] = selectedPiece;
		selectedPiece = null;
		enPassantPawn = null;
	}

	/**
	 * This method promotes the selected piece to the given piece type and then
	 * unselects the piece.
	 * 
	 * @param pieceType 'q' for Queen, 'r' for Rook, 'n' for Knight, or 'b' for
	 *                  Bishop
	 */
	public void promote(char pieceType) {
		int row = selectedPiece.getRow();
		int col = selectedPiece.getCol();
		boolean isBlack = selectedPiece.isBlack();
		ChessPiece newPiece = null;
		switch (pieceType) {
		case 'q':
			newPiece = new Queen(row, col, isBlack);
			break;
		case 'r':
			newPiece = new Rook(row, col, isBlack);
			break;
		case 'n':
			newPiece = new Knight(row, col, isBlack);
			break;
		case 'b':
			newPiece = new Bishop(row, col, isBlack);
			break;
		default:
			return;
		}
		HashSet<ChessPiece> piecesSet = isBlack ? blackPieces : whitePieces;
		piecesSet.remove(selectedPiece);
		piecesSet.add(newPiece);
		pieces[row][col] = newPiece;
		selectedPiece = null;
	}

	/**
	 * This method checks whether the king of the specified color is in check in the
	 * current board setup.
	 * 
	 * @param blackToMove true if it is black's turn to move and false if white's
	 *                    turn
	 * @return true if the king is in check and false if not
	 */
	public boolean isCheck(boolean blackToMove) {
		King king = blackToMove ? blackKing : whiteKing;
		return validator.isCheck(king.getRow(), king.getCol(), blackToMove, toByteArray());
	}

	/**
	 * This method attempts to castle by moving the king of the specified color to
	 * the specified location and returns MOVE if successful or FAILURE if
	 * unsuccessful.
	 * 
	 * @param row         the row to move to
	 * @param col         the col to move to
	 * @param blackToMove true if it is black's turn and false if it is white's turn
	 * @return MOVE if successful or FAILURE if unsuccessful
	 */
	private int castle(int row, int col, boolean blackToMove) {
		King king;
		if (blackToMove && (row == 0)) {
			king = blackKing;
		} else if (!blackToMove && (row == 7)) {
			king = whiteKing;
		} else {
			return FAILURE;
		}
		char side;
		if (col == 2) {
			side = 'q';
		} else if (col == 6) {
			side = 'k';
		} else {
			return FAILURE;
		}
		return castle(king, side, blackToMove);
	}

	/**
	 * This method attempts to castle the specified king on the specified side ('q'
	 * for queenside or 'k' for kingside) and returns MOVE if successful or FAILURE
	 * if unsuccessful.
	 * 
	 * @param king        the king to move
	 * @param side        the side to which the king is moving, either 'q' for
	 *                    queenside or 'k' for kingside
	 * @param blackToMove true if it is black's turn to move and false if it is
	 *                    white's turn
	 * @return MOVE if successful or FAILURE if unsuccessful
	 */
	private int castle(King king, char side, boolean blackToMove) {
		// if castling is illegal, return FAILURE
		if (!validator.isCastleLegal(king, side, blackToMove, toByteArray()))
			return FAILURE;

		// if castling is legal, determine the positions of the king and rook involved
		// and the
		// direction the king must move
		int direction;
		int rookCol;
		if (side == 'q') {
			direction = -1;
			rookCol = 0;
		} else {
			direction = 1;
			rookCol = 7;
		}
		int row = king.getRow();
		int kingCol = king.getCol();

		// castle and return MOVE
		Rook rook = (Rook) pieces[row][rookCol];
		pieces[row][kingCol] = null;
		pieces[row][kingCol + (2 * direction)] = king;
		king.setPos(row, kingCol + (2 * direction));
		pieces[row][rookCol] = null;
		pieces[row][kingCol + direction] = rook;
		rook.setPos(row, kingCol + direction);
		king.markCastle();
		enPassantPawn = null;
		return MOVE;
	}

	/**
	 * This method attempts to capture en passant by moving the selected piece to
	 * the given location and returns CAPTURE if successful or FAILURE if
	 * unsuccessful
	 * 
	 * @param row the row to move to
	 * @param col the col to move to
	 * @return CAPTURE if successful or FAILURE if unsuccessful
	 */
	private int enPassant(int row, int col, ChessPiece piece) {
		if (!validator.isEnPassantLegal(row, col, enPassantPawn, piece, toByteArray())) {
			return FAILURE;
		}
		int captureRow = enPassantPawn.getRow();
		int captureCol = enPassantPawn.getCol();
		capturePiece(captureRow, captureCol);
		pieces[captureRow][captureCol] = null;
		pieces[row][col] = piece;
		pieces[piece.getRow()][piece.getCol()] = null;
		piece.setPos(row, col);
		enPassantPawn = null;
		return CAPTURE;
	}

	/**
	 * This method checks whether the current player has any legal moves.
	 * 
	 * @param blackToMove true if it is black's turn to move and false if it is
	 *                    white's turn
	 * @return true if the current player has at least one legal move and false
	 *         otherwise
	 */
	public boolean anyLegalMoves(boolean blackToMove) {
		HashSet<ChessPiece> pieces = blackToMove ? blackPieces : whitePieces;

		// check all pieces of the correct color for legal moves
		// return true if any piece has a legal move
		for (ChessPiece piece : pieces) {
			if (getLegalMoves(piece.getRow(), piece.getCol(), blackToMove).size() > 0)
				return true;
		}

		// if no piece has a legal move, return false
		return false;
	}

	/**
	 * This method returns a list of moves the piece at the specified location can
	 * legally make; if the specified location is empty or if the piece is not the
	 * current player's color, this method returns an empty list. Each move is
	 * represented as a two-element array, where the first element indicates the row
	 * and the second element indicates the col of the piece's position at the end
	 * of the move.
	 * 
	 * @param row         the row of the selected piece
	 * @param col         the col of the selected piece
	 * @param blackToMove true if it is black's turn to move and false if it is
	 *                    white's turn
	 * @return a list of legal moves, each represented as a two-element array
	 *         containing the row and col of the piece's position at the end of the
	 *         move
	 */
	public List<int[]> getLegalMoves(int row, int col, boolean blackToMove) {
		ChessPiece piece = pieces[row][col];

		// return an empty list if the selected square is empty or contains a piece of
		// the wrong
		// color
		if ((piece == null) || (piece.isBlack() != blackToMove)) {
			return new ArrayList<int[]>();
		}

		// get a list of legal moves ignoring check, en passant, and castling
		List<int[]> oldMoves = piece.legalMoves(toByteArray());

		// make a new list and copy all the elements of the old list that don't result
		// in check into
		// the new list
		List<int[]> newMoves = new ArrayList<>();
		for (int[] move : oldMoves) {
			if (!validator.doesMoveEndInCheck(move[0], move[1], blackToMove, piece, toByteArray())) {
				newMoves.add(move);
			}
		}

		// if en passant or castling is legal, add it to the list
		if ((piece instanceof Pawn) && (validator.isEnPassantLegal(enPassantPawn, piece, toByteArray()))) {
			int direction = blackToMove ? 1 : -1;
			newMoves.add(new int[] { enPassantPawn.getRow() + direction, enPassantPawn.getCol() });
		} else if (piece instanceof King) {
			if (validator.isCastleLegal((King) piece, 'q', blackToMove, toByteArray())) {
				newMoves.add(new int[] { piece.getRow(), piece.getCol() - 2 });
			}
			if (validator.isCastleLegal((King) piece, 'k', blackToMove, toByteArray())) {
				newMoves.add(new int[] { piece.getRow(), piece.getCol() + 2 });
			}
		}

		// return the list of legal moves
		return newMoves;
	}

	/**
	 * This method returns a BoardState object representing the current board state.
	 * 
	 * @param blackToMove true if it is black's turn to move and false if it is
	 *                    white's turn
	 * @return a BoardState object representing the current board state
	 */
	public BoardState getBoardState(boolean blackToMove) {
		boolean isEnPassantLegal = validator.isEnPassantLegal(enPassantPawn, toByteArray());
		return new BoardState(toByteArray(), blackToMove, isEnPassantLegal, blackKing.canCastleKingside(),
				blackKing.canCastleQueenside(), whiteKing.canCastleKingside(), whiteKing.canCastleQueenside());
	}
}
