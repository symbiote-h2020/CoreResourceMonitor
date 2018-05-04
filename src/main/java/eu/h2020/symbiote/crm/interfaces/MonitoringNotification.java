/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.h2020.symbiote.crm.interfaces;

import com.fasterxml.jackson.databind.ObjectMapper;
import static eu.h2020.symbiote.crm.resources.CrmDefinitions.*;

import eu.h2020.symbiote.core.internal.crm.MonitoringResponseSecured;
import eu.h2020.symbiote.crm.managers.AuthorizationManager;
import eu.h2020.symbiote.crm.model.authorization.AuthorizationResult;
import eu.h2020.symbiote.crm.repository.MonitoringInfo;
import eu.h2020.symbiote.crm.repository.MonitoringRepository;
import eu.h2020.symbiote.security.communication.payloads.SecurityRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import eu.h2020.symbiote.cloud.monitoring.model.CloudMonitoringPlatform;
import eu.h2020.symbiote.cloud.monitoring.model.CloudMonitoringPlatformRequest;
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

    @Autowired
    AuthorizationManager authManager;
    

    public MonitoringResponseSecured receiveMessage(byte[] body) {
        String message = new String(body);
        try {
            log.debug("Monitoring notification message received.\n" + message);
            ObjectMapper mapper = new ObjectMapper();
            CloudMonitoringPlatformRequest msg = mapper.readValue(message, CloudMonitoringPlatformRequest.class);
            SecurityRequest secReq = msg.getSecurityRequest();
            if(secReq == null)
                throw new Exception("SecurityRequest is NULL");

            CloudMonitoringPlatform clMonPlatform = msg.getBody();
            AuthorizationResult result = authManager.checkNotificationSecured(clMonPlatform.getPlatformId(),secReq);
            if(!result.isValidated()) {
                throw new Exception("SecurityRequest is not valid");
            }
            registerMonitoringNotification(clMonPlatform);
            forwardMonitoringNotification(message);
        } catch (Throwable e) {
            log.error("Error while processing message:\n" + message + "\n" + e);
            MonitoringResponseSecured responseSecured = new MonitoringResponseSecured(500, message, new Object());
            responseSecured.setServiceResponse(authManager.generateServiceResponse().getServiceResponse());
            return responseSecured;
        }

        MonitoringResponseSecured responseSecured = new MonitoringResponseSecured(200, "OK", new Object());
        responseSecured.setServiceResponse(authManager.generateServiceResponse().getServiceResponse());
        return responseSecured;
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
