package com.google.code.guidatv.client.ui;

import java.util.Date;

import com.google.code.guidatv.client.ScheduleRemoteService;
import com.google.code.guidatv.client.ScheduleRemoteServiceAsync;
import com.google.code.guidatv.client.model.Channel;
import com.google.code.guidatv.client.model.ChannelEntry;
import com.google.code.guidatv.client.model.IntervalEntry;
import com.google.code.guidatv.client.model.ScheduleResume;
import com.google.code.guidatv.client.model.Transmission;
import com.google.code.guidatv.client.ui.widget.DoubleEntryTable;
import com.google.code.guidatv.client.ui.widget.ResizableVerticalPanel;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.DateTimeFormat.PredefinedFormat;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.datepicker.client.DateBox;

public class ScheduleWidget extends Composite {

    private final class UpdateCallback implements AsyncCallback<ScheduleResume> {
        @Override
        public void onSuccess(ScheduleResume schedule) {
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
                scheduleTable.getContentRowFormatter().addStyleName(j,
                        j % 2 == 0 ? style.evenrow() : style.oddrow());
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
    DoubleEntryTable scheduleTable;
    @UiField
    DateBox dateBox;
    @UiField
    ScheduleWidgetStyle style;

    interface Binder extends UiBinder<Widget, ScheduleWidget> {
    }

    public ScheduleWidget() {
        initWidget(binder.createAndBindUi(this));
        dateBox.setFormat(new DateBox.DefaultFormat(DateTimeFormat
                .getFormat("dd/MM/yyyy")));
        dateBox.addValueChangeHandler(new ValueChangeHandler<Date>() {

            @Override
            public void onValueChange(ValueChangeEvent<Date> event) {
                if (event.getValue().getHours() == 0) {
                    Date date = dateBox.getValue();
                    scheduleService
                            .getDayScheduleResume(date, new UpdateCallback());
                }
            }
        });
    }
}
