package com.censoredsoftware.Infractions;

import com.google.common.base.Strings;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.logging.Logger;

public class CommandManager implements CommandExecutor
{

	static Infractions plugin;
	static Logger log = Logger.getLogger("Minecraft");
	static Date date = new Date();

	public CommandManager(Infractions i)
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
				Util.messageSend(p, "---------------");
				Util.messageSend(p, "INFRACTIONS HELP");
				Util.messageSend(p, "---------------");
				if(Util.hasPermissionOrOP(p, "infractions.mod"))
				{
					Util.messageSend(p, Util.chatColor(p, ChatColor.GRAY) + "/cite <player> <infraction> [proof-url]");
					Util.messageSend(p, Util.chatColor(p, ChatColor.GRAY) + "/uncite <player> <key>" + Util.chatColor(p, ChatColor.WHITE) + " - Find the key with " + Util.chatColor(p, ChatColor.YELLOW) + "/history" + Util.chatColor(p, ChatColor.WHITE) + ".");
				}
				Util.messageSend(p, Util.chatColor(p, ChatColor.GRAY) + "/history [player]");
				Util.messageSend(p, Util.chatColor(p, ChatColor.GRAY) + "/infractions types " + Util.chatColor(p, ChatColor.WHITE) + "- Shows all valid infraction types.");
				Util.messageSend(p, Util.chatColor(p, ChatColor.GRAY) + "/virtues " + Util.chatColor(p, ChatColor.WHITE) + "- Shows all valid virtue types.");
				Util.messageSend(p, Util.chatColor(p, ChatColor.AQUA) + "Using the GNU " + Util.chatColor(p, ChatColor.DARK_AQUA) + "Affero" + Util.chatColor(p, ChatColor.AQUA) + " General Public License.");
				Util.messageSend(p, Util.chatColor(p, ChatColor.AQUA) + "Read the AGPL at " + Util.chatColor(p, ChatColor.YELLOW) + "http://bit.ly/TLY1xB");
				Util.messageSend(p, Util.chatColor(p, ChatColor.AQUA) + "Source: " + Util.chatColor(p, ChatColor.YELLOW) + "https://github.com/Clashnia/Infractions");
				return true;
			}
			else if(args[0].equalsIgnoreCase("types"))
			{
				Util.messageSend(p, "----------------");
				Util.messageSend(p, "INFRACTIONS TYPES");
				Util.messageSend(p, "----------------");
				Util.messageSend(p, Util.chatColor(p, ChatColor.GREEN) + "Level 1:");
				for(int i = 0; i < Levels.getLevel1().size(); i++)
				{
					Util.messageSend(p, Levels.getLevel1().get(i));
				}
				Util.messageSend(p, Util.chatColor(p, ChatColor.YELLOW) + "Level 2:");
				for(int i = 0; i < Levels.getLevel2().size(); i++)
				{
					Util.messageSend(p, Levels.getLevel2().get(i));
				}
				Util.messageSend(p, Util.chatColor(p, ChatColor.GOLD) + "Level 3:");
				for(int i = 0; i < Levels.getLevel3().size(); i++)
				{
					Util.messageSend(p, Levels.getLevel3().get(i));
				}
				Util.messageSend(p, Util.chatColor(p, ChatColor.RED) + "Level 4:");
				for(int i = 0; i < Levels.getLevel4().size(); i++)
				{
					Util.messageSend(p, Levels.getLevel4().get(i));
				}
				Util.messageSend(p, Util.chatColor(p, ChatColor.DARK_RED) + "Level 5:");
				for(int i = 0; i < Levels.getLevel5().size(); i++)
				{
					Util.messageSend(p, Levels.getLevel5().get(i));
				}
				return true;
			}
			else
			{
				Util.messageSend(p, "Too many arguments.");
				return false;
			}
		}
		else if(c.getName().equalsIgnoreCase("virtues"))
		{
			Util.messageSend(p, "----------------");
			Util.messageSend(p, "VIRTUES TYPES");
			Util.messageSend(p, "----------------");
			for(int i = 0; i < Virtues.getVirtues().size(); i++)
			{
				Util.messageSend(p, Virtues.getVirtues().get(i));
			}
			return true;
		}
		else if(c.getName().equalsIgnoreCase("cite"))
		{
			if(!Util.hasPermissionOrOP(p, "infractions.mod"))
			{
				Util.messageSend(p, "You do not have enough permissions.");
				return true;
			}
			if(Settings.getSettingBoolean("require_proof"))
			{
				if((args.length != 3))
				{
					Util.messageSend(p, "You must provide a valid URL as proof.");
					return false;
				}
				if(!Util.isValidURL(args[2]))
				{
					Util.messageSend(p, "You must provide a valid URL as proof.");
					return false;
				}
			}
			if(args.length == 0 || args.length == 1)
			{
				Util.messageSend(p, "Not enough arguments.");
				return false;
			}

			if(Util.getInfractionsPlayer(args[0]) == null)
			{
				Util.messageSend(p, "This player hasn't joined yet.");
				return true;
			}
			Random generator = new Random();
			int id = generator.nextInt();
			// Level 1
			if(Levels.getLevel1().contains(args[1]))
			{
				if(args.length == 3)
				{
					if(!Util.isValidURL(args[2]))
					{
						Util.messageSend(p, "You must provide a valid URL as proof.");
						return false;
					}
					Util.messageSend(p, "Proof URL: " + Util.chatColor(p, ChatColor.GOLD) + URLShortenUtil.convertURL(args[2]));
					Util.messageSend(p, Util.chatColor(p, ChatColor.GOLD) + "Success! " + Util.chatColor(p, ChatColor.WHITE) + "The level 1 infraction has been recieved.");
					Util.setScore(Util.getInfractionsPlayer(args[0]), 1 + Util.getScore(Util.getInfractionsPlayer(args[0])));
					Util.addInfraction(Util.getInfractionsPlayer(args[0]), 1, id, args[1], URLShortenUtil.convertURL(args[2]));
					Util.kickNotify(Util.getInfractionsPlayer(args[0]), args[1]);
					return true;
				}
				Util.messageSend(p, Util.chatColor(p, ChatColor.GOLD) + "Success! " + Util.chatColor(p, ChatColor.WHITE) + "The level 1 infraction has been recieved.");
				Util.setScore(Util.getInfractionsPlayer(args[0]), 1 + Util.getScore(Util.getInfractionsPlayer(args[0])));
				Util.addInfraction(Util.getInfractionsPlayer(args[0]), 1, id, args[1], "No proof.");
				Util.kickNotify(Util.getInfractionsPlayer(args[0]), args[1]);
				return true;
			}
			else if(Levels.getLevel2().contains(args[1]))
			{
				if(args.length == 3)
				{
					if(!Util.isValidURL(args[2]))
					{
						Util.messageSend(p, "You must provide a valid URL as proof.");
						return false;
					}
					Util.messageSend(p, "Proof URL: " + Util.chatColor(p, ChatColor.GOLD) + URLShortenUtil.convertURL(args[2]));
					Util.messageSend(p, Util.chatColor(p, ChatColor.GOLD) + "Success! " + Util.chatColor(p, ChatColor.WHITE) + "The level 2 infraction has been recieved.");
					Util.setScore(Util.getInfractionsPlayer(args[0]), 2 + Util.getScore(Util.getInfractionsPlayer(args[0])));
					Util.addInfraction(Util.getInfractionsPlayer(args[0]), 2, id, args[1], URLShortenUtil.convertURL(args[2]));
					Util.kickNotify(Util.getInfractionsPlayer(args[0]), args[1]);
					return true;
				}
				Util.messageSend(p, Util.chatColor(p, ChatColor.GOLD) + "Success! " + Util.chatColor(p, ChatColor.WHITE) + "The level 2 infraction has been recieved.");
				Util.setScore(Util.getInfractionsPlayer(args[0]), 2 + Util.getScore(Util.getInfractionsPlayer(args[0])));
				Util.addInfraction(Util.getInfractionsPlayer(args[0]), 2, id, args[1], "No proof.");
				Util.kickNotify(Util.getInfractionsPlayer(args[0]), args[1]);
				return true;
			}
			else if(Levels.getLevel3().contains(args[1]))
			{
				if(args.length == 3)
				{
					if(!Util.isValidURL(args[2]))
					{
						Util.messageSend(p, "You must provide a valid URL as proof.");
						return false;
					}
					Util.messageSend(p, "Proof URL: " + Util.chatColor(p, ChatColor.GOLD) + URLShortenUtil.convertURL(args[2]));
					Util.messageSend(p, Util.chatColor(p, ChatColor.GOLD) + "Success! " + Util.chatColor(p, ChatColor.WHITE) + "The level 3 infraction has been recieved.");
					Util.setScore(Util.getInfractionsPlayer(args[0]), 3 + Util.getScore(Util.getInfractionsPlayer(args[0])));
					Util.addInfraction(Util.getInfractionsPlayer(args[0]), 3, id, args[1], URLShortenUtil.convertURL(args[2]));
					Util.kickNotify(Util.getInfractionsPlayer(args[0]), args[1]);
					return true;
				}
				Util.messageSend(p, Util.chatColor(p, ChatColor.GOLD) + "Success! " + Util.chatColor(p, ChatColor.WHITE) + "The level 3 infraction has been recieved.");
				Util.setScore(Util.getInfractionsPlayer(args[0]), 3 + Util.getScore(Util.getInfractionsPlayer(args[0])));
				Util.addInfraction(Util.getInfractionsPlayer(args[0]), 3, id, args[1], "No proof.");
				Util.kickNotify(Util.getInfractionsPlayer(args[0]), args[1]);
				return true;
			}
			else if(Levels.getLevel4().contains(args[1]))
			{
				if(args.length == 3)
				{
					if(!Util.isValidURL(args[2]))
					{
						Util.messageSend(p, "You must provide a valid URL as proof.");
						return false;
					}
					Util.messageSend(p, "Proof URL: " + Util.chatColor(p, ChatColor.GOLD) + URLShortenUtil.convertURL(args[2]));
					Util.messageSend(p, Util.chatColor(p, ChatColor.GOLD) + "Success! " + Util.chatColor(p, ChatColor.WHITE) + "The level 4 infraction has been recieved.");
					Util.setScore(Util.getInfractionsPlayer(args[0]), 4 + Util.getScore(Util.getInfractionsPlayer(args[0])));
					Util.addInfraction(Util.getInfractionsPlayer(args[0]), 4, id, args[1], URLShortenUtil.convertURL(args[2]));
					Util.kickNotify(Util.getInfractionsPlayer(args[0]), args[1]);
					return true;
				}
				Util.messageSend(p, Util.chatColor(p, ChatColor.GOLD) + "Success! " + Util.chatColor(p, ChatColor.WHITE) + "The level 4 infraction has been recieved.");
				Util.setScore(Util.getInfractionsPlayer(args[0]), 4 + Util.getScore(Util.getInfractionsPlayer(args[0])));
				Util.addInfraction(Util.getInfractionsPlayer(args[0]), 4, id, args[1], "No proof.");
				Util.kickNotify(Util.getInfractionsPlayer(args[0]), args[1]);
				return true;
			}
			else if(Levels.getLevel5().contains(args[1]))
			{
				if(args.length == 3)
				{
					if(!Util.isValidURL(args[2]))
					{
						Util.messageSend(p, "You must provide a valid URL as proof.");
						return false;
					}
					Util.messageSend(p, "Proof URL: " + Util.chatColor(p, ChatColor.GOLD) + URLShortenUtil.convertURL(args[2]));
					Util.messageSend(p, Util.chatColor(p, ChatColor.GOLD) + "Success! " + Util.chatColor(p, ChatColor.WHITE) + "The level 5 infraction has been recieved.");
					Util.setScore(Util.getInfractionsPlayer(args[0]), 5 + Util.getScore(Util.getInfractionsPlayer(args[0])));
					Util.addInfraction(Util.getInfractionsPlayer(args[0]), 5, id, args[1], URLShortenUtil.convertURL(args[2]));
					Util.kickNotify(Util.getInfractionsPlayer(args[0]), args[1]);
					return true;
				}
				Util.messageSend(p, Util.chatColor(p, ChatColor.GOLD) + "Success! " + Util.chatColor(p, ChatColor.WHITE) + "The level 5 infraction has been recieved.");
				Util.setScore(Util.getInfractionsPlayer(args[0]), 5 + Util.getScore(Util.getInfractionsPlayer(args[0])));
				Util.addInfraction(Util.getInfractionsPlayer(args[0]), 5, id, args[1], "No proof.");
				Util.kickNotify(Util.getInfractionsPlayer(args[0]), args[1]);
				return true;
			}
			else if(Virtues.getVirtues().contains(args[1]))
			{
				if(args.length == 3)
				{
					if(!Util.isValidURL(args[2]))
					{
						Util.messageSend(p, "You must provide a valid URL as proof.");
						return false;
					}
					Util.messageSend(p, "Proof URL: " + Util.chatColor(p, ChatColor.GOLD) + URLShortenUtil.convertURL(args[2]));
					Util.messageSend(p, Util.chatColor(p, ChatColor.GOLD) + "Success! " + Util.chatColor(p, ChatColor.WHITE) + "The virtue has been recieved.");
					Util.setScore(Util.getInfractionsPlayer(args[0]), Settings.getSettingInt("virtue_value") + Util.getScore(Util.getInfractionsPlayer(args[0])));
					Util.addInfraction(Util.getInfractionsPlayer(args[0]), Settings.getSettingInt("virtue_value"), id, args[1], URLShortenUtil.convertURL(args[2]));
					Util.kickNotify(Util.getInfractionsPlayer(args[0]), args[1]);
					return true;
				}
				Util.messageSend(p, Util.chatColor(p, ChatColor.GOLD) + "Success! " + Util.chatColor(p, ChatColor.WHITE) + "The virtue has been recieved.");
				Util.setScore(Util.getInfractionsPlayer(args[0]), Settings.getSettingInt("virtue_value") + Util.getScore(Util.getInfractionsPlayer(args[0])));
				Util.addInfraction(Util.getInfractionsPlayer(args[0]), Settings.getSettingInt("virtue_value"), id, args[1], "No proof.");
				Util.kickNotify(Util.getInfractionsPlayer(args[0]), args[1]);
				return true;
			}
			else
			{
				Util.messageSend(p, Util.chatColor(p, ChatColor.YELLOW) + "Not a valid infraction type.");
			}
			return true;
		}
		else if(c.getName().equalsIgnoreCase("uncite"))
		{
			if(!(args.length == 2))
			{
				Util.messageSend(p, "Not enough arguments.");
				return false;
			}
			if(!Util.hasPermissionOrOP(p, "infractions.mod"))
			{
				Util.messageSend(p, "You do not have enough permissions.");
				return true;
			}
			HashMap<String, String> infractions = Util.getInfractions(Util.getInfractionsPlayer(args[0]));
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

					Util.removeInfraction(Util.getInfractionsPlayer(args[0]), args[1]);

					Util.setScore(Util.getInfractionsPlayer(args[0]), Util.getScore(Util.getInfractionsPlayer(args[0])) - Integer.parseInt(value));
					Util.messageSend(p, "Removed!");
					try
					{
						Util.checkScore(Util.getOnlinePlayer(Util.getInfractionsPlayer(args[0])));
					}
					catch(NullPointerException e)
					{
						// player is offline
					}
					return true;
				}
			}
			Util.messageSend(p, "No such infraction.");
			return true;
		}
		else if(c.getName().equalsIgnoreCase("history"))
		{
			if(!(args.length == 1) && !(p == null))
			{
				Util.messageSend(p, Util.chatColor(p, ChatColor.WHITE) + "--- " + Util.chatColor(p, ChatColor.YELLOW) + Util.getInfractionsPlayer(p.getName()) + Util.chatColor(p, ChatColor.WHITE) + " - " + Util.getScore(Util.getInfractionsPlayer(p.getName())) + "/" + Util.getMaxScore(p) + " ---");
				try
				{
					HashMap<String, String> infractions = Util.getInfractions(Util.getInfractionsPlayer(p.getName()));
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
						if(oV.toString().startsWith("-")) trimFront = 1 + String.valueOf(Settings.getSettingInt("virtue_value")).length();
						else trimFront = 2;
						Util.messageSend(p, oV.toString().substring(trimFront));
						if(Util.hasPermissionOrOP(p, "infractions.mod"))
						{
							key += " " + Util.chatColor(p, ChatColor.WHITE) + Strings.repeat("-", lineKey);
							Util.messageSend(p, "Key: " + Util.chatColor(p, ChatColor.GOLD) + key);
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
			if(Util.hasPermissionOrOP(p, "infractions.mod"))
			{
				/**
				 * DISPLAY ALL CURRENT INFRACTIONS
				 */
				try
				{
					Util.messageSend(p, Util.chatColor(p, ChatColor.WHITE) + "--- " + Util.chatColor(p, ChatColor.YELLOW) + Util.getInfractionsPlayer(args[0]) + Util.chatColor(p, ChatColor.WHITE) + " - " + Util.getScore(Util.getInfractionsPlayer(args[0])) + "/" + Util.getMaxScore(Util.getOnlinePlayer(Util.getInfractionsPlayer(args[0]))) + " ---");
				}
				catch(NullPointerException e)
				{
					Util.messageSend(p, Util.chatColor(p, ChatColor.WHITE) + "--- " + Util.chatColor(p, ChatColor.YELLOW) + Util.getInfractionsPlayer(args[0]) + Util.chatColor(p, ChatColor.WHITE) + " - " + Util.getScore(Util.getInfractionsPlayer(args[0])) + " ---");
				}
				try
				{
					HashMap<String, String> infractions = Util.getInfractions(Util.getInfractionsPlayer(args[0]));
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
						if(oV.toString().startsWith("-")) trimFront = 1 + String.valueOf(Settings.getSettingInt("virtue_value")).length();
						else trimFront = 2;
						Util.messageSend(p, oV.toString().substring(trimFront));
						if(Util.hasPermissionOrOP(p, "infractions.mod"))
						{
							key += " " + Util.chatColor(p, ChatColor.WHITE) + Strings.repeat("-", lineKey);
							Util.messageSend(p, "Key: " + Util.chatColor(p, ChatColor.GOLD) + key);
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
		Util.messageSend(p, "Something went wrong, please try again.");
		return false;
	}
}
