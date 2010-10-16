package com.google.code.guidatv.client.ui;

import java.util.Date;

import com.google.code.guidatv.client.ScheduleRemoteService;
import com.google.code.guidatv.client.ScheduleRemoteServiceAsync;
import com.google.code.guidatv.client.model.Channel;
import com.google.code.guidatv.client.model.ChannelEntry;
import com.google.code.guidatv.client.model.IntervalEntry;
import com.google.code.guidatv.client.model.LoginInfo;
import com.google.code.guidatv.client.model.ScheduleResume;
import com.google.code.guidatv.client.model.Transmission;
import com.google.code.guidatv.client.pics.Pics;
import com.google.code.guidatv.client.service.impl.ChannelServiceImpl;
import com.google.code.guidatv.client.ui.widget.ChannelTree;
import com.google.code.guidatv.client.ui.widget.DoubleEntryTable;
import com.google.code.guidatv.client.ui.widget.ResizableVerticalPanel;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.DateTimeFormat.PredefinedFormat;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.datepicker.client.DateBox;
import com.google.gwt.user.client.ui.Anchor;

public class ScheduleWidget extends Composite {

    private final class UpdateCallback implements AsyncCallback<ScheduleResume> {
        @Override
        public void onSuccess(ScheduleResume schedule) {
            containerPanel.clear();
            containerPanel.add(scheduleTable);
            DateTimeFormat format = DateTimeFormat
                    .getFormat(PredefinedFormat.HOUR24_MINUTE);
            scheduleTable.clear();
            scheduleTable.setCornerWidget(new Label("Ora"));
            int i = 0;
            for (Channel channel : schedule.getChannels()) {
                scheduleTable.setRowHeaderWidget(i, new Label(channel.getName()));
                i++;
            }
            int j = 0;
            for (IntervalEntry interval : schedule.getIntervals()) {
                scheduleTable.setColumnHeaderWidget(j, new Label(format.format(interval.getStart())));
                i = 0;
                for (Channel channel : schedule.getChannels()) {
                    ChannelEntry entry = interval.getEntry(channel);
                    ResizableVerticalPanel transmissionPanel = new ResizableVerticalPanel();
                    if (entry != null) {
                        for (Transmission transmission : entry
                                .getTransmissions()) {
                            transmissionPanel.add(new TransmissionWidget(
                                    transmission));
                        }
                    }
                    scheduleTable.setWidget(j, i, transmissionPanel);
                    i++;
                }
                String styleName = j % 2 == 0 ? style.evenrow() : style.oddrow();
                scheduleTable.getContentRowFormatter().addStyleName(j,
                        styleName);
                scheduleTable.getHeaderColumnRowFormatter().addStyleName(j, styleName);
                j++;
            }
            scheduleTable.setHeaderColumnWidth("50px");
            scheduleTable.layout();
        }

        @Override
        public void onFailure(Throwable caught) {
            GWT.log("Error getting schedule", caught);
        }
    }

    interface ScheduleWidgetStyle extends CssResource {
        String oddrow();

        String evenrow();
    }

    private static final Binder binder = GWT.create(Binder.class);

    private ScheduleRemoteServiceAsync scheduleService = GWT
            .create(ScheduleRemoteService.class);

    @UiField
    DateBox dateBox;
    @UiField
    ScheduleWidgetStyle style;
    @UiField SimplePanel containerPanel;
    @UiField ChannelTree channelTree;
    @UiField Button updateButton;
    @UiField Label usernameLabel;
    @UiField Anchor logLink;

    private DoubleEntryTable scheduleTable;

    private Image loading;

    interface Binder extends UiBinder<Widget, ScheduleWidget> {
    }

    public ScheduleWidget() {
        initWidget(binder.createAndBindUi(this));
        channelTree.init(new ChannelServiceImpl());
        scheduleTable = new DoubleEntryTable();
        scheduleTable.setMinimumRowSize(30);
        Pics pics = GWT.create(Pics.class);
        loading = new Image(pics.loading());
        dateBox.setFormat(new DateBox.DefaultFormat(DateTimeFormat
                .getFormat("dd/MM/yyyy")));
        dateBox.setValue(new Date());
        updateButton.addClickHandler(new ClickHandler() {
            
            @Override
            public void onClick(ClickEvent event) {
                loadSchedule();
            }
        });
        scheduleService.getLoginInfo(GWT.getHostPageBaseURL(), new AsyncCallback<LoginInfo>() {

            @Override
            public void onFailure(Throwable caught) {
                GWT.log("Non riesco a ottenere i dati di login", caught);
            }

            @Override
            public void onSuccess(LoginInfo result) {
                usernameLabel.setText(result.getNickname());
                logLink.setHref(result.getUrl());
                logLink.setText(result.getLinkLabel());
            }
        });
        loadSchedule();
    }

    private void loadSchedule() {
        containerPanel.clear();
        containerPanel.add(loading);
        Date date = dateBox.getValue();
        scheduleService.getDayScheduleResume(date,
                channelTree.getSelectedChannels(),
                new UpdateCallback());
    }
}
