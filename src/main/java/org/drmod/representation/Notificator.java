package org.drmod.representation;

import org.drmod.parsers.ProjectStatus;

import java.util.ArrayList;
import java.util.List;

public class Notificator {

    private List<Observer> observers = new ArrayList<>();
    private List<ProjectStatus> projectStatuses;

    public void addObserver(Observer observer) {
        observers.add(observer);
    }

    public void removeObserver(Observer observer) {
        observers.remove(observer);
    }

    public void notifyObservers() {
        for(Observer observer : observers) {
            observer.update(projectStatuses);
        }
    }

    public void setProjectStatuses(List<ProjectStatus> projectStatuses) {
        this.projectStatuses = projectStatuses;
    }
}
