package algd2_mill;
import java.io.DataOutputStream;
import java.io.IOException;

import javafx.scene.layout.BorderPane;

public class GameBoard extends BorderPane {
	
	private State m_state;
	private Controller m_controller;
	
	public GameBoard(Controller controller) throws IOException {
		m_controller = controller;
		controller.startHumanGame();
	}
	
	public void setState(State s) {
		m_state = s;
		System.out.println(s);
	}
	
	public void showAction(Action a, boolean isComputerAction) {
		
		
		
		try {
			a.writeln(new DataOutputStream(System.out));
		} catch (IOException e) {
		}
	}
}
