package org.jdm.core;

public class DisplayController {

	private DisplayManager displayManager;

	public DisplayController(DisplayManager displayManager) {

		this.displayManager = displayManager;
	}

	public Object hideDM() {

		// hide the display manager
		displayManager.hideDM();
		return "okay";
	}

	public Object showDM(boolean fs) {

		// show the display manager
		displayManager.showDM(fs);
		return "okay";
	}

	public Object setInterval(String[] request) {

		// set the interval to rotate displays
		try {

			displayManager.setRotationInterval(Integer.parseInt(request[1]));
			return "okay";
		} catch (Exception e) {

			return new String[] { "[rotationIntervalInMS]" };
		}
	}

	public Object addDisplay(String[] request) {

		// add a new display
		try {
			String[] dimms = request[1].split(",");

			int width = Integer.parseInt(dimms[0]);
			int height = Integer.parseInt(dimms[1]);

			displayManager.addDisplay(new Display(width, height));

			return "okay";
		} catch (Exception e) {

			return new String[] { "[gridWidth,gridHeight]" };
		}
	}

	public Object deleteDisplay(String[] request) {

		// delete a display
		try {

			displayManager.delDisplay(Integer.parseInt(request[1]));
			return "okay";
		} catch (Exception e) {

			return new String[] { "[displayID]" };
		}
	}

	public Object clear() {

		// clear all displays
		displayManager.clear();
		return "okay";
	}

	public Object showDisplay(String[] request) {

		try {
			// show a display
			displayManager.setActiveDisplay(Integer.parseInt(request[1]));
			return "okay";
		} catch (Exception e) {

			return new String[] { "[displayID]" };
		}
	}

	public Object createPanel(String[] request) {

		try {
			// add a panel to a display
			int displayID = Integer.parseInt(request[1]);

			String[] pos = request[2].split(",");
			int x = Integer.parseInt(pos[0]);
			int y = Integer.parseInt(pos[1]);

			Panel panel = PanelBuilder.build(request[3]);
			displayManager.getDisplay(displayID).setPanel(x, y, panel);
			displayManager.renderLastDisplay();

			return "okay";
		} catch (Exception e) {

			return new String[] { "[displayID]", "[posX,posY]", "[type]|[panel parameters]" };
		}
	}

	public Object deletePanel(String[] request) {

		// delete a panel from a display
		try {

			displayManager.remove(Integer.parseInt(request[1]));
			return "okay";
		} catch (Exception e) {

			return new String[] { "[panelID]" };
		}
	}

	public Object updatePanel(String[] request) {

		// update a panel with data
		try {

			int displayID = Integer.parseInt(request[1]);

			String[] pos = request[2].split(",");
			int x = Integer.parseInt(pos[0]);
			int y = Integer.parseInt(pos[1]);

			displayManager.getDisplay(displayID).getPanel(x, y).update(request[3]);

			return "okay";
		} catch (Exception e) {

			return new String[] { "[displayID]", "[posX,posY]", "[panel parameters]" };
		}
	}

	public void draw() {

		displayManager.render();
	}
}
