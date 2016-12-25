/**
 * 
 * @author christoph.stamm
 * @version 24.11.2009
 *
 */
public interface IGameNode extends INode<IAction> {
	/**
	 * Create new node and add it to this as a child node.
	 * @param a Action
	 * @param score Score
	 */
	public GameNode add(IAction a, int score);
	
	/**
	 * Create new node and add it to this as a child node.
	 * @param a Action
	 */
	public GameNode add(IAction a);
	
	/**
	 * Create new nodes recursively.
	 * @param curHeight current subtree height
	 * @param height Subtree height
	 * @param color Color of next actions
	 * @param root Subtree root
	 * @param rootState Game state at root
	 * @return Number of created nodes
	 */
	public int create(int curHeight, int height, byte color, GameNode root, State rootState);
	
	/**
	 * Compute game state at this node
	 * @param s Game state at given node v
	 * @param v Game node v must be ancestor of this
	 * @return Game state at this node
	 */
	public State computeState(State s, GameNode v);
	
	/**
	 * @return Score of a winner node
	 */
	public int getWinnerScore();
	
	/**
	 * @return Score of this node
	 */
	public int score();
}
