package com.google.code.guidatv.client;

import com.google.code.guidatv.client.ui.ScheduleWidget;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class GuidaTv implements EntryPoint {
//	private Button clickMeButton;
	public void onModuleLoad() {
		RootPanel rootPanel = RootPanel.get("mainPanel");

		ScheduleWidget widget = new ScheduleWidget();
        rootPanel.add(widget);
//		clickMeButton.setText("Click me!");
//		clickMeButton.addClickHandler(new ClickHandler(){
//			public void onClick(ClickEvent event) {
//				Window.alert("Hello, GWT World!");
//			}
//		});
	}
}
