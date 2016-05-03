package org.jdm.api.yfinance;

import org.json.JSONObject;

public class StockData {

	private double price;

	private long lastUpdate;

	private StockData last;

	public StockData(JSONObject fetch) {
		this.price = fetch.getDouble("price");
		this.lastUpdate = fetch.getLong("ts");
	}

	public double getPrice() {
		return price;
	}

	public long getLastUpdate() {
		return lastUpdate;
	}

	public StockData getLast() {

		return last;
	}

	public void setLast(StockData last) {

		this.last = last;
	}

}
