package org.jdm.core;

import org.jdm.display.panel.BuildPanel;
import org.jdm.display.panel.InfoPanel;

public class PanelBuilder {

	public static Panel build(String string) {

		Panel panel;

		String[] parameters = string.split("\\|");

		switch (parameters[0]) {

		case "info":
			panel = new InfoPanel();
			panel.onUpdate(parameters[1]);
			break;

		case "jenkins":
			panel = new BuildPanel();
			panel.onUpdate(parameters[1]);
			break;

		default:
			panel = null;
			break;
		}

		return panel;
	}
}
