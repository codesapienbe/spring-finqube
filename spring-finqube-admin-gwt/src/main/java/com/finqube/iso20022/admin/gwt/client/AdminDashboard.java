package com.finqube.iso20022.admin.gwt.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * Minimal GWT EntryPoint for the Admin Dashboard shell.
 *
 * <p>Renders a placeholder label to verify that the GWT application
 * is loading correctly when the JS has been compiled.</p>
 */
public final class AdminDashboard implements EntryPoint {

    /**
     * Called by the GWT bootstrap when the module loads.
     */
    @Override
    public void onModuleLoad() {
        RootPanel rootPanel = RootPanel.get();
        rootPanel.add(new Label("Spring Finqube Admin (GWT shell)"));
    }
}
