package org.jdm.api.yfinance;

import org.jdm.api.APISync;
import org.json.JSONArray;
import org.json.JSONObject;

public final class YFinanceAPI extends APISync {

	private static YFinanceAPI api;

	private YFinanceAPI() {

		super();
	}

	@Override
	public void onUpdate() {

		// loop each key
		for (String key : getKeys()) {

			// download new data
			StockData now = new StockData(fetchData(key));

			// get the previous info
			StockData last = (StockData) get(key);
			if (last != null) {

				// add it to the new object
				now.setLast(last);
			}

			// add the new object to the cache
			set(key, now);
		}
	}

	public JSONObject fetchData(String symbol) {

		try {

			String url = "http://finance.yahoo.com/webservice/v1/symbols/" + symbol + "/quote?format=json";

			JSONObject root = fetchJSONObject(url);

			JSONObject jsonList = root.getJSONObject("list");

			JSONArray resources = jsonList.getJSONArray("resources");

			JSONObject resource = resources.getJSONObject(0).getJSONObject("resource");

			return resource.getJSONObject("fields");
		} catch (Exception e) {

			e.printStackTrace();
			return null;
		}
	}

	public static YFinanceAPI getInstance() {

		// if no existing instance
		if (api == null) {

			// create one
			synchronized (YFinanceAPI.class) {

				if (api == null) {

					api = new YFinanceAPI();
				}
			}
		}

		// return it
		return api;
	}
}
