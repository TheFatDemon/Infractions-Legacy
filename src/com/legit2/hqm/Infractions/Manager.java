package com.legit2.hqm.Infractions;

import java.util.logging.Logger;

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
		if (!Save.hasPlayer(p)) {
			Logger.getLogger("Minecraft")
					.info("[Infractions] "
							+ p.getName()
							+ " joined and no save was detected. Creating new file.");
			Save.addPlayer(p);
		}
		Save.saveData(p, "LASTLOGINTIME", System.currentTimeMillis());
	}
}
