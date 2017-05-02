/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.h2020.symbiote.db;

import org.springframework.data.mongodb.core.mapping.Document;
import eu.h2020.symbiote.cloud.monitoring.model.CloudMonitoringPlatform;
import org.springframework.data.annotation.Id;

/**
 *
 * @author Matteo Pardi <m.pardi@nextworks.it>
 */
@Document(collection="monitoring")
public class MonitoringInfo {
    @Id
    String id;
    
    CloudMonitoringPlatform platformData;
    
    public MonitoringInfo(CloudMonitoringPlatform platformData) {
        this.platformData = platformData;
    }
}
