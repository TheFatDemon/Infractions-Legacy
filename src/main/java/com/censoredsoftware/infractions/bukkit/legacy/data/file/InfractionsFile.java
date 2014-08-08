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

package com.censoredsoftware.infractions.bukkit.legacy.data.file;

import com.censoredsoftware.infractions.bukkit.legacy.InfractionsPlugin;
import com.censoredsoftware.infractions.bukkit.legacy.data.DataSerializable;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Maps;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Abstract class extending ConfigFile for easy yaml file creation inside of Demigods.
 *
 * @param <K> The id type.
 * @param <V> The data type.
 */
public abstract class InfractionsFile<K, V extends DataSerializable<K>, I> {
    private final String name;
    private final String fileName, fileType, savePath;
    ConcurrentMap<K, I> dataStore = Maps.newConcurrentMap();
    Method valueConstructor;

    public InfractionsFile(String fileName, String fileType, String savePath, String name, Method valueConstructor) {
        this.fileName = fileName;
        this.fileType = fileType;
        this.savePath = savePath;
        this.name = name;
        this.valueConstructor = valueConstructor;
    }

    public final String getName() {
        return name;
    }

    public final ConcurrentMap<K, I> getLoadedData() {
        return dataStore;
    }

    @SuppressWarnings("unchecked")
    public final Map<String, Object> serialize(K id) {
        return ((V) getLoadedData().get(id)).serialize();
    }

    public String getDirectoryPath() {
        return savePath;
    }

    public final String getFullFileName() {
        return fileName + fileType;
    }

    public final void loadDataFromFile() {
        dataStore = getCurrentFileData();
    }

    public final boolean containsKey(K key) {
        return key != null && dataStore.containsKey(key);
    }

    public final I get(K key) {
        return dataStore.get(key);
    }

    public final void put(K key, I value) {
        dataStore.put(key, value);
    }

    public final Collection<I> values() {
        return dataStore.values();
    }

    public final void clear() {
        dataStore.clear();
    }

    public ConcurrentMap<K, I> getCurrentFileData() {
        // Grab the current file.
        FileConfiguration data = YamlFileUtil.getConfiguration(getDirectoryPath(), getFullFileName());

        // Convert the raw file data into more usable data, in map form.
        ConcurrentHashMap<K, I> map = new ConcurrentHashMap<K, I>();
        for (String stringId : data.getKeys(false)) {
            try {
                I v = valueFromData(stringId, data.getConfigurationSection(stringId));
                if (stringId.equals("null") || v == null) {
                    InfractionsPlugin.getInst().getLogger().warning("Corrupt: " + stringId + ", in file: " + getFullFileName());
                    continue;
                }
                map.put(keyFromString(stringId), valueFromData(stringId, data.getConfigurationSection(stringId)));
            } catch (Exception ignored) {
                ignored.printStackTrace();
            }
        }
        return map;
    }

    public boolean saveDataToFile() {
        // Grab the current file, and its data as a usable map.
        FileConfiguration currentFile = YamlFileUtil.getConfiguration(getDirectoryPath(), getFullFileName());
        final Map<K, I> currentFileMap = getCurrentFileData();

        // Create/overwrite a configuration section if new data exists.
        for (K key : Collections2.filter(getLoadedData().keySet(), new Predicate<K>() {
            @Override
            public boolean apply(K key) {
                return key != null && (!currentFileMap.containsKey(key) || !currentFileMap.get(key).equals(getLoadedData().get(key)));
            }
        }))
            currentFile.createSection(key.toString(), serialize(key));

        // Remove old unneeded data.
        for (K key : Collections2.filter(currentFileMap.keySet(), new Predicate<K>() {
            @Override
            public boolean apply(K key) {
                return !getLoadedData().keySet().contains(key);
            }
        }))
            currentFile.set(key.toString(), null);

        // Save the file!
        return YamlFileUtil.saveFile(getDirectoryPath(), getFullFileName(), currentFile);
    }

    /**
     * Convert a key from a string.
     *
     * @param stringKey The provided string.
     * @return The converted key.
     */
    public abstract K keyFromString(String stringKey);

    /**
     * Convert to a get from a number of objects representing the data.
     *
     * @param stringKey The string key for the data.
     * @param data      The provided data object.
     * @return The converted get.
     */
    public abstract I valueFromData(String stringKey, ConfigurationSection data);
}
