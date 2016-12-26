package algd2_mill;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class GameBoard extends Pane {
	
	private State m_state;
	private Controller m_controller;
	private Canvas m_canvas = new Canvas(300, 300);
	private Label m_player1Status = new Label("asdas");
	private Label m_player2Status = new Label("asdas");
	private static final double SIZE = 300;
	private static final double STONESIZE = 25;
	private List<HitBox> hitBoxes = new ArrayList<>(State.NPOS);
	
	public GameBoard(Controller controller) {
		m_controller = controller;
		getChildren().add(m_canvas);
		for (byte pos = 0; pos < State.NPOS; pos++) {
			int x = scaleX(State.BOARD[pos].x), y = scaleY(State.BOARD[pos].y);
			hitBoxes.add(new HitBox(pos, x, y, STONESIZE/2));
		}
		getChildren().addAll(hitBoxes);
	}
	
	private void draw() {
		
		// Background
		GraphicsContext gc = m_canvas.getGraphicsContext2D();
		gc.setFill(Color.BURLYWOOD);
		gc.fillRect(0, 0, m_canvas.getWidth(), m_canvas.getHeight());
		gc.setStroke(Color.BISQUE);
		gc.setLineWidth(5);
		gc.strokeRect(scaleX(0), scaleY(0), scaleX(6)-scaleX(0), scaleY(6)-scaleY(0));
		gc.strokeRect(scaleX(1), scaleY(1), scaleX(5)-scaleX(1), scaleY(5)-scaleY(1));
		gc.strokeRect(scaleX(2), scaleY(2), scaleX(4)-scaleX(2), scaleY(4)-scaleY(2));
		gc.strokeLine(scaleX(3), scaleY(0), scaleX(3), scaleY(2));
		gc.strokeLine(scaleX(4), scaleY(3), scaleX(6), scaleY(3));
		gc.strokeLine(scaleX(3), scaleY(4), scaleX(3), scaleY(6));
		gc.strokeLine(scaleX(0), scaleY(3), scaleX(2), scaleY(3));
		for (byte pos = 0; pos < State.BOARD.length; pos++) {
			int x = scaleX(State.BOARD[pos].x), y = scaleY(State.BOARD[pos].y);
			gc.setFill(Color.BISQUE);
			gc.fillOval(x-STONESIZE/4, y-STONESIZE/4, STONESIZE/2, STONESIZE/2);
		}
		
		// Stones
		for (byte pos = 0; pos < State.BOARD.length; pos++) {
			int x = scaleX(State.BOARD[pos].x), y = scaleY(State.BOARD[pos].y);
			if (m_state.color(pos) != IController.NONE) {
				if (m_state.color(pos) == IController.BLACK) gc.setFill(Color.BLACK);
				else if (m_state.color(pos) == IController.WHITE) gc.setFill(Color.WHITE);
				gc.fillOval(x-STONESIZE/2, y-STONESIZE/2, STONESIZE, STONESIZE);
			}
		}
	}
	
	public void setState(State s) {
		m_state = s;
	}
	
	private int scaleX(int x) {
		return (int) (SIZE * 1/20 + x * SIZE * 9/10 /6);
	}
	
	private int scaleY(int y) {
		return (int) (SIZE * 1/20 +  y * SIZE * 9/10 /6);
	}
	
	public void showAction(Action a, boolean isComputerAction) {
		
		if (isComputerAction) System.out.println("PC ACT");
		
		try {
			a.writeln(new DataOutputStream(System.out));
			System.out.println(m_state);
		} catch (IOException e) {
		}
		draw();
	}
	
	class HitBox extends Circle {
		byte pos;
		public HitBox(byte pos, int x, int y, double r) {
			super(x, y, r);
			this.pos = pos;
			setFill(Color.TRANSPARENT);
			setOnMouseClicked(e -> {
				m_controller.play(new Placing(m_controller.humanColor(), pos));
				m_controller.compute();
			});
			setOnMouseEntered(e -> setStroke(Color.RED));
			setOnMouseExited(e -> setStroke(Color.TRANSPARENT));
		}
	}
}


