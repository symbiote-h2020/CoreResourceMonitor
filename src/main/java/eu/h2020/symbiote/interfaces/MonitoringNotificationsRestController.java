/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.h2020.symbiote.interfaces;

import eu.h2020.symbiote.exceptions.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Matteo Pardi <m.pardi@nextworks.it>
 *
 * REST controller to receive notifications from Monitoring component
 * 
 */
@RestController
public class MonitoringNotificationsRestController {

    private static final Logger log = LoggerFactory.getLogger(MonitoringNotificationsRestController.class);
    
    /**
     * Used to get status notifications from Monitoring component
     * 
     * 
     * @param platformId    the id of the resource to query 
     * 
     */
    @RequestMapping(value="/crm/monitoring/{platformId}", method=RequestMethod.POST)
    public void statusNotification(@PathVariable String platformId) {        
        try {
            log.info("Received status notification with platformId = " + platformId);
            
            
        } catch(EntityNotFoundException enf) {
            throw enf;
        } catch (Exception e) {
            String err = "Unable to get notification with platformId: " + platformId;
            log.error(err + "\n" + e.getMessage());
            throw new GenericException(err);
        }        
    }
    
}
