package org.drmod.parsers.model.gocd;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

import java.io.Serializable;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Pipeline implements Serializable {

    private List<Stage> stages;

    @JsonProperty("stages")
    public List<Stage> getStages() {
        return stages;
    }

    public void setStages(List<Stage> stages) {
        this.stages = stages;
    }
}
