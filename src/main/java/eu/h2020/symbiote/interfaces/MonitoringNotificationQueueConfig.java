/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.h2020.symbiote.interfaces;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 *
 * @author Matteo Pardi <m.pardi@nextworks.it>
 */
@Configuration
public class MonitoringNotificationQueueConfig {        
    @Value("${rabbit.exchange.crm.name}")
    private String CRM_EXCHANGE_IN;
    @Value("${rabbit.routingKey.crm.monitoring}")
    private String CRM_ROUTING_KEY;
    @Value("${rabbit.exchange.crm.durable}")
    private boolean CRM_EXCHANGE_DURABLE;
    @Value("${rabbit.exchange.crm.autodelete}")
    private boolean CRM_EXCHANGE_AUTODELETE;
    @Value("${rabbit.exchange.crm.queue}")
    private String CRM_MONITORING_QUEUE;
    
    
    @Bean
    TopicExchange ExchangeIn() {
        return new TopicExchange(CRM_EXCHANGE_IN, CRM_EXCHANGE_DURABLE, CRM_EXCHANGE_AUTODELETE);
    }
    
    @Bean
    Queue queue() {
        return new Queue(CRM_MONITORING_QUEUE, CRM_EXCHANGE_DURABLE);
    }
    
    @Bean
    SimpleMessageListenerContainer container(ConnectionFactory connectionFactory,
                                            MessageListenerAdapter listenerAdapter) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.setQueueNames(CRM_MONITORING_QUEUE);
        container.setMessageListener(listenerAdapter);
        return container;
    }
    
    @Bean
    Binding binding(Queue queue, TopicExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with(CRM_ROUTING_KEY);
    }

    @Bean
    MonitoringNotification receiver() {
        return new MonitoringNotification();
    }

    @Bean
    MessageListenerAdapter listenerAdapter(MonitoringNotification receiver) {
        return new MessageListenerAdapter(receiver, "receiveMessage");
    }
}
