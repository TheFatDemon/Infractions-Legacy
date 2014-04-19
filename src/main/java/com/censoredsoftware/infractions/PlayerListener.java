package com.censoredsoftware.infractions;

import com.censoredsoftware.infractions.util.MiscUtil;
import com.censoredsoftware.infractions.util.SaveUtil;
import com.censoredsoftware.infractions.util.SettingUtil;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.logging.Logger;

public class PlayerListener implements Listener
{
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e)
	{ // sync to master file
		final Player p = e.getPlayer();
		if(SettingUtil.getSettingBoolean("motd")) p.sendMessage("This server is policed with infractions.");
		if(p.hasPermission("infractions.ignore"))
		{
			if(MiscUtil.getInfractionsList(p.getName()) != null)
				for(String infractionID : MiscUtil.getInfractionsList(p.getName()))
					MiscUtil.removeInfraction(p.getName(), infractionID);
			return;
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
