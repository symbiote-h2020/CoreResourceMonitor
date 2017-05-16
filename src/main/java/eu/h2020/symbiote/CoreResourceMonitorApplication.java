package eu.h2020.symbiote;

import eu.h2020.symbiote.security.InternalSecurityHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class CoreResourceMonitorApplication {
        
    @Value("${rabbit.host}") 
    private String rabbitMQHostIP;

    @Value("${rabbit.username}") 
    private String rabbitMQUsername;

    @Value("${rabbit.password}") 
    private String rabbitMQPassword;

    @Value("${symbiote.coreaam.url}") 
    private String coreAAMUrl;

    public static void main(String[] args) {
        SpringApplication.run(CoreResourceMonitorApplication.class, args);
    }   
    
    @Bean
    public InternalSecurityHandler securityHandler() {
        return new InternalSecurityHandler(coreAAMUrl, rabbitMQHostIP, rabbitMQUsername, rabbitMQPassword);
    }
}
