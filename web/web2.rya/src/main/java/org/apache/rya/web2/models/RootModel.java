package org.apache.rya.web2.models;

public class RootModel {
    public String getMessage() {
        return "Apache Rya";
    }

    public String getVersion() {
        return "4.0.1-incubating-SNAPSHOT";
    }

    public String[] getEndpoints() {
        return new String[] {
          "/sparql"
        };
    }
}
