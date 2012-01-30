package com.elaine.search.client;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

//annotation associates the service with a default path
//relative to the module base URL
@RemoteServiceRelativePath("search")
public interface QueryExecutionService extends RemoteService
{
	public QueryResults executeSearch(QueryParams params) throws Exception;
	
}
