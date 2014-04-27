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
import com.censoredsoftware.infractions.bukkit.legacy.data.DataAccess;
import com.censoredsoftware.infractions.bukkit.legacy.data.IdType;
import com.censoredsoftware.infractions.bukkit.legacy.data.Register;
import com.censoredsoftware.infractions.bukkit.legacy.util.MiscUtil;
import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.bukkit.configuration.ConfigurationSection;

import java.util.*;

public class LegacyDossier extends DataAccess<UUID, LegacyDossier> implements Dossier
{
	private UUID mojangid;
	private Set<String> infractions;
	protected String lastKnownName;

	protected LegacyDossier()
	{
	}

	public LegacyDossier(UUID mojangId, Infraction... infractions)
	{
		this(mojangId, Sets.newHashSet(infractions));
	}

	public LegacyDossier(UUID mojangId, Set<Infraction> infractions)
	{
		this.mojangid = mojangId;
		this.infractions = Sets.newHashSet(Collections2.transform(infractions, new Function<Infraction, String>()
		{
			@Override
			public String apply(Infraction infraction)
			{
				String id = MiscUtil.getInfractionId(infraction);
				((LegacyDatabase) Infractions.getDatabase()).getInfractionMap().put(id, LegacyInfraction.of(infraction));
				return id;
			}
		}));
	}

	@Register(idType = IdType.UUID)
	public static LegacyDossier of(UUID id, ConfigurationSection conf)
	{
		return (LegacyDossier) unserialize(id, conf.getValues(true));
	}

	@Override
	public UUID getId()
	{
		return mojangid;
	}

	@Override
	public int getScore()
	{
		int score = 0;
		for(Infraction infraction : getInfractions())
			score += infraction.getScore();
		return score;
	}

	@Override
	public Set<Infraction> getInfractions()
	{
		return Sets.newHashSet(Collections2.transform(infractions, new Function<String, Infraction>()
		{
			@Override
			public Infraction apply(String s)
			{
				return ((LegacyDatabase) Infractions.getDatabase()).getInfractionMap().get(s).toInfraction();
			}
		}));
	}

	@Override
	public void cite(Infraction infraction)
	{
		String id = MiscUtil.getInfractionId(infraction);
		((LegacyDatabase) Infractions.getDatabase()).getInfractionMap().put(id, LegacyInfraction.of(infraction));
		infractions.add(id);
	}

	@Override
	public void acquit(Infraction infraction)
	{
		String id = MiscUtil.getInfractionId(infraction);
		((LegacyDatabase) Infractions.getDatabase()).getInfractionMap().remove(id);
		infractions.remove(id);
	}

	@Override
	public CompleteDossier complete(String playerName)
	{
		CompleteDossier dossier = new LegacyCompleteDossier(getId(), playerName, getInfractions());
		((LegacyDatabase) Infractions.getDatabase()).getDossierMap().put(getId(), dossier);
		return dossier;
	}

	@Override
	public CompleteDossier complete() throws ClassCastException
	{
		return (CompleteDossier) this;
	}

	@Override
	public Map<String, Object> serialize()
	{
		Map<String, Object> map = new HashMap<String, Object>();
		if(lastKnownName != null) map.put("lastKnownName", lastKnownName);

		List<String> infractionList;
		if(infractions != null) infractionList = Lists.newArrayList(infractions);
		else infractionList = Lists.newArrayList();

		map.put("infractions", infractionList);

		return map;
	}

	@SuppressWarnings("unchecked")
	public static Dossier unserialize(UUID id, Map<String, Object> map)
	{
		LegacyDossier dossier;
		if(map.containsKey("lastKnownName"))
		{
			dossier = new LegacyCompleteDossier();
			dossier.lastKnownName = map.get("lastKnownName").toString();
		}
		else dossier = new LegacyDossier();
		if(map.containsKey("infractions"))
		{
			dossier.infractions = Sets.newHashSet((List<String>) map.get("infractions"));
		}
		else dossier.infractions = Sets.newHashSet();
		dossier.mojangid = id;
		return dossier;
	}
}
