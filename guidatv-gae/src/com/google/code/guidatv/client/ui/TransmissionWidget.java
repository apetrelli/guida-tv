package com.google.code.guidatv.client.ui;

import com.google.code.guidatv.client.model.Transmission;
import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.DateTimeFormat.PredefinedFormat;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiConstructor;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DisclosurePanel;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class TransmissionWidget extends Composite {

    private static final Binder binder = GWT.create(Binder.class);
    @UiField DisclosurePanel panel;
    @UiField Label time;
    @UiField Label description;
    @UiField Image infoLink;
    @UiField Image googleLink;
    @UiField Image wikipediaLink;
    @UiField Image imdbLink;

    interface Binder extends UiBinder<Widget, TransmissionWidget> {
    }

    @UiConstructor
    public TransmissionWidget(Transmission transmission) {
        initWidget(binder.createAndBindUi(this));
        ((HasText) panel.getHeader()).setText(transmission.getName());
        DateTimeFormat format = DateTimeFormat.getFormat(PredefinedFormat.HOUR24_MINUTE);
        time.setText(format.format(transmission.getStart()));
        description.setText(transmission.getDescription());
    }

}
