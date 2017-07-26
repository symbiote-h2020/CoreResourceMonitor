/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.h2020.symbiote;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 *
 * @author Matteo Pardi <m.pardi@nextworks.it>
 */
@Configuration
public class CrmDefinitions {
    @Value("${rabbit.exchange.crm.name}")
    public static String CRM_EXCHANGE_IN;
    @Value("${rabbit.routingKey.crm.monitoring}")
    public static String CRM_ROUTING_KEY;
    @Value("${rabbit.exchange.crm.durable}")
    public static boolean CRM_EXCHANGE_DURABLE;
    @Value("${rabbit.exchange.crm.autodelete}")
    public static boolean CRM_EXCHANGE_AUTODELETE;
    @Value("${rabbit.exchange.crm.queue}")
    public static String CRM_MONITORING_QUEUE;
    
    @Value("${rabbit.exchange.crm.out.name}")
    public static String CRM_EXCHANGE_OUT;
    @Value("${rabbit.exchange.crm.out.queue}")
    public static String CRM_EXCHANGE_OUT_QUEUE;
}
