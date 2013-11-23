package evemanutool.utils.datahandling;

public abstract class ThreadedHandler {

	/*
	 * An Abstract superclass for multithreaded handlers.
	 * This provides a unified life-cycle control.
	 */
	
	//Handler status.
	private boolean running = false;
	
	public abstract void init();
	public abstract void exit();
	public abstract void kill();
	
	protected boolean isRunning() {
		return running;
	}
	
	protected void setRunning(boolean running) {
		this.running = running;
	}
}
