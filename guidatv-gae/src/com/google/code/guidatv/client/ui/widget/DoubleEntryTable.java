package com.google.code.guidatv.client.ui.widget;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ScrollEvent;
import com.google.gwt.event.dom.client.ScrollHandler;
import com.google.gwt.event.logical.shared.HasResizeHandlers;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTMLTable.RowFormatter;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;

public class DoubleEntryTable extends Composite {

    @UiField FlowPanel mainPanel;
    @UiField Grid mainGrid;
    @UiField FlexTable cornerTable;
    @UiField FlexTable headerRowTable;
    @UiField FlexTable headerColumnTable;
    @UiField FlexTable contentTable;
    @UiField FlowPanel headerRowBlock;
    @UiField FlowPanel headerColumnBlock;
    @UiField ScrollPanel contentBlock;

    private static final Binder binder = GWT.create(Binder.class);

    private int minimumRowSize;

    interface Binder extends UiBinder<Widget, DoubleEntryTable> {
    }

    public DoubleEntryTable() {
        initWidget(binder.createAndBindUi(this));
        contentBlock.addScrollHandler(new ScrollHandler() {

            @Override
            public void onScroll(ScrollEvent event) {
                DOM.setElementPropertyInt(headerColumnBlock.getElement(), "scrollTop", contentBlock.getScrollPosition());
                DOM.setElementPropertyInt(headerRowBlock.getElement(), "scrollLeft", contentBlock.getHorizontalScrollPosition());
            }
        });
        mainGrid.getCellFormatter().setVerticalAlignment(1, 0, HasVerticalAlignment.ALIGN_TOP);
        Window.addResizeHandler(new ResizeHandler() {

            @Override
            public void onResize(ResizeEvent event) {
                resizeTable();
            }
        });
    }

    public void setMinimumRowSize(int minimumRowSize) {
        this.minimumRowSize = minimumRowSize;
    }

    public void setCornerWidget(Widget widget) {
        cornerTable.setWidget(0, 0, widget);
    }

    public void setRowHeaderWidget(int column, Widget widget) {
        headerRowTable.setWidget(0, column, widget);
    }

    public void setColumnHeaderWidget(int row, Widget widget) {
        headerColumnTable.setWidget(row, 0, widget);
    }

    public void setWidget(final int row, final int column, Widget widget) {
        contentTable.setWidget(row, column, widget);
        if (widget instanceof HasResizeHandlers) {
            HasResizeHandlers resizableWidget = (HasResizeHandlers) widget;
            resizableWidget.addResizeHandler(new ResizeHandler() {

                @Override
                public void onResize(ResizeEvent event) {
                    resizeRow(row);
                    resizeColumn(column);
                }
            });
        }
    }

    public void clear() {
        cornerTable.clear();
        headerRowTable.clear();
        headerColumnTable.clear();
        contentTable.clear();
    }

    public RowFormatter getContentRowFormatter() {
        return contentTable.getRowFormatter();
    }

    public void setHeaderColumnWidth(String width) {
        for (int i=0; i < headerColumnTable.getRowCount(); i++) {
            Widget widget = headerColumnTable.getWidget(i, 0);
            if (widget != null) {
                widget.setWidth(width);
            }
        }
    }

    public void layout() {
        resizeTable();
        for (int i=0; i < contentTable.getRowCount(); i++) {
            resizeRow(i);
        }
        for (int i=0; i < contentTable.getCellCount(0); i++) {
            resizeColumn(i);
        }
    }

    private void setBodyHeight(String height) {
        contentBlock.setHeight(height);
        headerColumnBlock.setHeight(Integer.toString(contentBlock.getElement().getClientHeight()) + "px");
    }

    private void setBodyWidth(String width) {
        contentBlock.setWidth(width);
        headerRowBlock.setWidth(Integer.toString(contentBlock.getElement().getClientWidth()) + "px");
    }

    private void resizeRow(int row) {
        Element parentCell = DOM.getParent(contentTable.getWidget(row, 0).getElement());
        int clientHeight = parentCell.getClientHeight();
        if (clientHeight < minimumRowSize) {
            clientHeight = minimumRowSize;
            parentCell.getStyle().setHeight(clientHeight, Unit.PX);
        }
        headerColumnTable.getWidget(row, 0).setHeight(
                Integer.toString(clientHeight) + "px");
    }

    private void resizeTable() {
        setBodyHeight(Integer.toString(mainPanel.getOffsetHeight() - headerRowBlock.getOffsetHeight()) + "px");
        setBodyWidth(Integer.toString(mainPanel.getOffsetWidth() - headerColumnBlock.getOffsetWidth()) + "px");
    }

    private void resizeColumn(int column) {
        headerRowTable.getWidget(0, column).setWidth(
                Integer.toString(DOM.getParent(contentTable.getWidget(0, column).getElement())
                        .getClientWidth()) + "px");
    }
}
