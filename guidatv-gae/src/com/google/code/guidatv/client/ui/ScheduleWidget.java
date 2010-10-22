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
import com.google.code.guidatv.client.service.ChannelService;
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
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.datepicker.client.DateBox;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.uibinder.client.UiHandler;

public class ScheduleWidget extends Composite {

    private final class UpdateCallback implements AsyncCallback<ScheduleResume> {
        @Override
        public void onSuccess(ScheduleResume schedule) {
            Date now = new Date();
            Date scheduleStart = schedule.getStart();
            boolean isToday = now.getYear() == scheduleStart.getYear()
                    && now.getMonth() == scheduleStart.getMonth()
                    && now.getDate() == scheduleStart.getDate();
            containerPanel.clear();
            containerPanel.add(scheduleTable);
            DateTimeFormat format = DateTimeFormat
                    .getFormat(PredefinedFormat.HOUR24_MINUTE);
            scheduleTable.removeAllRows();
            scheduleTable.setCornerWidget(new Label("Ora"));
            int i = 0;
            for (Channel channel : schedule.getChannels()) {
                scheduleTable.setRowHeaderWidget(i, new Label(channel.getName()));
                i++;
            }
            int j = 0;
            boolean selectionDone = false;
            int selectedRow = -1;
            for (IntervalEntry interval : schedule.getIntervals()) {
                Date intervalStart = interval.getStart();
                scheduleTable.setColumnHeaderWidget(j, new Label(format.format(intervalStart)));
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
                String styleName;
                if (isToday && !selectionDone && (((now.getHours() - intervalStart.getHours()) * 60) + (now.getMinutes() - intervalStart.getMinutes())) <= 30) {
                    selectionDone = true;
                    styleName = style.nowrow();
                    selectedRow = j;
                } else {
                    styleName = j % 2 == 0 ? style.evenrow() : style.oddrow();
                }
                scheduleTable.getContentRowFormatter().addStyleName(j,
                        styleName);
                scheduleTable.getHeaderColumnRowFormatter().addStyleName(j, styleName);
                j++;
            }
            scheduleTable.setHeaderColumnWidth("50px");
            scheduleTable.layout();
            if (selectedRow >= 0) {
                scheduleTable.ensureRowVisible(selectedRow);
            }
        }

        @Override
        public void onFailure(Throwable caught) {
            GWT.log("Error getting schedule", caught);
        }
    }

    interface ScheduleWidgetStyle extends CssResource {
        String oddrow();

        String evenrow();

        String nowrow();
    }

    private static final Binder binder = GWT.create(Binder.class);

    private ScheduleRemoteServiceAsync scheduleService = GWT
            .create(ScheduleRemoteService.class);
    
    private ChannelService channelService;

    @UiField
    DateBox dateBox;
    @UiField
    ScheduleWidgetStyle style;
    @UiField SimplePanel containerPanel;
    @UiField ChannelTree channelTree;
    @UiField Button updateButton;
    @UiField Label usernameLabel;
    @UiField Anchor logLink;
    @UiField Button saveButton;

    private DoubleEntryTable scheduleTable;

    private Image loading;

    interface Binder extends UiBinder<Widget, ScheduleWidget> {
    }

    public ScheduleWidget() {
        initWidget(binder.createAndBindUi(this));
        channelService = new ChannelServiceImpl();
        channelTree.init(channelService);
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
        containerPanel.clear();
        containerPanel.add(loading);
        scheduleService.getLoginInfo(GWT.getHostPageBaseURL(), new AsyncCallback<LoginInfo>() {

            @Override
            public void onFailure(Throwable caught) {
                GWT.log("Non riesco a ottenere i dati di login", caught);
            }

            @Override
            public void onSuccess(LoginInfo result) {
                String nickname = result.getNickname();
                if (nickname != null) {
                    usernameLabel.setText("Benvenuto " + nickname + "!");
                    saveButton.setEnabled(true);
                } else {
                    usernameLabel.setText("Benvenuto!");
                }
                channelTree.setSelectedChannels(result.getPreferredChannels());
                logLink.setHref(result.getUrl());
                logLink.setText(result.getLinkLabel());
                loadSchedule();
            }
        });
    }

    private void loadSchedule() {
        containerPanel.clear();
        containerPanel.add(loading);
        Date date = dateBox.getValue();
        scheduleService.getDayScheduleResume(date,
                channelTree.getSelectedChannels(),
                new UpdateCallback());
    }
    
    @UiHandler("saveButton")
    void onSaveButtonClick(ClickEvent event) {
        scheduleService.savePreferredChannels(channelTree.getSelectedChannels(), new AsyncCallback<Void>() {

            @Override
            public void onFailure(Throwable caught) {
                GWT.log("Cannot save preferred channels", caught);
            }

            @Override
            public void onSuccess(Void result) {
                Window.alert("Lista canali salvata!");
                
            }
        });
    }
}
