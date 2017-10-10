/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.h2020.symbiote;

import org.springframework.context.annotation.Configuration;

/**
 *
 * @author Matteo Pardi <m.pardi@nextworks.it>
 */
@Configuration
public class CrmDefinitions {    
    public static String CRM_EXCHANGE_IN = "symbIoTe.CoreResourceMonitor.exchange.in";
    public static String CRM_MONITORING_QUEUE = "symbIoTe.crm.monitoring.queue.in";
    public static String CRM_ROUTING_KEY = "monitoring";  
    public static boolean CRM_EXCHANGE_DURABLE = true;
    public static boolean CRM_EXCHANGE_AUTODELETE = false;   
    public static String CRM_EXCHANGE_OUT = "symbIoTe.CoreResourceMonitor.exchange.out";
}
