package com.finqube.iso20022.admin.dashboard;

import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

/**
 * Placeholder view for system monitoring.
 */
@Route("monitoring")
@PageTitle("Monitoring | Spring Finqube Admin")
public class MonitoringView extends VerticalLayout {
    public MonitoringView() {
        add(new H2("Monitoring"));
        setSizeFull();
    }
}
