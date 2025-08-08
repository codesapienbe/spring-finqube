package com.finqube.iso20022.examples.gwt;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * Minimal GWT example EntryPoint.
 */
public class ExamplesGwtEntryPoint implements EntryPoint {
    /**
     * Called by GWT upon module load; renders a simple label to the root panel.
     */
    @Override
    public void onModuleLoad() {
        RootPanel.get().add(new Label("Spring Finqube GWT Example loaded"));
    }
}
