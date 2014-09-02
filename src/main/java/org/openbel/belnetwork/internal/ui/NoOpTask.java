package org.openbel.belnetwork.internal.ui;

import org.cytoscape.work.Task;
import org.cytoscape.work.TaskFactory;
import org.cytoscape.work.TaskIterator;
import org.cytoscape.work.TaskMonitor;

/**
 * {@link NoOpTask} provides an empty task to provide to a {@link TaskIterator}
 * that will not yield {@link java.lang.NullPointerException} when called.
 * <br><br>
 * This is useful when your implementation descends from a {@link TaskFactory},
 * but you can do all the work in your factory. An example of this is
 * {@link ShowEvidenceFactory}.
 */
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
