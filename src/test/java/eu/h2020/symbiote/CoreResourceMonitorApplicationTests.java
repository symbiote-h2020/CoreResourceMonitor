package eu.h2020.symbiote;

<<<<<<< HEAD
import eu.h2020.symbiote.cloud.monitoring.model.CloudMonitoringPlatform;
import eu.h2020.symbiote.db.MonitoringInfo;
import eu.h2020.symbiote.db.MonitoringRepository;
import eu.h2020.symbiote.cloud.monitoring.model.CloudMonitoringDevice;
=======
import eu.h2020.symbiote.cloud.monitoring.model.CloudMonitoringDevice;
import eu.h2020.symbiote.cloud.monitoring.model.CloudMonitoringPlatform;
import eu.h2020.symbiote.db.MonitoringInfo;
import eu.h2020.symbiote.db.MonitoringRepository;
import eu.h2020.symbiote.cloud.monitoring.model.Metric;
import java.util.ArrayList;
>>>>>>> d1665692028ac8126ab766ae4a53c3bc87eb5c60
import java.util.List;
import java.util.Optional;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest({"eureka.client.enabled=false"})
@TestConfiguration 
public class CoreResourceMonitorApplicationTests {
    
    private static final Logger log = LoggerFactory.getLogger(CoreResourceMonitorApplication.class);

    @Autowired
    private MonitoringRepository monitoringRepository;
    
    
    @Test
    public void contextLoads() {
    }
    
    @Test
    public void testMonitoringInfo() throws Exception{
        //insert
        String platformId = "pl_test_1";
        String devId = "dev_test_1";
        int devAvailability = 1;
        int devLoad = 2;
        String timestamp = "ts";
        CloudMonitoringDevice dev = new CloudMonitoringDevice();
        dev.setId(devId);
        dev.setAvailability(devAvailability);
        dev.setLoad(devLoad);
        dev.setTimestamp(timestamp);
        CloudMonitoringDevice [] devList = {dev};
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
    
    private MonitoringInfo addMonitoringInfo(String platformId, CloudMonitoringDevice[] devList) {
        CloudMonitoringPlatform cmp = new CloudMonitoringPlatform();
        if(platformId != null)
            cmp.setInternalId(platformId);
        if(devList != null && devList.length > 0)
            cmp.setDevices(devList);
        MonitoringInfo monitoringInfo = new MonitoringInfo(cmp);        
        MonitoringInfo monInfoResult = monitoringRepository.save(monitoringInfo);
        
        return monInfoResult;
    }
    

    
}