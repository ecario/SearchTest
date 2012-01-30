package com.elaine.search.client.navigator;

import java.io.Serializable;

public class NavigatorPathPart implements Serializable
{
	String rawValue;
	
	String orderNum;
	String typeInd;
	String nodeValue;
	
	public String getRawValue() 	{ return rawValue; }
	public String getOrderNum() 	{ return orderNum; }
	public String getTypeInd() 		{ return typeInd; }
	public String getNodeValue() 	{ return nodeValue; }
	
	public void setOrderNum(String orderNum) 	{ this.orderNum = orderNum; }
	public void setTypeInd(String typeInd) 		{ this.typeInd = typeInd; }
	public void setNodeValue(String nodeValue) 	{ this.nodeValue = nodeValue; }

	public void setRawValue(String rawValue) 	
	{ 	
		this.rawValue = rawValue; 
		parseNode();
	}

	protected void parseNode()
	{
		orderNum = rawValue.substring(0,5);
		typeInd = rawValue.substring(5,6);
		nodeValue = rawValue.substring(6);
	}
	
}
