package org.github.kodifollowshowssync.followshows;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.codec.digest.DigestUtils;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.UrlEncodedContent;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonObjectParser;
import com.google.api.client.json.gson.GsonFactory;

public class FollowShowsConnector {
	private HashMap<String, String> loginData;
	private HttpRequestFactory requestFactory;
	
	public FollowShowsConnector(String mail, String password) {
		this.loginData=new HashMap<>();
		loginData.put("login", mail);
		loginData.put("password", DigestUtils.sha1Hex(password));
	};
	
	public void login() throws IOException {
		requestFactory = new NetHttpTransport().createRequestFactory(new HttpRequestInitializer() {
			@Override
			public void initialize(HttpRequest request) {
				request.getHeaders().setUserAgent("Kodi");
				request.setParser(new JsonObjectParser(new GsonFactory()));
			}
		});
		
		HttpRequest request = requestFactory.buildPostRequest(
			new GenericUrl("https://api.followshows.com/login"),
			new UrlEncodedContent(loginData)
		);
		
		request.execute();
	}

	public HttpResponse request(String url, Object... parameter) throws IOException {
		HashMap<String, String> data=new HashMap<>(loginData);
		for(int i=0;i<parameter.length;i+=2)
			data.put(parameter[i].toString(), parameter[i+1].toString());
		
		HttpRequest request = requestFactory.buildPostRequest(
			new GenericUrl(url),
			new UrlEncodedContent(data)
		);
		
		return request.execute();
	}

	public List<FSShow> searchShows(String searchString) throws IOException {
		HttpResponse req = this.request("https://api.followshows.com/search/shows","q",searchString);
		return Arrays.asList(req.parseAs(FSShow[].class));
	}

	public void followShow(FSShow show) throws IOException {
		this.request("https://api.followshows.com/show/follow", "show", show.getShortName());
	}

	public boolean isWatched(FSShow show, int season, int episode) throws IOException {
		return this.request("https://api.followshows.com/episode/watched", "show", show.getShortName(), "seasonNumber", season, "episodeNumber", episode).parseAs(boolean.class);
	}

	public void markWatched(FSShow show, int season, int episode) throws IOException {
		this.request("https://api.followshows.com/episodes/markAsWatched", "show", show.getShortName(), "seasonNumber", season, "episodeNumber", episode);
	}

	public HashMap<Integer, FSEpisode> getSeason(FSShow followShow, int season) throws IOException {
		FSEpisode[] episodes=this.request("https://api.followshows.com/shows/"+followShow.getShortName()+"/seasons/"+season).parseAs(FSEpisode[].class);
		
		HashMap<Integer, FSEpisode> res=new HashMap<>();
		for(FSEpisode ep:episodes)
			res.put(ep.getNumberInSeason(), ep);
		return res;
	}
	
}
