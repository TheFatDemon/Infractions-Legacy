package com.rosaloves.bitlyj;

public class Jmp
{
	public static Bitly.Provider as(String user, String apiKey)
	{
		return new SimpleProvider("http://j.mp/", user, apiKey, "http://api.j.mp/v3/");
	}
}
