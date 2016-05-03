package org.jdm;

import org.jdm.core.ConfigurationServer;
import org.jdm.core.DisplayController;
import org.jdm.core.DisplayManager;

public class Main {
	public static void main(String[] args) {

		// initialize and show the display manager
		DisplayManager dm = new DisplayManager();
		dm.showDM(false);

		// initialize the display controller
		DisplayController displayController = new DisplayController(dm);

		// initialize the configuration server
		ConfigurationServer configServer = new ConfigurationServer(9999, displayController);
		configServer.start();

	}
}
