package org.drmod.representation;

import org.drmod.parsers.ProjectStatus;

import java.util.List;

public abstract class Observer {

    public Observer(Notificator notificator){
        notificator.addObserver(this);
    }

    public abstract void update(List<ProjectStatus> projectStatuses);

}
