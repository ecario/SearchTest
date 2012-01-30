package com.elaine.search.client;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class QueryParams implements Serializable
{
	String searchExpression;
	String searchType;
	
	List<String> navigatorFields;
	String navPrefixFilter;
	
	String user;
	String searchProfile;
	String subscriptionLevel;
	String subscriptionType;
	
	List<String> scopeFilters;
	
	String pitState;
	Date researchDate;
	
	String client;
	
	public String getSearchExpression() 		{ return searchExpression; }
	public String getSearchType() 				{ return searchType; }
	public String getNavPrefixFilter()			{ return navPrefixFilter; }
	public List<String> getNavigatorFields()	{ return navigatorFields; }
	public String getUser()						{ return user; }
	public String getSearchProfile()			{ return searchProfile; }
	public String getSubscriptionLevel()		{ return subscriptionLevel; }
	public String getSubscriptionType()			{ return subscriptionType; }
	public List<String> getScopeFilters()		{ return scopeFilters; }
	public String getPITState()					{ return pitState; }
	public Date getResearchDate()				{ return researchDate; }
	public String getClient()					{ return client; }

	public void setSearchExpression(String searchExpression) 	{ this.searchExpression = searchExpression; }
	public void setSearchType(String searchType) 				{ this.searchType = searchType; }
	public void setNavPrefixFilter(String navPrefixFilter)		{ this.navPrefixFilter = navPrefixFilter; }
	public void setNavigatorFields(List<String> navFields)		{ this.navigatorFields = navFields; }
	public void setUser(String user)							{ this.user = user; }
	public void setSearchProfile(String searchProfile)			{ this.searchProfile = searchProfile; }
	public void setSubscriptionLevel(String subscriptionLevel)	{ this.subscriptionLevel = subscriptionLevel; }
	public void setSubscriptionType(String subscriptionType)	{ this.subscriptionType = subscriptionType; }
	public void setScopeFilters(List<String> scopeFilters)		{ this.scopeFilters = scopeFilters; }
	public void setPITState(String pitState)					{ this.pitState = pitState; }
	public void setResearchDate(Date researchDate)				{ this.researchDate = researchDate; }
	public void setClient(String client)							{ this.client = client; }
	
	public void addNavField(String fieldName)
	{
		if (navigatorFields == null)
			navigatorFields = new ArrayList<String>();
		
		navigatorFields.add(fieldName);
	}
	
	public void addScopeFilter(String scopeFilter)
	{
		if (scopeFilters == null)
			scopeFilters = new ArrayList<String>();
		
		scopeFilters.add(scopeFilter);
	}
	
	public QueryParams()
	{}
	
}
