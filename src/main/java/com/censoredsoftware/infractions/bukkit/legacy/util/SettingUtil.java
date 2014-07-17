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

package com.censoredsoftware.infractions.bukkit.legacy.util;

import com.censoredsoftware.infractions.bukkit.legacy.InfractionsPlugin;
import com.google.common.collect.Lists;

import java.util.List;

public class SettingUtil {
    static InfractionsPlugin plugin;

    static {
        plugin = InfractionsPlugin.getInst();
    }

    public static List<String> fetchListString(String path) {
        return plugin.getConfig().getStringList(path);
    }

    public static boolean getSettingBoolean(String id) {
        return !plugin.getConfig().isBoolean(id) || plugin.getConfig().getBoolean(id);
    }

    public static double getSettingDouble(String id) {
        if (plugin.getConfig().isDouble(id)) return plugin.getConfig().getDouble(id);
        else return -1;
    }

    public static int getSettingInt(String id) {
        if (plugin.getConfig().isInt(id)) return plugin.getConfig().getInt(id);
        else return -1;
    }

    public static String getSettingString(String id) {
        if (plugin.getConfig().isString(id)) return plugin.getConfig().getString(id);
        else return null;
    }

    public static Integer getLevel(String levelArg) {
        if (getLevel1().contains(levelArg)) return 1;
        if (getLevel2().contains(levelArg)) return 2;
        if (getLevel3().contains(levelArg)) return 3;
        if (getLevel4().contains(levelArg)) return 4;
        if (getLevel5().contains(levelArg)) return 5;

        InfractionsPlugin.getInst().getLogger().warning("Unable to find level for reason '" + levelArg + "'.");
        return null;
    }

    public static List<String> getLevel(int i) {
        List<String> level;
        level = SettingUtil.fetchListString("level_" + i);
        return level;
    }

    public static List<String> getAllLevels() {
        List<String> all = Lists.newArrayList();
        all.addAll(getLevel1());
        all.addAll(getLevel2());
        all.addAll(getLevel3());
        all.addAll(getLevel4());
        all.addAll(getLevel5());
        return all;
    }

    public static List<String> getLevel1() {
        List<String> level1;
        level1 = SettingUtil.fetchListString("level_1");
        return level1;
    }

    public static List<String> getLevel2() {
        List<String> level2;
        level2 = SettingUtil.fetchListString("level_2");
        return level2;
    }

    public static List<String> getLevel3() {
        List<String> level3;
        level3 = SettingUtil.fetchListString("level_3");
        return level3;
    }

    public static List<String> getLevel4() {
        List<String> level4;
        level4 = SettingUtil.fetchListString("level_4");
        return level4;
    }

    public static List<String> getLevel5() {
        List<String> level5;
        level5 = SettingUtil.fetchListString("level_5");
        return level5;
    }
}
