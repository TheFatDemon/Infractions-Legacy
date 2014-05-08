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
import com.censoredsoftware.infractions.bukkit.dossier.CompleteDossier;
import com.censoredsoftware.infractions.bukkit.legacy.InfractionsPlugin;
import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.Sets;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Set;
import java.util.UUID;

public class LegacyCompleteDossier extends LegacyDossier implements CompleteDossier
{
	public LegacyCompleteDossier(UUID mojangId, String lastKnownName, Set<Infraction> infractions)
	{
		super(mojangId, infractions);
		this.lastKnownName = lastKnownName;
	}

	public LegacyCompleteDossier(UUID mojangId, String lastKnownName, Set<String> rawInfractions, Void ignored)
	{
		super(mojangId, rawInfractions, ignored);
		this.lastKnownName = lastKnownName;
	}

	@Override
	public OfflinePlayer getOfflinePlayer()
	{
		return Bukkit.getOfflinePlayer(this.lastKnownName);
	}

	@Override
	public String getLastKnownName()
	{
		return this.lastKnownName;
	}

	@Override
	public Set<InetAddress> getAssociatedIPAddresses()
	{
		return Sets.newHashSet(Collections2.transform(this.ipAddresses, new Function<String, InetAddress>()
		{
			@Override
			public InetAddress apply(String s)
			{
				try
				{
					return InetAddress.getByName(s);
				}
				catch(UnknownHostException e)
				{
					return null;
				}
			}
		}));
	}

	public Set<String> getRawAssociatedIPAddresses()
	{
		return this.ipAddresses;
	}

	public void addIPAddress(String address)
	{
		this.ipAddresses.add(address);
	}

	public void removeIPAddress(InetAddress address)
	{
		this.ipAddresses.remove(address.getHostName());
	}

	@Override
	public CompleteDossier complete(String ignored)
	{
		return this;
	}

	@Override
	public CompleteDossier complete()
	{
		return this;
	}

	public void update(final Player player)
	{
		final LegacyCompleteDossier dossier = this;
		Bukkit.getScheduler().scheduleAsyncDelayedTask(InfractionsPlugin.getInst(), new Runnable()
		{
			@Override public void run()
			{
				if(player.isOnline()) addIPAddress(player.getAddress().getHostName());
				dossier.lastKnownName = player.getName();
			}
		}, 40);
	}
}
