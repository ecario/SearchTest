package com.elaine.search.server;

import java.util.HashMap;

public class SolrClientFactory
{
	/*
    Use factory to maintain singleton connections for different URL's
    CommonsHttpSolrServer is thread-safe and if you are using the following constructor,
    you *MUST* re-use the same instance for all requests.  If instances are created on
    the fly, it can cause a connection leak. The recommended practice is to keep a
    static instance of CommonsHttpSolrServer per solr server url and share it for all requests.
    See https://issues.apache.org/jira/browse/SOLR-861 for more details
  */
	private static HashMap<String,SolrClient> solrConnections = new HashMap<String, SolrClient>();
	
	public static SolrClient getSolrClient(String clientID)
		throws Exception
	{
		SolrClient client = solrConnections.get(clientID);
		if (client != null)
			return client;

		client = SolrClient.getInstance(clientID);
		solrConnections.put(clientID, client);
		return client;
	}
}
