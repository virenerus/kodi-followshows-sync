package org.github.kodifollowshowssync.followshows;

import com.google.api.client.util.Key;


public class FSShow {
	@Key
	public String name;
	@Key
	private String shortName;
	@Key
	private boolean followed;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getShortName() {
		return shortName;
	}
	public void setShortName(String shortName) {
		this.shortName = shortName;
	}
	public boolean isFollowed() {
		return followed;
	}
	public void setFollowed(boolean followed) {
		this.followed = followed;
	}
	
	@Override
	public String toString() {
		return name + " [shortName=" + shortName
				+ ", followed=" + followed + "]";
	}
}
