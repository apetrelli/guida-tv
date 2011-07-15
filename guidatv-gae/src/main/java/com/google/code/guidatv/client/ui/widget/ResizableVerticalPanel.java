package com.google.code.guidatv.client.ui.widget;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.event.logical.shared.HasResizeHandlers;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class ResizableVerticalPanel extends VerticalPanel implements HasResizeHandlers {

    private List<HandlerRegistration> registrations = new ArrayList<HandlerRegistration>();

    @Override
    public void insert(Widget w, int beforeIndex) {
        super.insert(w, beforeIndex);
        registrations.add(beforeIndex, registerWidget(w));
    }

    @Override
    public void add(Widget w) {
        super.add(w);
        registrations.add(registerWidget(w));
    }

    @Override
    public boolean remove(int index) {
        registrations.remove(index);
        return super.remove(index);
    }

    @Override
    public boolean remove(Widget w) {
        int index = getChildren().indexOf(w);
        if (index >= 0) {
            registrations.remove(index);
        }
        return super.remove(w);
    }

    @Override
    public HandlerRegistration addResizeHandler(ResizeHandler handler) {
        return addHandler(handler, ResizeEvent.getType());
    }

    private HandlerRegistration registerWidget(Widget w) {
        HandlerRegistration registration = null;
        if (w instanceof HasResizeHandlers) {
            HasResizeHandlers widget = (HasResizeHandlers) w;
            registration = widget.addResizeHandler(new ResizeHandler() {

                @Override
                public void onResize(ResizeEvent event) {
                    ResizeEvent.fire(ResizableVerticalPanel.this, getOffsetWidth(), getOffsetHeight());
                }
            });
        }
        return registration;
    }
}
