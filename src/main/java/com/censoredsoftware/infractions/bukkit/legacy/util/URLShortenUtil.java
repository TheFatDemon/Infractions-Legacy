package com.censoredsoftware.infractions.bukkit.legacy.util;

import com.rosaloves.bitlyj.Bitly.Provider;
import com.rosaloves.bitlyj.Url;

import static com.rosaloves.bitlyj.Bitly.as;
import static com.rosaloves.bitlyj.Bitly.shorten;

public class URLShortenUtil
{
	/**
	 * Converts a URL into a bit.ly shortened URL.
	 *
	 * @return String
	 */
	public static String convertURL(String input)
	{
		if(!SettingUtil.getSettingBoolean("bitly.use")) return input;
		if(!input.startsWith("http://") && !input.startsWith("https://")) input = ("http://" + input);
		Provider bitly = as(SettingUtil.getSettingString("bitly.user"), SettingUtil.getSettingString("bitly.key"));
		Url shortUrl = (Url) bitly.call(shorten(input));
		return shortUrl.getShortUrl();
	}
}
