package test;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import chess.Board;
import chess.Game;
import main.Mediator;

/**
 * This class is used to test the Game class.
 */
class GameTest {
	UserInterfaceTester ui;
	Mediator mediator;
	Game game;

	@BeforeEach
	void setUp() {
		ui = new UserInterfaceTester();
		mediator = ui.getMediator();
		game = (Game) mediator.getGame();
	}

	/**
	 * This method executes a move specified by the names of the start and stop
	 * squares
	 * 
	 * @param start the name of the square where the piece starts
	 * @param stop  the name of the square where the piece stops
	 */
	void move(String start, String stop) {
		int startRow = 56 - start.charAt(1);
		int stopRow = 56 - stop.charAt(1);
		int startCol = start.charAt(0) - 97;
		int stopCol = stop.charAt(0) - 97;
		if (game.handleSelectedSquare(startRow, startCol) != Board.SELECT)
			throw new IllegalArgumentException("Invalid selection");
		if (game.handleSelectedSquare(stopRow, stopCol) == Board.FAILURE)
			throw new IllegalArgumentException("Invalid move");
	}

	/**
	 * This method executes a series of move that result in a white pawn in square
	 * a8 ready to be promoted and verifies that control switches properly between
	 * the players
	 */
	void promoteWhitePawn() {
		assertFalse(ui.blackToMove());
		move("a2", "a4");
		assertTrue(ui.blackToMove());
		move("b7", "b5");
		assertFalse(ui.blackToMove());
		move("a4", "b5");
		assertTrue(ui.blackToMove());
		move("h7", "h5");
		assertFalse(ui.blackToMove());
		move("b5", "b6");
		assertTrue(ui.blackToMove());
		move("g7", "g5");
		assertFalse(ui.blackToMove());
		move("b6", "b7");
		assertTrue(ui.blackToMove());
		move("e7", "e6");
		assertFalse(ui.blackToMove());
		move("b7", "a8");

		// white's pawn is being promoted, so it should still be white's turn
		assertFalse(ui.blackToMove());
	}

	/**
	 * This method executes a series of move that result in a black pawn in square
	 * a1 ready to be promoted
	 */
	void promoteBlackPawn() {
		move("f2", "f4");
		move("a7", "a5");
		move("b2", "b4");
		move("a5", "b4");
		move("h2", "h4");
		move("b4", "b3");
		move("g2", "g4");
		move("b3", "b2");
		move("e2", "e3");
		move("b2", "a1");
	}

	@Test
	void testPromoteWhiteQueen() {
		promoteWhitePawn();
		assertTrue(ui.isPromotion());

		// selecting a square should result in an error message
		game.handleSelectedSquare(0, 0);
		assertTrue(ui.getMessage().startsWith("Error"));

		// it should still be white's turn
		assertFalse(ui.blackToMove());

		// entering the QUEEN option should promote the pawn to a queen
		game.handleSelectedOption(Mediator.QUEEN);
		assertTrue(ui.getMessage().contains("select a piece to move"));
		assertTrue(ui.getBoard()[0][0] == 'q');

		// it should now be black's turn
		assertTrue(ui.blackToMove());
	}

	@Test
	void testPromoteBlackQueen() {
		promoteBlackPawn();
		assertTrue(ui.isPromotion());

		// selecting a square should result in an error message
		game.handleSelectedSquare(0, 0);
		assertTrue(ui.getMessage().startsWith("Error"));

		// entering the QUEEN option should promote the pawn to a queen
		game.handleSelectedOption(Mediator.QUEEN);
		assertTrue(ui.getMessage().contains("select a piece to move"));
		assertTrue(ui.getBoard()[7][0] == 'Q');
	}

	@Test
	void testPromoteWhiteRook() {
		promoteWhitePawn();

		// entering the ROOK option should promote the pawn to a rook
		game.handleSelectedOption(Mediator.ROOK);
		assertTrue(ui.getMessage().contains("select a piece to move"));
		assertTrue(ui.getBoard()[0][0] == 'r');
	}

	@Test
	void testPromoteBlackRook() {
		promoteBlackPawn();

		// entering the ROOK option should promote the pawn to a rook
		game.handleSelectedOption(Mediator.ROOK);
		assertTrue(ui.getMessage().contains("select a piece to move"));
		assertTrue(ui.getBoard()[7][0] == 'R');
	}

	@Test
	void testPromoteWhiteKnight() {
		promoteWhitePawn();

		// entering the KNIGHT option should promote the pawn to a knight
		game.handleSelectedOption(Mediator.KNIGHT);
		assertTrue(ui.getMessage().contains("select a piece to move"));
		assertTrue(ui.getBoard()[0][0] == 'n');
	}

	@Test
	void testPromoteBlackKnight() {
		promoteBlackPawn();

		// entering the KNIGHT option should promote the pawn to a knight
		game.handleSelectedOption(Mediator.KNIGHT);
		assertTrue(ui.getMessage().contains("select a piece to move"));
		assertTrue(ui.getBoard()[7][0] == 'N');
	}

	@Test
	void testPromoteWhiteBishop() {
		promoteWhitePawn();

		// entering the BISHOP option should promote the pawn to a bishop
		game.handleSelectedOption(Mediator.BISHOP);
		assertTrue(ui.getMessage().contains("select a piece to move"));
		assertTrue(ui.getBoard()[0][0] == 'b');
	}

	@Test
	void testPromoteBlackBishop() {
		promoteBlackPawn();

		// entering the BISHOP option should promote the pawn to a bishop
		game.handleSelectedOption(Mediator.BISHOP);
		assertTrue(ui.getMessage().contains("select a piece to move"));
		assertTrue(ui.getBoard()[7][0] == 'B');
	}

	@Test
	void testIllegalMove() {
		// invalid selection
		game.handleSelectedSquare(4, 4);
		assertTrue(ui.getMessage().startsWith("Error"));

		// valid selection
		game.handleSelectedSquare(6, 0);
		assertTrue(ui.getMessage().toLowerCase().contains("pawn on square a2"));

		// invalid move
		game.handleSelectedSquare(6, 6);
		assertTrue(ui.getMessage().startsWith("Error"));
	}

	@Test
	void testStart() {
		game.start();
		assertTrue(ui.getMessage().toLowerCase().contains("new game"));
	}

	@Test
	void testCheckmate() {
		move("f2", "f3");
		assertFalse(ui.isGameOver());
		move("e7", "e5");
		assertFalse(ui.isGameOver());
		move("g2", "g4");
		assertFalse(ui.isGameOver());
		move("d8", "h4");
		assertTrue(ui.isGameOver());
		assertTrue(ui.getMessage().toLowerCase().contains("checkmate"));
	}

	@Test
	void testAgreedDraw() {
		game.handleSelectedOption(Mediator.DRAW);
		assertTrue(ui.isGameOver());
		assertTrue(ui.getMessage().toLowerCase().contains("draw"));
	}

	@Test
	void testResign() {
		game.handleSelectedOption(Mediator.RESIGN);
		assertTrue(ui.isGameOver());
		assertTrue(ui.getMessage().toLowerCase().contains("resign"));
	}

	@Test
	void testStalemate() {
		// moves needed for fastest stalemate found at
		// https://www.chess.com/forum/view/fun-with-chess/fastest-stalemate
		move("c2", "c4");
		assertFalse(ui.isGameOver());
		move("h7", "h5");
		assertFalse(ui.isGameOver());
		move("h2", "h4");
		assertFalse(ui.isGameOver());
		move("a7", "a5");
		assertFalse(ui.isGameOver());
		move("d1", "a4");
		assertFalse(ui.isGameOver());
		move("a8", "a6");
		assertFalse(ui.isGameOver());
		move("a4", "a5");
		assertFalse(ui.isGameOver());
		move("a6", "h6");
		assertFalse(ui.isGameOver());
		move("a5", "c7");
		assertFalse(ui.isGameOver());
		move("f7", "f6");
		assertFalse(ui.isGameOver());
		move("c7", "d7");
		assertFalse(ui.isGameOver());
		move("e8", "f7");
		assertFalse(ui.isGameOver());
		move("d7", "b7");
		assertFalse(ui.isGameOver());
		move("d8", "d3");
		assertFalse(ui.isGameOver());
		move("b7", "b8");
		assertFalse(ui.isGameOver());
		move("d3", "h7");
		assertFalse(ui.isGameOver());
		move("b8", "c8");
		assertFalse(ui.isGameOver());
		move("f7", "g6");
		assertFalse(ui.isGameOver());
		move("c8", "e6");
		assertTrue(ui.isGameOver());
		assertTrue(ui.getMessage().toLowerCase().contains("stalemate"));
	}

	@Test
	void testThreefoldRepetition() {
		move("b1", "c3");
		assertFalse(ui.isGameOver());
		move("b8", "c6");
		assertFalse(ui.isGameOver());
		move("c3", "b1");
		assertFalse(ui.isGameOver());
		move("c6", "b8");
		assertFalse(ui.isGameOver());
		move("b1", "c3");
		assertFalse(ui.isGameOver());
		move("b8", "c6");
		assertFalse(ui.isGameOver());
		move("c3", "b1");
		assertFalse(ui.isGameOver());
		move("c6", "b8");
		assertTrue(ui.isGameOver());
		assertTrue(ui.getMessage().toLowerCase().contains("threefold repetition"));
	}

	@Test
	void testFiftyMoveRule() {
		// make exactly fifty moves per color with no captures and no pawn moves
		// without triggering the threefold repetition move
		String[] whiteMoveSequence = { "b1", "a3", "b5", "c3", "d5", "e3", "f5", "g3", "h5" };
		String[] blackMoveSequence = { "b8", "a6", "b4", "c6", "d4", "e6", "f4", "g6", "h4" };

		// move each queenside knight 16 times, returning to starting position at the
		// end
		for (int i = 0; i < 8; i++) {
			move(whiteMoveSequence[i], whiteMoveSequence[i + 1]);
			assertFalse(ui.isGameOver());
			move(blackMoveSequence[i], blackMoveSequence[i + 1]);
			assertFalse(ui.isGameOver());
		}
		for (int i = 8; i > 0; i--) {
			move(whiteMoveSequence[i], whiteMoveSequence[i - 1]);
			assertFalse(ui.isGameOver());
			move(blackMoveSequence[i], blackMoveSequence[i - 1]);
			assertFalse(ui.isGameOver());
		}

		// move each kingside knight to a new position
		move("g1", "h3");
		move("g8", "h6");

		// move each queenside knight 16 times, returning to starting position at the
		// end
		for (int i = 0; i < 8; i++) {
			move(whiteMoveSequence[i], whiteMoveSequence[i + 1]);
			assertFalse(ui.isGameOver());
			move(blackMoveSequence[i], blackMoveSequence[i + 1]);
			assertFalse(ui.isGameOver());
		}
		for (int i = 8; i > 0; i--) {
			move(whiteMoveSequence[i], whiteMoveSequence[i - 1]);
			assertFalse(ui.isGameOver());
			move(blackMoveSequence[i], blackMoveSequence[i - 1]);
			assertFalse(ui.isGameOver());
		}

		// move each kingside knight to a new position
		move("h3", "g5");
		move("h6", "g4");

		// move each queenside knight 16 times, returning to starting position at the
		// end
		for (int i = 0; i < 8; i++) {
			move(whiteMoveSequence[i], whiteMoveSequence[i + 1]);
			assertFalse(ui.isGameOver());
			move(blackMoveSequence[i], blackMoveSequence[i + 1]);
			assertFalse(ui.isGameOver());
		}
		for (int i = 8; i > 0; i--) {
			move(whiteMoveSequence[i], whiteMoveSequence[i - 1]);
			assertFalse(ui.isGameOver());
			move(blackMoveSequence[i], blackMoveSequence[i - 1]);
			if (i > 1) {
				assertFalse(ui.isGameOver());
			}
		}

		// fifty-move rule should trigger
		assertTrue(ui.isGameOver());
		assertTrue(ui.getMessage().toLowerCase().contains("fifty-move rule"));
	}

	@Test
	void testFiftyMoveRuleWithPawnMove() {
		// make exactly fifty moves per color plus a pawn move
		// without triggering the threefold repetition move
		String[] whiteMoveSequence = { "b1", "a3", "b5", "c3", "d5", "e3", "f5", "g3", "h5" };
		String[] blackMoveSequence = { "b8", "a6", "b4", "c6", "d4", "e6", "f4", "g6", "h4" };

		// move each queenside knight 16 times, returning to starting position at the
		// end
		for (int i = 0; i < 8; i++) {
			move(whiteMoveSequence[i], whiteMoveSequence[i + 1]);
			assertFalse(ui.isGameOver());
			move(blackMoveSequence[i], blackMoveSequence[i + 1]);
			assertFalse(ui.isGameOver());
		}
		for (int i = 8; i > 0; i--) {
			move(whiteMoveSequence[i], whiteMoveSequence[i - 1]);
			assertFalse(ui.isGameOver());
			move(blackMoveSequence[i], blackMoveSequence[i - 1]);
			assertFalse(ui.isGameOver());
		}

		// move each kingside knight to a new position
		move("g1", "h3");
		move("g8", "h6");

		// move each queenside knight 16 times, returning to starting position at the
		// end
		for (int i = 0; i < 8; i++) {
			move(whiteMoveSequence[i], whiteMoveSequence[i + 1]);
			assertFalse(ui.isGameOver());
			move(blackMoveSequence[i], blackMoveSequence[i + 1]);
			assertFalse(ui.isGameOver());
		}
		for (int i = 8; i > 0; i--) {
			move(whiteMoveSequence[i], whiteMoveSequence[i - 1]);
			assertFalse(ui.isGameOver());
			move(blackMoveSequence[i], blackMoveSequence[i - 1]);
			assertFalse(ui.isGameOver());
		}

		// move one kingside knight and one pawn
		move("h3", "g5");
		move("f7", "f6");

		// move each queenside knight 16 times, returning to starting position at the
		// end
		for (int i = 0; i < 8; i++) {
			move(whiteMoveSequence[i], whiteMoveSequence[i + 1]);
			assertFalse(ui.isGameOver());
			move(blackMoveSequence[i], blackMoveSequence[i + 1]);
			assertFalse(ui.isGameOver());
		}
		for (int i = 8; i > 0; i--) {
			move(whiteMoveSequence[i], whiteMoveSequence[i - 1]);
			assertFalse(ui.isGameOver());
			move(blackMoveSequence[i], blackMoveSequence[i - 1]);
			if (i > 1) {
				assertFalse(ui.isGameOver());
			}
		}

		// fifty-move rule should not trigger
		assertFalse(ui.isGameOver());
	}

	@Test
	void testFiftyMoveRuleWithCapture() {
		// make fifty moves per color plus a capture
		// without triggering the threefold repetition move
		String[] whiteMoveSequence = { "b1", "a3", "b5", "c3", "d5", "e3", "f5", "g3", "h5" };
		String[] blackMoveSequence = { "b8", "a6", "b4", "c6", "d4", "e6", "f4", "g6", "h4" };

		// move each queenside knight 16 times, returning to starting position at the
		// end
		for (int i = 0; i < 8; i++) {
			move(whiteMoveSequence[i], whiteMoveSequence[i + 1]);
			assertFalse(ui.isGameOver());
			move(blackMoveSequence[i], blackMoveSequence[i + 1]);
			assertFalse(ui.isGameOver());
		}
		for (int i = 8; i > 0; i--) {
			move(whiteMoveSequence[i], whiteMoveSequence[i - 1]);
			assertFalse(ui.isGameOver());
			move(blackMoveSequence[i], blackMoveSequence[i - 1]);
			assertFalse(ui.isGameOver());
		}

		// move each kingside knight to a new position
		move("g1", "h3");
		move("g8", "f6");

		// move each queenside knight 16 times, returning to starting position at the
		// end
		for (int i = 0; i < 8; i++) {
			move(whiteMoveSequence[i], whiteMoveSequence[i + 1]);
			assertFalse(ui.isGameOver());
			move(blackMoveSequence[i], blackMoveSequence[i + 1]);
			assertFalse(ui.isGameOver());
		}
		for (int i = 8; i > 0; i--) {
			move(whiteMoveSequence[i], whiteMoveSequence[i - 1]);
			assertFalse(ui.isGameOver());
			move(blackMoveSequence[i], blackMoveSequence[i - 1]);
			assertFalse(ui.isGameOver());
		}

		// move knights to capture
		move("h3", "f4");
		move("f6", "h5");
		move("f4", "h5");

		// move pieces out of the way
		move("h8", "g8");
		move("h5", "g3");
		move("g8", "h8");
		move("g3", "e4");
		move("h8", "g8");

		// move each queenside knight 16 times, returning to starting position at the
		// end
		for (int i = 0; i < 8; i++) {
			move(whiteMoveSequence[i], whiteMoveSequence[i + 1]);
			assertFalse(ui.isGameOver());
			move(blackMoveSequence[i], blackMoveSequence[i + 1]);
			assertFalse(ui.isGameOver());
		}
		for (int i = 8; i > 0; i--) {
			move(whiteMoveSequence[i], whiteMoveSequence[i - 1]);
			assertFalse(ui.isGameOver());
			move(blackMoveSequence[i], blackMoveSequence[i - 1]);
			if (i > 1) {
				assertFalse(ui.isGameOver());
			}
		}

		// fifty-move rule should not trigger
		assertFalse(ui.isGameOver());
	}

}
