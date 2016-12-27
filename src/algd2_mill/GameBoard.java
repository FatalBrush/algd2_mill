package algd2_mill;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class GameBoard extends Pane {
	
	private State m_state;
	private Controller m_controller;
	private Canvas m_canvas = new Canvas();
	private Label m_player1Status = new Label("asdas");
	private Label m_player2Status = new Label("asdas");
	private static final double STONESIZE = 25;
	private List<HitBox> m_hitBoxes = new ArrayList<>(State.NPOS);
	private ActionPM m_actionResultingInMill; // used for taking action in hitbox clickevent. Not null means Taking Action is in progress.
	private byte m_from = -1; // used for moving and jumping actions in hitbox clickevent. Greater -1 means moving/jumping action is in progress.
	
	public GameBoard(Controller controller) {
		m_controller = controller;
		getChildren().add(m_canvas);
		for (byte pos = 0; pos < State.NPOS; pos++) {
			m_hitBoxes.add(new HitBox(pos, STONESIZE/2));
		}
		getChildren().addAll(m_hitBoxes);
		
		// make gui dynamic
		m_canvas.widthProperty().bind(widthProperty());
		m_canvas.heightProperty().bind(heightProperty());
		InvalidationListener listener = l -> {
			m_hitBoxes.forEach(h -> h.calculatePosition());
			draw();
		};
		heightProperty().addListener(listener);
		widthProperty().addListener(listener);
	}
	
	public void draw() {
		// Background
		GraphicsContext gc = m_canvas.getGraphicsContext2D();
		gc.setFill(Color.BURLYWOOD);
		gc.fillRect(0, 0, m_canvas.getWidth(), m_canvas.getHeight());
		gc.setStroke(Color.SADDLEBROWN);
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
			gc.setFill(Color.SADDLEBROWN);
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
		return (int) (m_canvas.getWidth() * 1/12 + x * m_canvas.getWidth() * 5/6 /6);
	}
	
	private int scaleY(int y) {
		return (int) (m_canvas.getHeight() * 1/12 +  y * m_canvas.getHeight() * 5/6 /6);
	}
	
	public void showAction(Action a, boolean isComputerAction) {
		
//		if (isComputerAction) System.out.println("PC ACT");
//		
//		try {
//			a.writeln(new DataOutputStream(System.out));
//			System.out.println(m_state);
//		} catch (IOException e) {
//		}
		m_controller.m_gameTree.print();
		draw();
	}
	
	class HitBox extends Circle {
		byte pos;
		public HitBox(byte pos, double r) {
			super(r);
			this.pos = pos;
			calculatePosition();
			setFill(Color.TRANSPARENT);
			setOnMouseClicked(e -> {
				byte c = m_controller.humanColor();
				Action a = null;
				if (m_from > -1) { // we're in a moving or jumping action and need to complete it
					a = new Moving(c, m_from, pos);
					m_from = -1; // must chose from and to again if failed
				}
				else if (m_actionResultingInMill != null) { // if we made a mill previous move
					a = new Taking(m_actionResultingInMill, pos); // take the clicked stone
				}
				else if (m_state.placingPhase(c)) {
					a = new Placing(c, pos);
				}
				else { // if moving or jumping: need to select another pos (from and to)
					m_from = pos;
				}
				switch (m_controller.play(a)) {
					case OK: {
						m_actionResultingInMill = null;
						m_controller.compute(); break;
					}
					case CLOSEDMILL: m_actionResultingInMill = (ActionPM)a; break;
					case INVALIDACTION: break;
					case FINISHED: break;
				}
			});
			setOnMouseEntered(e -> setStroke(Color.RED));
			setOnMouseExited(e -> setStroke(Color.TRANSPARENT));
		}
		void calculatePosition() {
			setCenterX(scaleX(State.BOARD[pos].x));
			setCenterY(scaleY(State.BOARD[pos].y));
		}
	}
}


