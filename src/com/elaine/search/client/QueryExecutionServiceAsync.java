package com.elaine.search.client;

import com.google.gwt.user.client.rpc.AsyncCallback;

/*
1. It must have the same name as the service interface, appended with Async 
(for example, StockPriceServiceAsync).
2. It must be located in the same package as the service interface.
3. Each method must have the same name and signature as in the service 
interface with an important difference: the method has no return type and 
the last parameter is an AsyncCallback object.
*/

public interface QueryExecutionServiceAsync
{
	void executeSearch(QueryParams params, AsyncCallback<QueryResults> callback);
}
