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

import com.censoredsoftware.infractions.bukkit.Database;
import com.censoredsoftware.infractions.bukkit.Infraction;
import com.censoredsoftware.infractions.bukkit.dossier.CompleteDossier;
import com.censoredsoftware.infractions.bukkit.dossier.Dossier;
import com.censoredsoftware.infractions.bukkit.evidence.Evidence;
import com.censoredsoftware.infractions.bukkit.legacy.data.DataManager;
import com.censoredsoftware.library.helper.MojangIdProvider;
import com.google.common.collect.Sets;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentMap;

public class LegacyDatabase implements Database
{
	ConcurrentMap<UUID, Dossier> getDossierMap()
	{
		return DataManager.getManager().getMapFor(LegacyDossier.class);
	}

	ConcurrentMap<String, LegacyInfraction> getInfractionMap()
	{
		return DataManager.getManager().getMapFor(LegacyInfraction.class);
	}

	ConcurrentMap<String, LegacyIssuer> getIssuerMap()
	{
		return DataManager.getManager().getMapFor(LegacyIssuer.class);
	}

	@Override
	public CompleteDossier getCompleteDossier(UUID playerId) throws NullPointerException
	{
		Dossier dossier = getDossier(playerId);
		if(dossier instanceof LegacyCompleteDossier) return (CompleteDossier) dossier;
		throw new NullPointerException("Incomplete dossier.");
	}

	@Override
	public CompleteDossier getCompleteDossier(String playerName)
	{
		UUID id = MojangIdProvider.getId(playerName);
		if(id != null)
		{
			Dossier dossier = getDossier(id);
			if(!(dossier instanceof LegacyCompleteDossier))
				getDossierMap().put(id, dossier.complete(playerName));
			return getCompleteDossier(id);
		}
		throw new NullPointerException("No such player exists.");
	}

	@Override
	public CompleteDossier getCompleteDossier(Player player)
	{
		return getCompleteDossier(player.getName());
	}

	@Override
	public Dossier getDossier(UUID playerId)
	{
		if(playerId == null) return null;
		getDossierMap().putIfAbsent(playerId, new LegacyDossier(playerId));
		return getDossierMap().get(playerId);
	}

	@Override
	public Dossier getDossier(String playerName)
	{
		UUID id = MojangIdProvider.getId(playerName);
		if(id != null)
			return getDossier(id);
		throw new NullPointerException("No such player exists.");
	}

	@Override
	public void addDossier(Dossier dossier)
	{
		getDossierMap().put(dossier.getId(), dossier);
	}

	@Override
	public void removeDossier(Dossier dossier)
	{
		getDossierMap().remove(dossier.getId());
	}

	@Override
	public void removeDossier(UUID playerId)
	{
		getDossierMap().remove(playerId);
	}

	@Override
	public Set<Dossier> allDossiers()
	{
		return Sets.newHashSet(getDossierMap().values());
	}

	@Override
	public Set<Infraction> allInfractions()
	{
		Set<Infraction> infractions = Sets.newHashSet();
		for(Dossier dossier : allDossiers())
			infractions.addAll(dossier.getInfractions());
		return infractions;
	}

	@Override
	public Set<Evidence> allEvidence()
	{
		Set<Evidence> evidence = Sets.newHashSet();
		for(Infraction infraction : allInfractions())
			evidence.addAll(infraction.getEvidence());
		return evidence;
	}

	@Override
	public Plugin getPlugin()
	{
		return null;
	}
}
