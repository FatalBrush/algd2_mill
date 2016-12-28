package algd2_mill;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Random;

public abstract class GameNode extends Node<IAction> implements IGameNode {

	private int m_score;
	
	public GameNode(IAction data) {
		super(data);
	}
	
	public GameNode(IAction data, int score) {
		super(data);
		m_score = score;
	}
	
	@Override
	public GameNode add(IAction a, int score) {
		GameNode n;
		// A MinNode is a child of a MaxNode and vice versa
		if (this instanceof MinNode) {
			n = new MaxNode(a, score);
		}
		else {
			n = new MinNode(a, score);
		}
		add(n);
		return n;
	}

	@Override
	public GameNode add(IAction a) {
		return add(a, 0);
	}

	@Override
	public int create(int curHeight, int height, byte color, GameNode root, State rootState) {
		assert curHeight <= height;
		if (curHeight == height) return 0; // no more nodes need to be created
		
		// else add all possible immediate follow-up actions (meaning 1 level below in the tree)
		int nodesAdded = 0;
		if (rootState.placingPhase(color)) {
			for (byte pos = 0; pos < State.NPOS; pos++) { // try all possible Placing actions
				if (rootState.isValidPlace(pos, color)) { 
					ActionPM a = new Placing(color, pos);
					nodesAdded += createTakingNodes(a, color, root, rootState, pos);
				}
			}
		}
		else { // if moving or jumping phase
			for (byte from = 0; from < State.NPOS; from++) { // try all possible Moving actions
				for (byte to = 0; to < State.NPOS; to++) {
					if (rootState.isValidMove(from, to, color)) {
						ActionPM a = new Moving(color, from, to);
						nodesAdded += createTakingNodes(a, color, root, rootState, to);
					}
				}
			}
		}
		
		if (++curHeight == height) return nodesAdded;
		
		// if tree is not deep enough after adding 1 more level:
		for (Object n : root.m_children.toArray()) { // add 1 more level on each child (toArray() to avoid ConcurrentModificationException)
			nodesAdded += create(curHeight, height, State.oppositeColor(color), (GameNode)n, ((GameNode)n).computeState(rootState.clone(), root));
			// clone() so original state does not get changed
		}
		return nodesAdded;
	}

	/**
	 * 
	 * @param rootState state to check, will not change (is cloned)
	 * @param a action that leads to Taking
	 * @param color the color of the player that takes a stone
	 * @return amount of nodes created
	 */
	private int createTakingNodes(ActionPM a, byte color, GameNode root, State rootState, byte pos) {
		int nodesAdded = 0;
		State s = rootState.clone();
		a.update(s); //TODO check here somewhere if its a valid move or sth. before updating, else assertionerror
		if (!s.inMill(pos, color)) { // if Action does not result in a mill
			root.add(a);
			nodesAdded++;
		}
		else if(s.takingIsPossible(State.oppositeColor(color))) { // if Placing does result in a mill: it can become several Taking actions
			for (byte takepos = 0; takepos < State.NPOS; takepos++) {
				if (s.isValidTake(takepos, State.oppositeColor(color))) {
					root.add(new Taking(a, takepos));
					nodesAdded++;
				}
			}
		}
		return nodesAdded;
	}
	
	@Override
	public State computeState(State s, GameNode v) { // clone the state you insert here! not done in method, because it's called recursively
		if (m_parent == v) {
			m_data.update(s);
			return s;
		}
		State parentState = ((GameNode)m_parent).computeState(s, v);
		m_data.update(parentState);
		return parentState;	
	}
	
	protected boolean isLeaf() {
		return size() == 0;
	}
	
	protected Collection<Node<IAction>> leaves() {
		ArrayList<Node<IAction>> list = new ArrayList<>();
		for (Node<IAction> n : m_children) {
			if (n.size() == 0) list.add(n);
			else list.addAll(((GameNode)n).leaves());
		}
		return list;
	}

	@Override
	public int score() {
		// TODO
		return (int)(Math.random()*100000);
	}

}
