package com.censoredsoftware.infractions.bukkit.legacy.compat;

import com.censoredsoftware.infractions.bukkit.Database;
import com.censoredsoftware.infractions.bukkit.Infraction;
import com.censoredsoftware.infractions.bukkit.dossier.CompleteDossier;
import com.censoredsoftware.infractions.bukkit.dossier.Dossier;
import com.censoredsoftware.infractions.bukkit.evidence.Evidence;
import com.censoredsoftware.library.helper.MojangIdProvider;
import com.google.common.collect.Sets;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class LegacyDatabase implements Database
{
	private final ConcurrentMap<UUID, Dossier> DOSSIER_MAP = new ConcurrentHashMap<UUID, Dossier>();

	@Override
	public CompleteDossier getCompleteDossier(UUID playerId) throws NullPointerException
	{
		Dossier dossier = getDossier(playerId);
		if(dossier instanceof CompleteDossier) return (CompleteDossier) dossier;
		throw new NullPointerException("Incomplete dossier.");
	}

	@Override
	public CompleteDossier getCompleteDossier(String playerName)
	{
		UUID id = MojangIdProvider.getId(playerName);
		if(id != null)
		{
			DOSSIER_MAP.putIfAbsent(id, new LegacyCompleteDossier(id, playerName));
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
		DOSSIER_MAP.putIfAbsent(playerId, new LegacyDossier(playerId));
		return DOSSIER_MAP.get(playerId);
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
		DOSSIER_MAP.put(dossier.getMojangId(), dossier);
	}

	@Override
	public void removeDossier(Dossier dossier)
	{
		DOSSIER_MAP.remove(dossier.getMojangId());
	}

	@Override
	public void removeDossier(UUID playerId)
	{
		DOSSIER_MAP.remove(playerId);
	}

	@Override
	public Set<Dossier> allDossiers()
	{
		return Sets.newHashSet(DOSSIER_MAP.values());
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
