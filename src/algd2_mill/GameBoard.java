package algd2_mill;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.scene.Cursor;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;


public class GameBoard extends Pane {
	
	private State m_state;
	private Controller m_controller;
	private Canvas m_canvas = new Canvas();
	private String m_humanName;
	private String m_computerName;
	private static final double STONESIZE = 25;
	private List<HitBox> m_hitBoxes = new ArrayList<>(State.NPOS);
	private ActionPM m_actionResultingInMill; // used for taking action in hitbox clickevent. Not null means Taking Action is in progress.
	private byte m_from = -1; // used for moving and jumping actions in hitbox clickevent. Greater -1 means moving/jumping action is in progress.
	private byte m_lastMove = -1; // used to indicate where to show a mark on the board to show the last move
	private byte m_lastTake = -1; // used to indicate where to show a mark on the board to show the last take
	private int m_turns = 0; // turn counter
	
	public GameBoard(Controller controller) {
		m_controller = controller;
		getChildren().add(m_canvas);
		for (byte pos = 0; pos < State.NPOS; pos++) {
			m_hitBoxes.add(new HitBox(pos, STONESIZE/2));
		}
		getChildren().addAll(m_hitBoxes);
		
		// make gui size dynamic
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
		
		// Unplaced stones
		for (int i = 0; i < m_state.unplacedStones(IController.WHITE); i++) {
			gc.setFill(Color.WHITE);
			int x = scaleX(-1), y = scaleY(i) * 7/9;
			gc.fillOval(x-STONESIZE/2, y-STONESIZE/2, STONESIZE, STONESIZE);
		}
		for (int i = 0; i < m_state.unplacedStones(IController.BLACK); i++) {
			gc.setFill(Color.BLACK);
			int x = scaleX(7), y = scaleY(i) * 7/9;
			gc.fillOval(x-STONESIZE/2, y-STONESIZE/2, STONESIZE, STONESIZE);
		}
		
		// Stones on field
		for (byte pos = 0; pos < State.BOARD.length; pos++) {
			int x = scaleX(State.BOARD[pos].x), y = scaleY(State.BOARD[pos].y);
			if (m_state.color(pos) != IController.NONE) {
				if (m_state.color(pos) == IController.BLACK) gc.setFill(Color.BLACK);
				else if (m_state.color(pos) == IController.WHITE) gc.setFill(Color.WHITE);
				gc.fillOval(x-STONESIZE/2, y-STONESIZE/2, STONESIZE, STONESIZE);
			}
		}
		
		// Last move & take indication
		if (m_lastMove >= 0) {
			int x = scaleX(State.BOARD[m_lastMove].x), y = scaleY(State.BOARD[m_lastMove].y);
			gc.setFill(Color.DARKVIOLET);
			gc.fillOval(x-STONESIZE/4, y-STONESIZE/4, STONESIZE/2, STONESIZE/2);
		}
		if (m_lastTake >= 0) {
			int x = scaleX(State.BOARD[m_lastTake].x), y = scaleY(State.BOARD[m_lastTake].y);
			gc.setStroke(Color.ORANGERED);
			gc.setLineWidth(2);
			gc.strokeOval(x-STONESIZE/3, y-STONESIZE/3, STONESIZE/1.5, STONESIZE/1.5);
		}
	}
	
	public void setState(State s) {
		m_state = s;
	}

	public void setComputerName(String name) {
		m_computerName = name;
	}

	public void setHumanName(String name) {
		m_humanName = name;
	}
	
	private int scaleX(int x) {
		return (int) (m_canvas.getWidth() * 1/6 + x * m_canvas.getWidth() * 2/3 /6);
	}
	
	private int scaleY(int y) {
		return (int) (m_canvas.getHeight() * 1/12 +  y * m_canvas.getHeight() * 5/6 /6);
	}
	
	public void showAction(Action a, boolean isComputerAction) {
		if (isComputerAction) {
			if (a instanceof Taking) {
				m_lastMove = ((Taking)a).action().m_to;
				m_lastTake = ((Taking)a).takePosition();
			}
			else if(a instanceof ActionPM) {
				m_lastMove = ((ActionPM)a).m_to;
				m_lastTake = -1;
			}
		}
//		if (isComputerAction) System.out.println("PC ACT");
//		
//		try {
//			a.writeln(new DataOutputStream(System.out));
//			System.out.println(m_state);
//		} catch (IOException e) {
//		}
		//m_controller.m_gameTree.print();
		draw();
	}
	
	private void endGame(byte winner) {
		String s = (winner == m_controller.humanColor() ? m_humanName : m_computerName) + " (" + (winner == IController.WHITE ? "white" : "black") + ")";
	    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
	    alert.setTitle("GAME OVER");
	    alert.setHeaderText(s + " wins in " + m_turns + " turns!");
	    alert.setContentText("Rematch?");

	    Optional<ButtonType> result = alert.showAndWait();
	    if (result.get() == ButtonType.OK){
	    	m_turns = 0;
	    	m_lastMove = -1;
	    	m_lastTake = -1;
	        m_controller.setStarter(m_controller.humanColor() == winner); // Computer starts next game if human won
	    } else {
	    	m_controller.exit();
	    	Platform.exit();
	    }
	}
	
	class HitBox extends Circle {
		byte pos;
		public HitBox(byte pos, double r) {
			super(r);
			setCursor(Cursor.HAND);
			this.pos = pos;
			calculatePosition();
			setFill(Color.TRANSPARENT);
			setOnMouseClicked(e -> {
				byte c = m_controller.humanColor();
				Action a = null;
				if (m_from > -1 && pos != m_from) { // we're in a moving or jumping action and need to complete it
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
					if (m_state.color(pos) == c) // make sure a stone of the player is selected
						m_from = pos;
				}
				if (a != null) {
					switch (m_controller.play(a)) {
						case OK: {
							m_turns++;
							m_actionResultingInMill = null;
							m_controller.compute(); // Computer plays
							if (m_state.finished()) { // if Computer could end the game: call endGame()
								endGame(State.oppositeColor(c));
							}
							break;
						}
						case CLOSEDMILL: m_actionResultingInMill = (ActionPM)a; break;
						case INVALIDACTION: break; // Just do nothing if the human played an invalid action.
						case FINISHED: endGame(c); break;
					}
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


