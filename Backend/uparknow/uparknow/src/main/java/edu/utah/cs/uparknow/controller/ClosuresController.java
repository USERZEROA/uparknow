package edu.utah.cs.uparknow.controller;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
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
import edu.utah.cs.uparknow.model.Closures;
import edu.utah.cs.uparknow.model.ClosuresId;
import edu.utah.cs.uparknow.service.ClosuresService;

@RestController
@RequestMapping("/api/v1")
public class ClosuresController {

    @Autowired
    private ClosuresService closuresService;

    @GetMapping("/closures")
    public List<Closures> getAllClosures() {
        return closuresService.getAllClosures();
    }

    @GetMapping("/closures/{spaceId}/{manaId}/{modStart}")
    public ResponseEntity<Closures> getClosuresById(
            @PathVariable("spaceId") Integer spaceId,
            @PathVariable("manaId") Integer manaId,
            @PathVariable("modStart") @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") Date modStart) {
        ClosuresId id = new ClosuresId(spaceId, manaId, modStart);
        Closures closures = closuresService.getClosuresById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Closures not found for id :: " + id));
        return ResponseEntity.ok().body(closures);
    }

    @PostMapping("/closures")
    public ResponseEntity<Closures> createClosures(@RequestBody Closures closures) {
        Closures createdClosures = closuresService.createClosures(closures);
        return ResponseEntity.status(201).body(createdClosures);
    }

    @PutMapping("/closures/{spaceId}/{manaId}/{modStart}")
    public ResponseEntity<Closures> updateClosures(
            @PathVariable("spaceId") Integer spaceId,
            @PathVariable("manaId") Integer manaId,
            @PathVariable("modStart") @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") Date modStart,
            @RequestBody Closures closuresDetails) {
        ClosuresId id = new ClosuresId(spaceId, manaId, modStart);
        Closures updatedClosures = closuresService.updateClosures(id, closuresDetails);
        return ResponseEntity.ok(updatedClosures);
    }

    @DeleteMapping("/closures/{spaceId}/{manaId}/{modStart}")
    public ResponseEntity<Void> deleteClosures(
            @PathVariable("spaceId") Integer spaceId,
            @PathVariable("manaId") Integer manaId,
            @PathVariable("modStart") @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") Date modStart) {
        ClosuresId id = new ClosuresId(spaceId, manaId, modStart);
        closuresService.deleteClosures(id);
        return ResponseEntity.noContent().build();
    }
}
