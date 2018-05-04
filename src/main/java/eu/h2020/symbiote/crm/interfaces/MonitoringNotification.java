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
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author Matteo Pardi <m.pardi@nextworks.it>
 */
@Service
public class MonitoringNotification {
    private static final Logger log = LoggerFactory.getLogger(MonitoringNotification.class);
    
    @Autowired
    MonitoringRepository monitoringRepo;
    
    @Autowired
    RabbitTemplate rabbitTemplate;

    @Autowired
    AuthorizationManager authManager;

    @RabbitListener(
            bindings = @QueueBinding(
                    value = @Queue(value = "${rabbit.queueName.crm.monitoring}",
                            durable = "${rabbit.exchange.crm.durable}",
                            autoDelete = "${rabbit.exchange.crm.autodelete}",
                            exclusive = "false"),
                    exchange = @Exchange(
                            value = "${rabbit.exchange.crm.name}",
                            ignoreDeclarationExceptions = "true",
                            durable = "${rabbit.exchange.crm.durable}",
                            autoDelete  = "${rabbit.exchange.crm.autodelete}",
                            internal = "${rabbit.exchange.crm.internal}",
                            type = "${rabbit.exchange.crm.type}"),
                    key = "${rabbit.routingKey.crm.monitoring}")
    )
    public MonitoringResponseSecured receiveMessage(CloudMonitoringPlatformRequest msg) {
        String message = null;

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            message = objectMapper.writeValueAsString(msg);

            log.debug("Monitoring notification message received.\n" + message);
            SecurityRequest secReq = msg.getSecurityRequest();

            CloudMonitoringPlatform clMonPlatform = msg.getBody();
            AuthorizationResult result = authManager.checkNotificationSecured(clMonPlatform.getPlatformId(), secReq);
            if(!result.isValidated()) {
                throw new Exception("SecurityRequest is not valid");
            }
            registerMonitoringNotification(clMonPlatform);
            forwardMonitoringNotification(message);

            MonitoringResponseSecured responseSecured = new MonitoringResponseSecured(200, "OK", null);
            responseSecured.setServiceResponse(authManager.generateServiceResponse().getServiceResponse());
            log.debug("ServiceResponse = " + responseSecured.getServiceResponse());
            log.debug("MonitoringResponseSecured = " + responseSecured);
            return responseSecured;

        } catch (Throwable e) {
            log.error("Error while processing message:\n" + message + "\n" + e);
            MonitoringResponseSecured responseSecured = new MonitoringResponseSecured(500, e.getMessage(), null);
            responseSecured.setServiceResponse(authManager.generateServiceResponse().getServiceResponse());
            log.debug("ServiceResponse = " + responseSecured.getServiceResponse());
            log.debug("MonitoringResponseSecured = " + responseSecured);
            return responseSecured;
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

            rabbitTemplate.convertAndSend(CRM_EXCHANGE_OUT, CRM_ROUTING_KEY, message);
        } catch (Exception e ) {
            log.error("Error " + e);
        }
    }
}
