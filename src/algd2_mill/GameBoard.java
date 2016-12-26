package algd2_mill;
import java.io.DataOutputStream;
import java.io.IOException;

import javafx.scene.layout.BorderPane;

public class GameBoard extends BorderPane {
	
	private State m_state;
	private Controller m_controller;
	
	public GameBoard(Controller controller) throws IOException {
		m_controller = controller;
	}
	
	public void setState(State s) {
		m_state = s;
	}
	
	public void showAction(Action a, boolean isComputerAction) {
		
		if (isComputerAction) System.out.println("PC ACT");
		
		try {
			a.writeln(new DataOutputStream(System.out));
			System.out.println(m_state);
		} catch (IOException e) {
		}
	}
}
