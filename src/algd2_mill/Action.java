package algd2_mill;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * An action represents a game state change. There are three different actions:
 * - placing a stone: a new stone is placed on the game board
 * - moving a stone: an existing stone is moved on the game board
 * - taking a stone: an existing stone is removed
 *  
 * @author Christoph Stamm
 * @version 16.7.2009
 *
 */
public abstract class Action implements IAction {
	protected byte m_color;		// color of stone in action: WHITE, BLACK
	
	public Action() {
		m_color = IController.NONE;
	}
	
	public Action(byte color) {
		assert color == IController.WHITE || color == IController.BLACK : "wrong color";

		m_color = color;
	}
	
	/**
	 * @return Color of the stone be placed or moved
	 */
	public byte color() {
		return m_color;
	}
	
	public static Action readln(String[] token) {
		if (token[0].equals("PLACE")) {
        	// my opponent placed a stone
        	assert token.length == 3;
        	byte color = Byte.parseByte(token[1]);
        	int pos = Integer.parseInt(token[2]);
        	// create place action and play it
        	return new Placing(color, pos);
        	
        } else if (token[0].equals("MOVE")) {
        	// my opponent moved a stone
        	assert token.length == 4;
        	byte color = Byte.parseByte(token[1]);
        	int from = Integer.parseInt(token[2]);
        	int to = Integer.parseInt(token[3]);
        	// create move action and play it
        	return new Moving(color, from, to);
        	
        } else if (token[0].equals("TAKE")) {
        	// my opponent 
        	assert token.length > 1;
        	if (token[1].equals("PLACE")) {
            	// my opponent placed a stone
            	assert token.length == 5;
            	byte color = Byte.parseByte(token[2]);
            	int pos = Integer.parseInt(token[3]);
            	// create place action
            	ActionPM a = new Placing(color, pos);
            	
            	int takepos = Integer.parseInt(token[4]);
            	// create take and play it
            	return new Taking(a, takepos);
            	
        	} else if (token[1].equals("MOVE")) {
            	// my opponent moved a stone
            	assert token.length == 6;
            	byte color = Byte.parseByte(token[2]);
            	int from = Integer.parseInt(token[3]);
            	int to = Integer.parseInt(token[4]);
            	// create move action
            	ActionPM a = new Moving(color, from, to);
            	
            	int takepos = Integer.parseInt(token[5]);
            	// create take and play it
            	return new Taking(a, takepos);
            	
        	}	
        }
		return null;
	}
	
}

///////////////////////////////////////////////////////////////////////////////
/**
 * Abstract base class for placing and moving actions
 */
abstract class ActionPM extends Action {
	protected byte m_to; 	// game board position: 0..23
	
	/**
	 * Constructor
	 * @param color Color of the stone being placed or moved
	 * @param to Target position
	 */
	public ActionPM(byte color, int to) {
		super(color);
		assert to >= 0 && to < State.NPOS : "wrong board position";
		m_to = (byte)to;
	}
	
	/**
	 * 
	 * @return Target position
	 */
	public byte endPosition() {
		return m_to;
	}	
}

///////////////////////////////////////////////////////////////////////////////
/**
 * Placing action
 */
class Placing extends ActionPM {
	/**
	 * Constructor
	 * @param color Color of the stone being placed
	 * @param to Target position
	 */
	public Placing(byte color, int to) {
		super(color, to);
	}	

	@Override
	public boolean equals(Object o) {
		if (o != null && o instanceof Placing) {
			Placing a = (Placing)o;
			
			return a.m_color == m_color && a.m_to == m_to;
		}
		return false;
	}

	@Override
	public String toString() {
		return String.format("__-%02d:__", m_to);
	}
	
	public boolean isValid(State s) {
		assert s != null : "s is null";
		return s.isValidPlace(m_to, m_color);
	}
	
	public void update(State s) {
		assert s != null : "s is null";
		s.update(this);
	}
	
	public void writeln(DataOutputStream os) throws IOException {
		os.writeBytes("PLACE " + m_color + " " + m_to + '\n');
	}
}

///////////////////////////////////////////////////////////////////////////////
/**
 * Moving action
 */
class Moving extends ActionPM {
	protected byte m_from; 	// game board position: 0..23
	
	/**
	 * Constructor
	 * @param color Color of the stone being moved
	 * @param from Start position
	 * @param to Target position
	 */
	public Moving(byte color, int from, int to) {
		super(color, to);
		assert from >= 0 && from < State.NPOS : "wrong board position";
		assert from != to : "wrong move";
		m_from = (byte)from;
	}	

	@Override
	public boolean equals(Object o) {
		if (o != null && o instanceof Moving) {
			Moving a = (Moving)o;
			
			return a.m_color == m_color && a.m_to == m_to && a.m_from == m_from;
		}
		return false;
	}
		
	@Override
	public String toString() {
		return String.format("%02d-%02d:__", m_from, m_to);
	}
	
	/**
	 * @return Start position
	 */
	public byte startPosition() {
		return m_from;
	}
	
	public boolean isValid(State s) {
		assert s != null : "s is null";
		return s.isValidMove(m_from, m_to, m_color);
	}
	
	public void update(State s) {
		assert s != null : "s is null";
		s.update(this);
	}
	
	public void writeln(DataOutputStream os) throws IOException {
		os.writeBytes("MOVE " + m_color + " " + m_from + " " + m_to + '\n');
	}
}

///////////////////////////////////////////////////////////////////////////////
/**
 * Taking action
 * @author  christoph.stamm
 */
class Taking extends Action {
	/**
	 * @uml.property  name="m_action"
	 * @uml.associationEnd  
	 */
	private ActionPM m_action;	// action that resulted in a taking action
	private byte m_pos; 		// game board position: 0..23
	
	/***
	 * Constructor
	 * @param action Immediate action before this taking action
	 * @param pos Position of the stone to be taken
	 */
	public Taking(ActionPM action, int pos) {
		super(action.color());
		assert action != null && (action instanceof Placing || action instanceof Moving) : "wrong action";
		assert pos >= 0 && pos < State.NPOS : "wrong board position";
		
		m_action = action; // the action resulting in a take
		m_pos = (byte)pos;
	}	

	@Override
	public boolean equals(Object o) {
		if (o != null && o instanceof Taking) {
			Taking a = (Taking)o;
			
			return a.m_color == m_color && a.m_pos == m_pos && a.m_action.equals(m_action);
		}
		return false;
	}

	@Override
	public String toString() {
		if (m_action instanceof Moving) {
			return String.format("%02d-%02d:%02d", ((Moving)m_action).m_from, m_action.m_to, m_pos);						
		} else {
			return String.format("__-%02d:%02d", m_action.m_to, m_pos);			
		}
	}
	
	/**
	 * @return Position of the stone to be taken
	 */
	public byte takePosition() {
		return m_pos;
	}
	
	public byte takeColor() {
		return State.oppositeColor(m_color);
	}
	
	/**
	 * @return Immediate action before this taking action
	 */
	public ActionPM action() {
		return m_action;
	}
	
	public boolean isValid(State s) {
		assert s != null : "s is null";
		return s.isValidTake(m_pos, State.oppositeColor(m_color));
	}
	
	public void update(State s) {
		assert s != null : "s is null";
		if (m_action instanceof Moving) {
			s.update((Moving)m_action);
		} else {
			s.update((Placing)m_action);
		}
		s.update(this);
	}
	
	public void writeln(DataOutputStream os) throws IOException {
		if (m_action instanceof Moving) {
			os.writeBytes("TAKE MOVE " + m_color + " " + ((Moving)m_action).m_from + " " + m_action.m_to + " " + m_pos + '\n');
		} else {
			os.writeBytes("TAKE PLACE " + m_color + " " + m_action.m_to + " " + m_pos + '\n');
		}
	}
}

