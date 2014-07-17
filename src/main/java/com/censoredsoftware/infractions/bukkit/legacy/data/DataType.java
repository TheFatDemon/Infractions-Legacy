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

package com.censoredsoftware.infractions.bukkit.legacy.data;

import com.censoredsoftware.infractions.bukkit.legacy.compat.LegacyDossier;
import com.censoredsoftware.infractions.bukkit.legacy.compat.LegacyInfraction;
import com.censoredsoftware.infractions.bukkit.legacy.compat.LegacyIssuer;

/**
 * Meta data for each data type.
 */
@SuppressWarnings("unchecked")
public enum DataType {
    /**
     * Issuer.
     */
    ISSUER(LegacyIssuer.class, IdType.STRING, "iss"),
    /**
     * Infraction.
     */
    INFRACTION(LegacyInfraction.class, IdType.STRING, "inf"),
    /**
     * Dossier.
     */
    DOSSIER(LegacyDossier.class, IdType.UUID, "dos"),
    /**
     * ServerData.
     */
    SERVER(ServerData.class, IdType.UUID, "srv"),
    /**
     * Returned when no valid type can be found.
     */
    INVALID(Invalid.class, IdType.VOID, "IF_YOU_SEE_THIS_PLEASE_TELL_US_ON_THE_SITE_YOU_DOWNLOADED_INFRACTIONS_FROM");

    private Class clazz;
    private IdType idType;
    private String abbr;

    /**
     * Meta data for a data type.
     *
     * @param clazz  The object class that holds the data.
     * @param idType The id type this data type uses.
     * @param abbr   The abbreviation for use in certain data managers.
     */
    private <V extends DataSerializable<?>> DataType(Class<V> clazz, IdType idType, String abbr) {
        this.clazz = clazz;
        this.idType = idType;
        this.abbr = abbr;
    }

    @Override
    public String toString() {
        return name();
    }

    public Class getDataClass() {
        return clazz;
    }

    public IdType getIdType() {
        return idType;
    }

    public String getAbbreviation() {
        return abbr;
    }
}
