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
import com.censoredsoftware.infractions.bukkit.dossier.Dossier;
import com.censoredsoftware.infractions.bukkit.legacy.data.ServerData;
import com.censoredsoftware.infractions.bukkit.legacy.util.MiscUtil;
import com.censoredsoftware.infractions.bukkit.legacy.util.SettingUtil;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.Set;

public class PlayerListener implements Listener
{
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e)
	{
		// sync to master file
		final Player p = e.getPlayer();
		Dossier dossier = Infractions.getCompleteDossier(p);
		if(SettingUtil.getSettingBoolean("motd"))
		{
			// TODO Consolidate this into some sort of useful notification system.
			p.sendMessage("This server is policed with infractions.");
		}
		if(p.hasPermission("infractions.ignore"))
		{

			Set<Infraction> infractions = dossier.getInfractions();
			if(!infractions.isEmpty())
				for(Infraction infraction : infractions)
					dossier.acquit(infraction);
			return;
		}
		if(ServerData.exists(p.getName(), "NEWINFRACTION"))
		{
			if((Boolean) ServerData.get(p.getName(), "NEWINFRACTION"))
			{
				p.sendMessage(ChatColor.RED + "You have a new infraction!" + ChatColor.WHITE + " Use " + ChatColor.YELLOW + "/history" + ChatColor.WHITE + " for more information.");
				ServerData.remove(p.getName(), "NEWINFRACTION");
			}
		}
		if(ServerData.exists(p.getName(), "NEWVIRTUE"))
		{
			if((Boolean) ServerData.get(p.getName(), "NEWVIRTUE"))
			{
				p.sendMessage(ChatColor.RED + "You have a new virtue!" + ChatColor.WHITE + " Use " + ChatColor.YELLOW + "/history" + ChatColor.WHITE + " for more information.");
				ServerData.remove(p.getName(), "NEWVIRTUE");
			}
		}

		ServerData.put(p.getName(), "LASTLOGINTIME", System.currentTimeMillis());
		MiscUtil.checkScore(p);
	}
}
