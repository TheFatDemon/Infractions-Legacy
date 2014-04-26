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
