package algd2_mill;

/**
 * 
 * @author Christoph Stamm
 * @version 16.7.2009
 *
 */
/**
 * @author  christoph.stamm
 */
public class Tree<T> implements ITree {
	/**
	 * @uml.property  name="m_root"
	 * @uml.associationEnd  
	 */
	protected Node<T> m_root;	// root node
	protected int m_size;		// number of nodes
	
	/**
	 * Standard constructor creates empty tree.
	 */
	public Tree() {}
	
	/**
	 * Create new tree with given node as root
	 * @param root Root node
	 */
	public Tree(Node<T> root) {
		m_root = root;
	}
	
	/**
	 * @return Number of nodes in this tree
	 */
	public int size() {
		return m_size;
	}
	
	/**
	 * Simple console tree printing
	 */
	public void print() {
		System.out.println("Tree");
		if (m_root != null) {
			print(m_root, 0, 0);
		} else {
			System.out.println("tree is empty");
		}
	}
	
	/**
	 * Recursive pre-order tree printing
	 * @param p Tree node
	 * @param level Tree level: used as x-coordinate
	 * @param childNumber Child ordering number: used as y-coordinate
	 */
	private void print(Node<T> p, int level, int childNumber) {
		assert p != null : "p is null";
		
		if (childNumber > 0) {
			for(int i = 0; i < level; i++) {
				System.out.print("\t       |");				
			}
		}
		System.out.print(p);
		System.out.print('\t');
		
		if (p.m_children.size() > 0) {
			int n = 0;
			for(Node<T> v: p.m_children) {
				print(v, level + 1, n++);
			}
			for(int i = 0; i < level; i++) {
				System.out.print("\t       |");				
			}
			System.out.println();
		} else {
			System.out.println();
		}
	}
}
