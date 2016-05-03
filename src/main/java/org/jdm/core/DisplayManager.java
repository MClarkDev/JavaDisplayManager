package org.jdm.core;

import java.awt.Dimension;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.util.ArrayList;

import javax.swing.JFrame;

import org.jdm.util.DrawingUtils;

public class DisplayManager extends JFrame {

	private static final long serialVersionUID = 6367140938950102118L;

	private int rotationInterval = 15000;

	private int activeDisplay = 0;

	// array list to preserve order
	private ArrayList<Display> displays = new ArrayList<Display>();

	public DisplayManager() {

		setTitle("jdm");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLayout(null);

		getContentPane().setCursor(DrawingUtils.getHiddenCursor());
		getContentPane().setLayout(null);

		new Thread() {
			public void run() {

				while (!isInterrupted()) {

					try {

						if (!displays.isEmpty()) {

							renderNext();
						}
						Thread.sleep(rotationInterval);
					} catch (Exception e) {
					}
				}
			}
		}.start();

		addComponentListener(new ComponentListener() {

			public void componentResized(ComponentEvent e) {

				render();
			}

			@Override
			public void componentHidden(ComponentEvent arg0) {
				// TODO Auto-generated method stub
			}

			@Override
			public void componentMoved(ComponentEvent arg0) {
				// TODO Auto-generated method stub
			}

			@Override
			public void componentShown(ComponentEvent arg0) {
				// TODO Auto-generated method stub
			}
		});
	}

	public void showDM() {

		showDM(false);
	}

	public void showDM(boolean fullscreen) {

		if (fullscreen) {

			// set fullscreen
			setUndecorated(true);
			setExtendedState(JFrame.MAXIMIZED_BOTH);

			GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();

			if (gd.isFullScreenSupported()) {

				gd.setFullScreenWindow(this);
			}
		} else {

			// not fullscreen
			Dimension size = new Dimension(1280, 800);

			setSize(size);
			setResizable(true);
			try {
				setUndecorated(false);
			} catch (Exception e) {
			}
		}

		// show frame
		setLocationRelativeTo(null);
		setVisible(true);
		toFront();
	}

	public void hideDM() {

		// if fullscreen
		GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();

		if (gd.isFullScreenSupported() && gd.getFullScreenWindow() != null) {

			gd.getFullScreenWindow().dispose();
			gd.setFullScreenWindow(null);
		}

		setAlwaysOnTop(false);
		toBack();
		setExtendedState(JFrame.ICONIFIED);
		setVisible(false);
		invalidate();
		dispose();
	}

	public void setActiveDisplay(int index) {

		activeDisplay = index;
		render(activeDisplay);
	}

	public void addDisplay(Display display) {

		displays.add(display);
	}

	public void delDisplay(int index) {

		displays.remove(index);
	}

	public Display getDisplay(int index) {

		return displays.get(index);
	}

	public void setRotationInterval(int rotationInterval) {

		this.rotationInterval = rotationInterval;
	}

	public void render() {

		render(activeDisplay);
	}

	public void renderNext() {

		// next display
		if (++activeDisplay >= displays.size()) {

			activeDisplay = 0;
		}

		render(activeDisplay);
	}

	public void renderLastDisplay() {

		activeDisplay = displays.size() - 1;
		render(activeDisplay);
	}

	public void render(int index) {

		try {

			System.out.println("Rendering Display [ " + index + " ]");

			setLayout(null);
			getContentPane().removeAll();
			getContentPane().setLayout(null);

			Display display = displays.get(index);

			int width = getContentPane().getWidth();
			int height = getContentPane().getHeight();

			getContentPane().add(display);

			display.setLocation(0, 0);
			display.setSize(width, height);

			display.draw();

			// repaint the window
			invalidate();
			repaint();
			revalidate();
		} catch (Exception e) {

			e.printStackTrace();
		}
	}
}
