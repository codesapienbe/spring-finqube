package com.finqube.iso20022.admin.dashboard;

import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

/**
 * Placeholder view for configuration management.
 */
@Route("configuration")
@PageTitle("Configuration | Spring Finqube Admin")
public class ConfigurationView extends VerticalLayout {
    public ConfigurationView() {
        add(new H2("Configuration"));
        setSizeFull();
    }
}
