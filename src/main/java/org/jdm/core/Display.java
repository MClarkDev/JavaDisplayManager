package org.jdm.core;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;

import javax.swing.JPanel;

public class Display extends JPanel {

	public Display() {
		super();
		setOpaque(false);
		setBackground(Color.BLACK);
	}

	private static final long serialVersionUID = -8025974966030477942L;

	private Panel[][] panels;

	public Display(int width, int height) {

		// the panels
		panels = new Panel[width][height];
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

	public void setPanel(int x, int y, Panel panel) {

		panels[x][y] = panel;
	}

	public Panel getPanel(int x, int y) {

		return panels[x][y];
	}

	public Panel[][] getPanels() {

		return panels;
	}

	public void draw() {

		int padding = 8;
		int halfPad = padding / 2;

		int width = (int) (getSize().getWidth());
		int height = (int) (getSize().getHeight());

		// clear the display
		removeAll();
		setLayout(null);

		// loop each panel
		for (int x = 0; x < panels.length; x++) {

			for (int y = 0; y < panels[x].length; y++) {

				Panel panel = panels[x][y];

				if (panel != null) {

					// add to the grid
					add(panel);

					// size
					int gridWidth = panel.getGridWidth();
					int gridHeight = panel.getGridHeight();

					int panelWidth = (width / panels.length);
					int panelHeight = height / panels[x].length;

					int myWidth = (gridWidth * panelWidth);
					int myHeight = (gridHeight * (panelHeight));

					panel.setSize(myWidth - padding, myHeight - padding);
					panel.setPadding(padding);

					// position

					int posX = panelWidth * x;
					int posY = panelHeight * y;

					panel.setLocation(posX + halfPad, posY + halfPad);

					// draw
					// panel.draw();
				}
			}
		}

		// draw the grid
		invalidate();
		repaint();
		revalidate();
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);

		g.setColor(Color.black);
		g.fillRect(0, 0, getWidth(), getHeight());
	}
}
