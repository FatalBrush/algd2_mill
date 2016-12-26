package algd2_mill;

/**
 * 
 * @author christoph.stamm
 * @version 14.9.2010
 *
 */
public interface IController {
	// colors
	public final static byte NONE = -1;
	public final static byte BLACK = 0;
	public final static byte WHITE = 1;
	
	// return status flag used in controller actions
	public enum Status { OK, INVALIDACTION, CLOSEDMILL, FINISHED };
	// everything is ok
	// action is invalid
	// a mill have been closed
	// game over
	
	/**
	 * Determines the player who opens the game.
	 * @param computer If computer is true, then this computer player will open the game.
	 */
	public void setStarter(boolean computer);
	
	/**
	 * Sets a name for this computer player.
	 * @param name Computer player name
	 */
	public void setPlayerName(String name);
	
	/**
	 * Play action a and return status
	 * @param a Action
	 */
	public Status play(Action a);
	
	/**
	 * Compute new move and return it
	 * @return computed move
	 */
	public Action compute();
	
	/**
	 * Invokes the parallel computer player just for one move
	 */
	public void computeAsync();
	
	/**
	 * Use this method to check the status after compute()
	 * @return current controller status
	 */
	public Status getStatus();
	
	/**
	 * Returns the winner of the game
	 * @return NONE: no winner, BLACK: black, WHITE: white
	 */
	public int getWinner();
	
	/**
	 * The server asks for closing the connection to it
	 */
	public void closeConnection();
	
	/**
	 * Return color of human player.
	 * @return Color of human player or IController.NONE if not initialized yet.
	 */
	public byte humanColor();	

	/**
	 * Return true if a human player plays in this game.
	 * @return false if both players are computers using a game server.
	 */
	public boolean humanPlayer();
	
}
