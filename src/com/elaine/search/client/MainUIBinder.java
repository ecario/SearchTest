package com.elaine.search.client;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.elaine.search.client.navigator.Navigator;
import com.elaine.search.client.navigator.NavigatorPath;
import com.elaine.search.client.navigator.Node;
import com.elaine.search.client.tree.TreeNode;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.logical.shared.OpenEvent;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlexTable.FlexCellFormatter;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.datepicker.client.DateBox;

public class MainUIBinder extends Composite
{
	private static MainUIBinderUiBinder uiBinder = GWT.create(MainUIBinderUiBinder.class);

	// service proxy
	private QueryExecutionServiceAsync queryExecutionService = GWT.create(QueryExecutionService.class);

	// maps path values to TreeItem nodes, for easier finding
	HashMap<String, TreeItem> nodeMap = new HashMap<String, TreeItem>();
	
	// state information
	TreeItem itemToExpand;
	String scopeName = null;
	String scopeValue = null;
	Date researchDate = null;
	String pitState = null;
	boolean noPIT = false;
	
	// boilerplate
	interface MainUIBinderUiBinder extends UiBinder<Widget, MainUIBinder>
	{ }

	public MainUIBinder()
	{
		initWidget(uiBinder.createAndBindUi(this));
		dbResearchDate.setValue(new Date());
	}

	//MessageBox errorBox = new MessageBox("Error");

	@UiField
	TextBox txtQuery;
	
	@UiField
	TextBox txtQueryType;
	
	@UiField
	Button btnSearch;
	
	@UiField
	Tree treeNavigation;
	
	@UiField
	FlexTable tblResults;
	
	@UiField
	ListBox lbPerms;

	@UiField
	ListBox lbPermType;

	@UiField
	DateBox dbResearchDate;
	
	@UiField
	ListBox lbPITState;
	
	@UiField
	CheckBox cbNoPIT;

	@UiField
	ListBox lbClient;

	
	@UiHandler("btnSearch")
	void onClick(ClickEvent event)
	{
		scopeName = null;
		scopeValue = null;
		runSearch();
	}

	@UiHandler("txtQuery")
	void onKeyPress(KeyPressEvent event)
	{
		if (event.getCharCode() == KeyCodes.KEY_ENTER)
		{
			scopeName = null;
			scopeValue = null;
			runSearch();
		}
	}
	
	
	@UiHandler("treeNavigation")
	void onOpen(OpenEvent<TreeItem> event)
	{
		TreeItem treeItem = event.getTarget();

		// there may be a dummy node there
		if (treeItem.getChildCount() == 1)
		{
			TreeItem child = treeItem.getChild(0);
			if (child.getUserObject() == null)
			{
				itemToExpand = treeItem;
				getChildNodes(treeItem);
			}
		}
	}
	
	@UiHandler("treeNavigation")
	public void onSelection(SelectionEvent<TreeItem> event)
	{
		TreeItem treeItem = event.getSelectedItem();
		
		Node innerData = (Node)treeItem.getUserObject();
		if (innerData instanceof NavigatorPath)
		{
			NavigatorPath navPath = (NavigatorPath)treeItem.getUserObject();
			scopeName = navPath.getLevelField();
			scopeValue = navPath.getName();
			runSearch();
			
		}
	}
	 
	
	protected void runSearch()
	{
		// 1. gather up all parameters in the search form

		String searchExpression = txtQuery.getText();

		String queryType = txtQueryType.getText();
		
		int subscriptionIndex = lbPerms.getSelectedIndex();
		String subscriptionLevel = lbPerms.getItemText(subscriptionIndex);

		int subTypeIndex = lbPermType.getSelectedIndex();
		String subType = lbPermType.getItemText(subTypeIndex);

		int pitStateIndex = lbPITState.getSelectedIndex();
		pitState = lbPITState.getValue(pitStateIndex);
		researchDate = dbResearchDate.getValue();
		noPIT = cbNoPIT.getValue();
		
		int clientIndex = lbClient.getSelectedIndex();
		String client = lbClient.getValue(clientIndex);
		
		QueryParams params = new QueryParams();
		params.setSearchExpression(searchExpression);
		params.setSearchType(queryType);
		params.setSearchProfile("standard");
		params.setSubscriptionLevel(subscriptionLevel);
		params.setSubscriptionType(subType);
		params.setClient(client);
		
		if (scopeName != null && scopeValue != null)
			params.addScopeFilter(scopeName + ":" + scopeValue);
		
		if (!noPIT)
		{
			params.setPITState(pitState);
			params.setResearchDate(researchDate);
		}

		
		
		// 2. send to back-end service which will execute the query and return some results
		
		// Initialize the service proxy
		if (queryExecutionService == null)
			queryExecutionService = GWT.create(QueryExecutionService.class);
		
		// set up the callback object
		AsyncCallback<QueryResults> callback = new AsyncCallback<QueryResults>()
		{
			public void onFailure(Throwable caught)
			{
				String details = caught.getMessage();
				
				//errorBox.showDialog(details);
				ErrorBoxUIBinder errorBox = new ErrorBoxUIBinder();
				errorBox.show(details);
				errorBox.center();
			}
		// 3. take navigation and results and format into respective visuals
			
			public void onSuccess(QueryResults results)
			{
				nodeMap.clear();
				formatSearchResults(results);
				treeNavigation.clear();
				formatNavigationResults(results);
			}
			
		};
		
		// make the call to the stock price service
		queryExecutionService.executeSearch(params, callback);
	}

	protected void formatSearchResults(QueryResults results)
	{
		//tblResults.clear();	// stupid GWT this doesn't work
		int count = tblResults.getRowCount();
		while (count > 0) 
		{
		   tblResults.removeRow(0); 
		   count = tblResults.getRowCount();
		}
		
		String message = results.getNumberResults() + " results found in " + results.getQueryTime() + " ms.";
		//htmlResultStats.setHTML(message);
		
		tblResults.setHTML(0, 0, message);
		//tblResults.getFlexCellFormatter().setHeight(0,0,"24");		
		tblResults.getFlexCellFormatter().setColSpan(0,0,2);		

		tblResults.setHTML(1, 0, " ");
		tblResults.setHTML(1, 1, " ");

		List<QueryResult> resultItems = results.getResults();
		
		if (resultItems == null)
			return;
		
		int row = 2;
		for (QueryResult doc : resultItems)
		{
			Map<String, String> docFields = doc.getFieldMap();
			String title = docFields.get("displayTitle");
			String teaser = docFields.get("wkteaser");
			String rank = docFields.get("score");
			
			FlexCellFormatter fmt = tblResults.getFlexCellFormatter();
			
			tblResults.setHTML(row, 0, rank);
			fmt.setStylePrimaryName(row, 0, "resultRank");
			
			tblResults.setHTML(row, 1, title);
			fmt.setStylePrimaryName(row, 1, "resultTitle");
			
			tblResults.setHTML(row+1, 0, " ");	// empty column
			
			tblResults.setHTML(row+1, 1, teaser);
			fmt.setStylePrimaryName(row+1, 1, "resultTeaser");

			// empty row
			tblResults.setHTML(row+2, 0, " ");
			tblResults.setHTML(row+2, 1, " ");
			
			row +=3;
		}
		
	}
	
	protected void formatNavigationResults(QueryResults results)
	{
		HashMap<String, Navigator> navigators = results.getNavigators();

		// for each navigator add a top level node
		for (Navigator navigator : navigators.values())
		{
			TreeNode<Node> rootNav = navigator.getNavigatorRoot();
			TreeItem navHeader = createTreeItem(rootNav);
			treeNavigation.addItem(navHeader);
			if (rootNav.hasChildren())
				addChildren(rootNav, navHeader);		// recurse children
			navHeader.setState(true);					// open this node
		}
	}
	
	protected void addChildren(TreeNode<Node> sourceParent, TreeItem targetParent)
	{
		List<TreeNode<Node>> children = sourceParent.getChildren();
		for (TreeNode<Node> child : children)
		{
			TreeItem childItem = createTreeItem(child);
			targetParent.addItem(childItem);
			if (child.hasChildren())
				addChildren(child, childItem);
			else
				// add dummy item to show plus so we can expand
				childItem.addItem("working...");
		}
	}
	
	protected void getChildNodes(TreeItem parent)
	{
		// execute the same query, restrict the nodes returned to only those that
		// start with the node we're at, looking in the level field 1 more than the one
		// we're at
		
		Node node = (Node)parent.getUserObject();
		if (node instanceof NavigatorPath)
		{
			NavigatorPath currentNode = (NavigatorPath)node;
			
			// 1. gather up all parameters in the search form
			String searchExpression = txtQuery.getText();
			String queryType = txtQueryType.getText();
			String pathString = currentNode.getRawValue();
			String levelField = incrementLevelField(currentNode.getLevelField());

			int subscriptionIndex = lbPerms.getSelectedIndex();
			String subscriptionLevel = lbPerms.getItemText(subscriptionIndex);

			int subTypeIndex = lbPermType.getSelectedIndex();
			String subType = lbPermType.getItemText(subTypeIndex);
			
			int clientIndex = lbClient.getSelectedIndex();
			String client = lbClient.getValue(clientIndex);


			QueryParams params = new QueryParams();
			params.setSearchExpression(searchExpression);
			params.setSearchType(queryType);
			params.setSearchProfile("expandNode");
			params.setSubscriptionLevel(subscriptionLevel);
			params.setSubscriptionType(subType);
			params.setClient(client);
			
			params.setNavPrefixFilter(pathString);
			params.addNavField(levelField);
			
			if (!noPIT)
			{
				params.setPITState(pitState);
				params.setResearchDate(researchDate);
			}

			// 2. send to back-end service which will execute the query and return some results
			
			// Initialize the service proxy
			if (queryExecutionService == null)
				queryExecutionService = GWT.create(QueryExecutionService.class);
			
			// set up the callback object
			AsyncCallback<QueryResults> callback = new AsyncCallback<QueryResults>()
			{
				public void onFailure(Throwable caught)
				{
					String details = caught.getMessage();
					
					//errorBox.showDialog(details);
					ErrorBoxUIBinder errorBox = new ErrorBoxUIBinder();
					errorBox.show(details);
					errorBox.center();
				}
			// 3. take navigation and results and format into respective visuals
				
				public void onSuccess(QueryResults results)
				{
					//formatSearchResults(results);
					//treeNavigation.clear();
					expandNavigationResults(results);
				}
				
			};
			
			// make the call to the stock price service
			queryExecutionService.executeSearch(params, callback);			
		}
	}
	
	protected String incrementLevelField(String fieldName)
	{
		try
		{
			String[] parts = fieldName.split("_");
			int fieldNum = Integer.parseInt(parts[1]);
			fieldNum++;
			
			String newFieldName = parts[0] + "_" + fieldNum;
			return newFieldName;
		}
		catch (Exception e)
		{
			ErrorBoxUIBinder errorBox = new ErrorBoxUIBinder();
			errorBox.show("Problem incrementing navigator field " + fieldName);
			errorBox.center();
		}

		return null;
	}
	
	protected void expandNavigationResults(QueryResults results)
	{
		HashMap<String, Navigator> navigators = results.getNavigators();

		// for each navigator returned...
		for (Navigator navigator : navigators.values())
		{
			// get the data root from the navigator
			TreeNode<Node> rootNav = navigator.getNavigatorRoot();

			// get the root node from the display tree...
			TreeItem rootNode = findMatchingRoot(navigator);
			
			// ...if it's not found (it really should be) 
			if (rootNode == null)
			{
				// ...create it
				TreeItem navHeader = createTreeItem(rootNav);
				// ...and add to the display tree
				treeNavigation.addItem(navHeader);
				rootNode = navHeader;
			}
			
			// ... if it has children
			if (rootNav.hasChildren())
				//... merge them into the display tree (recurse)
				mergeTrees(rootNav);
			else	// no navigation results returned - end of the line
				itemToExpand.removeItems();
				
		}
	}
	
	protected void mergeTrees(TreeNode<Node> sourceParent)
	{
		// get the children of the data tree node
		List<TreeNode<Node>> children = sourceParent.getChildren();

		// this source parent has no children
		if (children.size() == 0)
		{
			//look up the path for this in the nodeMap
			NavigatorPath navPath = (NavigatorPath)sourceParent.getData();
			TreeItem thisItem = nodeMap.get(navPath.getName());
			// add a dummy, so we can expand later
			thisItem.addItem("working...");
			return;
		}
		
		// for each child
		for (TreeNode<Node> child : children)
		{
			// get the inner data
			NavigatorPath navPath = (NavigatorPath)child.getData();
			String parentPath = navPath.getParent();
			
			//look up the parent path in the nodeMap
			TreeItem parentItem = nodeMap.get(parentPath);
			
			//...if it's there (it should be)
			if (parentItem != null)
			{
				//...remove dummy nodes 
				removeDummyChild(parentItem);
				// ...add this new node
				TreeItem item = createTreeItem(child);
				parentItem.addItem(item);
				mergeTrees(child);
			}
		}			
	}
	
	protected TreeItem findMatchingRoot(Node nodeToFind)
	{
		return nodeMap.get(nodeToFind.getName());
		/*
		Iterator<TreeItem> i = treeNavigation.treeItemIterator();
		while (i.hasNext())
		{
			TreeItem item = i.next();
			Node node = (Node)item.getUserObject();
			if (node.getName().equals(nodeToFind.getName()))		// found the nav root
				return item;
		}
		return null;		// not found
		*/
	}
	
	protected TreeItem createTreeItem(TreeNode<Node> node)
	{
		TreeItem item = null;
		Node n = node.getData();
		if (n instanceof NavigatorPath)
		{
			NavigatorPath navPath = (NavigatorPath)n;
			String displayName = navPath.getDisplayName();
			long hitCount = navPath.getCount();
			item = new TreeItem(displayName + "[" + hitCount + "]");
			item.setUserObject(navPath);
			nodeMap.put(navPath.getName(), item);
		}
		else if (n instanceof Navigator)
		{
			Navigator navigator = (Navigator)n;
			item = new TreeItem(navigator.getName());
			item.setUserObject(navigator);
			nodeMap.put(navigator.getName(), item);
		}
		return item;
	}
	
	protected void removeDummyChild(TreeItem item)
	{
		if (item.getChildCount() == 1)
		{
			TreeItem possibleDummy = item.getChild(0);
			if (possibleDummy.getUserObject() == null)
				item.removeItems();
		}
	}
}
