package org.drmod.parsers.mapper;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.drmod.parsers.Status;
import org.drmod.parsers.model.gocd.Pagination;
import org.drmod.parsers.model.gocd.Stage;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class GocdMapper {

    public static Status parse(InputStream inputStream, TypeReference typeReference) {
        Status status;
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            Pagination gocdModel = objectMapper.readValue(inputStream, typeReference);

            Set<String> gocdStatuses = getGocdStatuses(gocdModel);

            status = mapStatus(gocdStatuses);
        } catch (IOException e) {
            status = Status.RESPONSE_ERROR;
        }
        return status;
    }

    private static Set<String> getGocdStatuses(Pagination gocdModel) {
        Set<String> gocdStatuses = Collections.emptySet();
        int pipelinesCount = gocdModel.getPipelines().size();
        for (int pipilineNumber = 1; pipilineNumber < pipelinesCount; pipilineNumber++) {
            gocdStatuses = aggregateData(gocdModel, pipilineNumber);
            if (!(gocdStatuses.isEmpty() || gocdStatuses.contains("Unknown")))
                break;
        }
        return gocdStatuses;
    }

    private static Status mapStatus(Set<String> collect) {
        Status status;
        if (collect.contains("Failed"))
            status = Status.FAILURE;
        else if (collect.contains("Cancelled"))
            status = Status.ABORTED;
        else if (collect.contains("Passed")) {
            status = Status.SUCCESS;
        } else {
            status = Status.UNDEFINED;
        }
        return status;
    }

    private static Set<String> aggregateData(Pagination gocdModel, int pipelineNumber) {
        return gocdModel.getPipelines().stream()
                .skip(pipelineNumber - 1)
                .limit(1)
                .flatMap(pipeline -> pipeline.getStages().stream())
                .map(Stage::getResult)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }
}
