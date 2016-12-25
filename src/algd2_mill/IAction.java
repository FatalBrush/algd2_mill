package algd2_mill;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * 
 * @author Christoph Stamm
 * @version 16.7.2009
 *
 */

public interface IAction {
	/**
	 * Checks if this action is available at given game state
	 * @param s Game state
	 * @return True if this action is available
	 */
	public boolean isValid(State s);
	
	/**
	 * Updates given game state with this action
	 * @param s Game state
	 */
	public void update(State s);
	
	/**
	 * Writes action to data output stream
	 * @param os Data output stream
	 */
	public void writeln(DataOutputStream os) throws IOException;
	
	/**
	 * Color of stone in action: WHITE, BLACK
	 * @return WHITE or BLACK
	 */
	public byte color();
}
