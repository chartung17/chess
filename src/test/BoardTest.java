package test;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import chess.Board;
import chess.BoardState;
import chess.piece.Bishop;
import chess.piece.ChessPiece;
import chess.piece.King;
import chess.piece.Knight;
import chess.piece.Pawn;
import chess.piece.Queen;
import chess.piece.Rook;

/**
 * This class is used to test the Board class.
 */
class BoardTest {
	Board board;

	@BeforeEach
	void setUp() {
		board = new Board();
	}

	@Test
	void testHandleSelectedSquare() {
		// attempt to select an empty square
		assertEquals(board.handleSelectedSquare(3, 3, false), Board.FAILURE);

		// attempt to select a black piece on white's turn
		assertEquals(board.handleSelectedSquare(0, 0, false), Board.FAILURE);

		// move white pawn from a2 to a4
		assertEquals(board.handleSelectedSquare(6, 0, false), Board.SELECT);
		assertEquals(board.handleSelectedSquare(4, 0, false), Board.PAWN_MOVE);

		// attempt to select a white piece on black's turn
		assertEquals(board.handleSelectedSquare(4, 0, true), Board.FAILURE);

		// move black pawn from b7 to b5
		assertEquals(board.handleSelectedSquare(1, 1, true), Board.SELECT);
		assertEquals(board.handleSelectedSquare(3, 1, true), Board.PAWN_MOVE);

		// move white pawn from a4 to capture black pawn at b5
		assertEquals(board.handleSelectedSquare(4, 0, false), Board.SELECT);
		assertEquals(board.handleSelectedSquare(3, 1, false), Board.CAPTURE);

		// move black bishop from c8 to a6
		assertEquals(board.handleSelectedSquare(0, 2, true), Board.SELECT);
		assertEquals(board.handleSelectedSquare(2, 0, true), Board.MOVE);

		// move white pawn from b5 to b6
		assertEquals(board.handleSelectedSquare(3, 1, false), Board.SELECT);
		assertEquals(board.handleSelectedSquare(2, 1, false), Board.PAWN_MOVE);

		// attempt to move black knight to square occupied by black bishop
		assertEquals(board.handleSelectedSquare(0, 1, true), Board.SELECT);
		assertEquals(board.handleSelectedSquare(2, 1, true), Board.FAILURE);

		// move black knight from b8 to c6
		assertEquals(board.handleSelectedSquare(0, 1, true), Board.SELECT);
		assertEquals(board.handleSelectedSquare(2, 2, true), Board.MOVE);

		// move white pawn from a6 to a7
		assertEquals(board.handleSelectedSquare(2, 1, false), Board.SELECT);
		assertEquals(board.handleSelectedSquare(1, 1, false), Board.PAWN_MOVE);

		// attempt to move black rook diagonally
		assertEquals(board.handleSelectedSquare(0, 0, true), Board.SELECT);
		assertEquals(board.handleSelectedSquare(1, 1, true), Board.FAILURE);

		// move black rook from a8 to c8
		assertEquals(board.handleSelectedSquare(0, 0, true), Board.SELECT);
		assertEquals(board.handleSelectedSquare(0, 2, true), Board.MOVE);

		// move white pawn from b7 to capture black rook on c8
		assertEquals(board.handleSelectedSquare(1, 1, false), Board.SELECT);
		assertEquals(board.handleSelectedSquare(0, 2, false), Board.PROMOTE);
	}

	@Test
	void testGetPieceType() {
		assertEquals(board.getPieceType(0, 0), "Rook");
		assertEquals(board.getPieceType(7, 1), "Knight");
		assertEquals(board.getPieceType(0, 2), "Bishop");
		assertEquals(board.getPieceType(7, 3), "Queen");
		assertEquals(board.getPieceType(7, 4), "King");
		assertEquals(board.getPieceType(6, 6), "Pawn");
		assertEquals(board.getPieceType(4, 4), null);
	}

	@Test
	void testGetSquareName() {
		assertEquals(board.getSquareName(0, 1), "b8");
		assertEquals(board.getSquareName(5, 7), "h3");
	}

	@Test
	void testBoardStateAndPromote() {
		// construct array that should be equal to the result of toByteArray() for the
		// starting
		// position
		byte[][] array = new byte[8][8];
		byte[] firstRow = { (byte) 'R', (byte) 'N', (byte) 'B', (byte) 'Q', (byte) 'K', (byte) 'B', (byte) 'N',
				(byte) 'R' };
		byte[] lastRow = { (byte) 'r', (byte) 'n', (byte) 'b', (byte) 'q', (byte) 'k', (byte) 'b', (byte) 'n',
				(byte) 'r' };
		array[0] = firstRow;
		array[7] = lastRow;
		for (int i = 0; i < 8; i++) {
			array[1][i] = (byte) 'P';
			array[6][i] = (byte) 'p';
		}
		// compare the above array to the result of toByteArray()
		assertArrayEquals(array, board.toByteArray());

		// verify result of toBoardState()
		BoardState state = new BoardState(array, true, false, true, true, true, true);
		assertEquals(state, board.getBoardState(true));

		// promote one pawn each to queen, rook, knight, and bishop, and attempt to
		// promote own to
		// king
		// update the above array to show the four successful promotions with the fifth
		// pawn
		// unchanged
		array[1][2] = (byte) 'Q';
		array[1][5] = (byte) 'R';
		array[6][0] = (byte) 'n';
		array[6][7] = (byte) 'b';
		board.handleSelectedSquare(1, 2, true);
		board.promote('q');
		board.handleSelectedSquare(1, 5, true);
		board.promote('r');
		board.handleSelectedSquare(6, 0, false);
		board.promote('n');
		board.handleSelectedSquare(6, 7, false);
		board.promote('b');
		board.handleSelectedSquare(6, 5, false);
		board.promote('k');
		// compare the array to the result of toByteArray()
		assertArrayEquals(array, board.toByteArray());

		// verify result of toBoardState()
		state = new BoardState(array, false, false, true, true, true, true);
		assertEquals(state, board.getBoardState(false));
	}

	/**
	 * This method executes a move specified by the names of the start and stop
	 * squares and the current player's color
	 * 
	 * @param start       the name of the square where the piece starts
	 * @param stop        the name of the square where the piece stops
	 * @param blackToMove the color of the current player
	 */
	void move(String start, String stop, boolean blackToMove) {
		int startRow = 56 - start.charAt(1);
		int stopRow = 56 - stop.charAt(1);
		int startCol = start.charAt(0) - 97;
		int stopCol = stop.charAt(0) - 97;
		if (board.handleSelectedSquare(startRow, startCol, blackToMove) != Board.SELECT)
			throw new IllegalArgumentException("Invalid selection");
		if (board.handleSelectedSquare(stopRow, stopCol, blackToMove) == Board.FAILURE)
			throw new IllegalArgumentException("Invalid move");
	}

	@Test
	void testCheckmate() {
		// test two-move checkmate
		assertFalse(board.isCheck(false));
		assertTrue(board.anyLegalMoves(false));
		move("f2", "f3", false);
		assertFalse(board.isCheck(true));
		assertTrue(board.anyLegalMoves(true));
		move("e7", "e5", true);
		assertFalse(board.isCheck(false));
		assertTrue(board.anyLegalMoves(false));
		move("g2", "g4", false);
		assertFalse(board.isCheck(true));
		assertTrue(board.anyLegalMoves(true));
		move("d8", "h4", true);
		assertTrue(board.isCheck(false));
		assertFalse(board.anyLegalMoves(false));
	}

	@Test
	void testCastleQueenside() {
		// white king should not be able to castle queenside if there are pieces between
		// it and the
		// queenside rook
		move("d2", "d4", false);
		move("c1", "f4", false);
		move("d1", "d2", false);
		assertEquals(board.handleSelectedSquare(7, 4, false), Board.SELECT);
		assertEquals(board.handleSelectedSquare(7, 2, false), Board.FAILURE);
		move("b1", "a3", false);
		assertEquals(board.handleSelectedSquare(7, 4, false), Board.SELECT);
		assertEquals(board.handleSelectedSquare(7, 2, false), Board.MOVE);

		// black king should not be able to castle queenside if there are pieces between
		// it and the
		// queenside rook
		move("d7", "d5", true);
		move("c8", "f5", true);
		move("d8", "d7", true);
		assertEquals(board.handleSelectedSquare(0, 4, true), Board.SELECT);
		assertEquals(board.handleSelectedSquare(0, 2, true), Board.FAILURE);
		move("b8", "a6", true);
		assertEquals(board.handleSelectedSquare(0, 4, true), Board.SELECT);
		assertEquals(board.handleSelectedSquare(0, 2, true), Board.MOVE);
	}

	@Test
	void testCastleKingside() {
		// white king should not be able to castle kingside if there are pieces between
		// it and the
		// kingside rook
		move("g2", "g4", false);
		move("f1", "h3", false);
		assertEquals(board.handleSelectedSquare(7, 4, false), Board.SELECT);
		assertEquals(board.handleSelectedSquare(7, 6, false), Board.FAILURE);
		move("g1", "f3", false);
		assertEquals(board.handleSelectedSquare(7, 4, false), Board.SELECT);
		assertEquals(board.handleSelectedSquare(7, 6, false), Board.MOVE);

		// black king should not be able to castle kingside if there are pieces between
		// it and the
		// kingside rook
		move("g7", "g5", true);
		move("f8", "h6", true);
		assertEquals(board.handleSelectedSquare(0, 4, true), Board.SELECT);
		assertEquals(board.handleSelectedSquare(0, 6, true), Board.FAILURE);
		move("g8", "f6", true);
		assertEquals(board.handleSelectedSquare(0, 4, true), Board.SELECT);
		assertEquals(board.handleSelectedSquare(0, 6, true), Board.MOVE);
	}

	/**
	 * This method creates a new board containing only two kings and four rooks in
	 * their starting positions.
	 */
	void setUpCastle() {
		ChessPiece[][] pieces = new ChessPiece[8][8];
		King blackKing = new King(0, 4, true);
		King whiteKing = new King(7, 4, false);
		pieces[0][4] = blackKing;
		pieces[7][4] = whiteKing;
		pieces[0][0] = new Rook(0, 0, true);
		pieces[0][7] = new Rook(0, 7, true);
		pieces[7][0] = new Rook(7, 0, false);
		pieces[7][7] = new Rook(7, 7, false);
		board = new Board(pieces, blackKing, whiteKing);
	}

	@Test
	void testCastleKingMoved() {
		setUpCastle();

		// white king cannot castle if not in its starting position
		move("e1", "e2", false);
		assertEquals(board.handleSelectedSquare(6, 4, false), Board.SELECT);
		assertEquals(board.handleSelectedSquare(6, 6, false), Board.FAILURE);
		assertEquals(board.handleSelectedSquare(6, 4, false), Board.SELECT);
		assertEquals(board.handleSelectedSquare(6, 2, false), Board.FAILURE);

		// white king cannot castle from starting position if it has already moved
		move("e2", "e1", false);
		assertEquals(board.handleSelectedSquare(7, 4, false), Board.SELECT);
		assertEquals(board.handleSelectedSquare(7, 6, false), Board.FAILURE);
		assertEquals(board.handleSelectedSquare(7, 4, false), Board.SELECT);
		assertEquals(board.handleSelectedSquare(7, 2, false), Board.FAILURE);

		// black king cannot castle if not in its starting position
		move("e8", "e7", true);
		assertEquals(board.handleSelectedSquare(1, 4, true), Board.SELECT);
		assertEquals(board.handleSelectedSquare(1, 6, true), Board.FAILURE);
		assertEquals(board.handleSelectedSquare(1, 4, true), Board.SELECT);
		assertEquals(board.handleSelectedSquare(1, 2, true), Board.FAILURE);

		// black king cannot castle from starting position if it has already moved
		move("e7", "e8", true);
		assertEquals(board.handleSelectedSquare(0, 4, true), Board.SELECT);
		assertEquals(board.handleSelectedSquare(0, 6, true), Board.FAILURE);
		assertEquals(board.handleSelectedSquare(0, 4, true), Board.SELECT);
		assertEquals(board.handleSelectedSquare(0, 2, true), Board.FAILURE);
	}

	@Test
	void testCastleRookMoved() {
		setUpCastle();

		// white cannot castle if the rooks have moved
		// verify board state after each move
		move("a1", "a2", false);
		move("a2", "a1", false);
		BoardState state = new BoardState(board.toByteArray(), true, false, true, true, true, false);
		assertEquals(state, board.getBoardState(true));
		assertEquals(board.handleSelectedSquare(7, 4, false), Board.SELECT);
		assertEquals(board.handleSelectedSquare(7, 2, false), Board.FAILURE);
		move("h1", "h2", false);
		move("h2", "h1", false);
		assertEquals(board.handleSelectedSquare(7, 4, false), Board.SELECT);
		assertEquals(board.handleSelectedSquare(7, 6, false), Board.FAILURE);
		state = new BoardState(board.toByteArray(), true, false, true, true, false, false);
		assertEquals(state, board.getBoardState(true));

		// black cannot castle if the rooks have moved
		move("a8", "a7", true);
		move("a7", "a8", true);
		state = new BoardState(board.toByteArray(), false, false, true, false, false, false);
		assertEquals(state, board.getBoardState(false));
		assertEquals(board.handleSelectedSquare(0, 4, true), Board.SELECT);
		assertEquals(board.handleSelectedSquare(0, 2, true), Board.FAILURE);
		move("h8", "h7", true);
		move("h7", "h8", true);
		state = new BoardState(board.toByteArray(), false, false, false, false, false, false);
		assertEquals(state, board.getBoardState(false));
		assertEquals(board.handleSelectedSquare(0, 4, true), Board.SELECT);
		assertEquals(board.handleSelectedSquare(0, 6, true), Board.FAILURE);
	}

	@Test
	void testCastleRookCaptured() {
		setUpCastle();
		// neither king can castle if the rooks have moved or been captured
		move("a1", "a8", false);
		move("a8", "a1", false);
		move("h8", "h1", true);
		assertEquals(board.handleSelectedSquare(7, 4, false), Board.SELECT);
		assertEquals(board.handleSelectedSquare(7, 6, false), Board.FAILURE);
		assertEquals(board.handleSelectedSquare(7, 4, false), Board.SELECT);
		assertEquals(board.handleSelectedSquare(7, 2, false), Board.FAILURE);
		assertEquals(board.handleSelectedSquare(0, 4, true), Board.SELECT);
		assertEquals(board.handleSelectedSquare(0, 6, true), Board.FAILURE);
		assertEquals(board.handleSelectedSquare(0, 4, true), Board.SELECT);
		assertEquals(board.handleSelectedSquare(0, 2, true), Board.FAILURE);
	}

	@Test
	void testCastleThroughCheck() {
		setUpCastle();
		// cannot castle when in check
		move("e8", "f7", true);
		move("a8", "e8", true);
		assertEquals(board.handleSelectedSquare(7, 4, false), Board.SELECT);
		assertEquals(board.handleSelectedSquare(7, 6, false), Board.FAILURE);
		assertEquals(board.handleSelectedSquare(7, 4, false), Board.SELECT);
		assertEquals(board.handleSelectedSquare(7, 2, false), Board.FAILURE);

		// cannot castle if doing so requires the king passing through a square under
		// attack
		move("f7", "e7", true);
		move("e8", "f8", true);
		assertEquals(board.handleSelectedSquare(7, 4, false), Board.SELECT);
		assertEquals(board.handleSelectedSquare(7, 6, false), Board.FAILURE);
		move("f8", "d8", true);
		assertEquals(board.handleSelectedSquare(7, 4, false), Board.SELECT);
		assertEquals(board.handleSelectedSquare(7, 2, false), Board.FAILURE);

		// cannot castle if doing so requires the king landing on a square under attack
		move("d8", "g8", true);
		assertEquals(board.handleSelectedSquare(7, 4, false), Board.SELECT);
		assertEquals(board.handleSelectedSquare(7, 6, false), Board.FAILURE);
		move("g8", "c8", true);
		assertEquals(board.handleSelectedSquare(7, 4, false), Board.SELECT);
		assertEquals(board.handleSelectedSquare(7, 2, false), Board.FAILURE);

		// the rook can pass through a square under attack
		move("c8", "b8", true);
		assertEquals(board.handleSelectedSquare(7, 4, false), Board.SELECT);
		assertEquals(board.handleSelectedSquare(7, 2, false), Board.MOVE);
	}

	@Test
	void testEnPassant() {
		// invalid en passant: the pawn moving two was not the most recent move
		move("b2", "b4", false);
		move("c7", "c5", true);
		move("b4", "b5", false);
		BoardState state = new BoardState(board.toByteArray(), true, false, true, true, true, true);
		assertEquals(state, board.getBoardState(true));
		assertEquals(board.handleSelectedSquare(3, 1, false), Board.SELECT);
		assertEquals(board.handleSelectedSquare(2, 2, false), Board.FAILURE);

		// valid en passant
		move("a7", "a5", true);
		state = new BoardState(board.toByteArray(), false, true, true, true, true, true);
		assertEquals(state, board.getBoardState(false));
		assertEquals(board.handleSelectedSquare(3, 1, false), Board.SELECT);
		assertEquals(board.handleSelectedSquare(2, 0, false), Board.CAPTURE);

		// invalid en passant: the two pawns are not next to each other
		move("c5", "c4", true);
		move("e2", "e4", false);
		state = new BoardState(board.toByteArray(), true, false, true, true, true, true);
		assertEquals(state, board.getBoardState(true));
		assertEquals(board.handleSelectedSquare(4, 2, true), Board.SELECT);
		assertEquals(board.handleSelectedSquare(4, 4, true), Board.FAILURE);
	}

	@Test
	void testIllegalMoves() {
		// attempt to move white rook from a1 to a2
		assertEquals(board.handleSelectedSquare(7, 0, false), Board.SELECT);
		assertEquals(board.handleSelectedSquare(6, 0, false), Board.FAILURE);

		// attempt to move white rook from a1 to a3
		assertEquals(board.handleSelectedSquare(7, 0, false), Board.SELECT);
		assertEquals(board.handleSelectedSquare(5, 0, false), Board.FAILURE);

		// attempt to move white bishop from c1 to c3
		assertEquals(board.handleSelectedSquare(7, 2, false), Board.SELECT);
		assertEquals(board.handleSelectedSquare(5, 2, false), Board.FAILURE);

		// attempt to move white bishop from c1 to a3
		assertEquals(board.handleSelectedSquare(7, 2, false), Board.SELECT);
		assertEquals(board.handleSelectedSquare(5, 0, false), Board.FAILURE);

		// move white knight from g1 to h3, then attempt to move white rook from a1 to
		// g1
		assertEquals(board.handleSelectedSquare(7, 6, false), Board.SELECT);
		assertEquals(board.handleSelectedSquare(5, 7, false), Board.MOVE);
		assertEquals(board.handleSelectedSquare(7, 0, false), Board.SELECT);
		assertEquals(board.handleSelectedSquare(7, 6, false), Board.FAILURE);

		// attempt to move white queen from d1 to d3
		assertEquals(board.handleSelectedSquare(7, 3, false), Board.SELECT);
		assertEquals(board.handleSelectedSquare(5, 3, false), Board.FAILURE);

		// attempt to move white pawn from a2 to a5
		assertEquals(board.handleSelectedSquare(6, 0, false), Board.SELECT);
		assertEquals(board.handleSelectedSquare(3, 0, false), Board.FAILURE);

		// attempt to move white knight from b1 to b3
		assertEquals(board.handleSelectedSquare(7, 1, false), Board.SELECT);
		assertEquals(board.handleSelectedSquare(5, 1, false), Board.FAILURE);

		// attempt to move white knight from b1 to g1
		assertEquals(board.handleSelectedSquare(7, 1, false), Board.SELECT);
		assertEquals(board.handleSelectedSquare(7, 6, false), Board.FAILURE);

		// attempt to move white knight from b1 to d3
		assertEquals(board.handleSelectedSquare(7, 1, false), Board.SELECT);
		assertEquals(board.handleSelectedSquare(5, 3, false), Board.FAILURE);

		// attempt to move white knight from b1 to b1
		assertEquals(board.handleSelectedSquare(7, 1, false), Board.SELECT);
		assertEquals(board.handleSelectedSquare(7, 1, false), Board.FAILURE);

		// attempt illegal capture with pawn
		move("a2", "a4", false);
		move("c7", "c5", true);
		assertEquals(board.handleSelectedSquare(4, 0, false), Board.SELECT);
		assertEquals(board.handleSelectedSquare(3, 2, false), Board.FAILURE);
	}

	@Test
	void testLegalMoves() {
		// create board with one king of each color and one each of white rook, bishop,
		// and queen
		// in their starting positions, plus one white knight putting the black king in
		// check
		// and one black pawn attacking the white rook and bishop
		ChessPiece[][] pieces = new ChessPiece[8][8];
		King blackKing = new King(0, 4, true);
		King whiteKing = new King(7, 4, false);
		pieces[0][4] = blackKing;
		pieces[7][4] = whiteKing;
		pieces[7][0] = new Rook(7, 0, false);
		pieces[7][2] = new Bishop(7, 2, false);
		pieces[7][3] = new Queen(7, 3, false);
		pieces[1][6] = new Knight(1, 6, false);
		pieces[6][1] = new Pawn(6, 1, true);
		board = new Board(pieces, blackKing, whiteKing);

		// the knight should have four legal moves
		List<int[]> moves = pieces[1][6].legalMoves(board.toByteArray());
		assertEquals(moves.size(), 4);

		// the pawn should have three legal moves
		moves = pieces[6][1].legalMoves(board.toByteArray());
		assertEquals(moves.size(), 3);

		// the queen should have 14 legal moves
		moves = pieces[7][3].legalMoves(board.toByteArray());
		assertEquals(moves.size(), 14);

		// the rook should have 8 legal moves
		moves = pieces[7][0].legalMoves(board.toByteArray());
		assertEquals(moves.size(), 8);

		// the bishop should have six legal moves
		moves = pieces[7][2].legalMoves(board.toByteArray());
		assertEquals(moves.size(), 6);

		// if the king moves to d2, the bishop should have one legal move
		move("e1", "d2", false);
		moves = pieces[7][2].legalMoves(board.toByteArray());
		assertEquals(moves.size(), 1);

		// if the rook moves to b1, the rook should have 2 legal moves
		move("a1", "b1", false);
		moves = pieces[7][1].legalMoves(board.toByteArray());
		assertEquals(moves.size(), 2);
	}

	@Test
	void testCheck() {
		// create board in which the black king is in check by the white king
		ChessPiece[][] pieces = new ChessPiece[8][8];
		King blackKing = new King(0, 4, true);
		King whiteKing = new King(0, 3, false);
		pieces[0][4] = blackKing;
		pieces[0][3] = whiteKing;
		board = new Board(pieces, blackKing, whiteKing);

		// verify that the king is in check
		assertTrue(board.isCheck(true));

		// move white king away, the black king should no longer be in check
		move("d8", "c7", false);
		assertFalse(board.isCheck(true));

		// create new pieces one at a time, verify that they can all put the king in
		// check
		pieces[0][5] = new Rook(0, 5, false);
		assertTrue(board.isCheck(true));
		pieces[0][5] = null;
		assertFalse(board.isCheck(true));
		pieces[1][3] = new Pawn(1, 3, false);
		assertTrue(board.isCheck(true));
		pieces[1][3] = null;
		assertFalse(board.isCheck(true));
		pieces[1][4] = new Queen(1, 4, false);
		assertTrue(board.isCheck(true));
		pieces[1][4] = null;
		assertFalse(board.isCheck(true));
		pieces[1][5] = new Bishop(1, 5, false);
		assertTrue(board.isCheck(true));
		pieces[1][5] = null;
		assertFalse(board.isCheck(true));
		pieces[1][5] = new Pawn(1, 5, false);
		assertTrue(board.isCheck(true));
		pieces[1][5] = null;
		assertFalse(board.isCheck(true));
		pieces[1][6] = new Knight(1, 6, false);
		assertTrue(board.isCheck(true));

	}

}
