/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.h2020.symbiote.db;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import eu.h2020.symbiote.cloud.monitoring.model.CloudMonitoringPlatform;

/**
 *
 * @author Matteo Pardi <m.pardi@nextworks.it>
 */
@Document(collection="monitoring")
public class MonitoringInfo extends CloudMonitoringPlatform {
    
    @Id
    private final String id;
    
    public MonitoringInfo() {
        super();
        this.id = "";
    }
           
    public String getSymbioteId() {
        return id;
    }   
}
