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

import com.censoredsoftware.infractions.bukkit.Infractions;
import com.censoredsoftware.infractions.bukkit.dossier.CompleteDossier;
import com.censoredsoftware.infractions.bukkit.legacy.compat.LegacyCompleteDossier;
import com.censoredsoftware.infractions.bukkit.legacy.data.ServerData;
import com.censoredsoftware.infractions.bukkit.legacy.util.MiscUtil;
import com.censoredsoftware.infractions.bukkit.legacy.util.SettingUtil;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;

public class PlayerListener implements Listener {
    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerLogin(PlayerLoginEvent e) {
        final Player p = e.getPlayer();

        // Create data that we track
        CompleteDossier dossier = Infractions.getCompleteDossier(p);
        ((LegacyCompleteDossier) dossier).update(p);
        MiscUtil.getMaxScore(p);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerJoin(PlayerJoinEvent e) {
        final Player p = e.getPlayer();

        if (SettingUtil.getSettingBoolean("motd")) {
            // TODO Consolidate this into some sort of useful notification system.
            p.sendMessage(ChatColor.YELLOW + "⚠" + ChatColor.WHITE + " This server is policed with infractions.");
        }
        if (ServerData.exists(p.getName(), "NEWINFRACTION")) {
            if ((Boolean) ServerData.get(p.getName(), "NEWINFRACTION")) {
                p.sendMessage(ChatColor.YELLOW + "⚠" + ChatColor.RED + " You have a new infraction!" + ChatColor.WHITE + " Use " + ChatColor.YELLOW + "/history" + ChatColor.WHITE + " for more information.");
                ServerData.remove(p.getName(), "NEWINFRACTION");
            }
        }
        if (ServerData.exists(p.getName(), "NEWVIRTUE")) {
            if ((Boolean) ServerData.get(p.getName(), "NEWVIRTUE")) {
                p.sendMessage(ChatColor.DARK_AQUA + "✉" + ChatColor.RED + " You have a new virtue!" + ChatColor.WHITE + " Use " + ChatColor.YELLOW + "/history" + ChatColor.WHITE + " for more information.");
                ServerData.remove(p.getName(), "NEWVIRTUE");
            }
        }

        ServerData.put(p.getName(), "LASTLOGINTIME", System.currentTimeMillis());
        MiscUtil.checkScore(p);
    }
}
