package org.github.kodifollowshowssync.followshows;

import java.util.HashMap;
import java.util.Map;

import com.google.api.client.util.Key;


public class FSShow {
	@Key
	public String name;
	@Key
	private String shortName;
	@Key
	private boolean followed;
	private transient Map<Integer, Map<Integer, FSEpisode>> seasons;

	public FSShow() {
		seasons=new HashMap<>();
	}
	
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
	
	public boolean isSeasonLoaded(int season) {
		return seasons!=null && seasons.containsKey(season);
	}

	public void setSeason(int season, HashMap<Integer, FSEpisode> episodes) {
		if(seasons==null) seasons=new HashMap<>();
		seasons.put(season, episodes);
	}

	public Map<Integer, FSEpisode> getSeason(int season) {
		if(seasons==null) seasons=new HashMap<>();
		return seasons.get(season);
	}
}
