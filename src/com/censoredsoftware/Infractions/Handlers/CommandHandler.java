package com.censoredsoftware.Infractions.Handlers;

import java.util.*;
import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.censoredsoftware.Infractions.Infractions;
import com.censoredsoftware.Infractions.Utilities.*;
import com.google.common.base.Strings;

public class CommandHandler implements CommandExecutor
{

	static Infractions plugin;
	static Logger log = Logger.getLogger("Minecraft");
	static Date date = new Date();

	public CommandHandler(Infractions i)
	{
		plugin = i;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command c, String label, String[] args)
	{
		Player p = null;
		if(sender instanceof Player) p = (Player) sender;
		if(c.getName().equalsIgnoreCase("infractions"))
		{
			if(args.length == 0)
			{
				MiscUtil.messageSend(p, "---------------");
				MiscUtil.messageSend(p, "INFRACTIONS HELP");
				MiscUtil.messageSend(p, "---------------");
				if(MiscUtil.hasPermissionOrOP(p, "infractions.mod"))
				{
					MiscUtil.messageSend(p, MiscUtil.chatColor(p, ChatColor.GRAY) + "/cite <player> <infraction> [proof-url]");
					MiscUtil.messageSend(p, MiscUtil.chatColor(p, ChatColor.GRAY) + "/uncite <player> <key>" + MiscUtil.chatColor(p, ChatColor.WHITE) + " - Find the key with " + MiscUtil.chatColor(p, ChatColor.YELLOW) + "/history" + MiscUtil.chatColor(p, ChatColor.WHITE) + ".");
				}
				MiscUtil.messageSend(p, MiscUtil.chatColor(p, ChatColor.GRAY) + "/history [player]");
				MiscUtil.messageSend(p, MiscUtil.chatColor(p, ChatColor.GRAY) + "/infractions types " + MiscUtil.chatColor(p, ChatColor.WHITE) + "- Shows all valid infraction types.");
				MiscUtil.messageSend(p, MiscUtil.chatColor(p, ChatColor.GRAY) + "/virtues " + MiscUtil.chatColor(p, ChatColor.WHITE) + "- Shows all valid virtue types.");
				MiscUtil.messageSend(p, MiscUtil.chatColor(p, ChatColor.AQUA) + "Using the GNU " + MiscUtil.chatColor(p, ChatColor.DARK_AQUA) + "Affero" + MiscUtil.chatColor(p, ChatColor.AQUA) + " General Public License.");
				MiscUtil.messageSend(p, MiscUtil.chatColor(p, ChatColor.AQUA) + "Read the AGPL at " + MiscUtil.chatColor(p, ChatColor.YELLOW) + "http://bit.ly/TLY1xB");
				MiscUtil.messageSend(p, MiscUtil.chatColor(p, ChatColor.AQUA) + "Source: " + MiscUtil.chatColor(p, ChatColor.YELLOW) + "https://github.com/Clashnia/Infractions");
				return true;
			}
			else if(args[0].equalsIgnoreCase("types"))
			{
				MiscUtil.messageSend(p, "----------------");
				MiscUtil.messageSend(p, "INFRACTIONS TYPES");
				MiscUtil.messageSend(p, "----------------");
				MiscUtil.messageSend(p, MiscUtil.chatColor(p, ChatColor.GREEN) + "Level 1:");
				for(int i = 0; i < LevelUtil.getLevel1().size(); i++)
				{
					MiscUtil.messageSend(p, LevelUtil.getLevel1().get(i));
				}
				MiscUtil.messageSend(p, MiscUtil.chatColor(p, ChatColor.YELLOW) + "Level 2:");
				for(int i = 0; i < LevelUtil.getLevel2().size(); i++)
				{
					MiscUtil.messageSend(p, LevelUtil.getLevel2().get(i));
				}
				MiscUtil.messageSend(p, MiscUtil.chatColor(p, ChatColor.GOLD) + "Level 3:");
				for(int i = 0; i < LevelUtil.getLevel3().size(); i++)
				{
					MiscUtil.messageSend(p, LevelUtil.getLevel3().get(i));
				}
				MiscUtil.messageSend(p, MiscUtil.chatColor(p, ChatColor.RED) + "Level 4:");
				for(int i = 0; i < LevelUtil.getLevel4().size(); i++)
				{
					MiscUtil.messageSend(p, LevelUtil.getLevel4().get(i));
				}
				MiscUtil.messageSend(p, MiscUtil.chatColor(p, ChatColor.DARK_RED) + "Level 5:");
				for(int i = 0; i < LevelUtil.getLevel5().size(); i++)
				{
					MiscUtil.messageSend(p, LevelUtil.getLevel5().get(i));
				}
				return true;
			}
			else
			{
				MiscUtil.messageSend(p, "Too many arguments.");
				return false;
			}
		}
		else if(c.getName().equalsIgnoreCase("virtues"))
		{
			MiscUtil.messageSend(p, "----------------");
			MiscUtil.messageSend(p, "VIRTUES TYPES");
			MiscUtil.messageSend(p, "----------------");
			for(int i = 0; i < VirtueUtil.getVirtues().size(); i++)
			{
				MiscUtil.messageSend(p, VirtueUtil.getVirtues().get(i));
			}
			return true;
		}
		else if(c.getName().equalsIgnoreCase("cite"))
		{
			if(!MiscUtil.hasPermissionOrOP(p, "infractions.mod"))
			{
				MiscUtil.messageSend(p, "You do not have enough permissions.");
				return true;
			}
			if(SettingUtil.getSettingBoolean("require_proof"))
			{
				if((args.length != 3))
				{
					MiscUtil.messageSend(p, "You must provide a valid URL as proof.");
					return false;
				}
				if(!MiscUtil.isValidURL(args[2]))
				{
					MiscUtil.messageSend(p, "You must provide a valid URL as proof.");
					return false;
				}
			}
			if(args.length == 0 || args.length == 1)
			{
				MiscUtil.messageSend(p, "Not enough arguments.");
				return false;
			}

			if(MiscUtil.getInfractionsPlayer(args[0]) == null)
			{
				MiscUtil.messageSend(p, "This player hasn't joined yet.");
				return true;
			}
			Random generator = new Random();
			int id = generator.nextInt();
			// Level 1
			if(LevelUtil.getLevel1().contains(args[1]))
			{
				if(args.length == 3)
				{
					if(!MiscUtil.isValidURL(args[2]))
					{
						MiscUtil.messageSend(p, "You must provide a valid URL as proof.");
						return false;
					}
					MiscUtil.messageSend(p, "Proof URL: " + MiscUtil.chatColor(p, ChatColor.GOLD) + URLShortenUtil.convertURL(args[2]));
					MiscUtil.messageSend(p, MiscUtil.chatColor(p, ChatColor.GOLD) + "Success! " + MiscUtil.chatColor(p, ChatColor.WHITE) + "The level 1 infraction has been recieved.");
					MiscUtil.setScore(MiscUtil.getInfractionsPlayer(args[0]), 1 + MiscUtil.getScore(MiscUtil.getInfractionsPlayer(args[0])));
					MiscUtil.addInfraction(MiscUtil.getInfractionsPlayer(args[0]), 1, id, args[1], URLShortenUtil.convertURL(args[2]));
					MiscUtil.kickNotify(MiscUtil.getInfractionsPlayer(args[0]), args[1]);
					return true;
				}
				MiscUtil.messageSend(p, MiscUtil.chatColor(p, ChatColor.GOLD) + "Success! " + MiscUtil.chatColor(p, ChatColor.WHITE) + "The level 1 infraction has been recieved.");
				MiscUtil.setScore(MiscUtil.getInfractionsPlayer(args[0]), 1 + MiscUtil.getScore(MiscUtil.getInfractionsPlayer(args[0])));
				MiscUtil.addInfraction(MiscUtil.getInfractionsPlayer(args[0]), 1, id, args[1], "No proof.");
				MiscUtil.kickNotify(MiscUtil.getInfractionsPlayer(args[0]), args[1]);
				return true;
			}
			else if(LevelUtil.getLevel2().contains(args[1]))
			{
				if(args.length == 3)
				{
					if(!MiscUtil.isValidURL(args[2]))
					{
						MiscUtil.messageSend(p, "You must provide a valid URL as proof.");
						return false;
					}
					MiscUtil.messageSend(p, "Proof URL: " + MiscUtil.chatColor(p, ChatColor.GOLD) + URLShortenUtil.convertURL(args[2]));
					MiscUtil.messageSend(p, MiscUtil.chatColor(p, ChatColor.GOLD) + "Success! " + MiscUtil.chatColor(p, ChatColor.WHITE) + "The level 2 infraction has been recieved.");
					MiscUtil.setScore(MiscUtil.getInfractionsPlayer(args[0]), 2 + MiscUtil.getScore(MiscUtil.getInfractionsPlayer(args[0])));
					MiscUtil.addInfraction(MiscUtil.getInfractionsPlayer(args[0]), 2, id, args[1], URLShortenUtil.convertURL(args[2]));
					MiscUtil.kickNotify(MiscUtil.getInfractionsPlayer(args[0]), args[1]);
					return true;
				}
				MiscUtil.messageSend(p, MiscUtil.chatColor(p, ChatColor.GOLD) + "Success! " + MiscUtil.chatColor(p, ChatColor.WHITE) + "The level 2 infraction has been recieved.");
				MiscUtil.setScore(MiscUtil.getInfractionsPlayer(args[0]), 2 + MiscUtil.getScore(MiscUtil.getInfractionsPlayer(args[0])));
				MiscUtil.addInfraction(MiscUtil.getInfractionsPlayer(args[0]), 2, id, args[1], "No proof.");
				MiscUtil.kickNotify(MiscUtil.getInfractionsPlayer(args[0]), args[1]);
				return true;
			}
			else if(LevelUtil.getLevel3().contains(args[1]))
			{
				if(args.length == 3)
				{
					if(!MiscUtil.isValidURL(args[2]))
					{
						MiscUtil.messageSend(p, "You must provide a valid URL as proof.");
						return false;
					}
					MiscUtil.messageSend(p, "Proof URL: " + MiscUtil.chatColor(p, ChatColor.GOLD) + URLShortenUtil.convertURL(args[2]));
					MiscUtil.messageSend(p, MiscUtil.chatColor(p, ChatColor.GOLD) + "Success! " + MiscUtil.chatColor(p, ChatColor.WHITE) + "The level 3 infraction has been recieved.");
					MiscUtil.setScore(MiscUtil.getInfractionsPlayer(args[0]), 3 + MiscUtil.getScore(MiscUtil.getInfractionsPlayer(args[0])));
					MiscUtil.addInfraction(MiscUtil.getInfractionsPlayer(args[0]), 3, id, args[1], URLShortenUtil.convertURL(args[2]));
					MiscUtil.kickNotify(MiscUtil.getInfractionsPlayer(args[0]), args[1]);
					return true;
				}
				MiscUtil.messageSend(p, MiscUtil.chatColor(p, ChatColor.GOLD) + "Success! " + MiscUtil.chatColor(p, ChatColor.WHITE) + "The level 3 infraction has been recieved.");
				MiscUtil.setScore(MiscUtil.getInfractionsPlayer(args[0]), 3 + MiscUtil.getScore(MiscUtil.getInfractionsPlayer(args[0])));
				MiscUtil.addInfraction(MiscUtil.getInfractionsPlayer(args[0]), 3, id, args[1], "No proof.");
				MiscUtil.kickNotify(MiscUtil.getInfractionsPlayer(args[0]), args[1]);
				return true;
			}
			else if(LevelUtil.getLevel4().contains(args[1]))
			{
				if(args.length == 3)
				{
					if(!MiscUtil.isValidURL(args[2]))
					{
						MiscUtil.messageSend(p, "You must provide a valid URL as proof.");
						return false;
					}
					MiscUtil.messageSend(p, "Proof URL: " + MiscUtil.chatColor(p, ChatColor.GOLD) + URLShortenUtil.convertURL(args[2]));
					MiscUtil.messageSend(p, MiscUtil.chatColor(p, ChatColor.GOLD) + "Success! " + MiscUtil.chatColor(p, ChatColor.WHITE) + "The level 4 infraction has been recieved.");
					MiscUtil.setScore(MiscUtil.getInfractionsPlayer(args[0]), 4 + MiscUtil.getScore(MiscUtil.getInfractionsPlayer(args[0])));
					MiscUtil.addInfraction(MiscUtil.getInfractionsPlayer(args[0]), 4, id, args[1], URLShortenUtil.convertURL(args[2]));
					MiscUtil.kickNotify(MiscUtil.getInfractionsPlayer(args[0]), args[1]);
					return true;
				}
				MiscUtil.messageSend(p, MiscUtil.chatColor(p, ChatColor.GOLD) + "Success! " + MiscUtil.chatColor(p, ChatColor.WHITE) + "The level 4 infraction has been recieved.");
				MiscUtil.setScore(MiscUtil.getInfractionsPlayer(args[0]), 4 + MiscUtil.getScore(MiscUtil.getInfractionsPlayer(args[0])));
				MiscUtil.addInfraction(MiscUtil.getInfractionsPlayer(args[0]), 4, id, args[1], "No proof.");
				MiscUtil.kickNotify(MiscUtil.getInfractionsPlayer(args[0]), args[1]);
				return true;
			}
			else if(LevelUtil.getLevel5().contains(args[1]))
			{
				if(args.length == 3)
				{
					if(!MiscUtil.isValidURL(args[2]))
					{
						MiscUtil.messageSend(p, "You must provide a valid URL as proof.");
						return false;
					}
					MiscUtil.messageSend(p, "Proof URL: " + MiscUtil.chatColor(p, ChatColor.GOLD) + URLShortenUtil.convertURL(args[2]));
					MiscUtil.messageSend(p, MiscUtil.chatColor(p, ChatColor.GOLD) + "Success! " + MiscUtil.chatColor(p, ChatColor.WHITE) + "The level 5 infraction has been recieved.");
					MiscUtil.setScore(MiscUtil.getInfractionsPlayer(args[0]), 5 + MiscUtil.getScore(MiscUtil.getInfractionsPlayer(args[0])));
					MiscUtil.addInfraction(MiscUtil.getInfractionsPlayer(args[0]), 5, id, args[1], URLShortenUtil.convertURL(args[2]));
					MiscUtil.kickNotify(MiscUtil.getInfractionsPlayer(args[0]), args[1]);
					return true;
				}
				MiscUtil.messageSend(p, MiscUtil.chatColor(p, ChatColor.GOLD) + "Success! " + MiscUtil.chatColor(p, ChatColor.WHITE) + "The level 5 infraction has been recieved.");
				MiscUtil.setScore(MiscUtil.getInfractionsPlayer(args[0]), 5 + MiscUtil.getScore(MiscUtil.getInfractionsPlayer(args[0])));
				MiscUtil.addInfraction(MiscUtil.getInfractionsPlayer(args[0]), 5, id, args[1], "No proof.");
				MiscUtil.kickNotify(MiscUtil.getInfractionsPlayer(args[0]), args[1]);
				return true;
			}
			else if(VirtueUtil.getVirtues().contains(args[1]))
			{
				if(args.length == 3)
				{
					if(!MiscUtil.isValidURL(args[2]))
					{
						MiscUtil.messageSend(p, "You must provide a valid URL as proof.");
						return false;
					}
					MiscUtil.messageSend(p, "Proof URL: " + MiscUtil.chatColor(p, ChatColor.GOLD) + URLShortenUtil.convertURL(args[2]));
					MiscUtil.messageSend(p, MiscUtil.chatColor(p, ChatColor.GOLD) + "Success! " + MiscUtil.chatColor(p, ChatColor.WHITE) + "The virtue has been recieved.");
					MiscUtil.setScore(MiscUtil.getInfractionsPlayer(args[0]), SettingUtil.getSettingInt("virtue_value") + MiscUtil.getScore(MiscUtil.getInfractionsPlayer(args[0])));
					MiscUtil.addInfraction(MiscUtil.getInfractionsPlayer(args[0]), SettingUtil.getSettingInt("virtue_value"), id, args[1], URLShortenUtil.convertURL(args[2]));
					MiscUtil.kickNotify(MiscUtil.getInfractionsPlayer(args[0]), args[1]);
					return true;
				}
				MiscUtil.messageSend(p, MiscUtil.chatColor(p, ChatColor.GOLD) + "Success! " + MiscUtil.chatColor(p, ChatColor.WHITE) + "The virtue has been recieved.");
				MiscUtil.setScore(MiscUtil.getInfractionsPlayer(args[0]), SettingUtil.getSettingInt("virtue_value") + MiscUtil.getScore(MiscUtil.getInfractionsPlayer(args[0])));
				MiscUtil.addInfraction(MiscUtil.getInfractionsPlayer(args[0]), SettingUtil.getSettingInt("virtue_value"), id, args[1], "No proof.");
				MiscUtil.kickNotify(MiscUtil.getInfractionsPlayer(args[0]), args[1]);
				return true;
			}
			else
			{
				MiscUtil.messageSend(p, MiscUtil.chatColor(p, ChatColor.YELLOW) + "Not a valid infraction type.");
			}
			return true;
		}
		else if(c.getName().equalsIgnoreCase("uncite"))
		{
			if(!(args.length == 2))
			{
				MiscUtil.messageSend(p, "Not enough arguments.");
				return false;
			}
			if(!MiscUtil.hasPermissionOrOP(p, "infractions.mod"))
			{
				MiscUtil.messageSend(p, "You do not have enough permissions.");
				return true;
			}
			HashMap<String, String> infractions = MiscUtil.getInfractions(MiscUtil.getInfractionsPlayer(args[0]));
			Set<String> set = infractions.keySet();
			Collection<String> coll = infractions.values();
			Iterator<String> iterKey = set.iterator();
			Iterator<String> iterValue = coll.iterator();
			while(iterKey.hasNext())
			{
				Object oK = iterKey.next();
				Object oV = iterValue.next();
				String key = oK.toString();
				String earlyValue = oV.toString();
				if(key.contains(args[1]))
				{

					String[] preValue = earlyValue.split(":");
					String value = preValue[0];

					MiscUtil.removeInfraction(MiscUtil.getInfractionsPlayer(args[0]), args[1]);

					MiscUtil.setScore(MiscUtil.getInfractionsPlayer(args[0]), MiscUtil.getScore(MiscUtil.getInfractionsPlayer(args[0])) - Integer.parseInt(value));
					MiscUtil.messageSend(p, "Removed!");
					try
					{
						MiscUtil.checkScore(MiscUtil.getOnlinePlayer(MiscUtil.getInfractionsPlayer(args[0])));
					}
					catch(NullPointerException e)
					{
						// player is offline
					}
					return true;
				}
			}
			MiscUtil.messageSend(p, "No such infraction.");
			return true;
		}
		else if(c.getName().equalsIgnoreCase("history"))
		{
			if(!(args.length == 1) && !(p == null))
			{
				MiscUtil.messageSend(p, MiscUtil.chatColor(p, ChatColor.WHITE) + "--- " + MiscUtil.chatColor(p, ChatColor.YELLOW) + MiscUtil.getInfractionsPlayer(p.getName()) + MiscUtil.chatColor(p, ChatColor.WHITE) + " - " + MiscUtil.getScore(MiscUtil.getInfractionsPlayer(p.getName())) + "/" + MiscUtil.getMaxScore(p) + " ---");
				try
				{
					HashMap<String, String> infractions = MiscUtil.getInfractions(MiscUtil.getInfractionsPlayer(p.getName()));
					Set<String> set = infractions.keySet();
					Collection<String> coll = infractions.values();
					Iterator<String> iterKey = set.iterator();
					Iterator<String> iterValue = coll.iterator();
					while(iterKey.hasNext())
					{
						Object oK = iterKey.next();
						Object oV = iterValue.next();
						String key = oK.toString();
						int lineKey = (40 - key.length());
						int trimFront;
						if(oV.toString().startsWith("-")) trimFront = 1 + String.valueOf(SettingUtil.getSettingInt("virtue_value")).length();
						else trimFront = 2;
						MiscUtil.messageSend(p, oV.toString().substring(trimFront));
						if(MiscUtil.hasPermissionOrOP(p, "infractions.mod"))
						{
							key += " " + MiscUtil.chatColor(p, ChatColor.WHITE) + Strings.repeat("-", lineKey);
							MiscUtil.messageSend(p, "Key: " + MiscUtil.chatColor(p, ChatColor.GOLD) + key);
						}
					}
					return true;
				}
				catch(NullPointerException e)
				{
					return true;
				}
			}
			if((p == null) && !(args.length == 1))
			{
				log.info("[Infractions] You must provide a username in the console.");
				return false;
			}
			if(MiscUtil.hasPermissionOrOP(p, "infractions.mod"))
			{
				/**
				 * DISPLAY ALL CURRENT INFRACTIONS
				 */
				try
				{
					MiscUtil.messageSend(p, MiscUtil.chatColor(p, ChatColor.WHITE) + "--- " + MiscUtil.chatColor(p, ChatColor.YELLOW) + MiscUtil.getInfractionsPlayer(args[0]) + MiscUtil.chatColor(p, ChatColor.WHITE) + " - " + MiscUtil.getScore(MiscUtil.getInfractionsPlayer(args[0])) + "/" + MiscUtil.getMaxScore(MiscUtil.getOnlinePlayer(MiscUtil.getInfractionsPlayer(args[0]))) + " ---");
				}
				catch(NullPointerException e)
				{
					MiscUtil.messageSend(p, MiscUtil.chatColor(p, ChatColor.WHITE) + "--- " + MiscUtil.chatColor(p, ChatColor.YELLOW) + MiscUtil.getInfractionsPlayer(args[0]) + MiscUtil.chatColor(p, ChatColor.WHITE) + " - " + MiscUtil.getScore(MiscUtil.getInfractionsPlayer(args[0])) + " ---");
				}
				try
				{
					HashMap<String, String> infractions = MiscUtil.getInfractions(MiscUtil.getInfractionsPlayer(args[0]));
					Set<String> set = infractions.keySet();
					Collection<String> coll = infractions.values();
					Iterator<String> iterKey = set.iterator();
					Iterator<String> iterValue = coll.iterator();
					while(iterKey.hasNext())
					{
						Object oK = iterKey.next();
						Object oV = iterValue.next();
						String key = oK.toString();
						int lineKey = (40 - key.length());
						int trimFront;
						if(oV.toString().startsWith("-")) trimFront = 1 + String.valueOf(SettingUtil.getSettingInt("virtue_value")).length();
						else trimFront = 2;
						MiscUtil.messageSend(p, oV.toString().substring(trimFront));
						if(MiscUtil.hasPermissionOrOP(p, "infractions.mod"))
						{
							key += " " + MiscUtil.chatColor(p, ChatColor.WHITE) + Strings.repeat("-", lineKey);
							MiscUtil.messageSend(p, "Key: " + MiscUtil.chatColor(p, ChatColor.GOLD) + key);
						}
					}
					return true;
				}
				catch(NullPointerException e)
				{
					return true;
				}
			}
		}
		MiscUtil.messageSend(p, "Something went wrong, please try again.");
		return false;
	}
}
