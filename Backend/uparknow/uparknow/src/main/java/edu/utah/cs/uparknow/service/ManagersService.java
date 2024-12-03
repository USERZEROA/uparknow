package edu.utah.cs.uparknow.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import edu.utah.cs.uparknow.exception.ResourceNotFoundException;
import edu.utah.cs.uparknow.model.Managers;
import edu.utah.cs.uparknow.repository.ManagersRepository;

@Service
public class ManagersService {

    @Autowired
    private ManagersRepository managersRepository;

    public List<Managers> getAllManagers() {
        return managersRepository.findAll();
    }

    public Optional<Managers> getManagerById(Integer id) {
        return managersRepository.findById(id);
    }

    public Managers createManager(Managers manager) {
        return managersRepository.save(manager);
    }

    public Managers updateManager(Integer id, Managers managerDetails) {
        Managers manager = managersRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Manager not found for this id :: " + id));

        manager.setMana_Name(managerDetails.getMana_Name());
    

        return managersRepository.save(manager);
    }

    public void deleteManager(Integer id) {
        Managers manager = managersRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Manager not found for this id :: " + id));
        managersRepository.delete(manager);
    }
}
