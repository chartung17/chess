package ui;

import java.util.Scanner;

import main.Mediator;

/**
 * This class is a text user interface.
 */
public class TextUI extends UserInterface {
	// StringBuilder representation of the board
	private StringBuilder boardString;

	// Strings used to initialize boardString
	private final String header = "    a    b    c    d    e    f    g    h     \n";
	private final String separator = "  +---------------------------------------+  \n";
	private final String row = "9 | OO | XX | OO | XX | OO | XX | OO | XX | 9\n";
	private final int lineLength = header.length();

	// messages for user
	private final String optionsPrompt = "Please enter your next move. To view all options, enter \"help\".";
	private final String optionsList = "\n\nTo select a square on the chessboard, enter its column letter followed \n"
			+ "by its row number, with no spaces.\n\n" + "You may also enter any of the following commands:\n"
			+ "board     Show the board\n" + "details   View details on how the board is displayed\n"
			+ "draw      Offer to end the game in a draw\n" + "resign    Resign the game\n";
	private final String promotionOptions = "Enter \"queen\", \"rook\", \"knight\", or \"bishop\" to promote your pawn.\n";
	private final String boardDetails = "\n\nEach square is identified by a column letter (shown at the top and bottom \n"
			+ "of each column) and a row number (shown at the left and right ends of each \n"
			+ "row). Each square is represented using two characters. Empty black squares \n"
			+ "are displayed as \"XX\", and empty white squares are displayed as \"OO\". \n"
			+ "Occupied squares are displayed as a character representing the color of the \n"
			+ "occupying piece (\"B\" for black or \"W\" for white) followed by a character \n"
			+ "representing the type of piece (\"K\" for king, \"Q\" for queen, \"R\" for rook, \n"
			+ "\"N\" for knight, \"B\" for bishop, or \"P\" for pawn).\n";

	// scanner to get input from user
	Scanner in = new Scanner(System.in);

	/**
	 * This method initializes the user interface.
	 */
	public TextUI() {
		super();
		// initialize the StringBuilder representation of the board
		boardString = new StringBuilder(header + duplicate(separator + row, 8) + separator + header);

		// fix the line numbers in the board string
		for (int i = 1; i <= 8; i++) {
			int loc1 = lineLength * (18 - (2 * i));
			int loc2 = loc1 + lineLength - 2;
			boardString.replace(loc1, loc1 + 1, Integer.toString(i));
			boardString.replace(loc2, loc2 + 1, Integer.toString(i));
		}
	}

	/**
	 * This method merges a number of copies of the given String into a single
	 * String.
	 * 
	 * @param str the String to duplicate
	 * @param n   the number of times to duplicate the String
	 * @return the duplicated String
	 */
	private String duplicate(String str, int n) {
		StringBuilder sb = new StringBuilder(n * str.length());
		for (int i = 0; i < n; i++) {
			sb.append(str);
		}
		return sb.toString();
	}

	/**
	 * This method updates the specified square to display the given piece.
	 * 
	 * @param row   the row of the square
	 * @param col   the col of the square
	 * @param piece a byte representing the piece on the square
	 */
	private void updateSquare(int row, int col, byte piece) {
		int loc = (lineLength * (2 * (row + 1))) + (5 * col) + 4;
		String str = "XX";
		if (piece == 0) {
			if (((row + col) % 2) == 0) {
				str = "OO";
			}
		} else {
			str = (Character.isUpperCase(piece) ? "B" : "W") + Character.toUpperCase((char) piece);
		}
		boardString.replace(loc, loc + 2, str);
	}

	/**
	 * This method displays the board.
	 */
	private void printBoard() {
		System.out.println("\n\n\n\n\n");
		System.out.println(boardString);
	}

	@Override
	protected void updateBoard(byte[][] board) {
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {
				updateSquare(i, j, board[i][j]);
			}
		}
	}

	/**
	 * This method prompts the user for input and then handles the user's input.
	 */
	private void handleInput() {
		// print the options prompt
		System.out.println(isPromotion ? promotionOptions : optionsPrompt);

		// get user input
		String input = in.nextLine().toLowerCase();

		// handle user input
		if (isPromotion) {
			switch (input) {
			case "queen":
				isPromotion = false;
				mediator.handleSelectedOption(Mediator.QUEEN);
				break;
			case "rook":
				isPromotion = false;
				mediator.handleSelectedOption(Mediator.ROOK);
				break;
			case "knight":
				isPromotion = false;
				mediator.handleSelectedOption(Mediator.KNIGHT);
				break;
			case "bishop":
				isPromotion = false;
				mediator.handleSelectedOption(Mediator.BISHOP);
				break;
			default:
				messageLine1 = "Error: invalid input. Please try again.";
				display();
			}
		} else {
			switch (input) {
			case "board":
				display();
				break;
			case "details":
				System.out.println(boardDetails);
				handleInput();
				break;
			case "draw":
				String currentPlayer = blackToMove ? "Black" : "White";
				String otherPlayer = blackToMove ? "White" : "Black";
				while (true) {
					System.out.println(currentPlayer + " has offered to end the game in a draw. " + otherPlayer
							+ ", do you accept? Enter \"yes\" or \"no\".");
					input = in.nextLine().toLowerCase();
					if (input.equals("yes")) {
						mediator.handleSelectedOption(Mediator.DRAW);
						return;
					} else if (input.equals("no")) {
						messageLine1 = "The offer to draw was declined.";
						display();
						return;
					}
				}
			case "resign":
				while (true) {
					System.out.println((blackToMove ? "Black" : "White")
							+ ", are you sure you want to resign? Enter \"yes\" or \"no\".");
					input = in.nextLine().toLowerCase();
					if (input.equals("yes")) {
						mediator.handleSelectedOption(Mediator.RESIGN);
						return;
					} else if (input.equals("no")) {
						display();
						return;
					}
				}
			case "help":
				System.out.println(optionsList);
				handleInput();
				break;
			default:
				if ((input.length() == 2) && (input.charAt(0) >= 97) && (input.charAt(0) < 105)
						&& (input.charAt(1) >= 49) && (input.charAt(1) < 57)) {
					mediator.handleSelectedSquare(8 - (input.charAt(1) - 48), input.charAt(0) - 97);
				} else {
					messageLine1 = "Error: invalid input. Please try again.";
					display();
				}
			}
		}
	}

	@Override
	protected void display() {
		// print the board
		printBoard();

		// print the message
		System.out.println(messageLine1);
		System.out.println(messageLine2);
	}

	@Override
	public void start() {
		super.start();
		while (true) {
			// if the game is over, exit after user confirmatioon
			if (isGameOver) {
				System.out.println("Press enter to exit the game.");
				in.nextLine();
				in.close();
				break;
			}

			// get and handle user input
			handleInput();
		}
	}

}
