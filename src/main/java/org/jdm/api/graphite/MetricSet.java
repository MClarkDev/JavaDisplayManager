package org.jdm.api.graphite;

import org.json.JSONArray;
import org.json.JSONObject;

public class MetricSet {

	private final double[] time;
	private final double[][] data;

	public MetricSet(double[] time, double[][] data) {

		this.time = time;
		this.data = data;
	}

	public static MetricSet fromJSON(JSONArray json) {

		// the data
		double[] time = new double[json.length()];
		double[][] data = new double[json.length()][];

		// loop each data set
		for (int x = 0; x < json.length(); x++) {

			JSONObject targetData = json.getJSONObject(x);

			JSONArray points = targetData.getJSONArray("datapoints");

			time = new double[points.length()];
			data[x] = new double[points.length()];
			for (int y = 0; y < points.length(); y++) {

				JSONArray blob = points.getJSONArray(y);

				time[y] = blob.getDouble(1);

				try {

					data[x][y] = blob.getDouble(0);
				} catch (Exception e) {

					data[x][y] = 0;
				}
			}
		}

		return new MetricSet(time, data);
	}

	public static MetricSet Invalid(String host, String name) {
		// TODO Auto-generated method stub
		return null;
	}

	public double[] getTime() {

		return time;
	}

	public double[][] getData() {

		return data;
	}

}
