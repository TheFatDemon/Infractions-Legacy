package com.rosaloves.bitlyj;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

class SimpleProvider implements Bitly.Provider
{
	private final String url;
	private final String user;
	private final String apiKey;
	private final String endPoint;

	SimpleProvider(String url, String user, String apiKey, String endPoint)
	{
		this.url = url;
		this.user = user;
		this.apiKey = apiKey;
		this.endPoint = endPoint;
	}

	public <A> A call(BitlyMethod<A> m)
	{
		String url = getUrlForCall(m);
		Document response = filterErrorResponse(fetchUrl(url));
		return m.apply(this, response);
	}

	public String getUrl()
	{
		return this.url;
	}

	public String toString()
	{
		return "SimpleProvider [apiKey=" + this.apiKey + ", endPoint=" + this.endPoint + ", url=" + this.url + ", user=" + this.user + "]";
	}

	protected String getUrlForCall(BitlyMethod<?> m)
	{
		StringBuilder sb = new StringBuilder(this.endPoint).append(m.getName()).append("?").append("&login=").append(this.user).append("&apiKey=").append(this.apiKey).append("&format=xml");
		try
		{
			for(Pair<?, ?> p : m.getParameters())
				sb.append("&").append((String) p.getOne()).append("=").append(URLEncoder.encode((String) p.getTwo(), "UTF-8"));
		}
		catch(UnsupportedEncodingException e)
		{
			throw new RuntimeException(e);
		}

		return sb.toString();
	}

	private Document filterErrorResponse(Document doc)
	{
		Node statusCode = doc.getElementsByTagName("status_code").item(0);
		Node statusText = doc.getElementsByTagName("status_txt").item(0);

		if((statusCode == null) || (statusText == null))
		{
			throw new BitlyException("Unexpected response (no status and/or message)!");
		}

		int code = Integer.parseInt(Dom.getTextContent(statusCode));
		if(code == 200)
		{
			return doc;
		}
		throw new BitlyException(Dom.getTextContent(statusText));
	}

	private Document fetchUrl(String url)
	{
		try
		{
			HttpURLConnection openConnection = (HttpURLConnection) new URL(url).openConnection();
			if(openConnection.getResponseCode() == 200)
			{
				return DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(openConnection.getInputStream());
			}

			throw new BitlyException("Transport error! " + openConnection.getResponseCode() + " " + openConnection.getResponseMessage());
		}
		catch(IOException e)
		{
			throw new BitlyException("Transport I/O error!", e);
		}
		catch(Exception e)
		{
			throw new RuntimeException(e);
		}
	}
}
