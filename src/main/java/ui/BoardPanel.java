package ui;

import java.util.List;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.*;

import chess.Board;
import main.Mediator;

/**
 * This class is used to display the board in a GUI.
 */
/**
 * @author chart
 *
 */
public class BoardPanel extends JPanel implements MouseListener {
	private static final long serialVersionUID = 1L;
	public static final int BOARD_SIZE = 600;
	public static final int BORDER_SIZE = 15;
	public static final int SQUARE_SIZE = BOARD_SIZE / 8;
	private static final Color MOVE_COLOR = new Color(255, 255, 0, 200); // transparent yellow
	private BufferedImage image = new BufferedImage(BOARD_SIZE + 2 * BORDER_SIZE,
			BOARD_SIZE + 2 * BORDER_SIZE, BufferedImage.TYPE_INT_ARGB);
	public static final Font PIECE_FONT = new Font(Font.MONOSPACED, Font.BOLD, 60);
	private byte[][] board = new byte[8][8];
	private Mediator mediator;
	private int[] selectedSquare = null;
	private boolean handlingButton = false;

	/**
	 * This method creates a BoardPanel with the specified mediator.
	 * 
	 * @param mediator the mediator to use
	 */
	public BoardPanel(Mediator mediator) {
		this.mediator = mediator;
		setSize(BOARD_SIZE + 2 * BORDER_SIZE, BOARD_SIZE + 2 * BORDER_SIZE);
		setDoubleBuffered(true);
		this.addMouseListener(this);
		drawBoard();
	}

	/**
	 * This method displays the board to the user.
	 */
	public void paint(Graphics g) {
		g.drawImage(image, 0, 0, null);
	}

	/**
	 * @param board the board to set
	 */
	public void setBoard(byte[][] board) {
		this.board = board;
	}

	/**
	 * This method unselects the selected square.
	 */
	public void unselectSquare() {
		selectedSquare = null;
		drawBoard();
	}

	/**
	 * This method draws the board.
	 */
	public void drawBoard() {
		Graphics g = image.getGraphics();
		g.setFont(PIECE_FONT);

		// draw background
		g.setColor(GUI.BACKGROUND_COLOR);
		g.fillRect(0, 0, BOARD_SIZE + 2 * BORDER_SIZE, BOARD_SIZE + 2 * BORDER_SIZE);

		// draw squares
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {
				g.setColor(((i + j) % 2 == 0) ? Color.WHITE : Color.DARK_GRAY);
				g.fillRect(SQUARE_SIZE * j + BORDER_SIZE, SQUARE_SIZE * i + BORDER_SIZE,
						SQUARE_SIZE, SQUARE_SIZE);
			}
		}

		// draw legal moves
		if (selectedSquare != null) {
			List<int[]> moves = mediator.getLegalMoves(selectedSquare[0], selectedSquare[1]);
			moves.add(new int[] { selectedSquare[0], selectedSquare[1] });
			drawLegalMoves(moves, g);
		}

		// draw pieces
		drawPieces(g);

		// run the paint() method
		repaint();
	}

	/**
	 * This method highlights the selected piece and any squares to which it can legally move.
	 * 
	 * @param moves the legal moves of the selected piece
	 * @param g     the graphics on which to draw
	 */
	private void drawLegalMoves(List<int[]> moves, Graphics g) {
		for (int[] move : moves) {
			g.setColor(MOVE_COLOR);
			g.fillRect(SQUARE_SIZE * move[1] + BORDER_SIZE, SQUARE_SIZE * move[0] + BORDER_SIZE,
					SQUARE_SIZE, SQUARE_SIZE);
		}
	}

	/**
	 * This method draws the chess pieces.
	 * 
	 * @param g the graphics on which to draw
	 */
	private void drawPieces(Graphics g) {
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {
				char piece = (char) board[i][j];
				if (piece != '\0') {
					String filename = "";
					char pieceChar = '\0';
					switch (piece) {
					case 'Q':
						filename = "blackQueen.png";
						pieceChar = '\u265B';
						break;
					case 'R':
						filename = "blackRook.png";
						pieceChar = '\u265C';
						break;
					case 'P':
						filename = "blackPawn.png";
						pieceChar = '\u265F';
						break;
					case 'K':
						filename = "blackKing.png";
						pieceChar = '\u265A';
						break;
					case 'N':
						filename = "blackKnight.png";
						pieceChar = '\u265E';
						break;
					case 'B':
						filename = "blackBishop.png";
						pieceChar = '\u265F';
						break;
					case 'q':
						filename = "whiteQueen.png";
						pieceChar = '\u2655';
						break;
					case 'r':
						filename = "whiteRook.png";
						pieceChar = '\u2656';
						break;
					case 'p':
						filename = "whitePawn.png";
						pieceChar = '\u2659';
						break;
					case 'k':
						filename = "whiteKing.png";
						pieceChar = '\u2654';
						break;
					case 'n':
						filename = "whiteKnight.png";
						pieceChar = '\u2658';
						break;
					case 'b':
						filename = "whiteBishop.png";
						pieceChar = '\u2657';
					}
					try {
						BufferedImage pieceImage = ImageIO
								.read(getClass().getResourceAsStream(filename));
						g.drawImage(pieceImage, SQUARE_SIZE * j + BORDER_SIZE + 7,
								SQUARE_SIZE * i + BORDER_SIZE + 7, 60, 60, null);
					} catch (IOException e) {
						g.setColor(Character.isUpperCase(piece) ? Color.BLACK : Color.LIGHT_GRAY);
//						g.drawString("" + Character.toUpperCase(piece), SQUARE_SIZE * j + BORDER_SIZE + 10,
//								SQUARE_SIZE * (i + 1) + BORDER_SIZE - 10);
						g.drawString("" + pieceChar, SQUARE_SIZE * j + BORDER_SIZE + 8,
								SQUARE_SIZE * (i + 1) + BORDER_SIZE - 12);
					}
				}
			}
		}
	}

	/**
	 * This method handles a click on the board.
	 */
	@Override
	public void mouseReleased(MouseEvent e) {
		if (handlingButton)
			return;
		if ((e.getX() < BORDER_SIZE) || (e.getY() < BORDER_SIZE))
			return;
		int row = (int) ((e.getY() - BORDER_SIZE) / SQUARE_SIZE);
		int col = (int) ((e.getX() - BORDER_SIZE) / SQUARE_SIZE);
		if ((row >= 0) && (row < 8) && (col >= 0) && (col < 8)) {
			int result = mediator.handleSelectedSquare(row, col);
			if ((result == Board.SELECT) || (result == Board.PROMOTE)) {
				selectedSquare = new int[] { row, col };
			} else {
				selectedSquare = null;
			}
		}
		drawBoard();
	}

	/**
	 * @param handlingButton the handlingButton to set
	 */
	public void setHandlingButton(boolean handlingButton) {
		this.handlingButton = handlingButton;
	}

	/**
	 * This method does nothing.
	 */
	@Override
	public void mouseEntered(MouseEvent e) {
		// do nothing
	}

	/**
	 * This method does nothing.
	 */
	@Override
	public void mouseExited(MouseEvent e) {
		// do nothing
	}

	/**
	 * This method does nothing.
	 */
	@Override
	public void mousePressed(MouseEvent e) {
		// do nothing
	}

	/**
	 * This method does nothing.
	 */
	@Override
	public void mouseClicked(MouseEvent e) {
		// do nothing
	}
}
