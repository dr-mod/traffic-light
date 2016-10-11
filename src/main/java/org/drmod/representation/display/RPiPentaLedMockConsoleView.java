package org.drmod.representation.display;

import java.util.EnumSet;
import java.util.List;

import org.drmod.parsers.ProjectStatus;
import org.drmod.parsers.Status;
import org.drmod.representation.Notificator;
import org.drmod.representation.Observer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RPiPentaLedMockConsoleView extends AbstractView {

    private VirtualPinState pinGreenState;
    private VirtualPinState pinYellowState;
    private VirtualPinState pinRedState;
    private VirtualPinState pinSideGreenState;
    private VirtualPinState pinSideRedState;

    private final Logger logger = LoggerFactory.getLogger(RPiPentaLedMockConsoleView.class);

    private final EnumSet<Status> notImportantStatuses = EnumSet.of(Status.ABORTED, Status.NULL, Status.NOT_BUILT);

    public RPiPentaLedMockConsoleView(Notificator notificator) {
        super(notificator);
    }

    @Override
    public void wereChanges(List<ProjectStatus> projectStatuses) {
        EnumSet<Status> statuses = EnumSet.noneOf(Status.class);
        for(ProjectStatus projectStatus : projectStatuses) {
            statuses.add(projectStatus.getStatus());
        }

        resetVirtualPinStates();
        if (isOnlyNotImportantOrEmpty(statuses)) {
            nothingToShow();
        } else {
            showImportantStatus(statuses);
        }

        showResult();
    }

    private void showImportantStatus(EnumSet<Status> statuses) {
        if (statuses.contains(Status.FAILURE)) {
            failure();
        } else if (statuses.contains(Status.UNSTABLE)) {
            unstable();
        } else if (statuses.contains(Status.SUCCESS)) {
            stable();
        }

        if (statuses.contains(Status.CONNECTION_ERROR) ||
                statuses.contains(Status.RESPONSE_ERROR) ||
                statuses.contains(Status.UNDEFINED)) {
            problemsEncountered();
        }
    }

    private boolean isOnlyNotImportantOrEmpty(EnumSet<Status> statuses) {
        EnumSet<Status> currentStatuses = EnumSet.copyOf(statuses);
        currentStatuses.removeAll(notImportantStatuses);
        return currentStatuses.isEmpty();
    }

    private void showResult() {
        logger.info(String.format("%n-------%n" +
                      "|-%s-%s-|%n" +
                      "|-%s-%s-|%n" +
                      "|---%s-|%n" +
                      "-------%n", pinSideRedState, pinRedState, pinSideGreenState, pinYellowState, pinGreenState));

    }

    private void resetVirtualPinStates() {
        pinGreenState = VirtualPinState.DISABLED;
        pinYellowState = VirtualPinState.DISABLED;
        pinRedState = VirtualPinState.DISABLED;
        pinSideGreenState = VirtualPinState.DISABLED;
        pinSideRedState = VirtualPinState.DISABLED;
    }

    private void failure(){
        pinRedState = VirtualPinState.ENABLED;
    }

    private void unstable(){
        pinYellowState = VirtualPinState.ENABLED;
    }

    private void stable(){
        pinGreenState = VirtualPinState.ENABLED;
    }

    private void problemsEncountered(){
        pinSideRedState = VirtualPinState.ENABLED;
    }

    private void nothingToShow(){
        pinSideRedState = VirtualPinState.ENABLED;
    }

    enum VirtualPinState {
        ENABLED("O"), DISABLED("x");

        private String display;

        VirtualPinState(String display) {
            this.display = display;
        }

        @Override
        public String toString() {
            return display;
        }
    }
}
