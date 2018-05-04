package eu.h2020.symbiote.crm;

import eu.h2020.symbiote.crm.managers.AuthorizationManager;
import org.mockito.Mockito;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

/**
 * @author Vasileios Glykantzis (ICOM)
 * @since 2/22/2018.
 */
@Profile("test")
@Configuration
public class TestConfiguration {

    @Bean
    @Primary
    public AuthorizationManager authorizationManager() {
        return Mockito.mock(AuthorizationManager.class);
    }

}
