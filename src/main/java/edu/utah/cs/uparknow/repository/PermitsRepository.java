package edu.utah.cs.uparknow.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import edu.utah.cs.uparknow.model.Permits;

@Repository
public interface PermitsRepository extends JpaRepository<Permits, Integer> {
    
}
