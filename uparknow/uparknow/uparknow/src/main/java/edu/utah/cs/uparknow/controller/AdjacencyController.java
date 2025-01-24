package edu.utah.cs.uparknow.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import edu.utah.cs.uparknow.exception.ResourceNotFoundException;
import edu.utah.cs.uparknow.model.Adjacency;
import edu.utah.cs.uparknow.model.AdjacencyId;
import edu.utah.cs.uparknow.service.AdjacencyService;

@RestController
@RequestMapping("/api/v1")
public class AdjacencyController {

    @Autowired
    private AdjacencyService adjacencyService;

    @GetMapping("/adjacencies")
    public List<Adjacency> getAllAdjacencies() {
        return adjacencyService.getAllAdjacencies();
    }

    @GetMapping("/adjacencies/{lotId}/{placeId}")
    public ResponseEntity<Adjacency> getAdjacencyById(@PathVariable("lotId") Integer lotId,
                                                      @PathVariable("placeId") Integer placeId) {
        AdjacencyId id = new AdjacencyId(lotId, placeId);
        Adjacency adjacency = adjacencyService.getAdjacencyById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Adjacency not found for id :: " + id));
        return ResponseEntity.ok().body(adjacency);
    }

    @PostMapping("/adjacencies")
    public Adjacency createAdjacency(@RequestBody Adjacency adjacency) {
        return adjacencyService.createAdjacency(adjacency);
    }

    @PutMapping("/adjacencies/{lotId}/{placeId}")
    public ResponseEntity<Adjacency> updateAdjacency(@PathVariable("lotId") Integer lotId,
                                                     @PathVariable("placeId") Integer placeId,
                                                     @RequestBody Adjacency adjacencyDetails) {
        AdjacencyId id = new AdjacencyId(lotId, placeId);
        Adjacency updatedAdjacency = adjacencyService.updateAdjacency(id, adjacencyDetails);
        return ResponseEntity.ok(updatedAdjacency);
    }

    @DeleteMapping("/adjacencies/{lotId}/{placeId}")
    public ResponseEntity<Void> deleteAdjacency(@PathVariable("lotId") Integer lotId,
                                               @PathVariable("placeId") Integer placeId) {
        AdjacencyId id = new AdjacencyId(lotId, placeId);
        adjacencyService.deleteAdjacency(id);
        return ResponseEntity.noContent().build();
    }
}
