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

package com.censoredsoftware.infractions.bukkit.legacy.compat;

import com.censoredsoftware.infractions.bukkit.Infraction;
import com.censoredsoftware.infractions.bukkit.Infractions;
import com.censoredsoftware.infractions.bukkit.dossier.Dossier;
import com.censoredsoftware.infractions.bukkit.evidence.Evidence;
import com.censoredsoftware.infractions.bukkit.evidence.EvidenceType;
import com.censoredsoftware.infractions.bukkit.issuer.Issuer;
import com.censoredsoftware.infractions.bukkit.issuer.IssuerType;
import com.censoredsoftware.infractions.bukkit.legacy.InfractionsPlugin;
import com.censoredsoftware.library.helper.MojangIdProvider;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Logger;

@SuppressWarnings({ "ResultOfMethodCallIgnored", "ConstantConditions" })
public class LegacyData implements Runnable
{
	static final Logger log = InfractionsPlugin.getInst().getLogger();

	public static void asyncConvert()
	{
		if(InfractionsPlugin.getInst().getConfig().getBoolean("convert"))
		{
			// New thread
			new Thread(new LegacyData()).start();
		}
	}

	public static void syncConvert()
	{
		if(InfractionsPlugin.getInst().getConfig().getBoolean("convert"))
		{
			// Main thread
			new LegacyData().run();
		}
	}

	private static Issuer LEGACY_ISSUER = new Issuer(IssuerType.LEGACY, "LEGACY");

	@Override
	public void run()
	{
		log.warning("CONVERTING ALL DATA TO A NEW FORMAT.");
		log.warning("CONVERTING ALL DATA TO A NEW FORMAT.");
		log.warning("THIS WILL CAUSE LAG FOR A FEW SECONDS.");
		log.warning("THIS WILL CAUSE LAG FOR A FEW SECONDS.");

		List<String> playerList = Lists.newArrayList();

		for(Map.Entry<String, HashMap<String, Object>> entry : getLegacyData().entrySet())
			playerList.add(entry.getKey());

		// Don't convert if no suitable data is found
		if(playerList.isEmpty()) return;

		for(String target : playerList)
			convertLegacyInfraction(target);

		InfractionsPlugin.getInst().getConfig().set("convert", false);
		InfractionsPlugin.getInst().saveConfig();

		log.warning("CONVERSION COMPLETE.");
		log.warning("CONVERSION COMPLETE.");

		legacyData.clear();
	}

	@SuppressWarnings("unchecked")
	public static void convertLegacyInfraction(String target)
	{
		UUID id = MojangIdProvider.getId(target);
		if(id == null)
		{
			log.warning("Is \"" + target + "\" an actual player?");
			return;
		}

		Dossier dossier = Infractions.getDossier(id);
		dossier = dossier.complete(target);

		log.info("Organizing dossier for " + target + ".");

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

							// don't convert virtues
							if(score <= 0) continue;

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
								dossier.cite(infraction);
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
			if(error > 0) log.warning("Error converting " + error + " of " + count + " infractions for " + target + ".");
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
				}
				catch(Exception error)
				{
					log.severe("Deleting corrupt save for player " + load);
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
