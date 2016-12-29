package algd2_mill;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class Mill extends Application implements IView {
	
	private GameBoard m_gameBoard;
	private Controller m_controller;
	
	public static void main(String... args) {
		launch(args);
	}
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		m_controller = new Controller(this);
		m_gameBoard = new GameBoard(m_controller);
		prepareBoard();
		
		Scene scene = new Scene(m_gameBoard, Color.BURLYWOOD);
		primaryStage.setScene(scene);
		primaryStage.setWidth(350);
		primaryStage.setHeight(300);
		primaryStage.setTitle("algd2 Mill");
		primaryStage.show();
		primaryStage.setOnCloseRequest(e -> {
			m_controller.exit();
			Platform.exit();
		});
		
		m_controller.startHumanGame();

//		while(m_controller.getWinner() == IController.NONE) {
//			//m_controller.m_gameTree.print();
//			//Scanner s = new Scanner(System.in);
//			String[] play = s.nextLine().split("\\s+");
//			Action a = Action.readln(play);
//			m_controller.play(a);
//			//m_controller.m_gameTree.print();
//			m_controller.compute();
//		}
		//m_controller.compute();m_controller.compute();m_controller.compute();m_controller.compute();m_controller.compute();m_controller.compute();m_controller.compute();m_controller.compute();
	}
	
	@Override
	public void updateBoard(State s, Action a, boolean isComputerAction) {
		m_gameBoard.setState(s);
		m_gameBoard.showAction(a, isComputerAction);
		
	}
	@Override
	public void prepareBoard() {
		m_gameBoard.setState(new State());
		m_gameBoard.draw();
	}
	
	@Override
	public void setComputerName(String name) {
		m_gameBoard.setComputerName(name);
	}
	
	@Override
	public void setHumanName(String name) {
		m_gameBoard.setHumanName(name);
	}
}
