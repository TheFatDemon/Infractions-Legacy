/*
 * Copyright 2014 Alexander Chauncey
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.rosaloves.bitlyj;

public class Url
{
	private String shortBase;
	private String globalHash;
	private String userHash;
	private String shortUrl;
	private String longUrl;

	Url()
	{
	}

	Url(String shortBase, String globalHash, String userHash, String shortUrl, String longUrl)
	{
		this.shortBase = shortBase;
		this.globalHash = globalHash;
		this.userHash = userHash;
		this.shortUrl = shortUrl;
		this.longUrl = longUrl;

		if(this.shortUrl.length() == 0) this.shortUrl = (shortBase + userHash);
	}

	public String getGlobalHash()
	{
		return this.globalHash;
	}

	public String getUserHash()
	{
		return this.userHash;
	}

	public String getShortUrl()
	{
		return this.shortUrl;
	}

	public String getLongUrl()
	{
		return this.longUrl;
	}

	public String toString()
	{
		return "Url [shortBase=" + this.shortBase + ", globalHash=" + this.globalHash + ", longUrl=" + this.longUrl + ", shortUrl=" + this.shortUrl + ", userHash=" + this.userHash + "]";
	}
}
