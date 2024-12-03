package edu.utah.cs.uparknow.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import edu.utah.cs.uparknow.exception.ResourceNotFoundException;
import edu.utah.cs.uparknow.model.Permits;
import edu.utah.cs.uparknow.repository.PermitsRepository;

@Service
public class PermitsService {

    @Autowired
    private PermitsRepository permitsRepository;

    public List<Permits> getAllPermits() {
        return permitsRepository.findAll();
    }

    public Optional<Permits> getPermitById(Integer id) {
        return permitsRepository.findById(id);
    }

    public Permits createPermit(Permits permit) {
        return permitsRepository.save(permit);
    }

    public Permits updatePermit(Integer id, Permits permitDetails) {
        Permits permit = permitsRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Permit not found for this id :: " + id));

        permit.setPermit_Name(permitDetails.getPermit_Name());
    

        return permitsRepository.save(permit);
    }

    public void deletePermit(Integer id) {
        Permits permit = permitsRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Permit not found for this id :: " + id));
        permitsRepository.delete(permit);
    }
}
