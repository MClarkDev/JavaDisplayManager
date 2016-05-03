package org.jdm.core;

import org.jdm.display.panel.BuildPanel;
import org.jdm.display.panel.ClockA;
import org.jdm.display.panel.ClockD;
import org.jdm.display.panel.DatePanel;
import org.jdm.display.panel.GrafPanel;
import org.jdm.display.panel.InfoPanel;
import org.jdm.display.panel.StockPanel;
import org.jdm.display.panel.WebPanel;

public class PanelBuilder {

	public static Panel build(String string) {

		Panel panel;

		String[] parameters = string.split("\\|");

		switch (parameters[0]) {

		case "info":
			panel = new InfoPanel();
			panel.update(parameters[1]);
			break;

		case "jenkins":
			panel = new BuildPanel();
			panel.update(parameters[1]);
			break;

		case "clocka":
			panel = new ClockA();
			panel.update(parameters[1]);
			break;

		case "clockd":
			panel = new ClockD();
			panel.update(parameters[1]);
			break;

		case "graf":
			panel = new GrafPanel();
			panel.update(parameters[1]);
			break;

		case "stock":
			panel = new StockPanel();
			panel.update(parameters[1]);
			break;

		case "date":
			panel = new DatePanel();
			panel.update(parameters[1]);
			break;

		case "web":
			panel = new WebPanel();
			panel.onUpdate(parameters[1]);
			break;

		default:
			panel = null;
			break;
		}

		return panel;
	}
}
