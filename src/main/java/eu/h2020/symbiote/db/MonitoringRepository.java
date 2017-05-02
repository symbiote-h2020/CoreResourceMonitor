/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.h2020.symbiote.db;


import java.util.Optional;
import java.util.List;
import org.springframework.stereotype.Repository;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 *
 * @author Matteo Pardi <m.pardi@nextworks.it>
 */
@Repository
public interface MonitoringRepository extends MongoRepository<MonitoringInfo, String> {
    
    /**
     * This method will find a Resource instance in the database by 
     * its resourceId.
     * 
     * @param resourceId    the id of the resource
     * @return              the Resource instance
     */
    public Optional<MonitoringInfo> findById(String resourceId);
    
    
    @Override
    public List<MonitoringInfo> findAll();
}
