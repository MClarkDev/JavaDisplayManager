package org.jdm;

import org.jdm.core.ConfigServer;
import org.jdm.core.DisplayManager;

public class Main {
	public static void main(String[] args) {

		// initialize and show the display manager
		DisplayManager dm = new DisplayManager();
		dm.showDM();

		// initialize the config server
		ConfigServer configServer = new ConfigServer(dm, 9999);
		configServer.start();
	}
}
