package com.rosaloves.bitlyj;

public class Url {
	private String shortBase;
	private String globalHash;
	private String userHash;
	private String shortUrl;
	private String longUrl;

	Url() {
	}

	Url(String shortBase, String globalHash, String userHash, String shortUrl,
			String longUrl) {
		this.shortBase = shortBase;
		this.globalHash = globalHash;
		this.userHash = userHash;
		this.shortUrl = shortUrl;
		this.longUrl = longUrl;

		if (this.shortUrl.length() == 0)
			this.shortUrl = (shortBase + userHash);
	}

	public String getGlobalHash() {
		return this.globalHash;
	}

	public String getUserHash() {
		return this.userHash;
	}

	public String getShortUrl() {
		return this.shortUrl;
	}

	public String getLongUrl() {
		return this.longUrl;
	}

	public String toString() {
		return "Url [shortBase=" + this.shortBase + ", globalHash="
				+ this.globalHash + ", longUrl=" + this.longUrl + ", shortUrl="
				+ this.shortUrl + ", userHash=" + this.userHash + "]";
	}
}