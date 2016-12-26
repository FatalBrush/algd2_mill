package algd2_mill;
import java.awt.Point;
import java.io.DataOutputStream;
import java.io.IOException;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;

public class GameBoard extends BorderPane {
	
	private State m_state;
	private Controller m_controller;
	private Canvas m_canvas;
	private Label m_player1Status = new Label("asdas");
	private Label m_player2Status = new Label("asdas");
	private static final double STONESIZE = 25;
	
	public GameBoard(Controller controller) throws IOException {
		m_controller = controller;
		m_canvas = new Canvas();
		m_canvas.heightProperty().bind(heightProperty());
		m_canvas.widthProperty().bind(widthProperty());
		setCenter(m_canvas);
		setTop(new HBox(m_player1Status, m_player2Status));
		setOnMouseMoved(e -> m_player1Status.setText(""+e.getX()));
	}
	
	private void draw() {
		GraphicsContext gc = m_canvas.getGraphicsContext2D();
		// Background:
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
		
		// Stones:
		for (byte pos = 0; pos < State.BOARD.length; pos++) {
			int x = scaleX(State.BOARD[pos].x), y = scaleY(State.BOARD[pos].y);
			if (m_state.color(pos) == IController.NONE) {
				gc.setFill(Color.BISQUE);
				gc.fillOval(x-STONESIZE/4, y-STONESIZE/4, STONESIZE/2, STONESIZE/2);
			} else {
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
		return (int) (m_canvas.getWidth() * 1/20 + x * m_canvas.getWidth() * 9/10 /6);
	}
	
	private int scaleY(int y) {
		return (int) (m_canvas.getHeight() * 1/20 +  y * m_canvas.getHeight() * 9/10 /6);
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
	

}
