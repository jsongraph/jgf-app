package org.cytoscape.jgfnetwork.internal.ui;
import java.awt.Component;

import javax.swing.Icon;

import org.cytoscape.application.swing.CytoPanelComponent;
import org.cytoscape.application.swing.CytoPanelName;
import org.cytoscape.model.events.RowsSetEvent;
import org.cytoscape.model.events.RowsSetListener;
import org.cytoscape.model.CyEdge;

public class EdgeSelectedListener implements CytoPanelComponent, RowsSetListener{


    
//     @Override
//     public Component getComponent() { evPanel.panel }
//        @Override
//        CytoPanelName getCytoPanelName() { EAST }
//        @Override
//        String getTitle() { "Evidence" }
//        @Override
//        Icon getIcon() { null }
//    
    @Override
    public void handleEvent(RowsSetEvent ev) {
    
        
        Object source =ev.getSource();
        if( source.getClass() == CyEdge.class)
        {
        
        }
    }

@Override
public Component getComponent() {
    // TODO Auto-generated method stub
    return null;
}

@Override
public CytoPanelName getCytoPanelName() {
    // TODO Auto-generated method stub
    return null;
}

@Override
public Icon getIcon() {
    // TODO Auto-generated method stub
    return null;
}

@Override
public String getTitle() {
    // TODO Auto-generated method stub
    return "View Evidence";
}

}
