package algd2_mill;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import algd2_mill.model.GameNode;
import algd2_mill.model.GameTree;

public class GameTreeTest {

	GameTree t;
	
	@Before
	public void setUp() throws Exception {
		t = new GameTree();
	}

	@Test
	public void test() {
		t.create(1, null);
		t.print();
		System.out.println(((GameNode)t.m_root.m_children.poll().m_children.poll()).computeState(t.currentState(), (GameNode)t.m_root));
	}

}
