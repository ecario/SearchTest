package com.elaine.search.server;

import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.impl.CommonsHttpSolrServer;
import org.apache.solr.client.solrj.impl.XMLResponseParser;


public class SolrClient
{
	static Logger log = Logger.getLogger( "com.elaine" );
	
	/*
	    CommonsHttpSolrServer is thread-safe and if you are using the following constructor,
	    you *MUST* re-use the same instance for all requests.  If instances are created on
	    the fly, it can cause a connection leak. The recommended practice is to keep a
	    static instance of CommonsHttpSolrServer per solr server url and share it for all requests.
	    See https://issues.apache.org/jira/browse/SOLR-861 for more details
	*/
	
	// But, since we have multiple URL's due to multi-core support, we'll manage the singleton's through a factory
	// and just create new SolrClients here.  Probably need to architect this a little better.
	
	private CommonsHttpSolrServer server = null;

	public static SolrClient getInstance(String clientID)
		throws Exception
	{
		return new SolrClient(clientID);
	}

	private SolrClient(String clientID)
		throws Exception
	{
		PropertiesHandler props = PropertiesHandler.getInstance();
		String url = props.getProperty(clientID + ".solr.index.url");
		
		try
		{
			server  = new CommonsHttpSolrServer(url);
		}
		catch (Exception e)
		{
			log.error("Unable to establish connection to: " + url);
			throw new Exception("Unable to create connection to Solr: " + e.toString());
		}
		
		// some things you can set - mostly this is HTTP stuff
		
		/*
		server.setSoTimeout(1000);  // socket read timeout
		server.setConnectionTimeout(100);
		server.setDefaultMaxConnectionsPerHost(100);
		server.setMaxTotalConnections(100);
		server.setFollowRedirects(false);  // defaults to false
		  // allowCompression defaults to false.
		  // Server side must support gzip or deflate for this to have any effect.
		server.setAllowCompression(true);
		server.setMaxRetries(1); // defaults to 0.  > 1 not recommended.
		*/
		
		server.setParser(new XMLResponseParser()); // binary parser is used by default; we want xml
	}
	  
	public SolrServer getConnection()
	{
		return server;
	}
}
