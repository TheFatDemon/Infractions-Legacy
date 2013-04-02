package com.legit2.hqm.Infractions;

import java.util.HashMap;

public class Convert
{
	@SuppressWarnings("unchecked")
	public static HashMap<String, String> getFlatfileInfractions(String target)
	{
		if(Save.hasData(target, "INFRACTIONS"))
		{
			HashMap<String, String> original = ((HashMap<String, String>) Save.getData(target, "INFRACTIONS"));
			HashMap<String, String> toreturn = new HashMap<String, String>();
			for(String s : original.keySet())
			{
				toreturn.put(s, original.get(s));
			}
			convertFlatfileInfractions(target, toreturn); // clean original
			return toreturn;
		}
		return null;
	}

	public static void convertFlatfileInfractions(String p, HashMap<String, String> data)
	{
		Save.saveData(p, "INFRACTIONS", data);
	}
}
