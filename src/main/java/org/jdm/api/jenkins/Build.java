package org.jdm.api.jenkins;

import org.json.JSONArray;
import org.json.JSONObject;

public class Build {

	private final String host;
	private final String name;

	private long initiated = 0;

	private long elapsed = 0;

	private String committer = "";

	private BuildStatus status = BuildStatus.UNKNOWN;

	private boolean building = false;

	private String commit = "";

	private String branch = "";

	private long number = 0;

	private String updateURL = "";

	private long estimatedDuration = 0;

	private String description = "";

	private Build last;

	public Build(String host, String name) {

		this.host = host;
		this.name = name;
	}

	public String getHost() {
		return host;
	}

	public String getName() {
		return name;
	}

	public long getTimeStart() {
		return initiated;
	}

	public void setTimeStart(long initiated) {
		this.initiated = initiated;
	}

	public long getTimeElapsed() {
		return elapsed;
	}

	public void setTimeElapsed(long elapsed) {
		this.elapsed = elapsed;
	}

	public String getCommitter() {
		return committer;
	}

	public void setCommitter(String committer) {
		this.committer = committer;
	}

	public BuildStatus getStatus() {
		return status;
	}

	public void setStatus(BuildStatus status) {
		this.status = status;
	}

	public String getCommit() {
		return commit;
	}

	public void setCommit(String commit) {
		this.commit = commit;
	}

	public String getShortCommit() {
		if (commit.length() > 8) {
			return commit.substring(0, 8);
		} else {
			return commit;
		}
	}

	public String getBranch() {
		return branch;
	}

	public void setBranch(String branch) {
		String[] parts = branch.split("/");
		this.branch = parts[parts.length - 1];
	}

	public long getNumber() {
		return number;
	}

	public void setNumber(long l) {
		this.number = l;
	}

	public String getUpdateURL() {
		return updateURL;
	}

	public void setUpdateURL(String updateURL) {
		this.updateURL = updateURL;
	}

	public long getEstimatedDuration() {
		return estimatedDuration;
	}

	public void setEstimatedDuration(long estimatedDuration) {
		this.estimatedDuration = estimatedDuration;
	}

	public static Build fromJSON(String host, String name, JSONObject job) {

		// create a new build
		Build build = new Build(host, name);
		build.setUpdateURL(host + "/job/" + name + "/lastBuild/api/json");

		// if building
		if (job.getBoolean("building")) {

			// set status
			build.setBuilding(true);
			build.setStatus(BuildStatus.BUILDING);
		} else {

			// parse build details
			String result = job.getString("result");
			build.setStatus(BuildStatus.valueOf(result));
		}

		// some extra details
		JSONArray causes = null;
		JSONArray branches = null;
		JSONArray parameters = null;
		JSONObject lastBuiltRevision = null;

		try {

			// number
			build.setNumber(job.getLong("number"));
		} catch (Exception e) {
		}

		try {

			// progress
			build.setTimeStart(job.getLong("timestamp"));

			long duration = job.getLong("duration");
			if (duration == 0 && build.isBuilding()) {

				duration = System.currentTimeMillis() - build.getTimeStart();
			}
			build.setTimeElapsed(duration);
			build.setEstimatedDuration(job.getLong("estimatedDuration"));
		} catch (Exception e) {
		}

		try {

			// description
			build.setBranch(job.getString("description"));
		} catch (Exception e) {
		}

		// get actions of the build
		JSONArray actions = job.getJSONArray("actions");

		// loop through each action
		JSONObject object;
		for (int x = 0; x < actions.length(); x++) {

			// get the action object
			object = actions.getJSONObject(x);

			// figure out whats in it
			if (causes == null) {
				try {

					causes = object.getJSONArray("causes");

					build.setCommitter(causes.getJSONObject(0).getString("userId"));
				} catch (Exception e) {
				}
			}

			if (branches == null) {
				try {

					lastBuiltRevision = object.getJSONObject("lastBuiltRevision");
					branches = lastBuiltRevision.getJSONArray("branch");

					build.setBranch(branches.getJSONObject(0).getString("name"));
					build.setCommit(branches.getJSONObject(0).getString("SHA1"));

				} catch (Exception e) {
				}
			}

			if (parameters == null) {
				try {
					parameters = object.getJSONArray("parameters");

					// parameters
					parameters = actions.getJSONObject(2).getJSONArray("parameters");

					// parse the parameters
					for (int y = 0; y < parameters.length(); y++) {

						String key = parameters.getJSONObject(y).getString("name");
						String val = parameters.getJSONObject(y).getString("value");

						// lookup branch if not set
						if (build.getBranch().equals("") && key.contains("branch")) {

							build.setBranch(val);
						}
					}
				} catch (Exception e) {
				}
			}
		}

		return build;
	}

	public String getDescription() {

		return description;
	}

	public void setDescription(String description) {

		this.description = description;
	}

	public boolean isBuilding() {

		return building;
	}

	public void setBuilding(boolean building) {

		this.building = building;
	}

	public static Build Unknown(String host, String name) {

		return new Build(host, name);
	}

	public static Build Invalid(String host, String name) {

		Build b = new Build(host, name);
		b.setStatus(BuildStatus.INVALID);

		return b;
	}

	public Build getLast() {

		return last;
	}

	public void setLast(Build last) {

		this.last = last;
	}
}
