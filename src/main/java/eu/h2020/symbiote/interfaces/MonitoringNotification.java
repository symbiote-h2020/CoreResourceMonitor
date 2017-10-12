/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.h2020.symbiote.interfaces;

import com.fasterxml.jackson.databind.ObjectMapper;
import static eu.h2020.symbiote.CrmDefinitions.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import eu.h2020.symbiote.cloud.monitoring.model.CloudMonitoringPlatform;
import eu.h2020.symbiote.cloud.monitoring.model.CloudMonitoringPlatformRequest;
import eu.h2020.symbiote.db.MonitoringInfo;
import eu.h2020.symbiote.db.MonitoringRepository;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author Matteo Pardi <m.pardi@nextworks.it>
 */
public class MonitoringNotification {
    private static final Logger log = LoggerFactory.getLogger(MonitoringNotification.class);
    
    @Autowired
    MonitoringRepository monitoringRepo;
    
    @Autowired
    RabbitTemplate rabbitTemplate;    
    

    public void receiveMessage(String message) {
        try {            
            log.debug("Monitoring notification message received.\n" + message);
            ObjectMapper mapper = new ObjectMapper();
            CloudMonitoringPlatformRequest msg = mapper.readValue(message, CloudMonitoringPlatformRequest.class);
            if(msg.getSecurityRequest() == null)
                throw new Exception("SecurityRequest is NULL");
            
            registerMonitoringNotification(msg.getBody());
            forwardMonitoringNotification(message);
        } catch (Exception e) {
            log.error("Error while processing message:\n" + message + "\n" + e);
        }
    }
    
    private void registerMonitoringNotification(CloudMonitoringPlatform monitor) {
        try {
            MonitoringInfo info = new MonitoringInfo(monitor);
            monitoringRepo.save(info);
        } catch (Exception e ) {
            log.error("Error " + e);
        }
    }
    
    private void forwardMonitoringNotification(String message) {
        try {

            rabbitTemplate.convertAndSend(CRM_EXCHANGE_OUT,CRM_ROUTING_KEY, message);
        } catch (Exception e ) {
            log.error("Error " + e);
        }
    }
}
