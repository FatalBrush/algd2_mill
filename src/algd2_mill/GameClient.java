package algd2_mill;
import java.io.*;
import java.net.*;

/**
 * Client used in client-server games.
 * 
 * @author  Christoph Stamm
 * @version  14.9.2010
 * 
 */
public class GameClient {
	/**
	 * Parallel client thread.
	 */
	class Game extends Thread {
 		private IController.Status m_status;
	    public Exception m_error;
	    
		public void run() {
	        System.out.println("Thread starts");

	 		try {
				do {
					m_status = readAndWait();
				} while(m_status == IController.Status.OK || m_status == IController.Status.CLOSEDMILL);
	        } catch(IOException ex) {
	        	m_error = ex;
	        }
	        System.out.println("Thread terminates");
		}
		
		private IController.Status readAndWait() throws IOException {
			assert m_inFromServer != null;
			
	        String s = m_inFromServer.readLine(); 
	        //System.out.println("Protocol: " + s);
	        String[] token = s.split(" ");
	        
	        if (token[0].equals("PLAY")) {
	        	// I have to play: compute move
	        	Action a = m_controller.compute();
	    		IController.Status status = m_controller.getStatus();
	    		if (status == IController.Status.FINISHED) {
	    			stopGame(m_controller.getWinner());
	    		} else if (status == IController.Status.OK && a != null) {
		        	// send move to server
		        	a.writeln(m_outToServer);
		        }
	    		return status;
	        } else if (token[0].equals("WHITE")) {
	        	// I will play white stones and open the game
	        	m_controller.setStarter(true);
	    		return IController.Status.OK;
	        } else if (token[0].equals("BLACK")) {
	        	// I will play black stones
	        	m_controller.setStarter(false);
	    		return IController.Status.OK;
	        } else if (token[0].equals("STOP")) {
	        	// close connection
	        	m_controller.closeConnection();
	    		return IController.Status.FINISHED;
	        } else if (token[0].equals("FINISH")) {
	        	stopGame(IController.NONE);
	        	return IController.Status.FINISHED;
	        } else {
	        	// opponent plays action a
	        	Action a = Action.readln(token);
	        	if (a != null) {
	        		IController.Status status = m_controller.play(a);
	        		if (status == IController.Status.FINISHED) stopGame(m_controller.getWinner());
	        		return status;
	        	} else {
	        		return IController.Status.INVALIDACTION;
	        	}
	        }
		}
		
	}
	
	private final static int s_port = 18181;

    private Socket m_clientSocket;
	private IController m_controller;
    private BufferedReader m_inFromServer; 
    private DataOutputStream m_outToServer; 
    private Game m_game;
    private String m_playerName;
    
    /**
     * Constructor
     * @param controller
     */
    public GameClient(IController controller) {
    	m_controller = controller;
    }
    
    /**
     * Open connection to server
     * @return true if a connection has been established
     * @throws IOException
     */
	public boolean openConnection() throws IOException {
        try {
    		String hostName;
    		
    		try {
    			// read server address and player name
    			BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream("config.txt")));
    			hostName = reader.readLine();
    			m_playerName = reader.readLine();
    			reader.close();
    		} catch(FileNotFoundException ex) {
    			System.out.println("Warning: config.txt not found.");
    			hostName = "localhost";
    			m_playerName = "Miller";
    		}
    		
            m_clientSocket = new Socket(hostName, s_port); 
            m_outToServer = new DataOutputStream(m_clientSocket.getOutputStream()); 
            m_inFromServer = new BufferedReader(new InputStreamReader(m_clientSocket.getInputStream()));    
            
            // removes blanks in player's name
            StringBuilder sb = new StringBuilder(m_playerName);
            for (int i=0; i < sb.length(); i++) {
            	if (sb.charAt(i) == ' ') sb.setCharAt(i, '_');
            }
            
            // send registration message
            m_outToServer.writeBytes("REGISTER " + sb.toString() + '\n');

            // read answer from server
            String s = m_inFromServer.readLine(); 
            if (s.equals("OK")) {
            	m_controller.setPlayerName(m_playerName);
            	// registration successfully done
            	return true;
            }
        } catch(ConnectException ex) {
        }
        
        return false;
	}
	
	/**
	 * Start a new game. The connection has to be established in advanced.
	 */
	public void startGame() {
		assert m_clientSocket != null && m_clientSocket.isConnected() && m_clientSocket.isBound();
    	m_controller.setPlayerName(m_playerName);
		m_game = new Game();
		m_game.start();
	}
	
	/**
	 * Stop a running game.
	 * @param winner The winner of the game.
	 * @throws IOException
	 */
	public void stopGame(int winner) throws IOException {
		assert m_clientSocket != null && m_clientSocket.isConnected() && m_clientSocket.isBound();
		
		switch(winner) {
		case IController.BLACK:
			m_outToServer.writeBytes("FINISH BLACK\n");
			break;
		case IController.WHITE:
			m_outToServer.writeBytes("FINISH WHITE\n");
			break;
		default:
			m_outToServer.writeBytes("FINISH\n");
			break;
		}
	}	
	
	/**
	 * Close an open connection.
	 * @throws IOException
	 */
	public void closeConnection() throws IOException {
		if (m_clientSocket != null) {
			m_outToServer.writeBytes("STOP\n");
			m_clientSocket.close(); 
		}
	}
	
}

