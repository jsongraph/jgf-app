package org.openbel.belnetwork.internal.util;

import org.cytoscape.work.TaskMonitor;

public class NoOpTaskMonitor implements TaskMonitor {
    @Override
    public void setTitle(String s) {}

    @Override
    public void setProgress(double v) {}

    @Override
    public void setStatusMessage(String s) {}

    @Override
    public void showMessage(Level level, String s) {}
}