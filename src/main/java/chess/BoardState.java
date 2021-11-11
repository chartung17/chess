package chess;

import java.util.Map;

/**
 * This class contains all board state information needed by the threefold repetition rule
 */
public class BoardState implements Comparable<BoardState> {
	// since a large number of BoardState objects may exist simultaneously, instance
	// variables types
	// are chosen in order to minimize memory requirements while maintaining
	// readable code

	// the positions of pieces are stored as a long[8], since one long[8] can store
	// the same
	// information as one byte[8][8] in slightly more than 1/3 of the space
	long[] rows = new long[8];

	// other state info is stored in a single byte, since one byte can store the
	// same information as
	// 6 booleans in 1/6 of the space; the first two bits of stateInfo are always 0,
	// while remaining
	// bits indicate which player is moving next, whether en passant is legal, and
	// which players can
	// legally castle on which side
	private byte stateInfo;

	/**
	 * This method creates a BoardState object with the specified parameters.
	 * 
	 * @param board                   an 8x8 matrix representing the board
	 * @param blackToMove             true if it is black's turn to move and false if it is white's
	 *                                turn
	 * @param isEnPassantLegal        true if en passant is legal and false otherwise
	 * @param canBlackCastleKingside  true if black can castle kingside and false otherwise
	 * @param canBlackCastleQueenside true if black can castle queenside and false otherwise
	 * @param canWhiteCastleKingside  true if white can castle kingside and false otherwise
	 * @param canWhiteCastleQueenside true if white can castle queenside and false otherwise
	 */
	public BoardState(byte[][] board, boolean blackToMove, boolean isEnPassantLegal,
			boolean canBlackCastleKingside, boolean canBlackCastleQueenside,
			boolean canWhiteCastleKingside, boolean canWhiteCastleQueenside) {
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {
				rows[i] |= ((((long) board[i][j]) & 0b1111_1111) << (8 * (7 - j)));
			}
		}
		stateInfo |= (blackToMove ? 0b10_0000 : 0);
		stateInfo |= (isEnPassantLegal ? 0b1_0000 : 0);
		stateInfo |= (canBlackCastleKingside ? 0b1000 : 0);
		stateInfo |= (canBlackCastleQueenside ? 0b100 : 0);
		stateInfo |= (canWhiteCastleKingside ? 0b10 : 0);
		stateInfo |= (canWhiteCastleQueenside ? 0b1 : 0);
	}

	@Override
	public int compareTo(BoardState other) {
		// first compare the boards
		for (int i = 0; i < 8; i++) {
			long thisRow = this.rows[i];
			long otherRow = other.rows[i];
			if (thisRow != otherRow)
				return (thisRow > otherRow) ? 1 : -1;
		}
		// if the boards are the same, compare the state info
		// if the boards and state info are the same, the board states are equal
		return this.stateInfo - other.stateInfo;
	}

	/**
	 * This method adds this BoardState to the given Map. If the BoardState was already in the map,
	 * its count is incremented by 1, otherwise its count is set to 1. The count of this BoardState
	 * is then returned.
	 * 
	 * @param map a Map which maps BoardState objects to the number of times those BoardState
	 *            objects have appeared
	 * @return the count of this BoardState in the given map after insertion
	 */
	public int insertInto(Map<BoardState, Integer> map) {
		Integer count = map.getOrDefault(this, 0) + 1;
		map.put(this, count);
		return count;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof BoardState) {
			return compareTo((BoardState) obj) == 0;
		} else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		long hash = stateInfo;
		for (long row : rows) {
			hash += row;
		}
		return (int) (hash ^ (hash >> 32));
	}

}
