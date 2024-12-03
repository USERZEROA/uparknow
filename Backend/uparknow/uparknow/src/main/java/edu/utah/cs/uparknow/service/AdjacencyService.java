package edu.utah.cs.uparknow.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import edu.utah.cs.uparknow.exception.ResourceNotFoundException;
import edu.utah.cs.uparknow.model.Adjacency;
import edu.utah.cs.uparknow.model.AdjacencyId;
import edu.utah.cs.uparknow.repository.AdjacencyRepository;

@Service
public class AdjacencyService {

    @Autowired
    private AdjacencyRepository adjacencyRepository;

    public List<Adjacency> getAllAdjacencies() {
        return adjacencyRepository.findAll();
    }

    public Optional<Adjacency> getAdjacencyById(AdjacencyId id) {
        return adjacencyRepository.findById(id);
    }

    public Adjacency createAdjacency(Adjacency adjacency) {
        return adjacencyRepository.save(adjacency);
    }

    public Adjacency updateAdjacency(AdjacencyId id, Adjacency adjacencyDetails) {
        Adjacency adjacency = adjacencyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Adjacency not found for this id :: " + id));

        adjacency.setPlace_ID(adjacencyDetails.getPlace_ID());
        

        return adjacencyRepository.save(adjacency);
    }

    public void deleteAdjacency(AdjacencyId id) {
        Adjacency adjacency = adjacencyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Adjacency not found for this id :: " + id));
        adjacencyRepository.delete(adjacency);
    }
}
