package org.jdm.api.jenkins;

import java.net.URLEncoder;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

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
import org.jdm.display.panel.object.Build;
import org.json.JSONException;
import org.json.JSONObject;

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
public class JenkinsAPI {

	// instance map
	private static HashMap<String, JenkinsAPI> apis = new HashMap<String, JenkinsAPI>();

	// instance variables
	private final String host;

	private boolean valid = true;

	private ConcurrentHashMap<String, Build> builds;

	private APIThread apiThread;

	public JenkinsAPI(String host) {

		this.host = host;

		builds = new ConcurrentHashMap<String, Build>();

		apiThread = new APIThread(host);
		apiThread.setSyncInterval(30000);
		apiThread.start();
	}

	public ConcurrentHashMap<String, Build> getBuilds() {

		return builds;
	}

	public Build getBuild(String name) {

		// get it from cache
		Build b = builds.get(name);
		if (b != null) {

			// return if found
			return b;
		} else {

			try {

				// try and update if not
				return updateBuild(name);
			} catch (Exception e) {

				e.printStackTrace();

				// return unknown object if failed
				return Build.Unknown(host, name);
			}
		}
	}

	public String getHost() {

		return host;
	}

	public int getSyncInterval() {

		return apiThread.getSyncInterval();
	}

	public void setSyncInterval(int syncInterval) {

		apiThread.setSyncInterval(syncInterval);
	}

	public static synchronized JenkinsAPI getInstance(String host) {

		// get an instance from the map
		JenkinsAPI api = apis.get(host);

		// if no existing instance
		if (api == null) {

			// create one
			api = new JenkinsAPI(host);
			apis.put(host, api);
		}

		// return it
		return api;
	}

	private String fetchJSON(String url) throws Exception {

		System.out.println("Fetching [ " + url + " ]");

		SSLContext sslContext = SSLContext.getInstance("SSL");

		sslContext.init(null, new TrustManager[] { new X509TrustManager() {

			public X509Certificate[] getAcceptedIssuers() {

				return null;
			}

			public void checkClientTrusted(X509Certificate[] certs, String authType) {

			}

			public void checkServerTrusted(X509Certificate[] certs, String authType) {

			}
		} }, new SecureRandom());

		SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslContext,
				SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

		CloseableHttpClient httpClient = HttpClients.custom().setSSLSocketFactory(sslsf).build();

		HttpGet getRequest = new HttpGet(url);

		// execute the request
		HttpResponse response = httpClient.execute(getRequest);

		// get the body
		HttpEntity entity = response.getEntity();
		String json = EntityUtils.toString(entity);

		// close the connections
		getRequest.releaseConnection();
		httpClient.close();

		// return the json
		return json;
	}

	private Build updateBuild(String name) throws Exception {

		if (valid) {
			// job details url
			String encoded = URLEncoder.encode(name, "UTF-8");
			String buildURL = host + "/job/" + encoded + "/lastBuild/api/json";

			// fetch from jenkins
			String response = fetchJSON(buildURL);

			// update from json
			try {
				JSONObject json = new JSONObject(response);
				Build update = Build.fromJSON(host, name, json);

				if (update != null) {

					// update the cache
					builds.put(name, update);
				} else {

					update = Build.Unknown(host, name);
				}

				return update;
			} catch (JSONException e) {

				valid = false;
				return Build.Invalid(host, name);
			}
		} else {

			return Build.Invalid(host, name);
		}
	}

	private class APIThread extends Thread {

		private final String host;

		private int syncInterval = 15000;

		public APIThread(String host) {
			this.host = host;
		}

		public void setSyncInterval(int syncInterval) {
			this.syncInterval = syncInterval;
		}

		public int getSyncInterval() {
			return syncInterval;
		}

		// periodically update known builds
		public void run() {

			while (!isInterrupted()) {

				System.out.println("Syncing [ " + host + " ]");

				// iterate through known builds
				Iterator<Entry<String, Build>> it = builds.entrySet().iterator();
				while (it.hasNext()) {

					// get the entry
					Entry<String, Build> entry = it.next();
					String key = entry.getKey();

					try {
						// update build
						Build build = updateBuild(key);

						// update cache with a valid build
						if (build != null) {
							builds.put(key, build);
						}
					} catch (Exception e) {
						System.out.println("Failed to update [ " + host + ":" + key + " ]");
						e.printStackTrace();
					}
				}

				try {

					// sleep until next poll
					Thread.sleep(syncInterval);
				} catch (Exception e) {
				}
			}
		}
	}
}
