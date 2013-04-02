package com.rosaloves.bitlyj;

public final class Pair<A, B>
{
	private final A one;
	private final B two;

	public static <A, B> Pair<A, B> p(A one, B two)
	{
		return new Pair<A, B>(one, two);
	}

	private Pair(A one, B two)
	{
		this.one = one;
		this.two = two;
	}

	public A getOne()
	{
		return this.one;
	}

	public B getTwo()
	{
		return this.two;
	}

	public String toString()
	{
		return "Pair [one=" + this.one + ", two=" + this.two + "]";
	}
}
