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
import com.censoredsoftware.infractions.bukkit.dossier.CompleteDossier;
import com.censoredsoftware.infractions.bukkit.dossier.Dossier;
import com.google.common.collect.Sets;

import java.util.Set;
import java.util.UUID;

public class LegacyDossier implements Dossier
{
	private UUID mojangid;
	private Set<Infraction> infractions;

	public LegacyDossier(UUID mojangId, Infraction... infractions)
	{
		this(mojangId, Sets.newHashSet(infractions));
	}

	public LegacyDossier(UUID mojangId, Set<Infraction> infractions)
	{
		this.mojangid = mojangId;
		this.infractions = infractions;
	}

	@Override
	public UUID getMojangId()
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
		return new LegacyCompleteDossier(getMojangId(), playerName, getInfractions());
	}

	@Override
	public CompleteDossier complete() throws ClassCastException
	{
		return (CompleteDossier) this;
	}
}
