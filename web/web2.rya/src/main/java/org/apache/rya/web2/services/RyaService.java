package org.apache.rya.web2.services;

import javax.servlet.ServletOutputStream;

public interface RyaService {
    void queryRdf(String query,
                  String authorizations,
                  String visibility,
                  String infer,
                  String requestedMimeType,
                  ServletOutputStream outputStream);
}
