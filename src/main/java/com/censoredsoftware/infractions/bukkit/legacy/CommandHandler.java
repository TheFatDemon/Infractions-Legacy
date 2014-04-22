package com.censoredsoftware.infractions.bukkit.legacy;

import com.censoredsoftware.infractions.bukkit.Infraction;
import com.censoredsoftware.infractions.bukkit.Infractions;
import com.censoredsoftware.infractions.bukkit.evidence.Evidence;
import com.censoredsoftware.infractions.bukkit.issuer.Issuer;
import com.censoredsoftware.infractions.bukkit.legacy.util.*;
import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.Iterables;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Date;
import java.util.UUID;
import java.util.logging.Logger;

public class CommandHandler implements CommandExecutor
{

	static InfractionsPlugin plugin;
	static Logger log = Logger.getLogger("Minecraft");
	static Date date = new Date();

	public CommandHandler(InfractionsPlugin i)
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

			if(Infractions.getCompleteDossier(args[0]) == null)
			{
				MiscUtil.messageSend(p, "This player hasn't joined yet.");
				return true;
			}
			// Levels
			Integer level = LevelUtil.getLevel(args[1]);
			if(level != null)
			{
				if(args.length == 3)
				{
					if(!MiscUtil.isValidURL(args[2]))
					{
						MiscUtil.messageSend(p, "You must provide a valid URL as proof.");
						return false;
					}
					MiscUtil.messageSend(p, "Proof URL: " + MiscUtil.chatColor(p, ChatColor.GOLD) + URLShortenUtil.convertURL(args[2]));
					MiscUtil.addInfraction(MiscUtil.getInfractionsPlayer(args[0]), sender, level, args[1], URLShortenUtil.convertURL(args[2]));
				}
				else
				{
					MiscUtil.addInfraction(MiscUtil.getInfractionsPlayer(args[0]), sender, level, args[1], "No proof.");
				}
				MiscUtil.messageSend(p, MiscUtil.chatColor(p, ChatColor.GOLD) + "Success! " + MiscUtil.chatColor(p, ChatColor.WHITE) + "The level " + level + " infraction has been recieved.");
				MiscUtil.kickNotify(MiscUtil.getInfractionsPlayer(args[0]), args[1]);
				return true;
			}
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

			if(MiscUtil.removeInfraction(MiscUtil.getInfractionsPlayer(args[0]), args[1]))
			{
				MiscUtil.messageSend(p, "Removed!");
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
			MiscUtil.messageSend(p, "No such infraction.");
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

				p.performCommand("/history " + p.getName());
				return true;
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
					MiscUtil.messageSend(p, MiscUtil.chatColor(p, ChatColor.WHITE) + "--- " + MiscUtil.chatColor(p, ChatColor.YELLOW) + MiscUtil.getInfractionsPlayer(args[0]) + MiscUtil.chatColor(p, ChatColor.WHITE) + " - " + MiscUtil.getScore(MiscUtil.getInfractionsPlayer(args[0])) + "/" + MiscUtil.getMaxScore(Bukkit.getPlayer(MiscUtil.getInfractionsPlayer(args[0]))) + " ---");
				}
				catch(NullPointerException e)
				{
					MiscUtil.messageSend(p, MiscUtil.chatColor(p, ChatColor.WHITE) + "--- " + MiscUtil.chatColor(p, ChatColor.YELLOW) + MiscUtil.getInfractionsPlayer(args[0]) + MiscUtil.chatColor(p, ChatColor.WHITE) + " - " + MiscUtil.getScore(MiscUtil.getInfractionsPlayer(args[0])) + " ---");
				}
				try
				{
					for(Infraction infraction : Infractions.getCompleteDossier(MiscUtil.getInfractionsPlayer(args[0])).getInfractions())
					{
						MiscUtil.messageSend(p, infraction.getScore() + " - " + infraction.getReason() + " - " + infraction.getDateCreated());
						MiscUtil.messageSend(p, " -- Proof: " + MiscUtil.chatColor(p, ChatColor.GRAY) + Iterables.getFirst(Collections2.transform(infraction.getEvidence(), new Function<Evidence, Object>()
						{
							@Override
							public String apply(Evidence evidence)
							{
								return evidence.getRawData();
							}
						}), "No Proof."));
						if(MiscUtil.hasPermissionOrOP(p, "infractions.mod"))
						{
							String id = MiscUtil.getId(infraction);
							MiscUtil.messageSend(p, " -- Key: " + MiscUtil.chatColor(p, ChatColor.GOLD) + id);

							Issuer issuer = infraction.getIssuer();
							String issuerId;
							try
							{
								issuerId = Infractions.getCompleteDossier(UUID.fromString(issuer.getId())).getLastKnownName();
							}
							catch(Exception oops)
							{
								issuerId = issuer.getId();
							}

							MiscUtil.messageSend(p, " -- Issuer: " + MiscUtil.chatColor(p, ChatColor.GREEN) + issuerId);
							MiscUtil.messageSend(p, " -- Origin: " + MiscUtil.chatColor(p, ChatColor.AQUA) + infraction.getOrigin().getName());
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
		MiscUtil.messageSend(p, "Something went wrong, please try again.");
		return false;
	}
}
