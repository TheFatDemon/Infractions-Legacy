package com.legit2.hqm.Infractions;

import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class Manager implements Listener {
	/*
	 * Distributes all events to deities
	 */
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e) { // sync to master file
		final Player p = e.getPlayer();
		if (Settings.getSettingBoolean("motd")) {
			p.sendMessage("This server is policed with infractions.");
		}
		if (Save.hasData(p, "NEWINFRACTION")) {
			if ((Boolean)Save.getData(p, "NEWINFRACTION")) {
				p.sendMessage(ChatColor.RED + "You have a new infraction!" + ChatColor.WHITE + " Use " + ChatColor.YELLOW + "/history" + ChatColor.WHITE + " for more information.");
				Save.saveData(p, "NEWINFRACTION", false);
			}
		}
		if (!Save.hasPlayer(p)) {
			Logger.getLogger("Minecraft")
					.info("[Infractions] "
							+ p.getName()
							+ " joined for the first time.");
			Save.addPlayer(p);
		}
		Save.saveData(p, "LASTLOGINTIME", System.currentTimeMillis());
		Util.checkScore(p);
	}
}
