package org.openbel.belnetwork.internal.ui;

import org.cytoscape.work.Task;
import org.cytoscape.work.TaskMonitor;

class NoOpTask implements Task {
    @Override
    public void run(TaskMonitor taskMonitor) throws Exception {
        // no operations
    }

    @Override
    public void cancel() {
        // no cancel operations
    }
}
