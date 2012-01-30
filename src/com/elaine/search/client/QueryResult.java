package com.elaine.search.client;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class QueryResult implements Serializable
{
	Map<String, String> fieldMap;

	public Map<String, String> getFieldMap() 	{ return fieldMap; }
	
	public void setFieldMap(Map<String, String> fieldMap) 	{ this.fieldMap = fieldMap; }
	
	public void addField(String name, String value)
	{
		if (fieldMap == null)
			fieldMap = new HashMap<String, String>();
		
		fieldMap.put(name, value);
	}
	
	public String getFieldValue(String name)
	{
		if (fieldMap == null)
			return null;
		else
			return fieldMap.get(name);
	}

}
