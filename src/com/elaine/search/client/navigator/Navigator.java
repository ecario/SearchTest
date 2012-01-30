package com.elaine.search.client.navigator;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.elaine.search.client.tree.TreeNode;


public class Navigator implements Node, Serializable
{
	String name;
	long count;
	
	List<NavigatorPath> paths = null;
	
	TreeNode<Node> rootNav = null;
	
	@Override
	public String getName() 				{ return name; }

	@Override
	public String toString()				{ return name; }

	public long getCount() 					{ return count; }
	public List<NavigatorPath> getPaths() 	{ return paths; }
	
	public TreeNode<Node> getNavigatorRoot()	{ return rootNav; }

	public void setName(String name) 					{ this.name = name; }
	public void setCount(long count) 					{ this.count = count; }
	public void setPaths(List<NavigatorPath> paths) 	{ this.paths = paths; }
	
	
	public void addPath(NavigatorPath path)
	{
		if (paths == null)
			paths = new ArrayList<NavigatorPath>();
		
		paths.add(path);
	}
	
	public void buildTree()
	{
		rootNav = new TreeNode<Node>(this);
		
		// contains map of path key (raw full path) to node
		HashMap<String, TreeNode<Node>> nodes = new HashMap<String, TreeNode<Node>>();
		
		if (paths == null)		// no path values returned
			return;
		
		for (NavigatorPath path : paths)
		{
			TreeNode<Node> treeNode = new TreeNode<Node>(path);
			nodes.put(path.getName(), treeNode);				// add to nodes map
		}
	
		/*
		// validate the tree - make sure all nodes will have a intermediate parents in the tree
		// this is done for when we're only asking for a single level of the tree - saves a bit
		// of work later when merging the tree data with the display tree
		for (String path : nodes.keySet())
		{
			TreeNode<Node> node = nodes.get(path);
			String parent = ((NavigatorPath)node.getData()).getParent();
			if (parent != null)
			{
				TreeNode<Node> parentNode = nodes.get(parent);
				
				// this will occur when we're expanding nodes - we only get the level back 
				// that we ask for, so the real parent may have been retrieved on an earlier click.
				// So, we create intermediate parents for it:
				if (parentNode == null)
					createIntermediateParentNodes(node);
			}
		}
		*/
			
		// We've collected all the information about the nodes
		// start establishing parent-child relationships
		for (String path : nodes.keySet())
		{
			TreeNode<Node> node = nodes.get(path);
			String parent = ((NavigatorPath)node.getData()).getParent();
			if (parent != null)
			{
				TreeNode<Node> parentNode = nodes.get(parent);
				
				// this will occur when we're expanding nodes - we only get the level back 
				// that we ask for, so the real parent may have been retrieved on an earlier click.
				
				/*
				// So, we create intermediate parents for it:
				if (parentNode == null)
					createIntermediateParentNodes(node);
				*/
				if (parentNode == null)
					rootNav.addChild(node);		// add to root, we'll deal later
				else
					parentNode.addChild(node);
			}
			else
				rootNav.addChild(node);
		}
	}
	
	/*
	protected void createIntermediateParentNodes(TreeNode<Node> node)
	{
		String navPath = node.getData().getName();
		
		int levels = navPath.split("\\|").length;
		
		String[] parentPaths = new String[levels];
		
		String startPath = navPath;
		for (int i = 0; i < levels-1; i++)
		{
			String parent = calcNodeParent(startPath);
			parentPaths[i] = parent;
			startPath = parent;
		}
		
		for (String parentPath : parentPaths)
		{
			
			String rawValue;
			long count;
			
			String levelField;
			String masterField;
			String parent;
			
			
			// create stub navigator path
			// wrap in tree node
			// insert into hashmap
		}
		
	}

	
	protected String calcNodeParent(String path)
	{
		int pos = path.lastIndexOf('|');
		if (pos < 0)		// first node
			return null;
		
		String parent = path.substring(0,pos);
		return parent;
	}
	*/
}
