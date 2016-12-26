package algd2_mill;

/**
 * Black is a minimum player (minimizer); White is a maximum player (maximizer).
 * 
 * @author christoph.stamm
 * @version 14.9.2010
 *
 */
public interface IGameTree extends ITree {
	/**
	 * Creates a new game tree: the first action is white, on the next level plays black.
	 * White is a maximizer, black is a minimizer. 
	 * @param pa null if computer plays white, first action if human plays white
	 */
	public void create(int height, Placing pa);
	
	/**
	 * Return current game state.
	 * @return Current game state.
	 */
	public State currentState();
	
	/**
	 * Update tree (remove subtrees), current node, and current state for the human player
	 * @param a Action
	 */
	public void humanPlayer(Action a);
	
	/**
	 * Compute best next node at current node, update tree (remove subtrees), current node, 
	 * and current state for the computer player
	 * @return Best action or null
	 */
	public Action computerPlayer();
}
