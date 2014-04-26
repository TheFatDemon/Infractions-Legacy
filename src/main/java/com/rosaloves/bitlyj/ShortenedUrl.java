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
