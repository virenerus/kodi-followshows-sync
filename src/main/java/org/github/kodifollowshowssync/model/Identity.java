package org.github.kodifollowshowssync.model;

import org.github.kodifollowshowssync.followshows.FSShow;

public class Identity {
	private final String kodiName;
	private final FSShow fsShow;
	
	public Identity(String kodiName, FSShow fsShow) {
		super();
		this.kodiName = kodiName;
		this.fsShow = fsShow;
	}

	public String getKodiName() {
		return kodiName;
	}

	public FSShow getFSShow() {
		return fsShow;
	}
}
