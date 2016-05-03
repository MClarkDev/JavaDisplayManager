package org.jdm.display.panel;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.jdm.api.graphite.GraphiteAPI;
import org.jdm.api.graphite.MetricSet;
import org.jdm.core.Panel;

public class GrafPanel extends Panel {

	private double[] time;
	private double[][] data;
	private boolean drawBubble = true;
	private boolean drawLine = true;

	private String host;
	private String key;

	Color gridColor = Color.decode("#999999");
	Color labelColor = Color.decode("#AAAAAA");
	Color pointColor = Color.decode("#FFFFFF");
	Color lineColor = Color.decode("#FFFFFF");

	public GrafPanel() {
		super();

	}

	@Override
	protected void onDraw(Graphics g) {

		// get an instance of the api
		GraphiteAPI api = GraphiteAPI.getInstance();
		MetricSet metrics = (MetricSet) api.get(host + "|" + key);

		// draw
		if (metrics != null) {

			// get the data
			data = metrics.getData();
			time = metrics.getTime();

			long timeStart = (long) time[0];
			long timeEnd = (long) time[time.length - 1];

			int drawCount = 100;
			int top = getHeight();
			double dataMax = getMaxValue(data);
			boolean drawXLabel = false, drawYLabel = true;

			// X Axis
			int xPixelsPerDivision = (int) (getWidth() / 10);
			int xValuePerDivision = (int) ((timeEnd - timeStart) / 10);

			for (int x = 0; x < 10; x++) {

				g.setColor(gridColor);
				g.drawLine(x * xPixelsPerDivision, 0, x * xPixelsPerDivision, getHeight());

				if (drawXLabel && x > 0) {

					long pointTime = timeStart + (x * xValuePerDivision);
					Date date = new Date(pointTime);
					SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");
					String label = formatter.format(date);

					g.setColor(labelColor);
					g.drawString(label, (x * xPixelsPerDivision) - 15, top - 2);
				}
			}

			// Y Axis
			int yPixelsPerDivision = (int) (getHeight() / 10);
			float yValuePerDivision = new Float((dataMax / 10));
			int yValuePerPoint = (int) (dataMax / drawCount);

			for (int x = 0; x < 10; x++) {

				g.setColor(gridColor);
				g.drawLine(0, top - (x * yPixelsPerDivision), getWidth(), top - (x * yPixelsPerDivision));

				if (drawYLabel && x > 0) {

					String label = String.format("%2.2f", (yValuePerDivision * x));

					g.setColor(labelColor);
					g.drawString(label, 2, top - (x * yPixelsPerDivision) - 2);
				}
			}

			// loop each metric
			for (int index = 0; index < data.length; index++) {

				// Data
				if (data[index].length < 2) {

					return;
				}

				float xPixelsPerPoint = (Float.valueOf(getWidth()) / drawCount);
				float yPixelsPerPoint = new Float((Float.valueOf(getHeight()) / dataMax));

				// Draw data points
				Point lastPoint = new Point(0, top);
				for (int x = 0; x < drawCount; x++) {

					int point = data[index].length - drawCount + x;

					// Point Position
					int posX = (int) (xPixelsPerPoint * x);
					int posY = (int) (top - (yPixelsPerPoint * data[index][point]));

					if (drawBubble) {

						g.setColor(pointColor);
						g.drawArc(posX, posY, 3, 3, 0, 0);
					}

					if (drawLine) {

						g.setColor(lineColor);
						g.drawLine(lastPoint.x, lastPoint.y, posX, posY);
					}

					lastPoint = new Point(posX, posY);
				}
			}
		}
	}

	private static double getMaxValue(double[][] data) {

		double max = 0;
		for (int x = 0; x < data.length; x++) {

			for (int y = 0; y < data[x].length; y++) {

				max = (data[x][y] > max) ? data[x][y] : max;
			}
		}
		return max;
	}

	public static synchronized double[] getDoubleArray(ArrayList<Double> raw) {

		double dat[] = new double[raw.size()];
		for (int x = 0; x < raw.size(); x++) {
			dat[x] = Double.valueOf(raw.get(x));
		}
		return dat;
	}

	@Override
	protected void onUpdate(String string) {

		String[] params = string.split("&");

		host = params[0];
		key = params[1];
	}
}
