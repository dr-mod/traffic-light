package org.drmod;

import org.drmod.representation.Notificator;
import org.drmod.parsers.parser.Parser;
import org.drmod.parsers.ProjectStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static java.util.stream.Collectors.toList;

public class Run {

    private Integer interval = 10;
    private ExecutorService executor;
    private List<Parser> parsers;
    private Notificator notificator;

    private Logger logger = LoggerFactory.getLogger(Run.class);

    public Run(ExecutorService executor, Notificator notificator, List<Parser> parsers) {
        this.executor = executor;
        this.parsers = parsers;
        this.notificator = notificator;
    }

    public void start() {
        while (true) {
            try {
                TimeUnit.SECONDS.sleep(interval);

                List<Future<ProjectStatus>> parsersFutures = parsers.stream()
                        .map(executor::submit)
                        .collect(toList());

                List<ProjectStatus> projectStatuses = new ArrayList<>();
                for (Future<ProjectStatus> answer : parsersFutures)
                    projectStatuses.add(answer.get());

                notificator.setProjectStatuses(projectStatuses);
                notificator.notifyObservers();
            } catch (ExecutionException | InterruptedException e) {
                logger.error("Error in the main cycle: {}", e);
            }
        }
    }

    public void setInterval(Integer interval) {
        this.interval = interval;
    }

}
