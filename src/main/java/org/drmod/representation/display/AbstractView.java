package org.drmod.representation.display;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.drmod.parsers.ProjectStatus;
import org.drmod.representation.Notificator;
import org.drmod.representation.Observer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

abstract class AbstractView extends Observer {

    Logger logger = LoggerFactory.getLogger(AbstractView.class);

    private Set<ProjectStatus> previousProjectStatuses;

    public AbstractView(final Notificator notificator) {
        super(notificator);
    }

    @Override
    public void update(final List<ProjectStatus> projectStatuses) {
        if (null == projectStatuses) {
            return;
        }

        Set<ProjectStatus> currentProjectStatuses = new HashSet<>(projectStatuses);
        if (previousProjectStatuses == null || !previousProjectStatuses.equals(currentProjectStatuses)) {
            logger.debug("Data is not equal.");
            previousProjectStatuses = currentProjectStatuses;
            wereChanges(projectStatuses);
        }
    }

    protected abstract void wereChanges(final List<ProjectStatus> projectStatuses);
}
