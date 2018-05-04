package eu.h2020.symbiote.crm;

import eu.h2020.symbiote.cloud.monitoring.model.CloudMonitoringDevice;
import eu.h2020.symbiote.cloud.monitoring.model.CloudMonitoringPlatform;
import eu.h2020.symbiote.cloud.monitoring.model.CloudMonitoringPlatformRequest;
import eu.h2020.symbiote.core.internal.crm.MonitoringResponseSecured;
import eu.h2020.symbiote.crm.managers.AuthorizationManager;
import eu.h2020.symbiote.crm.model.authorization.AuthorizationResult;
import eu.h2020.symbiote.crm.model.authorization.ServiceResponseResult;
import eu.h2020.symbiote.crm.repository.MonitoringInfo;
import eu.h2020.symbiote.crm.repository.MonitoringRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doReturn;

@RunWith(SpringRunner.class)
@SpringBootTest({"eureka.client.enabled=false"})
@TestConfiguration
@ActiveProfiles("test")
public class CoreResourceMonitorApplicationTests {
    
    private static final Logger log = LoggerFactory.getLogger(CoreResourceMonitorApplication.class);

    @Autowired
    private MonitoringRepository monitoringRepository;

    @Autowired
    private AuthorizationManager authorizationManager;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Value("${rabbit.exchange.crm.name}")
    private String crmExchange;

    @Value("${rabbit.routingKey.crm.monitoring}")
    private String crmRoutingKey;

    @Test
    public void testMonitoringInfo() {
        //insert
        String platformId = "pl_test_1";
        String devId = "dev_test_1";
        /*int devAvailability = 1;
        int devLoad = 2;
        String timestamp = "ts";*/
        CloudMonitoringDevice dev = new CloudMonitoringDevice();
        dev.setId(devId);/*
        dev.setAvailability(devAvailability);
        dev.setLoad(devLoad);
        dev.setTimestamp(timestamp);*/
        List<CloudMonitoringDevice> devList = new ArrayList();
        devList.add(dev);
        MonitoringInfo monitoringInfoResult = addMonitoringInfo(platformId, devList);
        assert(monitoringInfoResult != null);
        String monitoringInfoId = monitoringInfoResult.getId();
        //search
        Optional<MonitoringInfo> monInfoOptional = monitoringRepository.findById(monitoringInfoId);
        assert(monInfoOptional.isPresent() == true);
        monInfoOptional = monitoringRepository.findById(monitoringInfoId+"2");
        assert(monInfoOptional.isPresent() == false);        
        List<MonitoringInfo> monitoringInfoList = monitoringRepository.findAll();
        assert(monitoringInfoList != null);
        assert(!monitoringInfoList.isEmpty());
        //delete
        monitoringRepository.delete(monitoringInfoResult);
        monInfoOptional = monitoringRepository.findById(monitoringInfoId);
        assert(monInfoOptional == null || !monInfoOptional.isPresent());
    }

    @Test
    public void receiveNotificationTest() {
        String serviceResponse = "serviceResponse";
        CloudMonitoringPlatform cloudMonitoringPlatform = new CloudMonitoringPlatform();
        cloudMonitoringPlatform.setPlatformId("platformId");

        doReturn(new AuthorizationResult("OK", true))
                .when(authorizationManager).checkNotificationSecured(any(), any());
        doReturn(new ServiceResponseResult(serviceResponse, true))
                .when(authorizationManager).generateServiceResponse();

        MonitoringResponseSecured responseSecured = (MonitoringResponseSecured) rabbitTemplate
                .convertSendAndReceive(crmExchange, crmRoutingKey,
                        new CloudMonitoringPlatformRequest(null, cloudMonitoringPlatform));

        assertEquals(serviceResponse, responseSecured.getServiceResponse());
    }
    
    private MonitoringInfo addMonitoringInfo(String platformId, List<CloudMonitoringDevice> devList) {
        CloudMonitoringPlatform cmp = new CloudMonitoringPlatform();
        if(platformId != null)
            cmp.setPlatformId(platformId);
        if(devList != null && devList.size()> 0)
            cmp.setMetrics(devList);
        MonitoringInfo monitoringInfo = new MonitoringInfo(cmp);        
        MonitoringInfo monInfoResult = monitoringRepository.save(monitoringInfo);
        
        return monInfoResult;
    }
}