package com.finqube.iso20022.admin.dashboard;

import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

/**
 * Placeholder view for security overview.
 */
@Route("security")
@PageTitle("Security | Spring Finqube Admin")
public class SecurityView extends VerticalLayout {
    public SecurityView() {
        add(new H2("Security"));
        setSizeFull();
    }
}
