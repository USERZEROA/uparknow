package edu.utah.cs.uparknow.service;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import edu.utah.cs.uparknow.exception.ResourceNotFoundException;
import edu.utah.cs.uparknow.model.Closures;
import edu.utah.cs.uparknow.model.ClosuresId;
import edu.utah.cs.uparknow.repository.ClosuresRepository;

@Service
public class ClosuresService {

    @Autowired
    private ClosuresRepository closuresRepository;

    public List<Closures> getAllClosures() {
        return closuresRepository.findAll();
    }

    public Optional<Closures> getClosuresById(ClosuresId id) {
        return closuresRepository.findById(id);
    }

    public Closures createClosures(Closures closures) {
        return closuresRepository.save(closures);
    }

    public Closures updateClosures(ClosuresId id, Closures closuresDetails) {
        Closures closures = closuresRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Closures not found for this id :: " + id));

        closures.setModReason(closuresDetails.getModReason());
        closures.setModEnd(closuresDetails.getModEnd());
        
        return closuresRepository.save(closures);
    }

    public void deleteClosures(ClosuresId id) {
        Closures closures = closuresRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Closures not found for this id :: " + id));
        closuresRepository.delete(closures);
    }
}
