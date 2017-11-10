package org.drmod.representation.display;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.ObjectWriter;
import org.drmod.parsers.ProjectStatus;
import org.drmod.parsers.Status;
import org.drmod.representation.Notificator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.EnumSet;
import java.util.List;

public class HueView extends AbstractView {

    private static final EnumSet<Status> NOT_IMPORTANT_STATUSES = EnumSet.of(Status.NULL, Status.NOT_BUILT);
    private static final int TIMEOUT = 5000;
    private final Logger logger = LoggerFactory.getLogger(HueView.class);

    private final ObjectWriter OBJECT_WRITER = new ObjectMapper().writer().withDefaultPrettyPrinter();

    private String url;
    private VirtualColor colorToSend;

    public HueView(Notificator notificator, String url) {
        super(notificator);
        this.url = url;
    }

    @Override
    public void wereChanges(List<ProjectStatus> projectStatuses) {
        EnumSet<Status> statuses = EnumSet.noneOf(Status.class);
        for (ProjectStatus projectStatus : projectStatuses) {
            statuses.add(projectStatus.getStatus());
        }


        if (isOnlyNotImportantOrEmpty(statuses)) {
            somethingWrong();
        } else {
            showImportantStatus(statuses);
        }

        showResult();
    }

    private void showImportantStatus(EnumSet<Status> statuses) {
        if (statuses.contains(Status.FAILURE)) {
            failure();
        } else if (statuses.contains(Status.UNSTABLE) || statuses.contains(Status.ABORTED)) {
            unstable();
        } else if (statuses.contains(Status.SUCCESS)) {
            stable();
        }

        if (statuses.contains(Status.CONNECTION_ERROR) ||
                statuses.contains(Status.RESPONSE_ERROR) ||
                statuses.contains(Status.UNDEFINED)) {
            somethingWrong();
        }
    }

    private boolean isOnlyNotImportantOrEmpty(EnumSet<Status> statuses) {
        EnumSet<Status> currentStatuses = EnumSet.copyOf(statuses);
        currentStatuses.removeAll(NOT_IMPORTANT_STATUSES);
        return currentStatuses.isEmpty();
    }

    private void showResult() {

        new Thread(() -> {
            logger.debug("Send color {}", colorToSend.toString());
            HttpClient httpClient = new DefaultHttpClient();
            HttpParams params = httpClient.getParams();
            HttpConnectionParams.setConnectionTimeout(params, TIMEOUT);
            try {
                String json = OBJECT_WRITER.writeValueAsString(new Color(colorToSend));
                StringEntity stringEntity = new StringEntity(json, ContentType.APPLICATION_JSON);
                HttpPut putRequest = new HttpPut(url);
                putRequest.setEntity(stringEntity);

                HttpResponse response = httpClient.execute(putRequest);
                if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
                    logger.error("Status code: " + response.getStatusLine().getStatusCode());
                }
            } catch (IOException ex) {
                logger.error(ex.getMessage());
            } finally {
                httpClient.getConnectionManager().shutdown();
            }
        }).start();
    }

    private void failure() {
        colorToSend = VirtualColor.RED;
    }

    private void unstable() {
        colorToSend = VirtualColor.YELLOW;
    }

    private void stable() {
        colorToSend = VirtualColor.GREEN;
    }


    private void somethingWrong() {
        colorToSend = VirtualColor.BLUE;
    }


    enum VirtualColor {
        GREEN(25500, 150), YELLOW(12750, 150), RED(0, 150), BLUE(46920, 100);

        private int hue;
        private int bri;
        private boolean on = true;

        VirtualColor(int hue, int bri) {
            this.hue = hue;
            this.bri = bri;
        }

        public int getHue() {
            return hue;
        }

        public boolean isOn() {
            return on;
        }

        public int getBri() {
            return bri;
        }

        @Override
        public String toString() {
            return "VirtualColor{" +
                    "hue=" + hue +
                    ", bri=" + bri +
                    ", on=" + on +
                    '}';
        }
    }

    class Color {

        private int hue;
        private int bri;
        private boolean on = true;

        public Color(VirtualColor virtualColor) {
            this.hue = virtualColor.getHue();
            this.bri = virtualColor.getBri();
            this.on = virtualColor.isOn();
        }

        public int getHue() {
            return hue;
        }

        public void setHue(int hue) {
            this.hue = hue;
        }

        public int getBri() {
            return bri;
        }

        public void setBri(int bri) {
            this.bri = bri;
        }

        public boolean isOn() {
            return on;
        }

        public void setOn(boolean on) {
            this.on = on;
        }
    }
}
