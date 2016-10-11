package org.drmod.parsers.mapper;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.drmod.parsers.Status;
import org.drmod.parsers.parser.JenkinsModel;

import java.io.IOException;
import java.io.InputStream;

public class Mapper {

    public static Status parse(InputStream inputStream, TypeReference typeReference) {
        Status status;
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            JenkinsModel jenkinsModel = objectMapper.readValue(inputStream,
                    typeReference);
            status = Status.typeOfResponse(jenkinsModel.getResult());
        } catch (IOException e) {
            status = Status.RESPONSE_ERROR;
        }
        return status;
    }
}
