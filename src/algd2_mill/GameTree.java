package algd2_mill;

import java.util.Iterator;

public class GameTree extends Tree<IAction> implements IGameTree {

	private GameNode m_currentNode;
	private State m_currentState;
	
	@Override
	public void create(int height, Placing pa) {
		m_currentState = new State();
		GameNode root;
		byte color = IController.WHITE; // next actions are white if computer starts
		if (pa != null) { // Human starts with a white action, so next actions are black
			pa.update(m_currentState);
			color = IController.BLACK;
			root = new MaxNode(pa); // root is MaxNode if player starts (because then we already know the first action)
		} else {
			root = new MinNode(null); // else MinNode
		}
		m_root = root;
		m_currentNode = root;
		m_size = root.create(0, height, color, m_currentNode, m_currentState);
	}

	@Override
	public State currentState() {
		return m_currentState;
	}

	@Override
	public void humanPlayer(Action a) {
		m_currentNode = cutUnusedBranches(a);
		boolean jumpingPhaseBefore = m_currentState.jumpingPhase(IController.WHITE) || m_currentState.jumpingPhase(IController.BLACK);
		a.update(m_currentState);
		boolean jumpingPhaseAfter = m_currentState.jumpingPhase(IController.WHITE) || m_currentState.jumpingPhase(IController.BLACK);
		if (jumpingPhaseBefore == jumpingPhaseAfter) // if we didn't just switch to the jumpingphase, increase treeheight
			extendTree(1); // extend tree by 1 level
		// otherwise don't increase the treeheight to reduce amounts of calculations
	}

	@Override
	public Action computerPlayer() {
		GameNode bestChoice = (GameNode)m_currentNode.m_children.peek();
		Action a = (Action) bestChoice.m_data;
		m_currentNode = cutUnusedBranches(a);
		assert m_currentNode == bestChoice;
		a.update(m_currentState);
		extendTree(1); // extend tree by 1 level
		return a;
	}
	
	/**
	 * Cut all the branches that we did not go through to not waste memory.
	 * @param a The node with Action a is not cut
	 * @return the next node
	 */
	public GameNode cutUnusedBranches(Action a) {
		Iterator<Node<IAction>> iter = m_currentNode.iterator();
		GameNode nextNode = null;
		while (iter.hasNext()) {
			Node<IAction> next = iter.next();
			if (next.m_data.equals(a)) { nextNode = (GameNode) next; }
			else { m_size -= next.count(); iter.remove(); }
		}
		return nextNode;
	}
	
	/**
	 * Extends the game tree by given amount of levels.
	 * @param levels
	 */
	public void extendTree(int levels) {
		if (m_currentNode.isLeaf()) {
			m_size += m_currentNode.create(0, levels, State.oppositeColor(m_currentNode.m_data.color()), m_currentNode, m_currentState);
		}
		else {
			for (Node<IAction> leaf : m_currentNode.leaves()) {
				GameNode n = (GameNode)leaf;
				m_size += n.create(0, levels, State.oppositeColor(n.m_data.color()), n, n.computeState(m_currentState.clone(), m_currentNode));
			}
		}
	}
}
