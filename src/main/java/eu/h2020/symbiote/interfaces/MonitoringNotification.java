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
import eu.h2020.symbiote.security.exceptions.aam.TokenValidationException;
import eu.h2020.symbiote.security.SecurityHandler;
import eu.h2020.symbiote.security.enums.ValidationStatus;
import eu.h2020.symbiote.security.token.Token;
import eu.h2020.symbiote.security.exceptions.SecurityHandlerException;

import eu.h2020.symbiote.db.MonitoringInfo;
import eu.h2020.symbiote.db.MonitoringRepository;
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
    SecurityHandler securityHandler;
    
    public String receiveMessage(String message) {
        String json = "";
        try {
            //checkToken();
            
            log.debug("Monitoring notification message received.\n" + message);
            ObjectMapper mapper = new ObjectMapper();
            CloudMonitoringPlatform msg = mapper.readValue(message, CloudMonitoringPlatform.class);
            
            registerMonitoringNotification(msg);
        } catch (Exception e) {
            log.error("Error while processing message:\n" + message + "\n" + e);
        }
        return json;
    }
    
    private void registerMonitoringNotification(CloudMonitoringPlatform monitor) {
        try {
            MonitoringInfo info = new MonitoringInfo(monitor);
            monitoringRepo.save(info);
        } catch (Exception e ) {
            log.error("Error " + e);
        }
    }
    
    private void checkToken(String tokenString) throws Exception {
        log.debug("Received a request for the following token: " + tokenString);
        try {
            Token token = new Token(tokenString);
            ValidationStatus status = securityHandler.verifyCoreToken(token);

            switch (status){
                case VALID: {
                    log.info("Token is VALID");  
                    break;
                }
                case VALID_OFFLINE: {
                    log.info("Token is VALID_OFFLINE");  
                    break;
                }
                case EXPIRED: {
                    log.info("Token is EXPIRED");
                    throw new TokenValidationException("Token is EXPIRED");
                }
                case REVOKED: {
                    log.info("Token is REVOKED");  
                    throw new TokenValidationException("Token is REVOKED");
                }
                case INVALID: {
                    log.info("Token is INVALID");  
                    throw new TokenValidationException("Token is INVALID");
                }
                case NULL: {
                    log.info("Token is NULL");  
                    throw new TokenValidationException("Token is NULL");
                }
            } 
        } catch (TokenValidationException e) { 
            log.error("Token " + tokenString + "could not be verified");
        }
    }
    
}
