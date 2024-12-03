package edu.utah.cs.uparknow.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import edu.utah.cs.uparknow.model.Adjacency;
import edu.utah.cs.uparknow.model.AdjacencyId;

@Repository
public interface AdjacencyRepository extends JpaRepository<Adjacency, AdjacencyId> {
    
}
