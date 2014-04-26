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

public final class Bitly
{
	public static Provider as(String user, String apiKey)
	{
		return new SimpleProvider("http://bit.ly/", user, apiKey, "http://api.bit.ly/v3/");
	}

	public static MethodBase<Object> info(String value)
	{
		return Methods.info(value);
	}

	public static MethodBase<Object> info(String[] value)
	{
		return Methods.info(value);
	}

	public static MethodBase<Object> expand(String value)
	{
		return Methods.expand(value);
	}

	public static MethodBase<Object> expand(String[] value)
	{
		return Methods.expand(value);
	}

	public static MethodBase<Object> shorten(String longUrl)
	{
		return Methods.shorten(longUrl);
	}

	public static MethodBase<Object> clicks(String string)
	{
		return Methods.clicks(string);
	}

	public static MethodBase<Object> clicks(String[] string)
	{
		return Methods.clicks(string);
	}

	public static abstract interface Provider
	{
		public abstract <A> A call(BitlyMethod<A> paramBitlyMethod);

		public abstract String getUrl();
	}
}
