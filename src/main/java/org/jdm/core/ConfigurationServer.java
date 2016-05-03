package org.jdm.core;

import org.jdm.network.ConnectionServer;
import org.jdm.network.SocketConnection;
import org.jdm.network.SocketListener;

public class ConfigurationServer implements SocketListener {

	private ConnectionServer connectionServer;

	private DisplayController displayController;

	// default config password
	private String configPassword = "jdm";

	public ConfigurationServer(int configPort, DisplayController displayController) {

		this.displayController = displayController;

		connectionServer = new ConnectionServer(configPort, this);
		connectionServer.setKeepAlive(true);
	}

	public void start() {

		connectionServer.start();
	}

	public void stop() {

		connectionServer.stop();
	}

	@Override
	public Object onDataReceived(SocketConnection connection, String[] request) {

		if (!connection.isAuthenticated()) {

			try {

				// auth first or fail fast
				String authPass = request[0];
				if (authPass.equals(configPassword)) {

					connection.setAuthenticated(true);
					return menu;
				} else {

					connection.disconnect("auth failure");
					return null;
				}
			} catch (Exception e) {

				connection.disconnect("auth failure");
				return null;
			}
		}

		// get the action
		String action;
		try {

			action = request[0];
		} catch (Exception e) {

			return "invalid command";
		}

		try {

			// parse the request
			switch (action) {

			// display manager

			case "h":
			case "hideDM":
				return displayController.hideDM();

			case "s":
			case "showDM":
				return displayController.showDM(false);

			case "fs":
			case "showDMfs":
				return displayController.showDM(true);

			// displays

			case "ld":
			case "listDisplays":
				return "not implemented";

			case "goto":
			case "setActiveDisplay":
				return displayController.showDisplay(request);

			case "c":
			case "createDisplay":
				return displayController.addDisplay(request);

			case "bg":
			case "setDisplayBackground":
				return "not implemented";

			case "dd":
			case "deleteDisplay":
				return displayController.deleteDisplay(request);

			case "clear":
				return displayController.clear();

			// panels

			case "lp":
			case "listPanels":
				return "not implemented";

			case "n":
			case "createPanel":
				return displayController.createPanel(request);

			case "u":
			case "updatePanel":
				return displayController.updatePanel(request);

			case "rm":
			case "delPanel":
				return displayController.deletePanel(request);

			// system

			case "i":
			case "setInterval":
				return displayController.setInterval(request);

			case "m":
			case "menu":
			case "help":
				return menu;

			case "w":
			case "write":
				return "not implemented";

			case "a":
			case "about":
				return "not implemented";

			case "x":
			case "exit":
				connection.disconnect("exit");
				return null;

			case "pass":
			case "setPass":
			case "setPassword":
				return setPass(request);

			default:

				return "invalid\n";
			}

		} catch (Exception e) {

			e.printStackTrace();
			return new String[] { "bad parameters", //
					e.getMessage() };
		} finally {

			displayController.draw();
		}
	}

	@Override
	public Object onConnect(SocketConnection connection) {

		return init;
	}

	@Override
	public void onDisconnect(SocketConnection connection, String reason) {

		// nothing to do for now
	}

	public Object setPass(String[] request) {

		// change the password
		try {

			this.configPassword = request[1];
			return "okay";
		} catch (Exception e) {

			return new String[] { "setPass", "[newPass]" };
		}
	}

	private static final String[] init = new String[] { //
			"", //
			"  ------  JDM Config Server  ------", //
			"", //
			" Enter Auth Password ", "" //
	};

	private static final String[] menu = new String[] { //
			"", //
			"   --- Main Menu ---", //
			"", //
			" Display Manager", //
			"", //
			"  h     : hide display manager", //
			"  s     : show display manager", //
			"  fs    : show fullscreen", //
			"", //
			"", //
			" Displays", //
			"", //
			"  ld    : list displays", //
			"  goto  : set active display", //
			"  c     : create display", //
			"  bg    : set color of display", //
			"  dd    : delete display", //
			"  clear : clear all displays", //
			"", //
			"", //
			" Panels", //
			"", //
			"  lp    : list panels", //
			"  n     : new panel", //
			"  u     : update panel ", //
			"  rm    : delete panel", //
			"", //
			"", //
			" System", //
			"", //
			"  i     : set rotation interval", //
			"  m     : print menu", //
			"  w     : write to file", //
			"  pass  : set config password", //
			"  a     : about", //
			"  x     : exit", //
			"" //
	};
}
