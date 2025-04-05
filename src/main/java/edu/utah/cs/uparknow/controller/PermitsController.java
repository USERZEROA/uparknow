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
import edu.utah.cs.uparknow.model.Permits;
import edu.utah.cs.uparknow.service.PermitsService;

@RestController
@RequestMapping("/api/v1")
public class PermitsController {

    @Autowired
    private PermitsService permitsService;

    @GetMapping("/permits")
    public List<Permits> getAllPermits() {
        return permitsService.getAllPermits();
    }

    @GetMapping("/permits/{id}")
    public ResponseEntity<Permits> getPermitById(@PathVariable("id") Integer id) {
        Permits permit = permitsService.getPermitById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Permit not found for this id :: " + id));
        return ResponseEntity.ok().body(permit);
    }

    @PostMapping("/permits")
    public Permits createPermit(@RequestBody Permits permit) {
        return permitsService.createPermit(permit);
    }

    @PutMapping("/permits/{id}")
    public ResponseEntity<Permits> updatePermit(@PathVariable("id") Integer id,
                                                @RequestBody Permits permitDetails) {
        Permits updatedPermit = permitsService.updatePermit(id, permitDetails);
        return ResponseEntity.ok(updatedPermit);
    }

    @DeleteMapping("/permits/{id}")
    public ResponseEntity<Void> deletePermit(@PathVariable("id") Integer id) {
        permitsService.deletePermit(id);
        return ResponseEntity.noContent().build();
    }
}
