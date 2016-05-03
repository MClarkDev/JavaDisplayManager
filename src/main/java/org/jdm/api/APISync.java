package org.jdm.api;

import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

public abstract class APISync {

	// sync every 15 minutes
	private int syncInterval = 900000;

	// the object cache
	private HashMap<String, Object> apiCache;

	protected APISync() {

		// initialize the cache
		apiCache = new HashMap<String, Object>();

		// start the sync timer
		(new Timer()).schedule(new TimerTask() {

			@Override
			public void run() {

				update();
			}

		}, 15000, syncInterval);
	}

	public Object get(String key) {

		if (!apiCache.containsKey(key)) {

			synchronized (apiCache) {

				apiCache.put(key, null);
			}
		}

		return apiCache.get(key);
	}

	public Set<String> getKeys() {

		return apiCache.keySet();
	}

	public void set(String key, Object o) {

		apiCache.put(key, o);
	}

	public void update() {

		onUpdate();
	}

	public abstract void onUpdate();

	protected JSONObject fetchJSONObject(String url) throws Exception {

		return new JSONObject(fetch(url));
	}

	protected JSONArray fetchJSONArray(String url) throws Exception {

		return new JSONArray(fetch(url));
	}

	protected String fetch(String url) throws Exception {

		System.out.println("Fetching [ " + url + " ]");

		SSLContext sslContext = SSLContext.getInstance("SSL");

		sslContext.init(null, new TrustManager[] { new X509TrustManager() {

			@Override
			public X509Certificate[] getAcceptedIssuers() {

				return null;
			}

			@Override
			public void checkClientTrusted(X509Certificate[] certs, String authType) {

			}

			@Override
			public void checkServerTrusted(X509Certificate[] certs, String authType) {

			}
		} }, new SecureRandom());

		SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslContext,
				SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

		CloseableHttpClient httpClient = HttpClients.custom().setSSLSocketFactory(sslsf).build();

		HttpGet getRequest = new HttpGet(url);

		// execute the request
		HttpResponse response;
		try {

			response = httpClient.execute(getRequest);
		} catch (Exception e) {

			throw new RuntimeException("Failed to connect to host [ " + url + " ]");
		}
		// get the body
		HttpEntity entity = response.getEntity();
		String json = EntityUtils.toString(entity);

		// close the connections
		getRequest.releaseConnection();
		httpClient.close();

		// return the json
		return json;
	}
}
