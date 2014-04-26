package com.censoredsoftware.infractions.bukkit.legacy.compat;

import com.censoredsoftware.infractions.bukkit.Infraction;
import com.censoredsoftware.infractions.bukkit.Infractions;
import com.censoredsoftware.infractions.bukkit.evidence.Evidence;
import com.censoredsoftware.infractions.bukkit.evidence.EvidenceType;
import com.censoredsoftware.infractions.bukkit.issuer.Issuer;
import com.censoredsoftware.infractions.bukkit.issuer.IssuerType;
import com.censoredsoftware.infractions.bukkit.legacy.InfractionsPlugin;
import com.censoredsoftware.infractions.bukkit.legacy.util.Messages;
import com.censoredsoftware.library.helper.MojangIdProvider;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.text.SimpleDateFormat;
import java.util.*;

@SuppressWarnings({ "ResultOfMethodCallIgnored", "ConstantConditions" })
public class LegacyData implements Runnable
{
	public static void convert()
	{
		if(InfractionsPlugin.getInst().getConfig().getBoolean("convert"))
		{
			// New thread
			new Thread(new com.censoredsoftware.infractions.bukkit.legacy.compat.LegacyData()).start();
		}
	}

	private static Issuer LEGACY_ISSUER = new Issuer(IssuerType.LEGACY, "Legacy");

	@Override
	public void run()
	{
		List<String> playerList = Lists.newArrayList();

		for(Map.Entry<String, HashMap<String, Object>> entry : getLegacyData().entrySet())
			playerList.add(entry.getKey());

		// Don't convert if no suitable data is found
		if(playerList.isEmpty()) return;

		for(String target : playerList)
			convertLegacyInfraction(target);

		InfractionsPlugin.getInst().getConfig().set("convert", false);
		InfractionsPlugin.getInst().saveConfig();
	}

	@SuppressWarnings("unchecked")
	public static void convertLegacyInfraction(String target)
	{
		if(hasData(target, "INFRACTIONS"))
		{
			int error = 0;
			int count = 0;

			// Data to get and return.
			for(Map.Entry<String, HashMap<String, Object>> entry : getLegacyData().entrySet())
			{
				if(!entry.getKey().equalsIgnoreCase(target)) continue;
				for(Map.Entry<String, Object> entry_ : entry.getValue().entrySet())
				{
					if(!entry_.getKey().equals("INFRACTIONS")) continue;
					for(String legacyData : ((HashMap<String, String>) entry_.getValue()).values())
					{
						// Get info from legacy infraction.
						String[] dataList = legacyData.substring(2).split(" - ");
						List<String> otherData = Lists.newArrayList(Arrays.asList(dataList));

						try
						{
							count++;

							// Convert score to new % system.
							int score;
							if(!legacyData.substring(0, 1).equals("-")) score = (Integer.parseInt(legacyData.substring(0, 1)));
							else score = (Integer.parseInt(legacyData.substring(0, 2)));

							if(score <= 0) continue;

							UUID id = MojangIdProvider.getId(target);
							String reason = otherData.get(0);
							String proof = otherData.get(1);
							String date = otherData.get(2);

							Long dateTime = null;
							try
							{
								dateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(date).getTime();
							}
							catch(Exception ignored)
							{
							}

							// Save data to new structure.
							if(id != null)
							{
								Infraction infraction = new Infraction(id, dateTime != null ? dateTime : System.currentTimeMillis(), reason, score, LEGACY_ISSUER, new Evidence(LEGACY_ISSUER, EvidenceType.UNKNOWN, dateTime != null ? dateTime : System.currentTimeMillis(), proof));
								Infractions.getDossier(id).cite(infraction);
							}
						}
						catch(Exception e)
						{
							error++;
							e.printStackTrace();
						}
					}
				}
			}
			if(error > 0) Messages.warning("Error converting " + error + " of " + count + " infractions for " + target + ".");
		}
	}

	private static String PATH = "plugins/Infractions/";

	// Legacy Save
	private static Map<String, HashMap<String, Object>> legacyData = Maps.newHashMap();

	@SuppressWarnings("unchecked")
	public LegacyData()
	{
		File f1 = new File(PATH + "Players/");
		File[] list = f1.listFiles();
		if(list == null) return;
		for(File element : list)
		{
			String load = element.getName();
			if(load.endsWith(".player"))
			{
				load = load.substring(0, load.length() - 7);
				try
				{
					ObjectInputStream ois = new ObjectInputStream(new FileInputStream(element));
					Object result = ois.readObject();
					HashMap<String, Object> cast = (HashMap<String, Object>) result;
					getLegacyData().put(load, cast);
					ois.close();

					// Delete legacy information after loading it in.
					element.delete();
				}
				catch(Exception error)
				{
					Messages.severe("Deleting corrupt save for player " + load);
					element.delete();
				}
			}
		}

		// Delete legacy folder if it is empty.
		if(f1.listFiles().length == 0) f1.delete();
	}

	public static boolean hasData(String p, String id)
	{
		return hasPlayer(p) && getLegacyData().get(p).containsKey(id);
	}

	/*
	 * Check if the player has saved information.
	 */
	public static boolean hasPlayer(String p)
	{
		return getLegacyData().containsKey(p);
	}

	/*
	 * getLegacyData() : Returns all Legacy data.
	 */
	public static Map<String, HashMap<String, Object>> getLegacyData()
	{
		return legacyData;
	}
}
