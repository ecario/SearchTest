package com.elaine.search.client;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;

public class MessageBox
{
	DialogBox dialogBox;
	VerticalPanel vPanel;
	SimplePanel sPanel;
	HTML message;
	Button btnClose;
	
	public MessageBox(String title)
	{
		boolean autoHide = false;
		dialogBox = new DialogBox(autoHide);
		dialogBox.setText(title);

		message = new HTML("");
		vPanel = new VerticalPanel();
		sPanel = new SimplePanel();
		
		btnClose = new Button("Close");
		btnClose.addClickHandler(new ClickHandler()
		{
			public void onClick(ClickEvent event)
			{
				dialogBox.hide();
			}
		});

		sPanel.add(btnClose);
		vPanel.add(this.message);
		vPanel.add(sPanel);
		dialogBox.setWidget(vPanel);
	}
	
	
	public void showDialog(String message)
	{
		this.message = new HTML(message);
		dialogBox.show();
	}
}
