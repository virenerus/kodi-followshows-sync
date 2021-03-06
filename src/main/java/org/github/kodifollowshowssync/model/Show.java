package org.github.kodifollowshowssync.model;

import java.io.IOException;
import java.net.MalformedURLException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

import org.github.kodifollowshowssync.followshows.FSEpisode;
import org.github.kodifollowshowssync.followshows.FSShow;
import org.github.kodifollowshowssync.followshows.FollowShowsConnector;

import com.google.api.client.http.HttpResponseException;

public class Show {
	private final int kodiId;
	private final String name;
	private FSShow followShow;
	
	
	public Show(int kodiId, String name) {
		this.kodiId = kodiId;
		this.name = name;
	}
	
	public int getKodiId() {
		return kodiId;
	}
	public String getName() {
		return name;
	}
	
	public void identifyAndSetWatched(FollowShowsConnector client, Map<String, Identity> knownIdentities) throws MalformedURLException, IOException {
		try {
			Identity identity=knownIdentities.get(name);
			if(identity!=null)
				followShow=identity.getFSShow();
			else {
				List<FSShow> possibleMatches=client.searchShows(name);
				if(!possibleMatches.isEmpty()) {			
					this.tryIdentity(possibleMatches.get(0));
				}
			}
			
			if(this.isInFollowShow()) {
				if(!followShow.isFollowed()) {
					client.followShow(followShow);
					System.out.println("FOLLOWED\t"+name);
				}
			}
		} catch(HttpResponseException e) {
			throw e;
		}
	}

	private void tryIdentity(FSShow showResult) {
		if(showResult.getName().replaceAll("[^\\p{Alpha}]", "").replaceAll("US$", "").equals(name.replaceAll("[^\\p{Alpha}]", "")))
			this.followShow=showResult;
		else
			System.out.println("Unconfirmed Identity:\t"+showResult.getName()+"\tas\t"+name);
	}

	public boolean isInFollowShow() {
		return followShow!=null;
	}

	public void syncWatchedEpisodes(Connection c, FollowShowsConnector client) throws SQLException, MalformedURLException, IOException {
		try(ResultSet episode=c.createStatement().executeQuery("SELECT c12 as season, c13 as episode, episode.idFile as fileId, files.playCount as playCount FROM episode LEFT JOIN files ON files.idFile=episode.idFile WHERE idShow="+kodiId)) {			
			while(episode.next()) {
				int season=episode.getInt("season");
				if(season==0)
					continue;
				int episodeNumber=episode.getInt("episode");
				int fileId=episode.getInt("fileId");
				boolean kodiWatched=episode.getInt("playCount")>0;
				
				if(!followShow.isSeasonLoaded(season))
					followShow.setSeason(season, client.getSeason(followShow, season));
				
				FSEpisode fsEp=followShow.getSeason(season).get(episodeNumber);
				
				boolean followShowWatched=fsEp.isWatched();
				
				if(kodiWatched && !followShowWatched) {
					client.markWatched(followShow,season,episodeNumber);
					System.out.println("FS WATCHED\t"+name+" s"+String.format("%02d", season)+"e"+String.format("%02d", episodeNumber));
				}
				else if(followShowWatched && !kodiWatched) {
					try(PreparedStatement stmt=c.prepareStatement("UPDATE files SET playCount=?, lastPlayed=? WHERE idFile=?")) {
						stmt.setInt(1, 1);
						stmt.setString(2, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date()));
						stmt.setInt(3, fileId);
						stmt.executeUpdate();
						System.out.println("KODI WATCHED\t"+name+" s"+String.format("%02d", season)+"e"+String.format("%02d", episodeNumber));
					}
				}
			}
		}
	}

	public FSShow getFollowShow() {
		return followShow;
	}
}
