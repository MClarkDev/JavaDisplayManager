package org.jdm.display.panel;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;

import org.jdm.api.yfinance.StockData;
import org.jdm.api.yfinance.YFinanceAPI;
import org.jdm.core.Panel;

public class StockPanel extends Panel {
	public StockPanel() {
		setGridSize(2, 1);
	}

	private String symbol;

	private FontMetrics metrics;

	private Font fontStandard;

	private int valueWidth;

	private int valuePosX, valuePosY;

	@Override
	protected void onDraw(Graphics g) {

		YFinanceAPI yfinanceAPI = YFinanceAPI.getInstance();
		StockData data = (StockData) yfinanceAPI.get(symbol);

		if (data == null) {

			// TODO render error
			return;
		}

		double price = data.getPrice();

		int height = getHeight();
		int width = getWidth();

		int padding = getPadding();

		Color c = Color.GREEN;
		g.setColor(new Color(c.getRed(), c.getGreen(), c.getBlue(), 56));
		g.fillRect(0, 0, width, height);

		int divHeight = height / 3;
		g.setColor(Color.gray);

		int fontSize = (int) ((divHeight - padding) * .75f);
		fontStandard = new Font("Courier New", Font.PLAIN, fontSize);

		g.setFont(fontStandard);
		metrics = g.getFontMetrics();

		String line = String.format("$%.2f", price);
		valueWidth = metrics.stringWidth(line);
		valuePosX = (width / 2) - (valueWidth / 2);
		valuePosY = 2 * divHeight - padding;
		g.drawString(line, valuePosX, valuePosY);
	}

	@Override
	protected void onUpdate(String string) {

		this.symbol = string;
	}
}
