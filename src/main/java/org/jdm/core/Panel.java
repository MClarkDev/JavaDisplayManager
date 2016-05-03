package org.jdm.core;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;

import javax.swing.JPanel;

public abstract class Panel extends JPanel {

	private int gridWidth = 1;

	private int gridHeight = 1;

	private int padding = 0;

	public Panel() {
		super();
		setBackground(Color.BLACK);

	}

	private static final long serialVersionUID = -8806188609505707837L;

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);

		// Performance options
		Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED);
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
		g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
		g2.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_OFF);
		g2.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_SPEED);
		g2.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_DISABLE);

		// clear
		g.clearRect(0, 0, getWidth(), getHeight());

		// draw the border
		// g.setColor( getBackground() );
		// g.drawRect( 0, 0, getWidth(), getHeight() );

		g.setColor(getBackground());
		g.fillRect(0, 0, getWidth(), getHeight());

		// call the implemented method
		try {
			onDraw(g);
		} catch (Exception e) {
			System.out.println("Failed to draw display object.");
			e.printStackTrace();
		}
	}

	/**
	 * request a redraw of the display
	 */
	public void draw() {

		this.invalidate();
		this.repaint();
		this.revalidate();
	}

	public void setGridSize(int w, int h) {

		this.gridWidth = w;
		this.gridHeight = h;
	}

	@Override
	public void setSize(int x, int y) {
		super.setSize(x, y);

		setPreferredSize(new Dimension(x, y));
	}

	@Override
	public void setLocation(int x, int y) {
		super.setLocation(x, y);

		setBounds(new Rectangle(new Point(x, y), getPreferredSize()));
	}

	protected abstract void onDraw(Graphics g);

	public void update(String update) {
		onUpdate(update);
	}

	protected abstract void onUpdate(String string);

	public int getGridWidth() {
		return gridWidth;
	}

	public int getGridHeight() {
		return gridHeight;
	}

	public int getPadding() {
		return padding;
	}

	public void setPadding(int padding) {
		this.padding = padding;
	}
}
