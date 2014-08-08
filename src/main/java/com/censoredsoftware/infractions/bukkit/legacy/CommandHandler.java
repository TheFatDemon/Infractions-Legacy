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
import com.censoredsoftware.infractions.bukkit.dossier.CompleteDossier;
import com.censoredsoftware.infractions.bukkit.evidence.Evidence;
import com.censoredsoftware.infractions.bukkit.issuer.IssuerType;
import com.censoredsoftware.infractions.bukkit.legacy.compat.LegacyCompleteDossier;
import com.censoredsoftware.infractions.bukkit.legacy.compat.LegacyDossier;
import com.censoredsoftware.infractions.bukkit.legacy.data.DataManager;
import com.censoredsoftware.infractions.bukkit.legacy.data.thread.AsyncIPMatcherTask;
import com.censoredsoftware.infractions.bukkit.legacy.util.MiscUtil;
import com.censoredsoftware.infractions.bukkit.legacy.util.SettingUtil;
import com.censoredsoftware.infractions.bukkit.legacy.util.URLUtil;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.net.InetAddress;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentMap;
import java.util.logging.Logger;

public class CommandHandler implements TabExecutor {
    static Logger log = InfractionsPlugin.getInst().getLogger();
    static SimpleDateFormat FORMAT = new SimpleDateFormat("MMM dd, yyyy @ hh:mm aa");

    @Override
    public boolean onCommand(CommandSender sender, Command c, String label, String[] args) {
        Player p = null;
        if (sender instanceof Player) p = (Player) sender;
        if (c.getName().equalsIgnoreCase("infractions")) {
            MiscUtil.sendMessage(p, "---------------");
            MiscUtil.sendMessage(p, "INFRACTIONS HELP");
            MiscUtil.sendMessage(p, "---------------");
            if (MiscUtil.hasPermissionOrOP(p, "infractions.mod")) {
                MiscUtil.sendMessage(p, ChatColor.GRAY + "  /cite <player> <infraction> [proof-url]");
                MiscUtil.sendMessage(p, ChatColor.GRAY + "  /uncite <player> <key>" + ChatColor.WHITE + " - Find the key with " + ChatColor.YELLOW + "/history" + ChatColor.WHITE + ".");
            }
            MiscUtil.sendMessage(p, ChatColor.GRAY + "  /history [player]");
            MiscUtil.sendMessage(p, ChatColor.GRAY + "  /reasons " + ChatColor.WHITE + "- Shows all valid infraction reasons.");
            MiscUtil.sendMessage(p, ChatColor.GRAY + "  /virtues " + ChatColor.WHITE + "- Shows all valid virtue types.");
            return true;
        } else if (c.getName().equalsIgnoreCase("reasons")) {
            MiscUtil.sendMessage(p, "------------------");
            MiscUtil.sendMessage(p, "INFRACTION REASONS");
            MiscUtil.sendMessage(p, "------------------");

            for (int i = 1; i < 6; i++) {
                MiscUtil.sendMessage(p, (i < 3 ? ChatColor.GREEN : i < 5 ? ChatColor.YELLOW : ChatColor.RED) + "Level " + i + ":");
                for (int j = 0; j < SettingUtil.getLevel(i).size(); j++)
                    MiscUtil.sendMessage(p, "  " + SettingUtil.getLevel(i).get(j));
            }
            return true;
        } else if (c.getName().equalsIgnoreCase("cite")) {
            if (!MiscUtil.hasPermissionOrOP(p, "infractions.mod")) {
                MiscUtil.sendMessage(p, "You do not have enough permissions.");
                return true;
            }
            if (SettingUtil.getSettingBoolean("require_proof")) {
                if ((args.length != 3)) {
                    MiscUtil.sendMessage(p, "You must provide a valid URL as proof.");
                    return false;
                }
                if (!URLUtil.isValidURL(args[2])) {
                    MiscUtil.sendMessage(p, "You must provide a valid URL as proof.");
                    return false;
                }
            }
            if (args.length == 0 || args.length == 1) {
                MiscUtil.sendMessage(p, "Not enough arguments.");
                return false;
            }

            if (Infractions.getCompleteDossier(args[0]) == null) {
                MiscUtil.sendMessage(p, "This player hasn't joined yet.");
                return true;
            }
            // Levels
            Integer level = SettingUtil.getLevel(args[1]);
            if (level != null) {
                if (args.length == 3) {
                    if (!URLUtil.isValidURL(args[2])) {
                        MiscUtil.sendMessage(p, "You must provide a valid URL as proof.");
                        return false;
                    }
                    MiscUtil.addInfraction(MiscUtil.getInfractionsPlayer(args[0]), sender, level, args[1], URLUtil.convertURL(args[2]));
                } else {
                    MiscUtil.addInfraction(MiscUtil.getInfractionsPlayer(args[0]), sender, level, args[1], "No proof.");
                }
                MiscUtil.sendMessage(p, ChatColor.GOLD + "Success! " + ChatColor.WHITE + "The level " + level + " infraction has been received.");
                MiscUtil.kickNotify(MiscUtil.getInfractionsPlayer(args[0]), args[1]);
                return true;
            }
        } else if (c.getName().equalsIgnoreCase("uncite")) {
            if (!(args.length == 2)) {
                MiscUtil.sendMessage(p, "Not enough arguments.");
                return false;
            }
            if (!MiscUtil.hasPermissionOrOP(p, "infractions.mod")) {
                MiscUtil.sendMessage(p, "You do not have enough permissions.");
                return true;
            }

            if (MiscUtil.removeInfraction(MiscUtil.getInfractionsPlayer(args[0]), args[1])) {
                MiscUtil.sendMessage(p, "Infraction removed!");
                try {
                    MiscUtil.checkScore(MiscUtil.getInfractionsPlayer(args[0]));
                } catch (NullPointerException e) {
                    // player is offline
                }
                return true;
            }
            MiscUtil.sendMessage(p, "No such infraction.");
            return true;
        } else if (c.getName().equalsIgnoreCase("history")) {
            if (!(args.length == 1) && !(p == null)) {
                if (MiscUtil.ignore(p)) {
                    sender.sendMessage(ChatColor.YELLOW + "Infractions does not track your history.");
                    return true;
                }

                p.performCommand("history " + p.getName());
                return true;
            }
            if ((p == null) && !(args.length == 1)) {
                log.info("You must provide a username in the console.");
                return false;
            }

            if (!MiscUtil.hasPermissionOrOP(p, "infractions.mod") && !p.getName().toLowerCase().equals(args[0])) {
                p.sendMessage(ChatColor.RED + "You don't have permission to do that.");
                return true;
            }

            /**
             * DISPLAY ALL CURRENT INFRACTIONS
             */

            String player = MiscUtil.getInfractionsPlayer(args[0]);
            if (player != null) {
                sender.sendMessage("   ");

                CompleteDossier dossier = Infractions.getCompleteDossier(player);

                Integer maxScore = MiscUtil.getMaxScore(Infractions.getCompleteDossier(player).getId());
                String chatLevel = InfractionsPlugin.getLevelForChat(player);
                MiscUtil.sendMessage(p, ChatColor.WHITE + (chatLevel.equals("") ? "" : chatLevel + " ") + ChatColor.YELLOW + player + ChatColor.WHITE + " - " + MiscUtil.getScore(player) + (maxScore == null ? " points towards a ban." : " points out of " + maxScore + " until a ban."));

                try {
                    boolean staff = MiscUtil.hasPermissionOrOP(p, "infractions.mod");
                    Set<Infraction> infractions = dossier.getInfractions();
                    if (!infractions.isEmpty()) {
                        for (Infraction infraction : infractions) {
                            MiscUtil.sendMessage(p, ChatColor.DARK_RED + "✘ " + ChatColor.DARK_PURPLE + StringUtils.capitalize(infraction.getReason()) + ChatColor.DARK_GRAY + " - " + ChatColor.BLUE + FORMAT.format(infraction.getDateCreated()));
                            MiscUtil.sendMessage(p, ChatColor.DARK_GRAY + "     Penalty: " + ChatColor.GRAY + infraction.getScore());
                            MiscUtil.sendMessage(p, ChatColor.DARK_GRAY + "     Proof: " + ChatColor.GRAY + Iterables.getFirst(Collections2.transform(infraction.getEvidence(), new Function<Evidence, Object>() {
                                @Override
                                public String apply(Evidence evidence) {
                                    return evidence.getRawData();
                                }
                            }), "No Proof."));
                            if (staff) {
                                String id = MiscUtil.getInfractionId(infraction);
                                MiscUtil.sendMessage(p, ChatColor.DARK_GRAY + "     Key: " + ChatColor.GRAY + id);
                                String issuerId = infraction.getIssuer().getId();
                                if (IssuerType.STAFF.equals(infraction.getIssuer().getType())) {
                                    UUID issuerUUID = UUID.fromString(issuerId);
                                    ConcurrentMap<UUID, LegacyDossier> map = DataManager.getManager().getMapFor(LegacyDossier.class);
                                    if (map.containsKey(issuerUUID) && map.get(issuerUUID) instanceof CompleteDossier) {
                                        CompleteDossier issuerDossier = (LegacyCompleteDossier) map.get(issuerUUID);
                                        issuerId = issuerDossier.getLastKnownName();
                                    } else {
                                        issuerId = "INVALID UUID: " + issuerId;
                                    }
                                }
                                MiscUtil.sendMessage(p, ChatColor.DARK_GRAY + "     Issuer: " + ChatColor.GRAY + issuerId);
                            }
                        }
                    } else
                        MiscUtil.sendMessage(p, ChatColor.DARK_GREEN + "✔ " + ChatColor.WHITE + "No infractions found for this player.");
                    if (!staff) return true;
                    Set<InetAddress> addresses = dossier.getAssociatedIPAddresses();
                    MiscUtil.sendMessage(p, ChatColor.BLUE + "✔ " + ChatColor.DARK_AQUA + "Associated IP Addresses:");
                    if (addresses.isEmpty())
                        MiscUtil.sendMessage(p, ChatColor.GRAY + "     No currently known addresses.");
                    else for (InetAddress address : addresses) {
                        MiscUtil.sendMessage(p, ChatColor.GRAY + "     " + address.getHostAddress());
                        Collection<String> others = AsyncIPMatcherTask.getRelatives(dossier.getId());
                        if (others.size() > 1) {
                            MiscUtil.sendMessage(p, ChatColor.DARK_GRAY + "       - also associated with:");
                            for (String other : others) {
                                if (other.equals(player)) continue;
                                MiscUtil.sendMessage(p, ChatColor.GRAY + "         " + ChatColor.YELLOW + other);
                            }
                        }
                    }

                    sender.sendMessage("   ");
                    return true;
                } catch (NullPointerException e) {
                    return true;
                }
            } else {
                MiscUtil.sendMessage(p, "A player with the name \"" + args[0] + "\" cannot be found.");
                return true;
            }
        } else if (c.getName().equalsIgnoreCase("clearhistory") && p != null && p.hasPermission("infractions.clearhistory") && args.length > 0) {
            try {
                Player remove = Bukkit.getServer().matchPlayer(args[0]).get(0);
                Infractions.removeDossier(remove.getUniqueId());
                remove.kickPlayer(ChatColor.GREEN + "Your Infractions history has been reset--please join again.");
                return true;
            } catch (Exception error) {
                sender.sendMessage(ChatColor.RED + "Could not find that player...");
                return false;
            }
        }
        MiscUtil.sendMessage(p, "Something went wrong, please try again.");
        return false;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, final String[] args) {

        List<String> list = Lists.newArrayList();
        if (!commandSender.hasPermission("infractions.mod")) return list;
        if (args.length == 1 && ("cite".equals(command.getName()) || "uncite".equals(command.getName()) || "history".equals(command.getName()) || "clearhistory".equals(command.getName()))) {
            for (Player player : Bukkit.getOnlinePlayers())
                if (player.getName().toLowerCase().startsWith(args[0].toLowerCase())) list.add(player.getName());
        } else if (args.length == 2) {
            boolean step1Done = false;
            try {
                step1Done = Infractions.getCompleteDossier(args[0]) != null;
            } catch (Exception ignored) {
            }
            Predicate<String> predicate = new Predicate<String>() {
                @Override
                public boolean apply(String s) {
                    return s.toLowerCase().startsWith(args[1].toLowerCase());
                }
            };
            if ("cite".equals(command.getName()))
                list.addAll(Collections2.filter(SettingUtil.getAllLevels(), predicate));
            else if ("uncite".equals(command.getName()) && step1Done)
                list.addAll(Collections2.filter(((LegacyDossier) Infractions.getCompleteDossier(args[0])).getInfractionIds(), predicate));
        }
        return list;
    }
}
