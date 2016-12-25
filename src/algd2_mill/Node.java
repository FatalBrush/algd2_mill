package algd2_mill;
/**
 * 
 * @author Christoph Stamm
 * @version 16.7.2009
 *
 */
import java.util.*;

/**
 * @author  christoph.stamm
 */
public abstract class Node<T> implements INode<T>, Comparable<Node<T>>, Iterable<Node<T>> {
	protected T m_data;						// private data
	protected Node<T> m_parent;				// parent node
	protected Queue<Node<T>> m_children;	// children
	
	/**
	 * Create leaf with private data
	 * @param data Private data
	 */
	public Node(T data) {
		m_data = data;
		m_children = new PriorityQueue<Node<T>>();
	}
	
	@Override
	public String toString() {
		return (m_data != null) ? m_data.toString() : "__-__:__";
	}
	
	/**
	 * Append child node at last position
	 * @param v Node
	 * @return Resulting position
	 */
	public int add(Node<T> v) {
		v.m_parent = this;
		m_children.add(v);
		return size() - 1;
	}
	
	/**
	 * Remove this node
	 */
	public void remove() {
		if (m_parent != null) m_parent.m_children.remove(this);
	}
	
	/**
	 * Return number of children
	 * @return Number of children
	 */
	public int size() {
		return m_children.size();
	}
	
	/**
	 * Return node data
	 * @return Node data
	 */
	public T data() {
		return m_data;
	}
	
	/**
	 * Count number of nodes in this subtree
	 * @return Number of nodes in this subtree
	 */
	public int count() {
		int c = 1;
		for(Node<T> v: m_children) {
			c += v.count();
		}
		return c;
	}
	
	/**
	 * This iterator allows iterating through all direct children of this node.
	 */
	public Iterator<Node<T>> iterator() {
		return m_children.iterator();
	}
	
}
