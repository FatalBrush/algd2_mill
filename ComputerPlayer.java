/**
 * 
 * @author christoph.stamm
 * @version  14.9.2010
 *
 */
public class ComputerPlayer extends Thread {
	private IController m_controller;
	private boolean m_signal;
	private IController.Status m_status;
	
	public ComputerPlayer(IController controller) {
		m_controller = controller;
	}
	
	public void finish() {
		m_status = IController.Status.FINISHED;
		m_signal = true;
		try {
			notify();
		} catch(IllegalMonitorStateException ex) {
		}
	}
	
	public synchronized void play() {
		m_signal = true;
		notify();
	}
	
	public void run() {
		synchronized(this) {
			try {
				do {
					while (!m_signal) wait();
					m_signal = false;
					if (m_status != IController.Status.FINISHED) {
						sleep(500);
						m_controller.compute();
						m_status = m_controller.getStatus();
					}
				} while(m_status == IController.Status.OK || m_status == IController.Status.CLOSEDMILL);
			} catch(InterruptedException ex) {
				
			}
		}
	}
}
