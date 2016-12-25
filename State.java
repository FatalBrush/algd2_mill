import java.awt.Point;

/**
 * 
 * @author Christoph Stamm
 * @version 16.7.2009
 *
 * board positions
 * ---------------
 * 00       01       02
 *    03    04    05
 *       06 07 08
 * 09 10 11    12 13 14
 *       15 16 17
 *    18    19    20
 * 21       22       23      
 *   
 */
public class State implements Cloneable {
	// scores
	public final static int WHITEWINS = Integer.MAX_VALUE;
	public final static int BLACKWINS = Integer.MIN_VALUE;
	
	// positions
	public final static byte INVALID = -1;	// invalid position
	public final static byte NPOS = 24;		// number of board positions
	
	// transposed board positions
	public static final byte[] TRANSPOSED = { 0, 9, 21, 3, 10, 18, 6, 11, 15, 1, 4, 7, 16, 19, 22, 8, 12, 17, 5, 13, 20, 2, 14, 23 };
	
	// valid moves for each board position
	public static final byte[][] MOVES = { 	{ 1, 9 }, 
											{ 0, 2, 4 },
											{ 1, 14 },
											{ 4, 10 },
											{ 1, 3, 5, 7 },
											{ 4, 13 },
											{ 7, 11 },
											{ 4, 6, 8 },
											{ 7, 12 },
											{ 0, 10, 21 },
											{ 3, 9, 11, 18 },
											{ 6, 10, 15 },
											{ 8, 13, 17 },
											{ 5, 12, 14, 20 },
											{ 2, 13, 23 },
											{ 11, 16 },
											{ 15, 17, 19 },
											{ 12, 16 },
											{ 10, 19 },
											{ 16, 18, 20, 22 },
											{ 13, 19 },
											{ 9, 22 },
											{ 19, 21, 23 },
											{ 14, 22 }
										 };
	
	// board coordinates for each board position
	public static final Point[] BOARD = {
		new Point(0, 0),
		new Point(3, 0),
		new Point(6, 0),
		new Point(1, 1),
		new Point(3, 1),
		new Point(5, 1),
		new Point(2, 2),
		new Point(3, 2),
		new Point(4, 2),
		new Point(0, 3),
		new Point(1, 3),
		new Point(2, 3),
		new Point(4, 3),
		new Point(5, 3),
		new Point(6, 3),
		new Point(2, 4),
		new Point(3, 4),
		new Point(4, 4),
		new Point(1, 5),
		new Point(3, 5),
		new Point(5, 5),
		new Point(0, 6),
		new Point(3, 6),
		new Point(6, 6)
	};

	// class methods
	
	/**
	 * Return opposite player's color
	 */
	public static byte oppositeColor(byte color) {
		assert color == IController.WHITE || color == IController.BLACK : "wrong color";
		
		return (byte)(IController.WHITE - color);
	}
	
	// instance variables
	private byte[] m_board = new byte[NPOS];	// valid positions 0..23
	private byte[] m_stonesOnBoard  = { 0, 0 };	// number of stones on board
	private byte[] m_unplacedStones = { 9, 9 };	// number of unplaced stones
	private byte m_winner = IController.NONE;				// IController.NONE, IController.WHITE, IController.BLACK	
	
	public State() {
		// initialize board: all positions are empty
		for (int i=0; i < NPOS; i++) {
			m_board[i] = IController.NONE;
		}
	}
	
	// instance methods
	
	/**
	 * Creates deep copy.
	 */
	public State clone() {
		State s = new State();
		s.m_board = m_board.clone();
		s.m_stonesOnBoard = m_stonesOnBoard.clone();
		s.m_unplacedStones = m_unplacedStones.clone();
		s.m_winner = m_winner;
		return s;
	}

	/**
	 * Return number of unplaced stones
	 * @param color color Player's color
	 * @return number of unplaced stones
	 */
	public int unplacedStones(byte color) {
		assert color == IController.WHITE || color == IController.BLACK : "wrong color";
		
		return m_unplacedStones[color];
	}
	
	/**
	 * Return true during placing phase
	 * @param color Player's color
	 * @return True if the player is in its placing phase
	 */
	public boolean placingPhase(byte color) {
		assert color == IController.WHITE || color == IController.BLACK : "wrong color";
		
		return m_unplacedStones[color] > 0;
	}
	
	/**
	 * Return true during moving phase
	 * @param color Player's color
	 * @return True if the player is in its moving phase
	 */
	public boolean movingPhase(byte color) {
		assert color == IController.WHITE || color == IController.BLACK : "wrong color";
		
		return m_unplacedStones[color] == 0;
	}
	
	/**
	 * Return true during jumping phase
	 * @param color Player's color
	 * @return True if the player is in its jumping phase
	 */
	public boolean jumpingPhase(byte color) {
		assert color == IController.WHITE || color == IController.BLACK : "wrong color";
		
		return movingPhase(color) && m_stonesOnBoard[color] == 3;
	}
	
	/**
	 * Set winner
	 * @param color Color of winner
	 */
	public void setWinner(byte color) {
		assert color == IController.WHITE || color == IController.BLACK || color == IController.NONE : "wrong color";
		
		m_winner = color;
	}
	
	/**
	 * Return true if the game has been finished
	 * @return True if the game has been finished
	 */
	public boolean finished() {
		return m_winner != IController.NONE;
	}
	
	/**
	 * Return winner's color or IController.NONE
	 * @return Color of the winner
	 */
	public byte winner() {
		return m_winner;
	}
	
	/**
	 * Return stone color at given position or IController.NONE
	 * @param pos Board position
	 * @return Stone color at position pos or IController.NONE
	 */
	public byte color(byte pos) {
		assert pos >= 0 && pos < State.NPOS : "wrong board position";
		
		return m_board[pos];
	}
	
	/**
	 * Return true if a stone of given color is part of a mill at given position
	 * @param pos Position
	 * @param color Color
	 * @return True if a stone of given color is part of a mill at given position
	 */
	public boolean inMill(int pos, byte color) {
		assert pos >= 0 && pos < State.NPOS : "wrong board position";
		assert color == IController.WHITE || color == IController.BLACK || color == IController.NONE : "wrong color";
		
		if (color == IController.NONE) return false;
		
		// horizontal mills
		int p1 = pos - (pos%3);
		int p2 = p1 + 1;
		int p3 = p2 + 1;
		
		if (m_board[p1] == color && m_board[p2] == color && m_board[p3] == color) {
			return true;
		}
		
		// vertical mills
		int t = TRANSPOSED[pos];
		p1 = t - (t%3);
		p2 = p1 + 1;
		p3 = p2 + 1;
		
		if (m_board[TRANSPOSED[p1]] == color && m_board[TRANSPOSED[p2]] == color && m_board[TRANSPOSED[p3]] == color) {
			return true;
		}
		
		return false;
	}
	
	/**
	 * Return true if taking a stone of given color is possible at all
	 * @param color Color of stone to be taken
	 * @return True if taking a stone of given color is possible
	 */
	public boolean takingIsPossible(byte color) {
		if (jumpingPhase(color)) {
			return true;
		} else {
			// placing or moving phase
			for (byte pos = 0; pos < NPOS; pos++) {
				if (m_board[pos] == color && !inMill(pos, color)) return true;
			}
			return false;
		}
	}
	
	/**
	 * Checks if a placing is possible
	 * @param pos Position
	 * @param color Color
	 * @return True if placing is possible
	 */
	public boolean isValidPlace(byte pos, byte color) {
		assert pos >= 0 && pos < State.NPOS : "wrong board position";
		assert color == IController.WHITE || color == IController.BLACK : "wrong color";
		
		return placingPhase(color) && m_board[pos] == IController.NONE;
	}
	
	/**
	 * Place stone s on board
	 * @param a Action
	 */
	public void update(Placing a) {
		assert a != null : "action is null";
		
		byte pos = a.endPosition();
		byte color = a.color();
		
		assert isValidPlace(pos, color) : "invalid action";
		
		m_board[pos] = color;
		m_unplacedStones[color]--;
		m_stonesOnBoard[color]++;
	}
	
	/**
	 * Checks if a move is possible
	 * @param from Start position
	 * @param to End position
	 * @param color Color
	 * @return True if mode is possible
	 */
	public boolean isValidMove(byte from, byte to, byte color) {
		assert from >= 0 && from < State.NPOS : "wrong board position";
		assert to >= 0 && to < State.NPOS : "wrong board position";
		assert color == IController.WHITE || color == IController.BLACK : "wrong color";
		
		if (movingPhase(color) && from != to && m_board[from] == color && m_board[to] == IController.NONE) {
			if (m_stonesOnBoard[color] > 3) {
				int i = 0;
				int len = MOVES[from].length;
				while(i < len && MOVES[from][i] != to) i++;
				return i < len;
			} else {
				// jumping allowed
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Move stone on board
	 * @param a Action
	 */
	public void update(Moving a) {
		assert a != null : "action is null";
		
		byte from = a.startPosition();
		byte to = a.endPosition();
		byte color = a.color();
		
		assert isValidMove(from, to, color) : "invalid action";
		
		m_board[from] = IController.NONE;
		m_board[to] = color;
	}

	/**
	 * Checks if a take at given position of given color is possible
	 * @param pos Position where a stone will be taken
	 * @param color Color of a stone to be taken
	 * @return True if action is possible
	 */
	public boolean isValidTake(byte pos, byte color) {
		assert pos >= 0 && pos < State.NPOS : "wrong board position";
		assert color == IController.WHITE || color == IController.BLACK : "wrong color";
		
		boolean valid = false;
		
		if (m_board[pos] == color) {
			if (movingPhase(color)) {
				if (m_stonesOnBoard[color] > 3) {
					// moving phase
					valid = !inMill(pos, color);
				} else {
					// jumping phase
					valid = true;
				}
			} else {
				// start phase
				valid = !inMill(pos, color);
			}
		}
		return valid;
	}
	
	/**
	 * Take stone and update winner
	 * @param a Action
	 */
	public void update(Taking a) {
		assert a != null : "action is null";
		
		byte pos = a.takePosition();
		byte color = a.takeColor();	// color of taken stone
		
		assert isValidTake(pos, color) : "invalid action";
		
		m_board[pos] = IController.NONE;
		m_stonesOnBoard[color]--;
		
		if (movingPhase(color) && m_stonesOnBoard[color] < 3) m_winner = a.color();
	}
	
	/**
	 * ASCII Art of game board
	 */
	public String toString() {
		StringBuilder str = new StringBuilder();
		
		for (int i=0; i<m_board.length; i++) {
			
			if(i == 1 || i == 2 || i == 22 || i == 23 || i == 6 || i == 15) {
				str.append("      ");
			} else if(i == 3 || i == 4 || i == 5 || i == 20 || i == 19 || i == 18 || i == 12) {
				str.append("   ");
			}
			
			if (m_board[i] == IController.BLACK) {
				str.append(" b ");
			} else if (m_board[i] == IController.WHITE) {
				str.append(" w ");
			} else {
				str.append(" - ");
			}
			
			if ((i+1)%3 == 0 && i != 11) {
				str.append("\r\n");
			}
		}
		
		return str.toString();
	}
	
	/**
	 * Compute score of this game state: Black is a minimizer, White a maximizer.
	 * If this state has already a winner, then one of the predefined values BLACKWINS or
	 * WHITEWINS should be returned.
	 * @return Score of this game state
	 */
	public int score() {
		byte winner = winner();
		
		if (winner != IController.NONE) {
			// there is a winner
			return (winner == IController.BLACK) ? BLACKWINS : WHITEWINS;
		} else {
			// compute score
			
		}
	}
}
