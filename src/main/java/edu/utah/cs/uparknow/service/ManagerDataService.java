package edu.utah.cs.uparknow.service;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.fasterxml.jackson.databind.JsonNode;
import edu.utah.cs.uparknow.model.Closures;
import edu.utah.cs.uparknow.model.ParkingSpaces;
import edu.utah.cs.uparknow.repository.ClosuresRepository;
import edu.utah.cs.uparknow.repository.ParkingSpacesRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Service
public class ManagerDataService {

    private final ParkingSpacesRepository parkingSpacesRepository;
    private final ClosuresRepository closuresRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    public ManagerDataService(ParkingSpacesRepository parkingSpacesRepository,
                              ClosuresRepository closuresRepository) {
        this.parkingSpacesRepository = parkingSpacesRepository;
        this.closuresRepository = closuresRepository;
    }

    @Transactional
    public void updateManagerClosures(JsonNode payloadNode, int currentManaId) {
        closuresRepository.deleteAllByManaId(currentManaId);

        entityManager.flush();
        entityManager.clear();
        
        List<ParkingSpaces> spaces = parkingSpacesRepository.findAll();
        for (ParkingSpaces ps : spaces) {
            if (ps.getClosures() != null && ps.getClosures().stream().anyMatch(c -> c.getManaId() == currentManaId)) {
                ps.getClosures().clear();
                parkingSpacesRepository.save(ps);
            }
        }

        entityManager.flush();
        entityManager.clear();

        for (JsonNode psNode : payloadNode) {
            int spaceId = psNode.get("space_ID").asInt();
            Optional<ParkingSpaces> optPs = parkingSpacesRepository.findById(spaceId);
            ParkingSpaces ps = optPs.orElse(null);
            
            if (psNode.has("closures") && psNode.get("closures").isArray()) {
                for (JsonNode closureNode : psNode.get("closures")) {
                    int manaId = closureNode.get("manaId").asInt();
                    if (manaId != currentManaId) {
                        continue;
                    }        
                    long modStartMillis = closureNode.get("modStart").asLong();
                    Date modStart = new Date(modStartMillis);
                    
                    Date modEnd = null;
                    if (closureNode.has("modEnd") && !closureNode.get("modEnd").isNull()) {
                        modEnd = new Date(closureNode.get("modEnd").asLong());
                    }
                    
                    String modReason = closureNode.get("modReason").asText();
                    Closures newClosure = new Closures();
                    newClosure.setSpaceId(spaceId);
                    newClosure.setManaId(manaId);
                    newClosure.setModStart(modStart);
                    newClosure.setModEnd(modEnd);
                    newClosure.setModReason(modReason);

                    if (ps != null) {
                        newClosure.setParkingSpace(ps);
                    }
                                       
                    closuresRepository.save(newClosure);
                }
            }
        }
    }
}
