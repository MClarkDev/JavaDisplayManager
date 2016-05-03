package org.jdm.display.panel;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.util.HashMap;

import org.jdm.api.jenkins.Build;
import org.jdm.api.jenkins.BuildStatus;
import org.jdm.api.jenkins.JenkinsAPI;
import org.jdm.core.Panel;

public class BuildPanel extends Panel {

	private static final long serialVersionUID = 6524117617353693061L;

	private static final int colorSchema = 9;

	// color map
	private HashMap<BuildStatus, Color> colorMap;

	public BuildPanel() {
		super();

		setLayout(null);

		// get the default color mapping
		colorMap = BuildStatus.getColorMap(colorSchema);
	}

	private FontMetrics metrics;

	private String host;

	private String name;

	private int titleWidth;

	private int titlePosX, titlePosY;

	private String value;

	private int valueWidth;

	private int valuePosX, valuePosY;

	private Font fontStandard, fontBold;

	@Override
	public void onDraw(Graphics g) {

		Build build;

		if (host == null || host.equals("") || name == null || name.equals("")) {

			build = Build.Unknown("", "");
		} else {

			JenkinsAPI jenkinsAPI = JenkinsAPI.getInstance();
			build = (Build) jenkinsAPI.get(host + "|" + name);

			if (build == null) {

				build = Build.Unknown(host, name);
			}
		}

		// get the size
		int width = getWidth();
		int height = getHeight();

		// background color based on last or present
		Color c = Color.decode("#AAAAAA");
		if (build.getStatus() != BuildStatus.BUILDING) {

			// show the current status
			c = BuildStatus.getColorMap(colorSchema).get(build.getStatus());
		} else {

			// show status of last build if applicable
			Build last = build.getLast();
			if (last != null && last.getStatus() != BuildStatus.UNKNOWN) {

				c = BuildStatus.getColorMap(colorSchema).get(last.getStatus());
			}
		}
		g.setColor(c);
		g.fillRect(0, 0, width, height);

		// dividers
		int divHeight = height / 8;

		g.setColor(Color.gray);
		g.drawLine(0, 2 * divHeight, width, 2 * divHeight);
		g.drawLine(0, 7 * divHeight, width, 7 * divHeight);

		// padding
		int padding = getPadding();

		// fonts
		int fontSize = (int) ((divHeight - padding) * .75f);
		fontStandard = new Font("Courier New", Font.PLAIN, fontSize);
		fontBold = new Font("Courier New", Font.BOLD, fontSize);

		// build name
		g.setColor(Color.black);
		g.setFont(fontBold);
		metrics = g.getFontMetrics();

		value = build.getName();
		valueWidth = metrics.stringWidth(value);
		valuePosX = (width / 2) - (valueWidth / 2);
		valuePosY = (int) (1.5 * divHeight) - padding;
		// TODO center this
		g.drawString(value, valuePosX, valuePosY);

		// progress
		if (build.getStatus() == BuildStatus.BUILDING) {

			// calculate approximate progress
			float progress = Float.valueOf(build.getTimeElapsed()) / Float.valueOf(build.getEstimatedDuration());

			// progress cannot go beyond 99
			if (progress >= .99) {

				progress = .99f;
			}

			// calculate the width of the status line
			int statusWidth = (int) ((width - 2) * progress);

			// draw the green progress line
			g.setColor(Color.green);
			g.fillRect(1, (7 * divHeight) + 1, statusWidth, (1 * divHeight));

			// draw progress string on top
			g.setColor(Color.black);
			g.setFont(fontStandard);
			metrics = g.getFontMetrics();

			value = String.format("%.1f%%", (progress * 100));
			valueWidth = metrics.stringWidth(value);
			valuePosX = (width / 2) - (valueWidth / 2);
			valuePosY = (8 * divHeight) - padding;

			g.drawString(value, valuePosX, valuePosY);

		} else {

			// idle
			g.setColor(Color.black);
			g.setFont(fontBold);
			metrics = g.getFontMetrics();

			value = build.getStatus().name();
			valueWidth = metrics.stringWidth(value);
			valuePosX = (width / 2) - (valueWidth / 2);
			valuePosY = (8 * divHeight) - padding;

			g.drawString(value, valuePosX, valuePosY);
		}

		// branch info
		g.setColor(Color.black);
		g.setFont(fontStandard);
		metrics = g.getFontMetrics();

		value = "xxxxxxxxxx";
		valueWidth = metrics.stringWidth(value);

		// draw names
		g.drawString(" Number : ", padding, (int) (3.5 * divHeight) - padding);
		g.drawString(" Author : ", padding, (int) (4.5 * divHeight) - padding);
		g.drawString(" Branch : ", padding, (int) (5.5 * divHeight) - padding);
		g.drawString(" Commit : ", padding, (int) (6.5 * divHeight) - padding);

		// draw values
		g.setFont(fontBold);
		g.drawString(Long.toString(build.getNumber()), padding + valueWidth, (int) (3.5 * divHeight) - padding);
		g.drawString(build.getCommitter(), padding + valueWidth, (int) (4.5 * divHeight) - padding);
		g.drawString(build.getBranch(), padding + valueWidth, (int) (5.5 * divHeight) - padding);
		g.drawString(build.getShortCommit(), padding + valueWidth, (int) (6.5 * divHeight) - padding);
	}

	@Override
	public String getName() {

		return name;
	}

	@Override
	public void setName(String name) {

		this.name = name;
	}

	public String getHost() {

		return host;
	}

	public void setHost(String host) {

		this.host = host;
	}

	@Override
	protected void onUpdate(String string) {

		String[] params = string.split(",");

		host = params[0];
		name = params[1];
	}
}
