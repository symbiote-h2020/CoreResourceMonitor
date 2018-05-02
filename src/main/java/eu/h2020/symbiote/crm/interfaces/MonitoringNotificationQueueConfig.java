/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.h2020.symbiote.crm.interfaces;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static eu.h2020.symbiote.crm.resources.CrmDefinitions.*;

/**
 *
 * @author Matteo Pardi <m.pardi@nextworks.it>
 */
@Configuration
public class MonitoringNotificationQueueConfig {
    private static final Logger log = LoggerFactory.getLogger(MonitoringNotificationQueueConfig.class);

    @Bean(name="exchangeOut")
    DirectExchange ExchangeOut() {
        return new DirectExchange(CRM_EXCHANGE_OUT, CRM_EXCHANGE_DURABLE, CRM_EXCHANGE_AUTODELETE);
    }
    
    @Bean(name="exchangeIn")
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
    Binding binding(Queue queue, @Qualifier("exchangeIn") TopicExchange exchange) {
        log.debug("Binding queue: " + queue.getName() + " to exchange: " + exchange.getName() + " with key: " + CRM_ROUTING_KEY);
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
