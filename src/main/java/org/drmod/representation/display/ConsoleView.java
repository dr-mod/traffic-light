package org.drmod.representation.display;

import org.drmod.parsers.ProjectStatus;
import org.drmod.representation.Notificator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class ConsoleView extends AbstractView {

    private Logger logger = LoggerFactory.getLogger(ConsoleView.class);

    public ConsoleView(Notificator notificator) {
        super(notificator);
    }

    @Override
    public void wereChanges(List<ProjectStatus> projectStatuses) {
        for(ProjectStatus ps : projectStatuses) {
            logger.info("Project: {}, status: {}", ps.getName(), ps.getStatus());
        }
    }
}
