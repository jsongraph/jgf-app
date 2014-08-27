package org.openbel.belnetwork.internal.ui;
import java.awt.Component;

import javax.swing.Icon;

import org.cytoscape.application.swing.CytoPanelComponent;
import org.cytoscape.application.swing.CytoPanelName;
import org.cytoscape.model.events.RowsSetEvent;
import org.cytoscape.model.events.RowsSetListener;

public class EdgeSelectedListener implements CytoPanelComponent, RowsSetListener{

    @Override
    public void handleEvent(RowsSetEvent ev) {
    }

    @Override
    public Component getComponent() {
        return null;
    }

    @Override
    public CytoPanelName getCytoPanelName() {
        return null;
    }

    @Override
    public Icon getIcon() {
        return null;
    }

    @Override
    public String getTitle() {
        return "View Evidence";
    }
}
