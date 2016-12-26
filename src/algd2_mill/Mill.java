package algd2_mill;
import java.util.Scanner;

import algd2_mill.IController.Status;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Mill extends Application implements IView{
	
	private GameBoard gameBoard;
	private static int s_boardSize = 300;
	private Controller m_controller;
	
	public static void main(String... args) {
		launch(args);
	}
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		m_controller = new Controller(this);
		gameBoard = new GameBoard(m_controller);
		prepareBoard();
		
		Scene scene = new Scene(gameBoard);
		primaryStage.setScene(scene);
		primaryStage.setWidth(s_boardSize);
		primaryStage.setHeight(s_boardSize);
		primaryStage.show();
		
		m_controller.startHumanGame();
		
		while(m_controller.getWinner() == IController.NONE) {
			Scanner s = new Scanner(System.in);
			String[] play = s.nextLine().split("\\s+");
			Action a = Action.readln(play);
			m_controller.play(a);
			m_controller.compute();
		}
			
	}
	
	@Override
	public void updateBoard(State s, Action a, boolean isComputerAction) {
		gameBoard.setState(s);
		gameBoard.showAction(a, isComputerAction);
		
	}
	@Override
	public void prepareBoard() {
		
	}
	@Override
	public void setComputerName(String name) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void setHumanName(String name) {
		// TODO Auto-generated method stub
		
	}
	
	public void refreshBoard() {
		//needed?
	}	
}
