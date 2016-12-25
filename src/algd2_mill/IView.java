package algd2_mill;
/**
 * 
 * @author Christoph Stamm
 * @version 14.9.2010
 *
 */
public interface IView {
	/**
	 * Show action and update game board
	 * @param s New game state
	 * @param a Action
	 * @param isComputerAction True, if it is a computer action
	 */
	public void updateBoard(State s, Action a, boolean isComputerAction);	
	
	/**
	 * Prepare game board for a new game
	 */
	public void prepareBoard();
	
	/**
	 * Set computer player name
	 * @param name Computer player name
	 */
	public void setComputerName(String name);

	/**
	 * Set human player name
	 * @param name Human player name
	 */
	public void setHumanName(String name);
}
