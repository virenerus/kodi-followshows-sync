package org.github.kodifollowshowssync.followshows;

import java.util.List;

import com.google.api.client.util.Key;

public class FSEpisode {
	@Key
	private int numberInSeason;
	@Key
    private int seasonNumber;
	@Key
    private String airDate;
	@Key
    private String title;
	@Key
    private String summary;
	@Key
    private String network;
	@Key
    private String showName;
	@Key
    private String showShortName;
	@Key
    private boolean watched;
	@Key
    private String code;
	@Key
	private List<FSVideo> videos;
	
	public int getNumberInSeason() {
		return numberInSeason;
	}
	public void setNumberInSeason(int numberInSeason) {
		this.numberInSeason = numberInSeason;
	}
	public int getSeasonNumber() {
		return seasonNumber;
	}
	public void setSeasonNumber(int seasonNumber) {
		this.seasonNumber = seasonNumber;
	}
	public String getAirDate() {
		return airDate;
	}
	public void setAirDate(String airDate) {
		this.airDate = airDate;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getSummary() {
		return summary;
	}
	public void setSummary(String summary) {
		this.summary = summary;
	}
	public String getNetwork() {
		return network;
	}
	public void setNetwork(String network) {
		this.network = network;
	}
	public String getShowName() {
		return showName;
	}
	public void setShowName(String showName) {
		this.showName = showName;
	}
	public String getShowShortName() {
		return showShortName;
	}
	public void setShowShortName(String showShortName) {
		this.showShortName = showShortName;
	}
	public boolean isWatched() {
		return watched;
	}
	public void setWatched(boolean watched) {
		this.watched = watched;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public List<FSVideo> getVideos() {
		return videos;
	}
	public void setVideos(List<FSVideo> videos) {
		this.videos = videos;
	}
}
