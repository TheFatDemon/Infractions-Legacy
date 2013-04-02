package com.legit2.hqm.Infractions;

import com.rosaloves.bitlyj.Bitly.Provider;
import com.rosaloves.bitlyj.Url;

import static com.rosaloves.bitlyj.Bitly.as;
import static com.rosaloves.bitlyj.Bitly.shorten;

public class URLShortenUtil
{
	/**
	 * Converts a URL into a bit.ly shortened URL.
	 * 
	 * @param url
	 * @return
	 */
	public static String convertURL(String input)
	{
		if(!input.startsWith("http://") && !input.startsWith("https://"))
		{
			input = ("http://" + input);
		}
		Provider bitly = as("justicehqm", "R_8e7103357a5a07b25206fe657fe59d07");
		Url shortUrl = (Url) bitly.call(shorten(input));
		return shortUrl.getShortUrl();
	}
}
