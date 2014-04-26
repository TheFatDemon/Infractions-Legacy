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

import java.util.*;

class ParameterMap extends AbstractCollection<Map.Entry<String, List<String>>>
{
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private Map<String, List<String>> parameters = new HashMap();

	public void add(String name, String value)
	{
		List<String> values = this.parameters.get(name);
		if(values == null) values = new ArrayList<String>();
		values.add(value);
		this.parameters.put(name, values);
	}

	public List<String> get(String name)
	{
		return this.parameters.get(name);
	}

	public Iterator<Map.Entry<String, List<String>>> iterator()
	{
		return this.parameters.entrySet().iterator();
	}

	public int size()
	{
		return this.parameters.size();
	}

	public String toString()
	{
		return "ParameterMap [parameters=" + this.parameters + "]";
	}
}
