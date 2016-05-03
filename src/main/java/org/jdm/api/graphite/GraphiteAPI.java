package org.jdm.api.graphite;

import java.net.URLEncoder;

import org.jdm.api.APISync;
import org.json.JSONArray;
import org.json.JSONException;

/**
 * JenkinsAPI
 * 
 * On creation, a background thread will be started, this thread will, on a user
 * defined sync interval, poll the json endpoint for all stale builds in the
 * cache. A build that is not in the cache will be fetched immediately, this
 * will block on network access. From this point forward, the build will be in
 * the cache, and refreshed on each sync interval - these will not block.
 * 
 * @author Matthew Clark
 */
public final class GraphiteAPI extends APISync {

	private static GraphiteAPI api;

	private GraphiteAPI() {
		super();
	}

	@Override
	public void onUpdate() {

		// iterate through known builds
		for (String key : getKeys()) {

			try {
				// update build
				MetricSet metricSet = updateMetricSet(key);

				// update cache with a valid build
				if (metricSet != null) {

					set(key, metricSet);
				}
			} catch (Exception e) {

				System.out.println("Failed to update [ " + key + " ]");
				e.printStackTrace();
			}
		}
	}

	private MetricSet updateMetricSet(String key) throws Exception {

		String[] details = key.split("\\|");

		String host = details[0];
		String name = details[1];

		// job details url
		String encoded = URLEncoder.encode(name, "UTF-8");
		String metricURL = host + "/render?target=" + encoded + "&format=json";

		// update from json
		try {

			JSONArray json = fetchJSONArray(metricURL);
			MetricSet metricSet = MetricSet.fromJSON(json);

			if (metricSet != null) {

				// update the cache
				set(key, metricSet);
				return metricSet;
			}

		} catch (JSONException e) {
e.printStackTrace();
		}

		// default return
		return MetricSet.Invalid(host, name);
	}

	public static GraphiteAPI getInstance() {

		// if no existing instance
		if (api == null) {

			// create one
			synchronized (GraphiteAPI.class) {

				if (api == null) {

					api = new GraphiteAPI();
				}
			}
		}

		// return it
		return api;
	}
}
