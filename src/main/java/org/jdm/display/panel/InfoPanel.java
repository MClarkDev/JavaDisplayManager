package org.jdm.display.panel;

import java.awt.Color;
import java.awt.Graphics;

import org.jdm.core.Panel;

public class InfoPanel extends Panel {

	private static final long serialVersionUID = 5205381773828965892L;

	@Override
	protected void onDraw(Graphics g) {

		int height = getHeight();
		int width = getWidth();

		int divHeight = height / 8;
		g.setColor(Color.gray);
		g.drawLine(0, 1 * divHeight, width, 1 * divHeight);
		g.drawLine(0, 2 * divHeight, width, 2 * divHeight);
		g.drawLine(0, 3 * divHeight, width, 3 * divHeight);
		g.drawLine(0, 4 * divHeight, width, 4 * divHeight);
		g.drawLine(0, 5 * divHeight, width, 5 * divHeight);
		g.drawLine(0, 6 * divHeight, width, 6 * divHeight);
		g.drawLine(0, 7 * divHeight, width, 7 * divHeight);
	}

	@Override
	protected void onUpdate(String string) {
		// TODO Auto-generated method stub
		
	}
}
