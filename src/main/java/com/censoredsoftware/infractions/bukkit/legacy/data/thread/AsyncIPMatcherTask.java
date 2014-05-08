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

package com.censoredsoftware.infractions.bukkit.legacy.data.thread;

import com.censoredsoftware.infractions.bukkit.Infractions;
import com.censoredsoftware.infractions.bukkit.dossier.CompleteDossier;
import com.censoredsoftware.infractions.bukkit.dossier.Dossier;
import com.censoredsoftware.infractions.bukkit.legacy.InfractionsPlugin;
import com.censoredsoftware.infractions.bukkit.legacy.compat.LegacyCompleteDossier;
import com.google.common.base.Supplier;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.google.common.collect.Sets;

import java.net.InetAddress;
import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class AsyncIPMatcherTask implements Runnable
{
	private static final Multimap<InetAddress, UUID> IP_MAP;
	private static final Multimap<InetAddress, String> IP_MAP_NAME;
	private static final Multimap<UUID, String> RELATIVE_NAMES;

	static
	{
		IP_MAP = Multimaps.newMultimap(new ConcurrentHashMap<InetAddress, Collection<UUID>>(), new Supplier<Collection<UUID>>()
		{
			@Override
			public Collection<UUID> get()
			{
				return Lists.newArrayList();
			}
		});
		IP_MAP_NAME = Multimaps.newMultimap(new ConcurrentHashMap<InetAddress, Collection<String>>(), new Supplier<Collection<String>>()
		{
			@Override
			public Collection<String> get()
			{
				return Lists.newArrayList();
			}
		});
		RELATIVE_NAMES = Multimaps.newMultimap(new ConcurrentHashMap<UUID, Collection<String>>(), new Supplier<Collection<String>>()
		{
			@Override
			public Collection<String> get()
			{
				return Lists.newArrayList();
			}
		});
	}

	public static Collection<UUID> getAccounts(InetAddress address)
	{
		return IP_MAP.get(address);
	}

	public static Collection<String> getRelatives(UUID id)
	{
		return RELATIVE_NAMES.get(id);
	}

	@Override
	public void run()
	{
		InfractionsPlugin.getInst().getLogger().info("Rebuilding IP relationship cache...");
		for(Dossier dossier : Infractions.allDossiers())
		{
			if(dossier instanceof LegacyCompleteDossier && !((LegacyCompleteDossier) dossier).getRawAssociatedIPAddresses().isEmpty())
			{
				UUID id = dossier.getId();
				String name = ((LegacyCompleteDossier) dossier).getLastKnownName();
				for(InetAddress address : ((LegacyCompleteDossier) dossier).getAssociatedIPAddresses())
				{
					IP_MAP.put(address, id);
					IP_MAP_NAME.put(address, name);
				}
			}
		}
		for(String name : Sets.newHashSet(IP_MAP_NAME.values()))
		{
			CompleteDossier dossier = Infractions.getCompleteDossier(name);
			UUID id = dossier.getId();
			for(InetAddress address : dossier.getAssociatedIPAddresses())
			{
				for(String other : IP_MAP_NAME.get(address))
				{
					if(!dossier.getLastKnownName().equals(other)) RELATIVE_NAMES.put(id, other);
				}
			}
		}
		InfractionsPlugin.getInst().getLogger().info("IP relationships have been cached successfully.");
	}
}
