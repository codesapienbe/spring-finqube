package com.finqube.iso20022.admin.dashboard;

import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

/**
 * Placeholder view for listing financial messages.
 */
@Route("messages")
@PageTitle("Messages | Spring Finqube Admin")
public class MessageListView extends VerticalLayout {
    public MessageListView() {
        add(new H2("Messages"));
        setSizeFull();
    }
}
