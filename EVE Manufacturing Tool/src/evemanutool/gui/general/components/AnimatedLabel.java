package evemanutool.gui.general.components;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JPanel;
import javax.swing.Timer;

@SuppressWarnings("serial")
public class AnimatedLabel extends JPanel implements ActionListener {

	//Constants.
	private static final int RIGHT_TEXT_MARGIN = 10;
	private static final double SPEED_FACTOR = 0.3;
	private static final double BASE_SPEED = 0.8;
//	private static final double SPEED_FACTOR = 0.3;
//	private static final double BASE_SPEED = 1.7;

	//Text coordinates.
	private double x; //Double to make animation smoother.
	private double y;
	
	//Text color.
	private int a = 255; //Alpha.
	
	//Text.
	private String text = "";

	//Animation.
	private Timer animIn;
	private Timer animOut;
	private int textWidth;
	private boolean textShowing;
	
	//Object locks.
	private final Object textShowLock = new Object();
	private Object textLock = new Object();

	public AnimatedLabel(int sizeX, int sizeY) {
		this.setOpaque(false);
		this.setTextShowing(false);
		this.setDoubleBuffered(true);
		this.textWidth = RIGHT_TEXT_MARGIN;
		animIn = new Timer(30, new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
//				x -= BASE_SPEED + (x - (getWidth() - textWidth)) * SPEED_FACTOR;
				y -= BASE_SPEED + (y - ((getHeight() + getFont().getSize()) / (double) 2)) * SPEED_FACTOR;
//				a = (int) ALPHA_MAX * (x - minX));
//				if (x > (getWidth() - textWidth)) {
//					setTextShowing(true);
//					animIn.stop();
//				}
				if (y < (int) (((getHeight() + getFont().getSize()) / (double) 2) + 0.5)) {
					setTextShowing(true);
					animIn.stop();
				}
				repaint();
			}
		});
		
		animOut = new Timer(30, new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
//				x += BASE_SPEED + (x - (getWidth() - textWidth)) * SPEED_FACTOR;
				y -= BASE_SPEED + (((getHeight() + getFont().getSize()) / (double) 2) - y) * SPEED_FACTOR;
//				a = (int) ALPHA_MAX * (x - minX));
				if (y < 0) {
					setTextShowing(false);
					animOut.stop();
				}
//				if (x > getWidth()) {
//					setTextShowing(false);
//					animOut.stop();
//				}
				repaint();
			}
		});
	}
	
	/*
	 * Returns true if text is showing.
	 */
	public boolean textShowing() {
		synchronized (textShowLock ) {
			return textShowing;
		}
	}
	
	public void setTextShowing(boolean textShowing) {
		synchronized (textShowLock) {
			this.textShowing = textShowing;
		}
	}
	
	public boolean animateIn() {
		if (	!animIn.isRunning() &&
				!animOut.isRunning() &&
				!textShowing()) {
			animIn.start();
			return true;
		}
		return false;
	}

	public boolean animateOut() {
		if (	!animIn.isRunning() &&
				!animOut.isRunning() &&
				textShowing()) {
			animOut.start();
			return true;
		}
		return false;
	}
	
	@Override
	public void paint(Graphics g) {
		
		//Correct position.
		if (	!animIn.isRunning() &&
				!animOut.isRunning()) {
			if (textShowing()) {
				y = (int) (((getHeight() + getFont().getSize()) / (double) 2) + 0.5);
//				x = getWidth() - textWidth;
			} else {
				y = getHeight() + getFont().getSize();
//				x = getWidth();
			}
		}
		
		//Set color.
		Color c = new Color(0, 0, 0, a);
		g.setColor(c);
		
		//Set x.
		x = getWidth() - textWidth;
//		y = (int) (((getHeight() + getFont().getSize()) / (double) 2) + 0.5);
		
//		System.out.println("x:" + (int) (x + 0.5) + "     y:" + (int) (y + 0.5));
		//Draw text.
		g.drawString(getText(), (int) (x + 0.5), (int) (y + 0.5));
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		
	}
	
	public void exit() {
		animIn.stop();
		animOut.stop();
	}

	public String getText() {
		synchronized (textLock) {
			return text;
		}
	}
	
	public void setText(String text) {
		synchronized (textLock) {
			//Set text starting and end values. 
			textWidth = getFontMetrics(getFont()).stringWidth(text) + RIGHT_TEXT_MARGIN;
			
			//Set text.
			if (text != null) {
				this.text = text;
			}
		}
	}
}
