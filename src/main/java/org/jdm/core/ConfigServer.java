package org.jdm.core;

import org.jdm.network.ConnectionServer;
import org.jdm.network.SocketConnection;
import org.jdm.network.SocketListener;

public class ConfigServer implements SocketListener {

	private DisplayManager displayManager;
	private ConnectionServer connectionServer;

	// default config password
	private String configPassword = "jdm";

	public ConfigServer(DisplayManager displayManager, int configPort) {

		this.displayManager = displayManager;

		connectionServer = new ConnectionServer(configPort, this);
		connectionServer.setKeepAlive(true);
	}

	public void start() {

		connectionServer.start();
	}

	public void stop() {

		connectionServer.stop();
	}

	public String getConfigPassword() {

		return configPassword;
	}

	public void setConfigPassword(String password) {

		configPassword = password;
	}

	@Override
	public Object onDataReceived(SocketConnection connection, String[] request) {

		try {

			String authPass = request[0];
			if (!authPass.equals(configPassword)) {

				return "Invalid config password";
			}
		} catch (Exception e) {

			return "Missing config password";
		}

		String action;

		try {

			action = request[1];
		} catch (Exception e) {

			return "Missing Action";
		}

		try {

			int displayID;

			String[] pos;

			int x, y;

			Panel panel;

			switch (action) {

			case "help":

				// show available actions
				return new String[] { "hideDM", "showDM", "setInterval", //
						"addDisplay", "delDisplay", "showDisplay", //
						"addPanel", "delPanel", "updatePanel" };

			case "hideDM":

				// hide the display manager
				displayManager.hideDM();
				break;

			case "showDM":

				// show the display manager
				displayManager.showDM();
				break;

			case "setInterval":

				// set the interval to rotate displays
				displayManager.setRotationInterval(Integer.parseInt(request[2]));
				break;

			case "addDisplay":

				// add a new display
				String[] dimms = request[2].split(",");
				int width = Integer.parseInt(dimms[0]);
				int height = Integer.parseInt(dimms[1]);

				displayManager.addDisplay(new Display(width, height));
				break;

			case "delDisplay":

				// delete a display
				displayManager.delDisplay(Integer.parseInt(request[2]));
				break;

			case "showDisplay":

				// show a display
				displayManager.setActiveDisplay(Integer.parseInt(request[2]));
				break;

			case "addPanel":

				// add a panel to a display
				displayID = Integer.parseInt(request[2]);

				pos = request[3].split(",");
				x = Integer.parseInt(pos[0]);
				y = Integer.parseInt(pos[1]);

				panel = PanelBuilder.build(request[4]);
				displayManager.getDisplay(displayID).setPanel(x, y, panel);
				displayManager.renderLastDisplay();
				break;

			case "delPanel":

				// delete a panel from a display
				displayManager.remove(Integer.parseInt(request[2]));
				break;

			case "updatePanel":

				// update a panel with data
				displayID = Integer.parseInt(request[2]);

				pos = request[3].split(",");
				x = Integer.parseInt(pos[0]);
				y = Integer.parseInt(pos[1]);

				displayManager.getDisplay(displayID).getPanel(x, y).update(request[4]);
				break;

			default:

				return "Invalid action";
			}

			displayManager.render();
			return "okay";

		} catch (Exception e) {

			e.printStackTrace();
			return new String[] { "Bad Parameters", //
					e.getMessage() };
		}
	}

	@Override
	public void onConnect(SocketConnection connection) {

		// nothing to do for now
	}

	@Override
	public void onDisconnect(SocketConnection connection, String reason) {

		// nothing to do for now
	}
}
