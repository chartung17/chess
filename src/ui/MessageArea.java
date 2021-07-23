package ui;

import java.awt.*;

import javax.swing.*;

/**
 * This class is used in the GUI to display messages to the user.
 */
public class MessageArea extends JTextArea {
	private static final long serialVersionUID = 1L;
	public static final Font MESSAGE = new Font(Font.DIALOG, Font.PLAIN, 28);
	public static final int MESSAGE_WIDTH = 300;

	/**
	 * This method creates a new MessageArea.
	 */
	public MessageArea() {
		setEditable(false);
		setFont(MESSAGE);
		setLineWrap(true);
		setWrapStyleWord(true);
		setSize(new Dimension(MESSAGE_WIDTH, MESSAGE_WIDTH));
		setBackground(GUI.BACKGROUND_COLOR);
		setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
	}

	/**
	 * This method sets the message.
	 * 
	 * @param messageLine1 the first line of the message
	 * @param messageLine2 the second line of the message
	 * @param isGameOver   true if the game is over and false otherwise
	 */
	public void setMessage(String messageLine1, String messageLine2, boolean isGameOver) {
		String message = (messageLine1.length() == 0) ? messageLine2 : messageLine1 + '\n' + messageLine2;
		if (isGameOver) {
			message += "\n\nWould you like to play again?";
		}
		setText(message);
	}
}
