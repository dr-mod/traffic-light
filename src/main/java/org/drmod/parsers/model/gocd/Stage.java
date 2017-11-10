package org.drmod.parsers.model.gocd;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Stage implements Serializable {

    private String result;

    @JsonProperty("result")
    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    @Override
    public String toString() {
        return "Stage{" +
                "result='" + result + '\'' +
                '}';
    }

}
