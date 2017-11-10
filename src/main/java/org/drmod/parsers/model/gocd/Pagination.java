package org.drmod.parsers.model.gocd;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

import java.io.Serializable;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Pagination implements Serializable {

    private List<Pipeline> pipelines;

    @JsonProperty("pipelines")
    public List<Pipeline> getPipelines() {
        return pipelines;
    }

    public void setPipelines(List<Pipeline> pipelines) {
        this.pipelines = pipelines;
    }
}
