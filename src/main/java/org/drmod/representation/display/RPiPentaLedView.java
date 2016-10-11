package org.drmod.representation.display;

import java.util.EnumSet;
import java.util.List;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;

import org.drmod.parsers.ProjectStatus;
import org.drmod.parsers.Status;
import org.drmod.representation.Notificator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RPiPentaLedView extends AbstractView {

    private final GpioController gpio;
    private final GpioPinDigitalOutput pinGreen;
    private final GpioPinDigitalOutput pinYellow;
    private final GpioPinDigitalOutput pinRed;
    private final GpioPinDigitalOutput pinSideGreen;
    private final GpioPinDigitalOutput pinSideRed;

    private VirtualPinState pinGreenState;
    private VirtualPinState pinYellowState;
    private VirtualPinState pinRedState;
    private VirtualPinState pinSideGreenState;
    private VirtualPinState pinSideRedState;

    private final Logger logger = LoggerFactory.getLogger(RPiPentaLedView.class);

    private final EnumSet<Status> notImportantStatuses = EnumSet.of(Status.ABORTED, Status.NULL, Status.NOT_BUILT);

    public RPiPentaLedView(Notificator notificator, GpioController gpio) {
        super(notificator);
        this.gpio = gpio;
        pinGreen = this.gpio.provisionDigitalOutputPin(RaspiPin.GPIO_04, PinState.LOW);
        pinYellow = this.gpio.provisionDigitalOutputPin(RaspiPin.GPIO_05, PinState.LOW);
        pinRed = this.gpio.provisionDigitalOutputPin(RaspiPin.GPIO_01, PinState.LOW);
        pinSideGreen = this.gpio.provisionDigitalOutputPin(RaspiPin.GPIO_00, PinState.LOW);
        pinSideRed = this.gpio.provisionDigitalOutputPin(RaspiPin.GPIO_02, PinState.LOW);

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
        setState(pinGreen, pinGreenState);
        setState(pinYellow, pinYellowState);
        setState(pinRed, pinRedState);
        setState(pinSideGreen, pinSideGreenState);
        setState(pinSideRed, pinSideRedState);

        logger.debug("RPiPentaLedView{" +
                "pinSideRedState=" + pinSideRedState +
                ", pinSideGreenState=" + pinSideGreenState +
                ", pinRedState=" + pinRedState +
                ", pinYellowState=" + pinYellowState +
                ", pinGreenState=" + pinGreenState +
                '}');
    }

    private void setState(GpioPinDigitalOutput pin, VirtualPinState state) {
        switch (state) {
            case ENABLED:
                pin.high();
                break;
            case DISABLED:
                pin.low();
                break;
        }
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
        ENABLED, DISABLED
    }
}
