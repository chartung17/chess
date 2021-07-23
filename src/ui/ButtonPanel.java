package ui;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

import main.Mediator;

/**
 * This class holds the buttons needed by the GUI.
 */
public class ButtonPanel extends JPanel implements ActionListener {
	enum ButtonState {
		DEFAULT, YES_NO, PROMOTION
	}

	private static final long serialVersionUID = 1L;
	private JButton resignButton, drawButton, yesButton, noButton, queenButton, rookButton, knightButton, bishopButton;
	private Mediator mediator;
	private boolean isGameOver, isResign, isDraw;

	// button labels
	private static final String RESIGN = "Resign";
	private static final String DRAW = "Offer Draw";
	private static final String YES = "Yes";
	private static final String NO = "No";
	private static final String QUEEN = "Queen";
	private static final String ROOK = "Rook";
	private static final String KNIGHT = "Knight";
	private static final String BISHOP = "Bishop";

	/**
	 * This method creates a ButtonPanel for the specified mediator.
	 * 
	 * @param mediator the mediator to use
	 */
	public ButtonPanel(Mediator mediator) {
		this.mediator = mediator;
		setAlignmentX(Component.CENTER_ALIGNMENT);
		setAlignmentY(Component.CENTER_ALIGNMENT);
		setBackground(GUI.BACKGROUND_COLOR);
		resignButton = createButton(RESIGN);
		drawButton = createButton(DRAW);
		yesButton = createButton(YES);
		noButton = createButton(NO);
		queenButton = createButton(QUEEN);
		rookButton = createButton(ROOK);
		knightButton = createButton(KNIGHT);
		bishopButton = createButton(BISHOP);
		setState(ButtonState.DEFAULT, false);
	}

	/**
	 * This method updates the state of the button panel, determining which buttons
	 * are displayed and what effect pressing those buttons will have.
	 * 
	 * @param state      the new state
	 * @param isGameOver true if the game is over and false otherwise
	 */
	public void setState(ButtonState state, boolean isGameOver) {
		removeAll();
		this.isGameOver = isGameOver;
		switch (state) {
		case DEFAULT:
			add(resignButton);
			add(drawButton);
			isResign = false;
			isDraw = false;
			break;
		case YES_NO:
			add(yesButton);
			add(noButton);
			break;
		case PROMOTION:
			add(queenButton);
			add(rookButton);
			add(knightButton);
			add(bishopButton);
		}
		repaint();
	}

	/**
	 * This method creates a button with the specified text.
	 * 
	 * @param text the button text
	 * @return the button that was created
	 */
	private JButton createButton(String text) {
		JButton button = new JButton(text);
		button.setActionCommand(text);
		button.addActionListener(this);
		button.setAlignmentX(Component.CENTER_ALIGNMENT);
		return button;
	}

	/**
	 * This method handles button clicks.
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		switch (e.getActionCommand()) {
		case RESIGN:
			isResign = true;
			mediator.handleButton(Mediator.RESIGN);
			break;
		case DRAW:
			isDraw = true;
			mediator.handleButton(Mediator.DRAW);
			break;
		case YES:
			if (isGameOver) {
				mediator.handleButton(-10);
				mediator.initializeGame();
				mediator.start();
			} else if (isResign) {
				mediator.handleSelectedOption(Mediator.RESIGN);
			} else if (isDraw) {
				mediator.handleSelectedOption(Mediator.DRAW);
			}
			break;
		case NO:
			if (isGameOver) {
				System.exit(0);
			} else {
				mediator.handleButton(-10);
			}
			break;
		case QUEEN:
			mediator.handleButton(Mediator.QUEEN);
			mediator.handleSelectedOption(Mediator.QUEEN);
			break;
		case ROOK:
			mediator.handleButton(Mediator.ROOK);
			mediator.handleSelectedOption(Mediator.ROOK);
			break;
		case KNIGHT:
			mediator.handleButton(Mediator.KNIGHT);
			mediator.handleSelectedOption(Mediator.KNIGHT);
			break;
		case BISHOP:
			mediator.handleButton(Mediator.BISHOP);
			mediator.handleSelectedOption(Mediator.BISHOP);
		}
	}
}
