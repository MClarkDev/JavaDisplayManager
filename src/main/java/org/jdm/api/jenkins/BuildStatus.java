package org.jdm.api.jenkins;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map.Entry;

public enum BuildStatus {
	UNKNOWN(0), ABORTED(1), BUILDING(2), FAILURE(3), UNSTABLE(4), SUCCESS(5), INVALID(9);

	private final int id;

	BuildStatus(int id) {

		this.id = id;
	}

	public int getId() {

		return this.id;
	}

	public static BuildStatus getById(int id) {

		for (BuildStatus e : values()) {
			if (e.getId() == id) {
				return e;
			}
		}
		return null;
	}

	public static HashMap<BuildStatus, Color> getColorMap(int id) {

		HashMap<BuildStatus, Color> colorMap = new HashMap<BuildStatus, Color>();

		switch (id) {
		case 0:
			colorMap.put(BuildStatus.UNKNOWN, Color.BLACK);
			colorMap.put(BuildStatus.ABORTED, Color.GRAY);
			colorMap.put(BuildStatus.BUILDING, Color.BLUE);
			colorMap.put(BuildStatus.FAILURE, Color.RED);
			colorMap.put(BuildStatus.UNSTABLE, Color.YELLOW);
			colorMap.put(BuildStatus.SUCCESS, Color.BLUE);
			colorMap.put(BuildStatus.INVALID, Color.ORANGE);
			break;

		case 9:
			colorMap.put(BuildStatus.UNKNOWN, Color.decode("#888888"));
			colorMap.put(BuildStatus.ABORTED, Color.decode("#AAAAAA"));
			colorMap.put(BuildStatus.BUILDING, Color.decode("#5555FF"));
			colorMap.put(BuildStatus.FAILURE, Color.decode("#FF5555"));
			colorMap.put(BuildStatus.UNSTABLE, Color.decode("#FFFF55"));
			colorMap.put(BuildStatus.SUCCESS, Color.decode("#33CC33"));
			colorMap.put(BuildStatus.INVALID, Color.decode("#555555"));
			break;
		}

		return colorMap;
	}

	public static BuildStatus findByColor(HashMap<BuildStatus, Color> colorMap, Color color) {

		for (Entry<BuildStatus, Color> entry : colorMap.entrySet()) {

			if (entry.getValue().equals(color)) {

				return entry.getKey();
			}
		}
		return BuildStatus.UNKNOWN;
	}
}
