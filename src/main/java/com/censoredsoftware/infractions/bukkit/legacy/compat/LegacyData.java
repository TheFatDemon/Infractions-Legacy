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
import com.censoredsoftware.infractions.bukkit.dossier.CompleteDossier;
import com.censoredsoftware.infractions.bukkit.dossier.Dossier;
import com.censoredsoftware.infractions.bukkit.evidence.Evidence;
import com.censoredsoftware.infractions.bukkit.evidence.EvidenceType;
import com.censoredsoftware.infractions.bukkit.issuer.Issuer;
import com.censoredsoftware.infractions.bukkit.issuer.IssuerType;
import com.censoredsoftware.infractions.bukkit.legacy.InfractionsPlugin;
import com.censoredsoftware.infractions.bukkit.legacy.data.DataManager;
import com.censoredsoftware.infractions.bukkit.legacy.util.MiscUtil;
import com.censoredsoftware.library.mcidprovider.McIdProvider;
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
		if(!InfractionsPlugin.getInst().getConfig().getBoolean("convert.done"))
		{
			// New thread
			new Thread(new LegacyData()).start();
		}
	}

	public static void syncConvert()
	{
		if(!InfractionsPlugin.getInst().getConfig().getBoolean("convert.done"))
		{
			// Main thread
			new LegacyData().run();
		}
	}

	private static Issuer LEGACY_ISSUER = new Issuer(IssuerType.LEGACY, "LEGACY");
	private static Logger messageLog = Logger.getLogger("Minecraft");
	private static boolean all = InfractionsPlugin.getInst().getConfig().getBoolean("convert.all");

	@Override
	public void run()
	{
		log.warning("--------------------------------------");
		log.warning("CONVERTING ALL DATA INTO A NEW FORMAT.");
		log.warning("THIS WILL CAUSE LAG FOR A FEW MOMENTS.");
		log.warning("--------------------------------------");

		List<String> playerList = Lists.newArrayList(getLegacyData().keySet());

		// Don't convert if no suitable data is found
		if(!playerList.isEmpty())
		{
			int error = 0;
			int count = 0;
			boolean messages = playerList.size() >= 1000;

			if(messages) messageLog.info("Sir, since you have so many infractions, the plugin author has called in an sizable crew to help you convert this entire pile of old data.");

			int quarter = playerList.size() / 4;
			int half = playerList.size() / 2;
			int lastquarter = playerList.size() - quarter;

			long startTime = System.currentTimeMillis();

			log.info("ORGANIZING DOSSIERS (THIS CAN TAKE AWHILE).");
			for(String player : playerList)
			{
				count++;
				if(messages && count == quarter) messageLog.info("Captain! We're about 1/4 of the way through these things.");
				else if(messages && count == half) messageLog.info("Sir, we still have about half of the pile left.");
				else if(messages && count == lastquarter) messageLog.info("Almost done! We're at 3/4 completion.");
				error += organizeDossier(player);
			}

			if(messages) messageLog.info("We've finished organizing the dossiers sir, and it only took " + MiscUtil.prettyTime(startTime) + ".");

			log.info("DOSSIERS HAVE BEEN ORGANIZED.");

			Set<Dossier> dossiers = Infractions.allDossiers();

			messages = dossiers.size() >= 1000;

			long halfTime = System.currentTimeMillis();

			log.info("CONSOLIDATING INFRACTIONS (SIT TIGHT).");
			if(messages) messageLog.info("We're nearly done, this part is much easier.");
			for(Dossier dossier : dossiers)
				if(dossier instanceof CompleteDossier) error += consolodateLegacyInfractions((CompleteDossier) dossier);

			if(messages)
			{
				messageLog.info("After another " + MiscUtil.prettyTime(halfTime) + " we're finally done!");
				messageLog.info("We've compiled a report for you sir:");
			}

			log.info("PROCESS COMPLETED " + MiscUtil.timeSincePretty(startTime).toUpperCase() + " WITH " + error + " ERRORS.");
			if(error > 0)
			{
				log.info("Sometimes an error or two is expected.");
				log.info("Don't panic! If you have to, just try again.");
			}

			if(messages) messageLog.info("Everything is ready, Captain. Should we set sail?");

			InfractionsPlugin.getInst().getConfig().set("convert.done", true);
			InfractionsPlugin.getInst().saveConfig();
		}

		log.warning("--------------------");
		log.warning("CONVERSION COMPLETE.");
		log.warning("--------------------");

		legacyData.clear();
	}

	private static String tryAgain = "";

	private static int organizeDossiers(List<String> playerList)
	{
		int error = 0;
		for(UUID id : McIdProvider.getIds(playerList))
		{
			if(id == null)
			{
				error += 1;
				continue;
			}
			Infractions.getDossier(id);
		}
		return error;
	}

	@SuppressWarnings("unchecked")
	private static int organizeDossier(String target)
	{
		if(all && !hasData(target, "INFRACTIONS")) return 0;
		UUID id = McIdProvider.getId(target);
		if(id == null)
		{
			if(!tryAgain.equals(target))
			{
				tryAgain = target;
				return organizeDossier(target);
			}
			log.warning("Is \"" + target + "\" an actual player?");
			return 1;
		}

		Dossier dossier = Infractions.getDossier(id);
		if(!(dossier instanceof CompleteDossier)) DataManager.getManager().getMapFor(LegacyDossier.class).put(id, dossier.complete(target));
		return 0;
	}

	private static int consolodateLegacyInfractions(CompleteDossier dossier)
	{
		int error = 0;
		int count = 0;

		if(hasData(dossier.getLastKnownName(), "INFRACTIONS"))
		{
			// Data to get and return.
			for(Map.Entry<String, HashMap<String, Object>> entry : getLegacyData().entrySet())
			{
				if(!entry.getKey().equalsIgnoreCase(dossier.getLastKnownName())) continue;
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

							Infraction infraction = new Infraction(dossier.getId(), dateTime != null ? dateTime : System.currentTimeMillis(), reason, score, LEGACY_ISSUER, new Evidence(LEGACY_ISSUER, EvidenceType.UNKNOWN, dateTime != null ? dateTime : System.currentTimeMillis(), proof));
							dossier.cite(infraction);
						}
						catch(Exception e)
						{
							error++;
							e.printStackTrace();
						}
					}
				}
			}
			if(error > 0) log.warning("Error converting " + error + " of " + count + " infractions for " + dossier.getLastKnownName() + ".");
		}
		return (error > 0 ? 1 : 0);
	}

	private static String PATH = "plugins/Infractions/";

	// Legacy Save
	private static Map<String, HashMap<String, Object>> legacyData = Maps.newHashMap();

	@SuppressWarnings("unchecked")
	private LegacyData()
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

	private static boolean hasData(String p, String id)
	{
		return hasPlayer(p) && getLegacyData().get(p).containsKey(id);
	}

	/*
	 * Check if the player has saved information.
	 */
	private static boolean hasPlayer(String p)
	{
		return getLegacyData().containsKey(p);
	}

	/*
	 * getLegacyData() : Returns all Legacy data.
	 */
	private static Map<String, HashMap<String, Object>> getLegacyData()
	{
		return legacyData;
	}
}
