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

package com.censoredsoftware.infractions.bukkit.legacy;

import com.censoredsoftware.infractions.bukkit.Infraction;
import com.censoredsoftware.infractions.bukkit.Infractions;
import com.censoredsoftware.infractions.bukkit.evidence.Evidence;
import com.censoredsoftware.infractions.bukkit.legacy.util.*;
import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.Iterables;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Set;
import java.util.logging.Logger;

public class CommandHandler implements CommandExecutor
{
	static Logger log = InfractionsPlugin.getInst().getLogger();

	@Override
	public boolean onCommand(CommandSender sender, Command c, String label, String[] args)
	{
		Player p = null;
		if(sender instanceof Player) p = (Player) sender;
		if(c.getName().equalsIgnoreCase("infractions"))
		{
			if(args.length == 0)
			{
				MiscUtil.sendMessage(p, "---------------");
				MiscUtil.sendMessage(p, "INFRACTIONS HELP");
				MiscUtil.sendMessage(p, "---------------");
				if(MiscUtil.hasPermissionOrOP(p, "infractions.mod"))
				{
					MiscUtil.sendMessage(p, ChatColor.GRAY + "/cite <player> <infraction> [proof-url]");
					MiscUtil.sendMessage(p, ChatColor.GRAY + "/uncite <player> <key>" + ChatColor.WHITE + " - Find the key with " + ChatColor.YELLOW + "/history" + ChatColor.WHITE + ".");
				}
				MiscUtil.sendMessage(p, ChatColor.GRAY + "/history [player]");
				MiscUtil.sendMessage(p, ChatColor.GRAY + "/infractions types " + ChatColor.WHITE + "- Shows all valid infraction types.");
				MiscUtil.sendMessage(p, ChatColor.GRAY + "/virtues " + ChatColor.WHITE + "- Shows all valid virtue types.");
				return true;
			}
			else if(args[0].equalsIgnoreCase("types"))
			{
				MiscUtil.sendMessage(p, "----------------");
				MiscUtil.sendMessage(p, "INFRACTIONS TYPES");
				MiscUtil.sendMessage(p, "----------------");
				MiscUtil.sendMessage(p, ChatColor.GREEN + "Level 1:");
				for(int i = 0; i < LevelUtil.getLevel1().size(); i++)
				{
					MiscUtil.sendMessage(p, LevelUtil.getLevel1().get(i));
				}
				MiscUtil.sendMessage(p, ChatColor.YELLOW + "Level 2:");
				for(int i = 0; i < LevelUtil.getLevel2().size(); i++)
				{
					MiscUtil.sendMessage(p, LevelUtil.getLevel2().get(i));
				}
				MiscUtil.sendMessage(p, ChatColor.GOLD + "Level 3:");
				for(int i = 0; i < LevelUtil.getLevel3().size(); i++)
				{
					MiscUtil.sendMessage(p, LevelUtil.getLevel3().get(i));
				}
				MiscUtil.sendMessage(p, ChatColor.RED + "Level 4:");
				for(int i = 0; i < LevelUtil.getLevel4().size(); i++)
				{
					MiscUtil.sendMessage(p, LevelUtil.getLevel4().get(i));
				}
				MiscUtil.sendMessage(p, ChatColor.DARK_RED + "Level 5:");
				for(int i = 0; i < LevelUtil.getLevel5().size(); i++)
				{
					MiscUtil.sendMessage(p, LevelUtil.getLevel5().get(i));
				}
				return true;
			}
			else
			{
				MiscUtil.sendMessage(p, "Too many arguments.");
				return false;
			}
		}
		else if(c.getName().equalsIgnoreCase("virtues"))
		{
			MiscUtil.sendMessage(p, "----------------");
			MiscUtil.sendMessage(p, "VIRTUES TYPES");
			MiscUtil.sendMessage(p, "----------------");
			for(int i = 0; i < VirtueUtil.getVirtues().size(); i++)
			{
				MiscUtil.sendMessage(p, VirtueUtil.getVirtues().get(i));
			}
			return true;
		}
		else if(c.getName().equalsIgnoreCase("cite"))
		{
			if(!MiscUtil.hasPermissionOrOP(p, "infractions.mod"))
			{
				MiscUtil.sendMessage(p, "You do not have enough permissions.");
				return true;
			}
			if(SettingUtil.getSettingBoolean("require_proof"))
			{
				if((args.length != 3))
				{
					MiscUtil.sendMessage(p, "You must provide a valid URL as proof.");
					return false;
				}
				if(!URLUtil.isValidURL(args[2]))
				{
					MiscUtil.sendMessage(p, "You must provide a valid URL as proof.");
					return false;
				}
			}
			if(args.length == 0 || args.length == 1)
			{
				MiscUtil.sendMessage(p, "Not enough arguments.");
				return false;
			}

			if(Infractions.getCompleteDossier(args[0]) == null)
			{
				MiscUtil.sendMessage(p, "This player hasn't joined yet.");
				return true;
			}
			// Levels
			Integer level = LevelUtil.getLevel(args[1]);
			if(level != null)
			{
				if(args.length == 3)
				{
					if(!URLUtil.isValidURL(args[2]))
					{
						MiscUtil.sendMessage(p, "You must provide a valid URL as proof.");
						return false;
					}
					MiscUtil.sendMessage(p, "Proof URL: " + ChatColor.GOLD + URLUtil.convertURL(args[2]));
					MiscUtil.addInfraction(MiscUtil.getInfractionsPlayer(args[0]), sender, level, args[1], URLUtil.convertURL(args[2]));
				}
				else
				{
					MiscUtil.addInfraction(MiscUtil.getInfractionsPlayer(args[0]), sender, level, args[1], "No proof.");
				}
				MiscUtil.sendMessage(p, ChatColor.GOLD + "Success! " + ChatColor.WHITE + "The level " + level + " infraction has been recieved.");
				MiscUtil.kickNotify(MiscUtil.getInfractionsPlayer(args[0]), args[1]);
				return true;
			}
		}
		else if(c.getName().equalsIgnoreCase("uncite"))
		{
			if(!(args.length == 2))
			{
				MiscUtil.sendMessage(p, "Not enough arguments.");
				return false;
			}
			if(!MiscUtil.hasPermissionOrOP(p, "infractions.mod"))
			{
				MiscUtil.sendMessage(p, "You do not have enough permissions.");
				return true;
			}

			if(MiscUtil.removeInfraction(MiscUtil.getInfractionsPlayer(args[0]), args[1]))
			{
				MiscUtil.sendMessage(p, "Removed!");
				try
				{
					MiscUtil.checkScore(MiscUtil.getInfractionsPlayer(args[0]));
				}
				catch(NullPointerException e)
				{
					// player is offline
				}
				return true;
			}
			MiscUtil.sendMessage(p, "No such infraction.");
			return true;
		}
		else if(c.getName().equalsIgnoreCase("history"))
		{
			if(!(args.length == 1) && !(p == null))
			{
				if(sender.hasPermission("infractions.ignore"))
				{
					sender.sendMessage(ChatColor.YELLOW + "Infractions does not track your history.");
					return true;
				}

				p.performCommand("history " + p.getName());
				return true;
			}
			if((p == null) && !(args.length == 1))
			{
				log.info("You must provide a username in the console.");
				return false;
			}

			if(!MiscUtil.hasPermissionOrOP(p, "infractions.mod") && !p.getName().toLowerCase().equals(args[0]))
			{
				p.sendMessage(ChatColor.RED + "You don't have permission to do that.");
				return true;
			}

			/**
			 * DISPLAY ALL CURRENT INFRACTIONS
			 */

			String player = MiscUtil.getInfractionsPlayer(args[0]);
			if(player != null)
			{
				Integer maxScore = MiscUtil.getMaxScore(Infractions.getCompleteDossier(player).getId());
				String chatLevel = InfractionsPlugin.getLevelForChat(player);
				MiscUtil.sendMessage(p, ChatColor.WHITE + (chatLevel.equals("") ? "" : chatLevel + " ") + ChatColor.YELLOW + player + ChatColor.WHITE + " - " + MiscUtil.getScore(player) + (maxScore == null ? " points towards a ban." : "points out of " + maxScore + " until a ban."));

				try
				{
					boolean staff = MiscUtil.hasPermissionOrOP(p, "infractions.mod");
					Set<Infraction> infractions = Infractions.getCompleteDossier(player).getInfractions();
					if(infractions.isEmpty())
					{
						MiscUtil.sendMessage(p, ChatColor.DARK_GREEN + "✔ " + ChatColor.WHITE + " No infractions found for this player.");
						return true;
					}
					for(Infraction infraction : infractions)
					{
						MiscUtil.sendMessage(p, ChatColor.DARK_RED + "✘ " + ChatColor.DARK_AQUA + StringUtils.capitalize(infraction.getReason()) + ChatColor.GRAY + " - " + ChatColor.WHITE + infraction.getDateCreated());
						MiscUtil.sendMessage(p, ChatColor.GRAY + "     Score: " + ChatColor.WHITE + infraction.getScore());
						MiscUtil.sendMessage(p, ChatColor.GRAY + "     Proof: " + ChatColor.WHITE + Iterables.getFirst(Collections2.transform(infraction.getEvidence(), new Function<Evidence, Object>()
						{
							@Override
							public String apply(Evidence evidence)
							{
								return evidence.getRawData();
							}
						}), "No Proof."));
						if(staff)
						{
							String id = MiscUtil.getInfractionId(infraction);
							MiscUtil.sendMessage(p, ChatColor.GRAY + "     Key: " + ChatColor.WHITE + id);
							MiscUtil.sendMessage(p, ChatColor.GRAY + "     Issuer: " + ChatColor.WHITE + infraction.getIssuer().getId());
						}
					}
					return true;
				}
				catch(NullPointerException e)
				{
					return true;
				}
			}
			else
			{
				MiscUtil.sendMessage(p, "No player found with that name.");
				return true;
			}
		}
		else if(c.getName().equalsIgnoreCase("clearhistory") && p != null && p.hasPermission("infractions.clearhistory") && args.length > 0)
		{
			try
			{
				Player remove = Bukkit.getServer().matchPlayer(args[0]).get(0);
				Infractions.removeDossier(remove.getUniqueId());
				remove.kickPlayer(ChatColor.GREEN + "Your Infractions history has been reset--please join again.");
				return true;
			}
			catch(Exception error)
			{
				sender.sendMessage(ChatColor.RED + "Could not find that player...");
				return false;
			}
		}
		MiscUtil.sendMessage(p, "Something went wrong, please try again.");
		return false;
	}
}
