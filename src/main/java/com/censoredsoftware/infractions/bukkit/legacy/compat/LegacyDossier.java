/*
 * Copyright (c) 2014 Alexander Chauncey
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
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

import javax.annotation.Nullable;
import java.util.*;

public class LegacyDossier extends DataAccess<UUID, LegacyDossier> implements Dossier
{
	private UUID mojangid;
	private Set<Infraction> infractions;
	protected String lastKnownName;

	LegacyDossier()
	{
	}

	public LegacyDossier(UUID mojangId, Infraction... infractions)
	{
		this(mojangId, Sets.newHashSet(infractions));
	}

	public LegacyDossier(UUID mojangId, Set<Infraction> infractions)
	{
		this.mojangid = mojangId;
		this.infractions = infractions;
	}

	@Register(idType = IdType.UUID)
	public LegacyDossier(UUID id, ConfigurationSection conf)
	{
		this.mojangid = id;

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
		return infractions;
	}

	@Override
	public void cite(Infraction infraction)
	{
		infractions.add(infraction);
	}

	@Override
	public void acquit(Infraction infraction)
	{
		infractions.remove(infraction);
	}

	@Override
	public CompleteDossier complete(String playerName)
	{
		return new LegacyCompleteDossier(getId(), playerName, getInfractions());
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

		List<String> infractionList = Lists.newArrayList(Collections2.transform(infractions, new Function<Infraction, String>()
		{
			@Override
			public String apply(Infraction infraction)
			{
				return MiscUtil.getId(infraction);
			}
		}));

		if(!infractionList.isEmpty()) map.put("infractions", infractionList);

		return map;
	}

	@SuppressWarnings("unchecked")
	public static Dossier unserialize(UUID id, Map<String, Object> map)
	{
		LegacyDossier dossier;
		if(map.isEmpty()) return Infractions.getDossier(id);
		if(map.containsKey("lastKnownName"))
		{
			dossier = new LegacyCompleteDossier();
			dossier.lastKnownName = map.get("lastKnownName").toString();
		}
		else dossier = new LegacyDossier();
		if(map.containsKey("infractions"))
		{
			dossier.infractions = Sets.newHashSet(Collections2.transform((List<String>) map.get("infractions"), new Function<String, Infraction>()
			{
				@Override
				public Infraction apply(@Nullable String s)
				{
					try
					{
						return ((LegacyDatabase) Infractions.getDatabase()).INFRACTION_MAP.get(s);
					}
					catch(Exception ignored)
					{
					}
					return null;
				}
			}));
		}
		return dossier;
	}
}
