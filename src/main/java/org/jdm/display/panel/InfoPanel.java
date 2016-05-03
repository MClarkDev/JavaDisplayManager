package org.jdm.display.panel;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;

import org.jdm.core.Panel;

public class InfoPanel extends Panel {

	private String[] data;

	private int stringIndex = 0;

	private boolean lines = true;

	private int lineCount = 0;

	public InfoPanel() {
		super();
	}

	private static final long serialVersionUID = 5205381773828965892L;

	private FontMetrics metrics;

	private Font fontStandard;

	private int valueWidth;

	private int valuePosX, valuePosY;

	@Override
	protected void onDraw(Graphics g) {

		int height = getHeight();
		int width = getWidth();

		int padding = getPadding();

		int divs = lineCount;

		int divHeight = height / divs;

		String line;
		for (int x = 0; x < divs; x++) {
			// TODO draw the strings
			int fontSize = (int) ((divHeight - padding) * .75f);
			fontStandard = new Font("Courier New", Font.PLAIN, fontSize);

			if (lines) {
				g.setColor(Color.LIGHT_GRAY);
				g.drawLine(padding, (x * divHeight), width - padding, (x * divHeight));
			}

			line = data[stringIndex + x];

			if (!line.equals("_")) {
				g.setColor(getForeground());
				g.setFont(fontStandard);
				metrics = g.getFontMetrics();

				valueWidth = metrics.stringWidth(line);
				valuePosX = (width / 2) - (valueWidth / 2);
				valuePosY = (x + 1) * divHeight - padding;
				// TODO center this
				g.drawString(line, valuePosX, valuePosY);
			}
		}
	}

	@Override
	protected void onUpdate(String string) {
		data = string.split(",");
		lineCount = data.length;

		stringIndex = 0;

		try {
			String color = data[stringIndex];
			Color c = Color.decode(color);
			setBackground(c);

			stringIndex++;
			lineCount -= 1;
		} catch (Exception e) {
		}

		try {
			String color = data[stringIndex];
			Color c = Color.decode(color);
			setForeground(c);

			stringIndex++;
			lineCount -= 1;
		} catch (Exception e) {
		}

		try {
			lines = Boolean.parseBoolean(data[stringIndex]);
			stringIndex++;
			lineCount -= 1;
		} catch (Exception e) {
		}

		if (lineCount < 1) {
			throw new IllegalArgumentException("<backgroundColor,><linesEnabled,>[display, data]");
		}
	}
}
