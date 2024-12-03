package edu.utah.cs.uparknow.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import edu.utah.cs.uparknow.model.Closures;
import edu.utah.cs.uparknow.model.ClosuresId;

@Repository
public interface ClosuresRepository extends JpaRepository<Closures, ClosuresId> {
    
}
