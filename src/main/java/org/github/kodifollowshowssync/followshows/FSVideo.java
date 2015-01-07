package org.github.kodifollowshowssync.followshows;

import com.google.api.client.util.Key;

public class FSVideo {
	@Key
	private String url;
	@Key
	private String source;
	@Key
	private String price;
	
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getSource() {
		return source;
	}
	public void setSource(String source) {
		this.source = source;
	}
	public String getPrice() {
		return price;
	}
	public void setPrice(String price) {
		this.price = price;
	}
}
