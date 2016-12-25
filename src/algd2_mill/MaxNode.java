package algd2_mill;
/**
 * A MaxNode is a child of a MinNode
 * 
 * @author christoph.stamm
 * @version 24.11.2009
 */
public class MaxNode extends GameNode {
	/**
	 * Create node with action
	 * @param a Action
	 */
	public MaxNode(IAction a) {
		super(a);	
	}
	
	/**
	 * Create node with action and score
	 * @param a Action
	 * @param score Score
	 */
	public MaxNode(IAction a, int score) {
		super(a, score);	
	}
	
	/**
	 * The children of a MinNode will be ordered in decreasing score order
	 */
	public int compareTo(Node<IAction> v) {
		int score1 = score(), score2 = ((GameNode)v).score();
		
		if (score1 == score2) return 0;
		else return (score1 > score2) ? -1 : 1;
	}
	
	/**
	 * Get winner score
	 */
	public int getWinnerScore() {
		return State.WHITEWINS;
	}
}
