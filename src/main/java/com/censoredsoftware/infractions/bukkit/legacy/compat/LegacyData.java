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

package com.censoredsoftware.infractions.bukkit.legacy.compat;

import com.censoredsoftware.infractions.bukkit.Infraction;
import com.censoredsoftware.infractions.bukkit.Infractions;
import com.censoredsoftware.infractions.bukkit.dossier.CompleteDossier;
import com.censoredsoftware.infractions.bukkit.dossier.Dossier;
import com.censoredsoftware.infractions.bukkit.evidence.Evidence;
import com.censoredsoftware.infractions.bukkit.evidence.EvidenceType;
import com.censoredsoftware.infractions.bukkit.issuer.Issuer;
import com.censoredsoftware.infractions.bukkit.issuer.IssuerType;
import com.censoredsoftware.infractions.bukkit.legacy.InfractionsPlugin;
import com.censoredsoftware.infractions.bukkit.legacy.data.DataManager;
import com.censoredsoftware.infractions.bukkit.legacy.data.file.FileDataManager;
import com.censoredsoftware.infractions.bukkit.legacy.data.file.InfractionsFile;
import com.censoredsoftware.infractions.bukkit.legacy.data.file.YamlFileUtil;
import com.censoredsoftware.infractions.bukkit.legacy.util.MiscUtil;
import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import net.minecraft.util.com.google.common.base.Predicate;
import net.minecraft.util.com.google.common.collect.Iterables;
import org.bukkit.Bukkit;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

@SuppressWarnings({"ResultOfMethodCallIgnored", "ConstantConditions"})
public class LegacyData implements Runnable, Listener {
    static boolean conversion_in_progress = true;
    static Listener listener = new LegacyData();
    static final Logger log = InfractionsPlugin.getInst().getLogger();
    static double speed = 1;
    static int total = 1;
    static int processed = 0;

    public static Long getEstimatedEnd() {
        int left = total - processed;
        return System.currentTimeMillis() + TimeUnit.SECONDS.toMillis((int) (left * speed));
    }

    public static String getKickMessage() {
        String timeLeft = MiscUtil.prettyTime(getEstimatedEnd(), false);
        return "Data is being converted, rejoin in " + timeLeft + ".";
    }

    public static void asyncConvertIds() {
        if (InfractionsPlugin.getInst().getConfig().getBoolean("convert.old_ids")) {
            InfractionsPlugin.getInst().getConfig().set("convert.old_ids", false);
            InfractionsPlugin.getInst().saveConfig();

            speed = 0.009;

            for (Player player : Bukkit.getOnlinePlayers()) {
                player.kickPlayer(getKickMessage());
            }
            Bukkit.getScheduler().scheduleAsyncDelayedTask(InfractionsPlugin.getInst(), new Runnable() {
                @Override
                public void run() {
                    conversion_in_progress = true;
                    Bukkit.getServer().getPluginManager().registerEvents(listener, InfractionsPlugin.getInst());

                    oldIds();
                    DataManager.saveAllData();

                    HandlerList.unregisterAll(listener);
                    conversion_in_progress = false;
                }
            });
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerPreLogin(AsyncPlayerPreLoginEvent event) {
        if (conversion_in_progress) {
            event.setLoginResult(AsyncPlayerPreLoginEvent.Result.KICK_OTHER);
            event.setKickMessage(getKickMessage());
        }
    }

    private static boolean isValid(UUID id) {
        try {
            String player = Bukkit.getOfflinePlayer(id).getName();
            return player != null;
        } catch (Exception ignored) {
        }
        return false;
    }

    private static void oldIds() {
        log.warning("--------------------------------------");
        log.warning("NOW FIXING CORRUPTED MOJANG UUID KEYS.");
        log.warning("THIS WILL CAUSE LAG FOR A FEW MOMENTS.");
        log.warning("--------------------------------------");

        InfractionsFile file = ((FileDataManager) DataManager.getManager()).yamlFiles.get(LegacyDossier.class);
        Configuration config = YamlFileUtil.getConfiguration(file.getDirectoryPath(), file.getFullFileName());

        Collection<String> names = Collections2.transform(Collections2.filter(Infractions.allDossiers(), new com.google.common.base.Predicate<Dossier>() {
            @Override
            public boolean apply(Dossier dossier) {
                return dossier instanceof LegacyCompleteDossier && !((LegacyCompleteDossier) dossier).confirmedValid;
            }
        }), new Function<Dossier, String>() {
            @Override
            public String apply(Dossier dossier) {
                return ((LegacyCompleteDossier) dossier).getLastKnownName();
            }
        });

        total = names.size();

        UUIDFetcher fetcher = new UUIDFetcher(names);
        try {
            Map<String, UUID> namesAndIds = fetcher.call();

            for (Dossier dossier : Infractions.allDossiers()) {
                if (((LegacyDossier) dossier).confirmedValid) {
                    continue;
                }
                UUID id = dossier.getId();
                if (dossier instanceof CompleteDossier) {
                    CompleteDossier completeDossier = (CompleteDossier) dossier;
                    UUID actualId = namesAndIds.get(completeDossier.getLastKnownName());
                    if (id.equals(actualId)) {
                        log.info("UUID FOR \'" + completeDossier.getLastKnownName() + "\' IS VALID.");
                    } else {
                        try {
                            ConfigurationSection data = config.getConfigurationSection(id.toString());

                            Set<Infraction> toAdd = Sets.newHashSet();
                            for (Infraction infraction : dossier.getInfractions()) {
                                completeDossier.acquit(infraction);
                                infraction.setPlayerId(actualId);
                                toAdd.add(infraction);
                            }

                            Infractions.removeDossier(dossier);
                            LegacyDossier newDossier = LegacyCompleteDossier.of(actualId, data);
                            newDossier.confirmedValid = true;
                            Infractions.addDossier(newDossier);

                            for (Infraction infraction : toAdd) {
                                newDossier.cite(infraction);
                            }

                            // Log it
                            log.warning("UUID FOR \'" + completeDossier.getLastKnownName() + "\' IS INVALID.");
                            log.warning("- OLD ID: " + id.toString());
                            log.warning("- ACTUAL: " + actualId.toString());
                        } catch (Exception ignored) {
                        }
                    }

                } else {
                    log.warning("INCOMPLETE DOSSIER WITH ID \'" + dossier.getId().toString() + "\'");
                    Infractions.getDatabase().removeDossier(dossier);
                }
                processed++;
            }
        } catch (Exception ignored) {
        }

        log.warning("--------------------------------------");
        log.warning("ALL PLAYERS' UUID CONVERSION COMPLETE.");
        log.warning("--------------------------------------");
    }


    public static void asyncConvert() {
        if (!InfractionsPlugin.getInst().getConfig().getBoolean("convert.done")) {
            LegacyData data = new LegacyData();

            total = (int) (getLegacyData().size() * 1.4);
            speed = 0.3;

            // Kick all players
            for (Player player : Bukkit.getOnlinePlayers()) {
                player.kickPlayer(getKickMessage());
            }

            // New thread
            new Thread(data).start();
        }
    }

    private static Issuer LEGACY_ISSUER = new Issuer(IssuerType.LEGACY, "LEGACY");
    private static Logger messageLog = Logger.getLogger("Minecraft");

    @Override
    public void run() {
        log.warning("--------------------------------------");
        log.warning("CONVERTING ALL DATA INTO A NEW FORMAT.");
        log.warning("THIS WILL CAUSE LAG FOR A FEW MOMENTS.");
        log.warning("--------------------------------------");

        conversion_in_progress = true;
        Bukkit.getServer().getPluginManager().registerEvents(listener, InfractionsPlugin.getInst());

        List<String> playerList = Lists.newArrayList(getLegacyData().keySet());

        // Don't convert if no suitable data is found
        if (!playerList.isEmpty()) {
            int error = 0;
            int count = 0;
            boolean messages = playerList.size() >= 1000;

            if (messages)
                messageLog.info("Sir, since you have so many infractions, the plugin author has called in an sizable crew to help you convert this entire pile of old data.");

            int quarter = playerList.size() / 4;
            int half = playerList.size() / 2;
            int lastquarter = playerList.size() - quarter;

            long startTime = System.currentTimeMillis();

            log.info("ORGANIZING DOSSIERS (THIS CAN TAKE AWHILE).");
            for (String player : Lists.newArrayList(playerList)) {
                count++;
                processed++;
                if (messages && count == quarter)
                    messageLog.info("Captain! We're about 1/4 of the way through these things.");
                else if (messages && count == half) messageLog.info("Sir, we still have about half of the pile left.");
                else if (messages && count == lastquarter) messageLog.info("Almost done! We're at 3/4 completion.");

                int result = organizeDossier(player);
                if (result == 1) playerList.remove(player);
                error += result;
            }

            if (messages)
                messageLog.info("We've finished organizing the dossiers sir, and it only took " + MiscUtil.prettyTime(startTime, true) + ".");

            log.info("DOSSIERS HAVE BEEN ORGANIZED.");

            Set<Dossier> dossiers = Infractions.allDossiers();

            messages = dossiers.size() >= 1000;

            long halfTime = System.currentTimeMillis();

            log.info("CONSOLIDATING INFRACTIONS (SIT TIGHT).");
            if (messages) messageLog.info("We're nearly done, this part is much easier.");
            for (String player : playerList)
                error += consolodateLegacyInfractions(player);

            if (messages) {
                messageLog.info("After " + MiscUtil.prettyTime(halfTime, true) + " we're finally done!");
                messageLog.info("We've compiled a report for you sir:");
            }

            DataManager.saveAllData();

            log.info("PROCESS COMPLETED " + MiscUtil.timeSincePretty(startTime).toUpperCase() + " WITH " + error + " ERRORS.");
            if (error > 0) {
                log.info("Sometimes an error or two is expected.");
                log.info("Don't panic! If you have to, just try again.");
            }

            if (messages) messageLog.info("Everything is ready, Captain. Should we set sail?");

            InfractionsPlugin.getInst().getConfig().set("convert.done", true);
            InfractionsPlugin.getInst().getConfig().set("convert.old_ids", false);
            InfractionsPlugin.getInst().saveConfig();
        }

        processed = 0;
        total = 1;

        HandlerList.unregisterAll(listener);
        conversion_in_progress = false;

        log.warning("--------------------");
        log.warning("CONVERSION COMPLETE.");
        log.warning("--------------------");

        legacyData.clear();
    }

    private static String tryAgain = "";

    private static int organizeDossiers(List<String> playerList) {
        int error = 0;
        try {
            for (UUID id : MiscUtil.getIds(playerList)) {
                if (id == null) {
                    error += 1;
                    continue;
                }
                Infractions.getDossier(id);
            }
        } catch (Exception ignored) {
        }
        return error;
    }

    @SuppressWarnings("unchecked")
    private static int organizeDossier(String target) {
        try {
            Infractions.getCompleteDossier(target);
        } catch (NullPointerException ex) {
            if (!tryAgain.equals(target)) {
                tryAgain = target;
                return organizeDossier(target);
            }
            log.warning("Is \"" + target + "\" an actual player?");
            return 1;
        }
        return 0;
    }

    private static int consolodateLegacyInfractions(final String target) {
        int error = 0;
        int count = 0;

        if (hasData(target, "INFRACTIONS")) {
            Set<Map.Entry<String, HashMap<String, Object>>> entrySet = getLegacyData().entrySet();

            Map.Entry<String, HashMap<String, Object>> entry = Iterables.find(entrySet, new Predicate<Map.Entry<String, HashMap<String, Object>>>() {
                @Override
                public boolean apply(Map.Entry<String, HashMap<String, Object>> entry) {
                    return entry.getKey().equalsIgnoreCase(target);
                }
            }, null);

            if (entry == null) return 1;

            for (Map.Entry<String, Object> entry_ : entry.getValue().entrySet()) {
                if (!entry_.getKey().equals("INFRACTIONS")) continue;
                for (String legacyData : ((HashMap<String, String>) entry_.getValue()).values()) {
                    // Get info from legacy infraction.
                    String[] dataList = legacyData.substring(2).split(" - ");
                    List<String> otherData = Lists.newArrayList(Arrays.asList(dataList));

                    try {
                        count++;

                        // Convert score to new % system.
                        int score;
                        if (!legacyData.substring(0, 1).equals("-"))
                            score = (Integer.parseInt(legacyData.substring(0, 1)));
                        else score = (Integer.parseInt(legacyData.substring(0, 2)));

                        // don't convert virtues
                        if (score <= 0) continue;

                        String reason = otherData.get(0);
                        String proof = otherData.get(1);
                        String date = otherData.get(2);

                        Long dateTime = null;
                        try {
                            dateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(date).getTime();
                        } catch (Exception ignored) {
                        }

                        Dossier dossier = Infractions.getDossier(target);

                        Infraction infraction = new Infraction(dossier.getId(), dateTime != null ? dateTime : System.currentTimeMillis(), reason, score, LEGACY_ISSUER, new Evidence(LEGACY_ISSUER, EvidenceType.UNKNOWN, dateTime != null ? dateTime : System.currentTimeMillis(), proof));
                        dossier.cite(infraction);
                    } catch (Exception e) {
                        error++;
                        e.printStackTrace();
                    }
                }
            }
            if (error > 0)
                log.warning("Error converting " + error + " of " + count + " infractions for " + target + ".");
            else log.info("Converted " + count + " infractions for " + target + ".");
        }
        return (error > 0 ? 1 : 0);
    }

    private static String PATH = "plugins/Infractions/";

    // Legacy Save
    private static Map<String, HashMap<String, Object>> legacyData = Maps.newHashMap();

    @SuppressWarnings("unchecked")
    private LegacyData() {
        File f1 = new File(PATH + "Players/");
        File[] list = f1.listFiles();
        if (list == null) return;
        for (File element : list) {
            String load = element.getName();
            if (load.endsWith(".player")) {
                load = load.substring(0, load.length() - 7);
                try {
                    ObjectInputStream ois = new ObjectInputStream(new FileInputStream(element));
                    Object result = ois.readObject();
                    HashMap<String, Object> cast = (HashMap<String, Object>) result;
                    getLegacyData().put(load, cast);
                    ois.close();
                } catch (Exception error) {
                    log.severe("Deleting corrupt save for player " + load);
                    element.delete();
                }
            }
        }

        // Delete legacy folder if it is empty.
        if (f1.listFiles().length == 0) f1.delete();
    }

    private static boolean hasData(String p, String id) {
        return hasPlayer(p) && getLegacyData().get(p).containsKey(id);
    }

    /*
     * Check if the player has saved information.
     */
    private static boolean hasPlayer(String p) {
        return getLegacyData().containsKey(p);
    }

    /*
     * getLegacyData() : Returns all Legacy data.
     */
    private static Map<String, HashMap<String, Object>> getLegacyData() {
        return legacyData;
    }
}
