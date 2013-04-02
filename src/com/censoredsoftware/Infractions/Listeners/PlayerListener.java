package com.censoredsoftware.Infractions.Listeners;

import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import com.censoredsoftware.Infractions.Handlers.UpdateHandler;
import com.censoredsoftware.Infractions.Utilities.MiscUtil;
import com.censoredsoftware.Infractions.Utilities.SaveUtil;
import com.censoredsoftware.Infractions.Utilities.SettingUtil;

public class PlayerListener implements Listener
{
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e)
	{ // sync to master file
		final Player p = e.getPlayer();
		if(SettingUtil.getSettingBoolean("motd"))
		{
			p.sendMessage("This server is policed with infractions.");

			if(SettingUtil.getSettingBoolean("update") || SettingUtil.getSettingBoolean("update_notify"))
			{
				if(SettingUtil.getSettingBoolean("update_notify") && UpdateHandler.shouldUpdate() && MiscUtil.hasPermissionOrOP(p, "infractions.mod"))
				{
					p.sendMessage(ChatColor.RED + "There is a new, stable release for Infractions.");
					if(!SettingUtil.getSettingBoolean("update"))
					{
						p.sendMessage(ChatColor.RED + "Please update ASAP.");
						p.sendMessage(ChatColor.RED + "Latest: " + ChatColor.GREEN + "dev.bukkit.org/server-mods/infractions");
					}
					else p.sendMessage(ChatColor.RED + "The plugin should update automatically on the next server reload.");
				}
			}
		}
		if(SaveUtil.hasData(p, "NEWINFRACTION"))
		{
			if((Boolean) SaveUtil.getData(p, "NEWINFRACTION"))
			{
				p.sendMessage(ChatColor.RED + "You have a new infraction!" + ChatColor.WHITE + " Use " + ChatColor.YELLOW + "/history" + ChatColor.WHITE + " for more information.");
				SaveUtil.saveData(p, "NEWINFRACTION", false);
			}
		}
		if(SaveUtil.hasData(p, "NEWVIRTUE"))
		{
			if((Boolean) SaveUtil.getData(p, "NEWVIRTUE"))
			{
				p.sendMessage(ChatColor.RED + "You have a new virtue!" + ChatColor.WHITE + " Use " + ChatColor.YELLOW + "/history" + ChatColor.WHITE + " for more information.");
				SaveUtil.saveData(p, "NEWVIRTUE", false);
			}
		}
		if(!SaveUtil.hasPlayer(p))
		{
			Logger.getLogger("Minecraft").info("[Infractions] " + p.getName() + " joined for the first time.");
			SaveUtil.addPlayer(p);
		}

		SaveUtil.saveData(p, "LASTLOGINTIME", System.currentTimeMillis());
		MiscUtil.checkScore(p);
	}
}
