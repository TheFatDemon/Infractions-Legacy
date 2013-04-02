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
