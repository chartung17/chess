package main;

import ui.GUI;
import ui.TextUI;
import ui.UserInterface;

/**
 * This class is used to run the app. It can be run with either zero or one command line arguments.
 * If one argument is given, it must be either "gui" to use the graphical user interface or "text"
 * to use the text user interface. If no argument is given, the graphical user interface will be
 * used by default.
 */
public class Main {
	private static String uiType = "gui";
	private static UserInterface ui = null;

	/**
	 * This method runs the app.
	 * 
	 * @param args the command line arguments; should be empty or contain "text" or "gui" as its
	 *             only element
	 */
	public static void main(String[] args) {
		// set uiType based on args
		if (args.length > 1) {
			System.out.println("Error: this program should be used with at most one argument");
			System.exit(1);
		} else if (args.length == 0) {
			// keep default uiType if not specified
		} else if (args[0].equals("text")) {
			uiType = "text";
		} else if (args[0].equals("gui")) {
			uiType = "gui";
		} else {
			System.out.println("Error: argument must be \"text\" or \"gui\"");
			System.exit(1);
		}

		// initialize user interface
		if (uiType.equals("text")) {
			ui = new TextUI();
		} else if (uiType.equals("gui")) {
			ui = new GUI();
		}

		// start user interface
		if (ui != null) {
			ui.start();
		}
	}

}
