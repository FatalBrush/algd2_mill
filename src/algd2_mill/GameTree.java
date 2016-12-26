package algd2_mill;

import java.util.Iterator;

public class GameTree extends Tree<IAction> implements IGameTree {

	private GameNode m_currentNode;
	private State m_currentState;
	
	@Override
	public void create(int height, Placing pa) {
		m_currentState = new State();
		byte color = IController.WHITE; // next actions are white if computer
		if (pa != null) { // Human starts, so next actions are black
			pa.update(m_currentState);
			color = IController.BLACK;
		}
		GameNode root = new MaxNode(pa);
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
		assert m_currentNode.m_children != null;
		GameNode nextNode = null; // the node that corresponds to the path the player went
		Iterator<Node<IAction>> iter = m_currentNode.iterator();
		System.out.println(a+"----------");
		while (iter.hasNext()) {
			// remove all paths but the one the player went
			Node<IAction> next = iter.next();
			System.out.println(next.m_data);
			if (((Action)next.m_data).equals(a)) nextNode = (GameNode) next;
			else { iter.remove(); m_size--; }
		}
		assert nextNode != null;
		m_currentState = nextNode.computeState(m_currentState, m_currentNode);
		m_currentNode = nextNode;
	}

	@Override
	public Action computerPlayer() {
		Action a = (Action) m_currentNode.m_children.peek().m_data;
		a.update(m_currentState);
		return a;
	}
}
