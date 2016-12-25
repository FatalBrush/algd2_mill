package algd2_mill;
import java.io.IOException;



/**
 * The game controller controls the game control flow.
 * The control flow is the following:
 * - choose randomly the player who begins the game
 * - create game tree
 * - start loop
 * - if computer plays: choose best known action from game tree
 * - if human plays: get action from user interface
 * - update game tree and game state with last action
 * - loop until game has been finished
 * 
 * @author  Christoph Stamm
 * @version  14.9.2010
 */
public class Controller implements IController {	
	// constants
	public static int TREEDEPTH = 2;		// relative game tree height measured in full-moves
	public static boolean VERBOSE = true;	// print additional output
	// testing
	public static int[] s_scores = new int[State.NPOS];
	
	private IView m_view;			// GUI
	private IGameTree m_gameTree;	// game tree
	private byte m_humanColor;		// IController.WHITE or IController.BLACK
	private boolean m_humanStarts;	// human starts the game
	private GameClient m_gc;		// client/server module
	private boolean m_serverGame;	// server sends human moves
	private ComputerPlayer m_compi;	// asynchronous computer player used in human game
	
	/**
	 * Constructor
	 * @param view Graphical user interface
	 */
	public Controller(IView view) {
		m_view = view;
		m_humanColor = IController.NONE;
		m_serverGame = false;
	}
	
	/**
	 * Start a new game between a human and a computer player
	 */
	public void startHumanGame() {
		if (m_serverGame) stopServerGame();
		
		// set player's name
		m_view.setComputerName("Computer");
		m_view.setHumanName("Player");
		
		// start computer player
		m_compi = new ComputerPlayer(this);
		
		// choose beginning player
		boolean b;
		
		if (m_humanColor == IController.NONE) {
			b = Math.random() < 0.5;
		} else {
			// computer will begin every second game
			b = (m_humanColor == IController.WHITE);
		}
		
		setStarter(b);
		m_compi.start();
		
		if (b) {
			m_compi.play();
		}
	}
	
	/**
	 * Start a new game between two computer players using a game server
	 */
	public void startServerGame() {
		if (!m_serverGame && m_compi != null) m_compi.finish();
		
    	// start client thread
		m_gc.startGame();
		m_view.prepareBoard();	
		m_serverGame = true;
	}
	
	/**
	 * Stop a game server game
	 */
	public void stopServerGame() {
		// stop client thread
		try {
			m_gc.stopGame((m_humanColor == IController.NONE) ? IController.WHITE : m_humanColor);
		} catch (IOException ex) {
        	if (VERBOSE) System.out.println("Error: IO exeception.\n" + ex.getMessage());
		}
		m_serverGame = false;
	}
	
	/**
	 * Connect to a game server.
	 * @return Returns true if the connection has been established
	 */
	public boolean connectToServer() {
		boolean retValue = false;
    	m_gc = new GameClient(this);
		
		// the server will decide who will open the game
		m_humanColor = IController.NONE;
		
		if (VERBOSE) System.out.println("Client tries to connect to server.");
        try {
        	if (m_gc.openConnection()) {
        		if (VERBOSE) System.out.println("Connection established, registration done, and game initialized.");
        		retValue = true;
        	} else {
        		if (VERBOSE) System.out.println("Connection or registration failed.");        		
        	}
    		
        } catch(IOException ex) {
        	if (VERBOSE) System.out.println("Error: IO exeception.\n" + ex.getMessage());
        }        	
        
        return retValue;
	}
	
	/**
	 * Set player who opens the game.
	 * called by parallel thread in a server game
	 */
	public void setStarter(boolean b) {
		if (b) {
			// computer starts
			if (VERBOSE) System.out.println("Computer starts");
			m_humanColor = IController.BLACK;
			m_humanStarts = false;
		} else {
			// human starts
			if (VERBOSE) System.out.println("Human starts");
			m_humanColor = IController.WHITE;
			m_humanStarts = true;
		}
		
		// refresh gui: must be called after beginning player have been chosen
		m_view.prepareBoard();	
		
		// create new game tree
		m_gameTree = new GameTree();		
		if (b) {
			// computer will open the game
			m_gameTree.create(TREEDEPTH, null);
			if (VERBOSE) System.out.println("\tgame tree created; tree size: " + m_gameTree.size());
			m_gameTree.print();
		}
	}
	
	/**
	 * Set names of players
	 */
	public void setPlayerName(String name) {
		m_view.setComputerName(name);
		m_view.setHumanName("Opponent");
	}
	
	/**
	 * Exit application.
	 */
	public void exit() {
		if (m_serverGame) closeConnection();
		System.exit(0);
	}
	
	/**
	 * Return color of human player.
	 * @return Color of human player or IController.NONE if not initialized yet.
	 */
	public byte humanColor() {
		return m_humanColor;
	}
	
	/**
	 * Return true if human player opens the game.
	 * @return True if human player starts.
	 */
	public boolean humanStarts() {
		return m_humanStarts;
	}
	
	/**
	 * Human plays action a
	 */
	public Status play(Action a) {
		assert a != null;
		
		Status status = null;
		
		if (a instanceof ActionPM) {
			status = human((ActionPM)a);
		} else if (a instanceof Taking) {
			status = human((Taking)a);
		} else {
			assert false;
			status = Status.INVALIDACTION;
		}
		return status;
	}
	
	/**
	 * Play human player action 
	 * @param a Action
	 * @return status flag
	 */
	private Status human(ActionPM a) {
		State s = m_gameTree.currentState();
		
		// play human action
		if (s == null) {
			if (a instanceof Placing) {
				assert m_humanColor == IController.WHITE : "wrong human player color";
				m_gameTree.create(TREEDEPTH, (Placing)a);
				s = m_gameTree.currentState();
				if (VERBOSE) System.out.println("Human has played\n\ttree size: " + m_gameTree.size());
				m_gameTree.print();

			} else {
				m_view.updateBoard(m_gameTree.currentState(), a, false);
				return Status.INVALIDACTION;
			}
		} else {
			// check if a is a valid human player action
			if (a.isValid(s)) {
				State sCopy = s.clone();
				
				// update temporary state with user action
				a.update(sCopy);
				
				// check if a mill has been closed
				if (sCopy.inMill(a.endPosition(), a.color())) {
					// action is not yet played, because it is part of a taking action
					if (VERBOSE) System.out.println("Human closed mill\n\ttree size: " + m_gameTree.size());
					// redraw game board
					m_view.updateBoard(sCopy, a, false);
					return Status.CLOSEDMILL;		
				} else {
					// play human player action a
					m_gameTree.humanPlayer(a);
					if (VERBOSE) System.out.println("Human has played\n\ttree size: " + m_gameTree.size());
					m_gameTree.print();
				}
			} else {
				if (VERBOSE) System.out.println("Human played an invalid action");
				m_view.updateBoard(m_gameTree.currentState(), a, false);
				return Status.INVALIDACTION;
			}
		}
		
		if (s.finished()) {
			if (VERBOSE) System.out.println("Human has won");
			m_view.updateBoard(m_gameTree.currentState(), a, false);
			return Status.FINISHED;
		} else {
			m_view.updateBoard(m_gameTree.currentState(), a, false);
			return Status.OK;
		}
	}
	
	/**
	 * Play human taking action
	 * @param a Action
	 * @return Status flag
	 */
	private Status human(Taking a) {
		State s = m_gameTree.currentState();
		
		// human take action
		if (s == null) {
			m_view.updateBoard(m_gameTree.currentState(), a, false);
			return Status.INVALIDACTION;
		} else {
			// first check if a taking action is possible at all
			if (s.takingIsPossible(State.oppositeColor(a.color()))) {
				// now check if a is a valid taking action
				if (a.isValid(s)) {
					// play human player action a
					m_gameTree.humanPlayer(a);
					if (VERBOSE) System.out.println("Human has played\n\ttree size: " + m_gameTree.size());
					m_gameTree.print();
					
					m_view.updateBoard(m_gameTree.currentState(), a, false);
					if (s.finished()) {
						if (VERBOSE) System.out.println("Human has won");
						return Status.FINISHED;
					} else {
						return Status.OK;
					}
				} else {
					// ActionPM part of a is valid, just the taking is invalid
					State sCopy = s.clone();
					
					// update state with user action
					a.action().update(sCopy);
						
					// redraw game board
					m_view.updateBoard(sCopy, a.action(), false);
					return Status.INVALIDACTION;
				}
			} else {
				m_view.updateBoard(m_gameTree.currentState(), a, false);
				return Status.OK;
			}
		}
	}
	
	/**
	 * Invokes the parallel computer player just for one move
	 */
	public void computeAsync() {
		assert !m_serverGame && m_compi != null;
		m_compi.play();
	}
	
	/**
	 * Play computer player action
	 * @return Status flag
	 */
	public Action compute() {
		// compute computer player action
		Action a = m_gameTree.computerPlayer();
		if (VERBOSE) System.out.println("Computer has played\n\ttree size: " + m_gameTree.size());
		m_gameTree.print();	
		
		// redraw game board: current game tree state is the state after computer played
		m_view.updateBoard(m_gameTree.currentState(), a, true);
				
		return a;
	}
	
	/**
	 * @return Returns current controller status.
	 */
	public Status getStatus() {
		if (m_gameTree.currentState().finished()) {
			if (VERBOSE) System.out.println("Game has been finished");
			return Status.FINISHED;
		} else {
			System.out.println("\ttree size: " + m_gameTree.size());
			m_gameTree.print();
			return Status.OK;
		}
	}
	
	/**
	 * Returns the winner of the game
	 * @return NONE: no winner, BLACK: black, WHITE: white
	 */
	public int getWinner() {
		return (m_gameTree != null && m_gameTree.currentState() != null) ? m_gameTree.currentState().winner() : NONE;
	}
	
	/**
	 * The server asks for closing its connection
	 */
	public void closeConnection() {
        try {
        	m_gc.closeConnection();
        	if (VERBOSE) System.out.println("Connection closed");
        } catch(IOException ex) {
        	if (VERBOSE) System.out.println("Error: IO exeception.\n" + ex.getMessage());
        }    
	}
	
	@Override
	public boolean humanPlayer() {
		return !m_serverGame;
	}
}
