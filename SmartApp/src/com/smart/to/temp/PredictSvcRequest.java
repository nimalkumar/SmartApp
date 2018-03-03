
package com.smart.to;

import java.util.HashMap;
import java.util.Map;

public class PredictSvcRequest {

    private Inputs inputs;
    private GlobalParameters globalParameters;
    
    public Inputs getInputs() {
        return inputs;
    }

    public void setInputs(Inputs inputs) {
        this.inputs = inputs;
    }

    public GlobalParameters getGlobalParameters() {
        return globalParameters;
    }

    public void setGlobalParameters(GlobalParameters globalParameters) {
        this.globalParameters = globalParameters;
    }

}
