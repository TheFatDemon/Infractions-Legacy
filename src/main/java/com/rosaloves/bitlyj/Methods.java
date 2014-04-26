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

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.*;

final class Methods
{
	public static MethodBase<Object> info(String value)
	{
		return new MethodBase<Object>("info", getUrlMethodArgs(new String[] { value }))
		{
			public UrlInfo apply(Bitly.Provider provider, Document document)
			{
				return Methods.parseInfo(provider, document.getElementsByTagName("info").item(0));
			}
		};
	}

	public static MethodBase<Object> info(String[] values)
	{
		return new MethodBase<Object>("info", getUrlMethodArgs(values))
		{
			public Set<UrlInfo> apply(Bitly.Provider provider, Document document)
			{
				HashSet<UrlInfo> inf = new HashSet<UrlInfo>();
				NodeList infos = document.getElementsByTagName("info");
				for(int i = 0; i < infos.getLength(); i++)
				{
					inf.add(Methods.parseInfo(provider, infos.item(i)));
				}
				return inf;
			}
		};
	}

	public static MethodBase<Object> expand(String values)
	{
		return new MethodBase<Object>("expand", getUrlMethodArgs(new String[] { values }))
		{
			public Url apply(Bitly.Provider provider, Document document)
			{
				return Methods.parseUrl(provider, document.getElementsByTagName("entry").item(0));
			}
		};
	}

	public static MethodBase<Object> expand(String[] values)
	{
		return new MethodBase<Object>("expand", getUrlMethodArgs(values))
		{
			public Set<Url> apply(Bitly.Provider provider, Document document)
			{
				HashSet<Url> inf = new HashSet<Url>();

				NodeList infos = document.getElementsByTagName("entry");
				for(int i = 0; i < infos.getLength(); i++)
				{
					inf.add(Methods.parseUrl(provider, infos.item(i)));
				}

				return inf;
			}
		};
	}

	@SuppressWarnings("unchecked")
	public static MethodBase<Object> shorten(String longUrl)
	{
		return new MethodBase<Object>("shorten", new Pair[] { Pair.p("longUrl", longUrl) })
		{
			public ShortenedUrl apply(Bitly.Provider provider, Document document)
			{
				NodeList infos = document.getElementsByTagName("data");
				return Methods.parseShortenedUrl(provider, infos.item(0));
			}
		};
	}

	@SuppressWarnings("unchecked")
	public static MethodBase<Object> clicks(String string)
	{
		return new MethodBase<Object>("clicks", new Pair[] { Pair.p(hashOrUrl(string), string) })
		{
			public UrlClicks apply(Bitly.Provider provider, Document document)
			{
				return Methods.parseClicks(provider, document.getElementsByTagName("clicks").item(0));
			}
		};
	}

	public static MethodBase<Object> clicks(String[] string)
	{
		return new MethodBase<Object>("clicks", getUrlMethodArgs(string))
		{
			public Set<UrlClicks> apply(Bitly.Provider provider, Document document)
			{
				HashSet<UrlClicks> clicks = new HashSet<UrlClicks>();
				NodeList nl = document.getElementsByTagName("clicks");
				for(int i = 0; i < nl.getLength(); i++)
				{
					clicks.add(Methods.parseClicks(provider, nl.item(i)));
				}
				return clicks;
			}
		};
	}

	static Collection<Pair<String, String>> getUrlMethodArgs(String[] value)
	{
		List<Pair<String, String>> pairs = new ArrayList<Pair<String, String>>();
		for(String p : value)
		{
			pairs.add(Pair.p(hashOrUrl(p), p));
		}
		return pairs;
	}

	static String hashOrUrl(String p)
	{
		return p.startsWith("http://") ? "shortUrl" : "hash";
	}

	static UrlClicks parseClicks(Bitly.Provider provider, Node item)
	{
		NodeList nl = item.getChildNodes();
		long user = 0L;
		long global = 0L;
		for(int i = 0; i < nl.getLength(); i++)
		{
			String name = nl.item(i).getNodeName();
			String value = Dom.getTextContent(nl.item(i));
			if("user_clicks".equals(name)) user = Long.parseLong(value);
			else if("global_clicks".equals(name))
			{
				global = Long.parseLong(value);
			}
		}
		return new UrlClicks(parseUrl(provider, item), user, global);
	}

	static ShortenedUrl parseShortenedUrl(Bitly.Provider provider, Node nl)
	{
		String gHash = "";
		String uHash = "";
		String sUrl = "";
		String lUrl = "";
		String isNew = "";
		NodeList il = nl.getChildNodes();
		for(int i = 0; i < il.getLength(); i++)
		{
			Node n = il.item(i);
			String name = n.getNodeName();
			String value = Dom.getTextContent(n).trim();

			if("new_hash".equals(name)) isNew = value;
			else if("url".equals(name)) sUrl = value;
			else if("long_url".equals(name)) lUrl = value;
			else if("global_hash".equals(name)) gHash = value;
			else if("hash".equals(name))
			{
				uHash = value;
			}
		}
		return new ShortenedUrl(provider.getUrl(), gHash, uHash, sUrl, lUrl, isNew.equals("1"));
	}

	static Url parseUrl(Bitly.Provider provider, Node nl)
	{
		String gHash = "";
		String uHash = "";
		String sUrl = "";
		String lUrl = "";
		NodeList il = nl.getChildNodes();
		for(int i = 0; i < il.getLength(); i++)
		{
			Node n = il.item(i);
			String name = n.getNodeName();
			String value = Dom.getTextContent(n);

			if("short_url".equals(name)) sUrl = value;
			else if("long_url".equals(name)) lUrl = value;
			else if("global_hash".equals(name)) gHash = value;
			else if("user_hash".equals(name)) uHash = value;
			else if("hash".equals(name))
			{
				uHash = value;
			}
		}
		return new Url(provider.getUrl(), gHash, uHash, sUrl, lUrl);
	}

	static UrlInfo parseInfo(Bitly.Provider provider, Node nl)
	{
		NodeList il = nl.getChildNodes();

		String title = "";
		String createdBy = "";

		for(int i = 0; i < il.getLength(); i++)
		{
			Node n = il.item(i);

			String name = n.getNodeName();
			String value = Dom.getTextContent(n);

			if("created_by".equals(name)) createdBy = value;
			else if("title".equals(name))
			{
				title = value;
			}

		}

		return new UrlInfo(parseUrl(provider, nl), createdBy, title);
	}
}
