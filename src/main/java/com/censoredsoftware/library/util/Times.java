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

package com.censoredsoftware.library.util;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class Times
{
	/**
	 * Return the seconds since the provided time.
	 *
	 * @param time The provided time.
	 * @return Seconds.
	 */
	public static double getSeconds(long time)
	{
		return (double) TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - time);
	}

	/**
	 * Return the minutes since the provided time.
	 *
	 * @param time The provided time.
	 * @return Minutes.
	 */
	public static double getMinutes(long time)
	{
		return (double) TimeUnit.MILLISECONDS.toMinutes(System.currentTimeMillis() - time);
	}

	/**
	 * Return the hours since the provided time.
	 *
	 * @param time The provided time.
	 * @return Hours.
	 */
	public static double getHours(long time)
	{
		return (double) TimeUnit.MILLISECONDS.toHours(System.currentTimeMillis() - time);
	}

	/**
	 * Return a readable String of the amount of time since a provided time.
	 *
	 * @param time  The provided time.
	 * @param round Round to an int.
	 * @return The readable String.
	 */
	public static String getTimeTagged(long time, boolean round)
	{
		DecimalFormat format = round ? new DecimalFormat("#") : new DecimalFormat("#.##");
		if(getHours(time) >= 1) return format.format(getHours(time)) + "h";
		else if(Double.valueOf(format.format(getMinutes(time))) >= 1) return format.format(getMinutes(time)) + "m";
		else return format.format(getSeconds(time)) + "s";
	}

	public static String prettyDate(long time)
	{
		long diff = (System.currentTimeMillis() - time) / 1000;
		double day_diff = Math.floor(diff / 86400);

		if(day_diff == 0 && diff < 60) return "just now";
		if(diff < 120) return "1 minute ago";
		if(diff < 3600) return Math.floor(diff / 60) + " minutes ago";
		if(diff < 7200) return "1 hour ago";
		if(diff < 86400) return Math.floor(diff / 3600) + " hours ago";
		if(day_diff == 1) return "Yesterday";
		if(day_diff < 7) return day_diff + " days ago";
		if(day_diff < 31) return Math.ceil(day_diff / 7) + " weeks ago";

		return new SimpleDateFormat("yyyy-MM-dd").format(new Date(time));
	}

	public static String timeSincePretty(long time)
	{
		long diff = (System.currentTimeMillis() - time) / 1000;
		double day_diff = Math.floor(diff / 86400);

		if(day_diff == 0 && diff < 60) return "in under a minute";
		if(diff < 120) return "in 1 minute";
		if(diff < 3600) return "in " + Math.floor(diff / 60) + " minutes";
		if(diff < 7200) return "in 1 hour";
		if(diff < 86400) return "in " + Math.floor(diff / 3600) + " hours";
		if(day_diff == 1) return "in over a day";
		if(day_diff < 7) return "in " + day_diff + " days";
		return "in " + Math.ceil(day_diff / 7) + " weeks";
	}
}
