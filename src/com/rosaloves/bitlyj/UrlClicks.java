package com.rosaloves.bitlyj;

public class UrlClicks
{
	private final long userClicks;
	private final long globalClicks;
	private final Url url;

	UrlClicks(Url url, long userClicks, long globalClicks)
	{
		this.url = url;
		this.userClicks = userClicks;
		this.globalClicks = globalClicks;
	}

	public Url getUrl()
	{
		return this.url;
	}

	public long getUserClicks()
	{
		return this.userClicks;
	}

	public long getGlobalClicks()
	{
		return this.globalClicks;
	}

	public String toString()
	{
		return "UrlClicks [globalClicks=" + this.globalClicks + ", userClicks=" + this.userClicks + ", url=" + this.url + "]";
	}

}
