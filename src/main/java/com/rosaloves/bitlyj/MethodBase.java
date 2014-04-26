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

package com.rosaloves.bitlyj;

import java.util.Arrays;

public abstract class MethodBase<A> implements BitlyMethod<A>
{
	private final String name;
	private final Iterable<Pair<String, String>> parameters;

	public MethodBase(String name, Pair<String, String>[] parameters)
	{
		this(name, Arrays.asList(parameters));
	}

	public MethodBase(String name, Iterable<Pair<String, String>> parameters)
	{
		this.name = name;
		this.parameters = parameters;
	}

	public String getName()
	{
		return this.name;
	}

	public Iterable<Pair<String, String>> getParameters()
	{
		return this.parameters;
	}

	public String toString()
	{
		return getClass().getSimpleName() + " [name=" + this.name + ", parameters=" + this.parameters + "]";
	}
}
