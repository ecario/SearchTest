package com.elaine.search.client;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.elaine.search.client.navigator.Navigator;
import com.elaine.search.client.navigator.Node;
import com.elaine.search.client.tree.TreeNode;

public class QueryResults implements Serializable
{
	long numberResults;
	long startPosition;
	float maxScore;
	long queryTime;
	
	List<QueryResult> results; 
	
	Map<String, String> headerMap;
	
	HashMap<String, Navigator> navigators;
	
	public QueryResults()
	{}

	public long getNumberResults() 					{ return numberResults; }
	public long getStartPosition() 					{ return startPosition; }
	public float getMaxScore() 						{ return maxScore; }
	public long getQueryTime()						{ return queryTime; }
	public Map<String, String> getHeaderMap() 		{ return headerMap; }
	public List<QueryResult> getResults()			{ return results; }
	public HashMap<String,Navigator> getNavigators()
													{ return navigators; }
	
	public void setNumberResults(long numberResults)				{ this.numberResults = numberResults; }
	public void setStartPosition(long startPosition) 				{ this.startPosition = startPosition; }
	public void setMaxScore(float maxScore) 						{ this.maxScore = maxScore; }
	public void setQueryTime(long queryTime)						{ this.queryTime = queryTime; }
	public void setHeaderMap(Map<String, String> headerMap) 		{ this.headerMap = headerMap; }
	public void setResults(List<QueryResult> results)				{ this.results = results; }
	public void setNavigators(HashMap<String, Navigator> navigators)
																	{ this.navigators = navigators; }
	
	public void addHeaderField(String name, String value)
	{
		if (headerMap == null)
			headerMap = new HashMap<String, String>();
		
		headerMap.put(name, value);
	}
	
	public String getHeaderValue(String name)
	{
		if (headerMap == null)
			return null;
		else
			return headerMap.get(name);
	}

	public void addResult(QueryResult result)
	{
		if (results == null)
			results = new ArrayList<QueryResult>();
		
		results.add(result);
	}
	
}
