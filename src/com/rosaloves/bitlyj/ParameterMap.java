package com.rosaloves.bitlyj;

import java.util.AbstractCollection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

class ParameterMap extends AbstractCollection<Map.Entry<String, List<String>>> {
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private Map<String, List<String>> parameters = new HashMap();

	public void add(String name, String value) {
		List<String> values = (List<String>) this.parameters.get(name);
		if (values == null)
			values = new ArrayList<String>();
		values.add(value);
		this.parameters.put(name, values);
	}

	public List<String> get(String name) {
		return (List<String>) this.parameters.get(name);
	}

	public Iterator<Map.Entry<String, List<String>>> iterator() {
		return this.parameters.entrySet().iterator();
	}

	public int size() {
		return this.parameters.size();
	}

	public String toString() {
		return "ParameterMap [parameters=" + this.parameters + "]";
	}
}