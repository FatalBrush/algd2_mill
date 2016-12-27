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
					State s = rootState.clone();
					a.update(s);
					if (!s.inMill(pos, color)) { // if Placing does not result in a mill
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
				}
			}
		}
		else if (rootState.movingPhase(color)) {

		}
		else if (rootState.jumpingPhase(color)) {
	
		}
		
		if (++curHeight == height) return nodesAdded;
		
		
		//TODO maybe avoid computeState and instead just update a cloned version
		// if tree is not deep enough after adding 1 more level:
		for (Object n : root.m_children.toArray()) { // add 1 more level on each child (toArray() to avoid ConcurrentModificationException)
			nodesAdded += create(curHeight, height, State.oppositeColor(color), (GameNode)n, ((GameNode)n).computeState(rootState.clone(), root));
			// clone() so original state does not get changed
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
