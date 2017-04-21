/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.h2020.symbiote.interfaces;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import eu.h2020.symbiote.cloud.monitoring.model.CloudMonitoringPlatform;

/**
 *
 * @author Matteo Pardi <m.pardi@nextworks.it>
 */
public class MonitoringNotification {
    private static final Logger log = LoggerFactory.getLogger(MonitoringNotification.class);
    
    public String receiveMessage(String message) {
        String json = "";
        try {
            log.debug("Monitoring notification message received.\n" + message);
            ObjectMapper mapper = new ObjectMapper();
            CloudMonitoringPlatform msg = mapper.readValue(message, CloudMonitoringPlatform.class);
            
        } catch (Exception e) {
            log.error("Error while processing message:\n" + message + "\n" + e);
        }
        return json;
    }
    
    private void registerMonitoringNotification() {
        try {
            
        } catch (Exception e ) {
            log.error("Error " + e);
        }
    }
}
