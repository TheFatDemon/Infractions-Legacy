package com.rosaloves.bitlyj;

public class UrlInfo
{
	private final Url url;
	private final String createdBy;
	private final String title;

	UrlInfo(Url url, String createdBy, String title)
	{
		this.url = url;
		this.createdBy = createdBy;
		this.title = title;
	}

	public String getCreatedBy()
	{
		return this.createdBy;
	}

	public String getTitle()
	{
		return this.title;
	}

	public Url getUrl()
	{
		return this.url;
	}

	public String toString()
	{
		return "Info [createdBy=" + this.createdBy + ", title=" + this.title + ", url=" + this.url + "]";
	}
}
