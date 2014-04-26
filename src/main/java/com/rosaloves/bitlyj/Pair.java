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
