package org.github.kodifollowshowssync;

import java.io.File;
import java.io.FileWriter;
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
import java.util.Map;
import java.util.stream.Collectors;

import org.github.kodifollowshowssync.followshows.FollowShowsConnector;
import org.github.kodifollowshowssync.model.Identity;
import org.github.kodifollowshowssync.model.Show;

import com.thoughtworks.xstream.XStream;

public class Syncer {

	private FollowShowsConnector client;
	private Map<String, Identity> knownIdentities=new HashMap<>();
	private final File identityFile=new File("identities.xml");
	
	public Syncer(String mail, String password) {
		if(identityFile.exists()) {
			for(Identity ident:(List<Identity>) getXStream().fromXML(identityFile))
				knownIdentities.put(ident.getKodiName(), ident);
		}
		
		client=new FollowShowsConnector(mail, password);
	}

	public void sync() throws MalformedURLException, IOException {
	    try (Connection c = openConnection()){
	    	client.login();
	    	List<Show> shows=followAllKodiShows(c,client);
	    	for(Show show:shows) {
	    		show.syncWatchedEpisodes(c, client);
	    	}
	    	//save now known identities
	    	List<Identity> list = shows.stream().map(s -> new Identity(s.getName(), s.getFollowShow())).collect(Collectors.toList());
	    	try(FileWriter out=new FileWriter(identityFile)) {
	    		getXStream().toXML(list, out);
	    	}
	    } catch (SQLException e) {
			e.printStackTrace();
		}
	    
	    
	    
	}

	private XStream getXStream() {
		XStream s=new XStream();
		s.alias("Identity", Identity.class);
		return s;
	}

	private List<Show> followAllKodiShows(Connection c, FollowShowsConnector client) throws SQLException, MalformedURLException, IOException {
		List<Show> shows=new ArrayList<>();
		try(ResultSet rs=c.createStatement().executeQuery("SELECT idShow, c00 as name FROM tvshow")) {
			while(rs.next()) {
				Show show=new Show(rs.getInt("idShow"),rs.getString("name"));
				show.identifyAndSetWatched(client, knownIdentities);
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
