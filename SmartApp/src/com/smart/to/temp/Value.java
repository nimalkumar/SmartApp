
package com.smart.to;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Value {

    private List<String> columnNames = null;
    private List<String> columnTypes = null;
    private List<List<String>> values = null;
    
    public List<String> getColumnNames() {
        return columnNames;
    }

    public void setColumnNames(List<String> columnNames) {
        this.columnNames = columnNames;
    }

    public List<String> getColumnTypes() {
        return columnTypes;
    }

    public void setColumnTypes(List<String> columnTypes) {
        this.columnTypes = columnTypes;
    }

    public List<List<String>> getValues() {
        return values;
    }

    public void setValues(List<List<String>> values) {
        this.values = values;
    }

}
