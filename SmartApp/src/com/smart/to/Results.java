
package com.smart.to;

import java.util.HashMap;
import java.util.Map;

public class Results {

    private Output1 output1;
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    public Output1 getOutput1() {
        return output1;
    }

    public void setOutput1(Output1 output1) {
        this.output1 = output1;
    }

    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}
