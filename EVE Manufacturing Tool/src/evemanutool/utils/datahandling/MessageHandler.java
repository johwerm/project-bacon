package evemanutool.utils.datahandling;

import java.util.concurrent.LinkedBlockingQueue;

import evemanutool.gui.general.components.AnimatedLabel;

public class MessageHandler extends ThreadedHandler {

	//Constants.
	private static final long TICK = 5;
	private static final int MIN_TICKS = 300; // 1 s.
	private static final int MAX_TICKS = 1000; // 5 s.
	
	//Message queue.
	private LinkedBlockingQueue<String> queue = new LinkedBlockingQueue<>();
	
	//Worker thread.
	private Thread worker;
	
	//GUI.
	private AnimatedLabel label;
	
	//Object locks.
	private final Object labelLock = new Object();
	
	public MessageHandler() {}
	
	public AnimatedLabel getLabel() {
		synchronized (labelLock) {
			return label;
		}
	}
	
	public void setLabel(AnimatedLabel label) {
		synchronized (labelLock) {
			this.label = label;
			
			//Make sure that it's not showing the text.
			this.label.setTextShowing(false);
		}
	}

	public void addMessage(String message) {
		queue.offer(message);
		System.out.println("Message: " + message);
	}

	@Override
	public void init() {
		if (!isRunning()) {
			setRunning(true);
			
			//Start worker.
			worker = new Thread(new MessageWorker());
			worker.start();
		}
	}

	@Override
	public void exit() {
		if (isRunning()) {
			getLabel().exit();
			worker.interrupt();
		}
	}

	@Override
	public void kill() {
		exit();
	}
	
	private class MessageWorker implements Runnable {


		@Override
		public void run() {
			while (!Thread.interrupted()) {
				try {
					//Get message from queue (blocking).
					//Set text and start animation.
					getLabel().setText(queue.take());
					getLabel().animateIn();
					
					//Wait for animation to complete.
					while (!getLabel().textShowing()) {
						Thread.sleep(TICK);
					}
					
					//Depending if there are more messages to show:
					//Let the message show for a while.
					for (int i = 0; i < MAX_TICKS ; i++) {
						if (!queue.isEmpty() && i > MIN_TICKS) {
							break;
						}
						Thread.sleep(TICK);
					}
					
					//Animate out and wait for completion.
					getLabel().animateOut();
					while (getLabel().textShowing()) {
						Thread.sleep(TICK);
					}
					
				} catch (InterruptedException e) {
					//End thread.
					return;
				}
			}
		}
	}
}
