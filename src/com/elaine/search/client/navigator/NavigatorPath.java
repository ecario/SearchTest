package com.elaine.search.client.navigator;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.elaine.search.client.tree.TreeNode;

public class NavigatorPath implements Node, Serializable
{
	String rawValue;
	long count;
	
	String levelField;
	String masterField;
	String parent;
	
	List<NavigatorPathPart> nodes;
	
	TreeNode<Node> miniTree;
	

	@Override
	public String getName() 					{ return rawValue; }
	
	@Override
	public String toString() 					{ return rawValue; }
	
	public String getRawValue() 				{ return rawValue; }
	public long getCount() 						{ return count; }
	public String getLevelField() 				{ return levelField; }
	public String getMasterField() 				{ return masterField; }
	public String getParent() 					{ return parent; }
	public List<NavigatorPathPart> getNodes() 	{ return nodes; }

	public void setCount(long count) 						{ this.count = count; }
	public void setNodes(List<NavigatorPathPart> nodes) 	{ this.nodes = nodes; }
	public void setLevelField(String levelField) 			{ this.levelField = levelField; }
	public void setMasterField(String masterField) 			{ this.masterField = masterField; }
	public void setParent(String parent) 					{ this.parent = parent; }
	

	public void setRawValue(String rawValue) 			
	{ 
		this.rawValue = rawValue;
		parseNodes();
	}
	
	protected void parseNodes()
	{
		nodes = new ArrayList<NavigatorPathPart>();

		NavigatorPathPart node = new NavigatorPathPart();

		// separate each node in the path
		if (rawValue.indexOf("|") < 0)
		{
			node.setRawValue(rawValue);
			nodes.add(node);
		}	
		else
		{
			// pipe is a special regex char that must be escaped
			String[] tokens = rawValue.split("\\|");
			for (String token : tokens)
			{
				node.setRawValue(token);
				nodes.add(node);
			}
		}
	}
	
	public String getDisplayName()
	{
		if (nodes == null)
			return "Unknown";		// this shouldn't happen
		
		int numParts = nodes.size();
		NavigatorPathPart lastPart = nodes.get(numParts-1);
		String hashcode = lastPart.getNodeValue();
		return hashcode;
		
	}
}
