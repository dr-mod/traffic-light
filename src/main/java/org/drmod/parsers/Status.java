package org.drmod.parsers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public enum Status {
    FAILURE, UNSTABLE, SUCCESS, NOT_BUILT, UNDEFINED, ABORTED, RESPONSE_ERROR, CONNECTION_ERROR, NULL;

    static Logger logger = LoggerFactory.getLogger(Status.class);

    public static Status typeOfResponse(String name) {
        logger.debug(name);
        try {
            return valueOf(name);
        } catch (IllegalArgumentException e) {
            return UNDEFINED;
        } catch (NullPointerException e) {
            return NULL;
        }

    }
}
