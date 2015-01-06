package org.github.kodifollowshowssync;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.logging.LogFactory;
import org.github.kodifollowshowssync.followshows.FollowShowsConnector;
import org.github.kodifollowshowssync.model.Show;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.UrlEncodedContent;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonObjectParser;
import com.google.api.client.json.gson.GsonFactory;

public class Syncer {

	private FollowShowsConnector client;
	
	public Syncer(String mail, String password) {
		client=new FollowShowsConnector(mail, password);
	}

	public void sync() throws MalformedURLException, IOException {
	    try (Connection c = openConnection()){
	    	client.login();
	    	List<Show> shows=followAllKodiShows(c,client);
	    	for(Show show:shows) {
	    		show.syncWatchedEpisodes(c, client);
	    	}
	    	
	    } catch (SQLException e) {
			e.printStackTrace();
		}
	    
	    
	    
	}

	private List<Show> followAllKodiShows(Connection c, FollowShowsConnector client) throws SQLException, MalformedURLException, IOException {
		List<Show> shows=new ArrayList<>();
		try(ResultSet rs=c.createStatement().executeQuery("SELECT idShow, c00 as name FROM tvshow")) {
			while(rs.next()) {
				Show show=new Show(rs.getInt("idShow"),rs.getString("name"));
				show.identifyAndSetWatched(client);
				if(show.isInFollowShow()) {
					shows.add(show);
				}
			}
		}
		return shows;
	}

	private Connection openConnection() throws SQLException, IOException {
		try {
			Class.forName("org.sqlite.JDBC");
		} catch (ClassNotFoundException e) {
			throw new Error(e);
		}
		return DriverManager.getConnection("jdbc:sqlite:"+getDBFile().getAbsolutePath());
	}

	private File getDBFile() throws IOException {
		File databaseFolder=new File(System.getenv("APPDATA"),"kodi/userdata/Database/");
		return Files.walk(databaseFolder.toPath())
			.filter(f -> f.getFileName().toString().startsWith("MyVideos"))
			.sorted((a,b)->b.getFileName().compareTo(a.getFileName()))
			.findFirst().get().toFile();
	}

}
