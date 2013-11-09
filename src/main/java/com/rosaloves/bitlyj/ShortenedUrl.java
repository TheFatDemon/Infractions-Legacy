package com.rosaloves.bitlyj;

public class ShortenedUrl extends Url
{
	private final boolean newHash;

	ShortenedUrl(String shortBase, String globalHash, String userHash, String shortUrl, String longUrl, boolean newHash)
	{
		super(shortBase, globalHash, userHash, shortUrl, longUrl);
		this.newHash = newHash;
	}

	public boolean isNewHash()
	{
		return this.newHash;
	}

	public String toString()
	{
		return "ShortenedUrl [newHash=" + this.newHash + ", getGlobalHash()=" + getGlobalHash() + ", getLongUrl()=" + getLongUrl() + ", getShortUrl()=" + getShortUrl() + ", getUserHash()=" + getUserHash() + "]";
	}
}
