package de.hs_mannheim.ffi.vs.syslog.model;
    
import javax.websocket.EncodeException;
import javax.websocket.Encoder;
import javax.websocket.EndpointConfig;

import de.hs_mannheim.ffi.vs.syslog.model.SyslogMessage;

public class SyslogMessageEncoder implements Encoder.Text<SyslogMessage> {
    @Override
    public String encode(SyslogMessage message) throws EncodeException {
        return message.toString();
    }

    @Override
    public void init(EndpointConfig config) {
        // Initialization logic, if needed
    }

    @Override
    public void destroy() {
        // Cleanup logic, if needed
    }
}

