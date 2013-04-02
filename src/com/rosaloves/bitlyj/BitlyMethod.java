package com.rosaloves.bitlyj;

import org.w3c.dom.Document;

public abstract interface BitlyMethod<A>
{
	public abstract String getName();

	public abstract Iterable<Pair<String, String>> getParameters();

	public abstract A apply(Bitly.Provider paramProvider, Document paramDocument);
}
