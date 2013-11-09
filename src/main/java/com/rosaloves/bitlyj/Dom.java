package com.rosaloves.bitlyj;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public final class Dom
{
	public static String getTextContent(Node n)
	{
		StringBuilder sb = new StringBuilder();
		NodeList nl = n.getChildNodes();
		for(int i = 0; i < nl.getLength(); i++)
		{
			Node child = nl.item(i);
			if(child.getNodeType() == 3) sb.append(child.getNodeValue());
		}
		return sb.toString();
	}
}
