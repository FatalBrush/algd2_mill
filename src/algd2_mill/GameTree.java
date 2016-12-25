package algd2_mill;

public class GameTree extends Tree<IAction> implements IGameTree {

	private GameNode m_currentNode;
	private State m_currentState;
	
	@Override
	public void create(int height, Placing pa) {
		m_currentState = new State();
		if (pa == null) pa = new Placing(IController.WHITE, 4); //computer always starts at position 4
		pa.update(m_currentState);
		GameNode root = new MaxNode(pa);
		m_root = root;
		m_currentNode = root;
		m_size = root.create(0, height, IController.BLACK, m_currentNode, m_currentState);
	}

	@Override
	public State currentState() {
		return m_currentState;
	}

	@Override
	public void humanPlayer(Action a) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Action computerPlayer() {
		return (Action) m_currentNode.m_children.peek().m_data;
	}
}
