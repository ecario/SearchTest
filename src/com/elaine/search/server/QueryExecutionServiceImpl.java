package com.elaine.search.server;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.response.FacetField;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.util.NamedList;

import com.elaine.search.client.QueryExecutionService;
import com.elaine.search.client.QueryParams;
import com.elaine.search.client.QueryResult;
import com.elaine.search.client.QueryResults;
import com.elaine.search.client.navigator.NavigatorPath;
import com.elaine.search.client.navigator.Navigator;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class QueryExecutionServiceImpl 
	extends RemoteServiceServlet
	implements QueryExecutionService
{
	Logger log = Logger.getLogger( "com.elaine" );
	PropertiesHandler props = PropertiesHandler.getInstance();
	
	SimpleDateFormat outfmt = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");

	
	@Override
	public QueryResults executeSearch(QueryParams params)
		throws Exception
	{
		
		try
		{
			String searchProfile = params.getSearchProfile();
			if (searchProfile == null)
				searchProfile = "standard";
			
			// TODO add some of these params to the front end
			SolrQuery solrQuery = new  SolrQuery()
			.setQuery(params.getSearchExpression())
			.setQueryType(params.getSearchType())
		    .setStart(Integer.parseInt(props.getProperty(searchProfile + ".startRow")))
		    .setRows(Integer.parseInt(props.getProperty(searchProfile + ".endRow")))
		    .setParam("echoParams", props.getProperty(searchProfile + ".echoParams"))
			.setFacet(Boolean.valueOf(props.getProperty(searchProfile + ".facetOn")))
			.setFacetMinCount(Integer.parseInt(props.getProperty(searchProfile + ".facetMinCount")))
			.setFacetSort(props.getProperty(searchProfile + ".facetSort"));

			String[] fields = props.getPropertyArray(searchProfile + ".returnedFields");
			for (String field : fields)
				solrQuery.addField(field);

		    String subscriptionQuery = buildPermsClause(params.getSubscriptionLevel(), params.getSubscriptionType());
		    if (subscriptionQuery.length() > 0)
		    	solrQuery.addFilterQuery(subscriptionQuery);
			
		    if (params.getScopeFilters() != null && params.getScopeFilters().size() > 0)
		    {
		    	for (String scopeFilter : params.getScopeFilters())
		    		solrQuery.addFilterQuery(scopeFilter);
		    }

		    if (params.getPITState() != null && params.getResearchDate() != null)
		    {
		    	// NOW = researchDate
		    	// (wkpitstartdate_available:[* TO NOW] AND wkpitenddate_available:[NOW TO *])
		    	
		    	String state = params.getPITState();
		    	String researchDate = outfmt.format(params.getResearchDate());
		    	StringBuilder buf = new StringBuilder();
		    	
		    	buf.append("(");
		    	buf.append("wkpitstartdate_" + state);
		    	buf.append(":[* TO ");
		    	buf.append(researchDate);
		    	buf.append("]");
		    	buf.append(" AND ");
		    	buf.append("wkpitenddate_" + state);
		    	buf.append(":[");
		    	buf.append(researchDate);
		    	buf.append(" TO *])");
		    	
		    	solrQuery.addFilterQuery(buf.toString());
		    }
		    
		    
			if (params.getNavigatorFields() != null && params.getNavigatorFields().size() > 0)
			{
				for (String field : params.getNavigatorFields())
					solrQuery.addFacetField(field);

				// TODO add flexibility to allow more than one nav spec to have a prefix filter
				// something of a short cut for now: apply the prefix to the first nav field
				// typically, there would only be 1 in my application, we can allow more flexibility
				// later
				if (params.getNavPrefixFilter() != null)
					solrQuery.setFacetPrefix(params.getNavigatorFields().get(0), params.getNavPrefixFilter());
			}
			else
			{
				String[] facets = props.getPropertyArray(searchProfile + ".facetFields");
				for (String facet : facets)
					solrQuery.addFacetField(facet);
			}
			
			String clientID = params.getClient();
			
			QueryResponse resp = executeSolrQuery(solrQuery, clientID);
	
			QueryResults results = new QueryResults();
			
			handleHeader(resp, results);
		
			handleResults(resp, results);	// includes highlights
			
			handleFacets(resp, results);

			return results;
		}
		catch (Exception e)
		{
			log.error("QueryExecutionService error: ", e);
			log.trace(e);
			throw e;
		}
	}

	private QueryResponse executeSolrQuery(SolrQuery query, String gaClient)
		throws Exception
	{
		SolrClient client = SolrClientFactory.getSolrClient(gaClient);
		SolrServer connection = client.getConnection();
		QueryResponse response = connection.query(query);
		return response;
	}

	//recursive
	protected void handleNamedList(NamedList nl, QueryResults results, boolean header)
	{
		int numObjects = nl.size();
		for (int i = 0; i < numObjects; i++)
		{
			String name = nl.getName(i);
			String value = null;
			if (nl.getVal(i) instanceof Integer)
			{	
				Integer intVal = (Integer)nl.getVal(i);
				value = intVal.toString();
			}
			else if (nl.getVal(i) instanceof String)
			{
				value = (String)nl.getVal(i);
			}
			else if (nl.getVal(i) instanceof NamedList)
				handleNamedList((NamedList)nl.getVal(i), results, header);
			
			results.addHeaderField(name, value);
		}
	}
	
	protected void handleHeader(QueryResponse resp, QueryResults results)
	{
		NamedList<Object> nl = resp.getHeader();
		handleNamedList(nl, results, true);
		
		if (results.getHeaderValue("QTime") != null)
			results.setQueryTime(Long.parseLong(results.getHeaderValue("QTime")));	
	}
	
	protected void handleResults(QueryResponse resp, QueryResults qresult)
	{
		SolrDocumentList results = resp.getResults();
		
		qresult.setMaxScore(results.getMaxScore());
		qresult.setNumberResults(results.getNumFound());
		qresult.setStartPosition(results.getStart());
		
		for (SolrDocument doc : results)
		{
			QueryResult resultItem = new QueryResult();
			
			Map<String, Object> fieldMap = doc.getFieldValueMap();
			for (String name : fieldMap.keySet())
				resultItem.addField(name, fieldMap.get(name).toString());
			
			String wkdocid = (String)doc.get("wkdocid");
			if (resp.getHighlighting().get(wkdocid) != null) 
			{
				List<String> highlightSnippets = resp.getHighlighting().get(wkdocid).get("body");
				StringBuilder buf = new StringBuilder();
				for (String s : highlightSnippets)
					 buf.append("..."+ s + "...");
				resultItem.addField("wkteaser", buf.toString());
			}
			
			qresult.addResult(resultItem);
		}
	}
	
	protected void handleFacets(QueryResponse resp, QueryResults qresult)
	{
		HashMap<String, Navigator> navigators = new HashMap<String, Navigator>();
		
		List<FacetField> facets = resp.getFacetFields();
		
		if (facets == null)	// no navigation returned
		{
			qresult.setNavigators(null);
			return;
		}
		
		for (FacetField facet : facets)
		{
			// We've created facets as a level per field, and the 
			// field name ends with the level #, e.g. wkcshpaths_1, wkcshpaths_2
			// We should probably hide this implementation detail from the
			// presentation layer, so we'll "collapse" level fields into a 
			// single Navigator

			String facetFieldName = facet.getName();
			String stdNavName = getStandardNavName(facetFieldName);

			// does the master navigator already exist in the list of navigator trees?
			Navigator nav = null;
			if (navigators.containsKey(stdNavName))		// yes...get it
				nav = navigators.get(stdNavName);
			else
			{
				nav = new Navigator();					// no...create it
				nav.setName(stdNavName);
				navigators.put(stdNavName, nav);
			}
			
			// get individual paths for the facet field
			List<FacetField.Count> values = facet.getValues();
			if (values == null)		// empty navigator
			{
				nav.setPaths(null);
				continue;
			}
			
			for (FacetField.Count node : values)
			{
				NavigatorPath path = new NavigatorPath();
				path.setRawValue(node.getName());
				path.setCount(node.getCount());
				path.setLevelField(facetFieldName);
				path.setMasterField(stdNavName);
				path.setParent(calcNodeParent(path.getName()));
				nav.addPath(path);
			}
		}
		
		// now, have each Navigator build a tree representation of it and its child paths
		for (Navigator navigator : navigators.values())
			navigator.buildTree();
		
		qresult.setNavigators(navigators);
	}
	
	protected String getStandardNavName(String fieldName)
	{
		String[] parts = fieldName.split("_");
		return parts[0];
	}
	
	protected String calcNodeParent(String path)
	{
		int pos = path.lastIndexOf('|');
		if (pos < 0)		// first node
			return null;
		
		String parent = path.substring(0,pos);
		return parent;
	}
	
	protected String buildPermsClause(String subscriptionLevel, String subscriptionType)
	{
		String[] permIDs = null;
		String field = "wktocs";
		
		if (subscriptionType.equals("int"))
		{
			permIDs = props.getPropertyArray(subscriptionLevel + ".user.perms." + subscriptionType);
			field = "wktocs2";
		}
		else
			permIDs = props.getPropertyArray(subscriptionLevel + ".user.perms");
		
		if (permIDs == null || permIDs.length == 0)
			return "";
		
		StringBuilder buf = new StringBuilder();
		buf.append("(");
		for (int i = 0; i < permIDs.length; i++)
		{
			if (i < permIDs.length - 1)
				buf.append(field + ":" + permIDs[i] + " OR ");
			else
				buf.append(field + ":" + permIDs[i]);
		}
		buf.append(")");
		return buf.toString();
	}
}
