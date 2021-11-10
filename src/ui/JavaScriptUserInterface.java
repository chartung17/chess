package ui;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;

@RestController
@SpringBootApplication
public class JavaScriptUserInterface extends UserInterface {
	private byte[][] board = new byte[8][8];
	private int[] selectedSquare = null;
	private boolean handlingButton = false;

	@Override
	protected void updateBoard(byte[][] board) {
		this.board = board;
	}

	@Override
	protected void display() {
		// do nothing
	}
	
	private static JavaScriptUserInterface getUI(HttpSession session) {
		JavaScriptUserInterface ui = (JavaScriptUserInterface) session.getAttribute("ui");
		if (ui == null) {
			ui = new JavaScriptUserInterface();
			session.setAttribute("ui", ui);
		}
		return ui;
	}

	@RequestMapping(value = "/square/{row:[\\d]+}/{col:[\\d]+}", method = RequestMethod.GET)
	@CrossOrigin
	public static String handleSelectedSquare(@PathVariable int row, @PathVariable int col,
			HttpSession session) {
		JavaScriptUserInterface ui = getUI(session);
		ui.mediator.handleSelectedSquare(row, col);
		System.out.println(ui.board);
		return "{\"status\":200,\"message\":\"success\"}";
	}

	public static void main(String[] args) {
		SpringApplication.run(JavaScriptUserInterface.class, args);
	}

}
