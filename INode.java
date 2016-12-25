import java.util.Iterator;

/**
 * 
 * @author Christoph Stamm
 * @version 16.7.2009
 *
 */

public interface INode<T> {
	/**
	 * Append child node at last position
	 * @param v Node
	 * @return Resulting position
	 */
	public int add(Node<T> v);
	
	/**
	 * Remove this node
	 */
	public void remove();
	
	/**
	 * Return number of children
	 * @return Number of children
	 */
	public int size();
	
	/**
	 * Return node data
	 * @return Node data
	 */
	public T data();
	
	/**
	 * Count number of nodes in this subtree
	 * @return Number of nodes in this subtree
	 */
	public int count();

	/**
	 * This iterator allows iterating through all direct children of this node.
	 */
	public Iterator<Node<T>> iterator();
}
