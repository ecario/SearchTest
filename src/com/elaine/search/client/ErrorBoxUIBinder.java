package com.elaine.search.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiFactory;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;

public class ErrorBoxUIBinder extends DialogBox
{
	private static ErrorBoxUIBinderUiBinder uiBinder = GWT.create(ErrorBoxUIBinderUiBinder.class);

	interface ErrorBoxUIBinderUiBinder extends
			UiBinder<Widget, ErrorBoxUIBinder>
	{ }

	public ErrorBoxUIBinder()
	{
		setWidget(uiBinder.createAndBindUi(this));
	}

	/*
	@UiFactory
	DialogBox thatsJustMe()
	{
		return this;
	}
	*/
	
	@UiField
	Button btnClose;

	@UiField
	HTML htmlMessage;
	
	@UiHandler("btnClose")
	void onClick(ClickEvent e)
	{
		hide();
	}

	public void show(String message)
	{
		htmlMessage.setHTML(message);
		show();
	}
}
