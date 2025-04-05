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
import edu.utah.cs.uparknow.model.Managers;
import edu.utah.cs.uparknow.service.ManagersService;

@RestController
@RequestMapping("/api/v1")
public class ManagersController {

    @Autowired
    private ManagersService managersService;

    @GetMapping("/managers")
    public List<Managers> getAllManagers() {
        return managersService.getAllManagers();
    }

    @GetMapping("/managers/{id}")
    public ResponseEntity<Managers> getManagerById(@PathVariable("id") Integer id) {
        Managers manager = managersService.getManagerById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Manager not found for this id :: " + id));
        return ResponseEntity.ok().body(manager);
    }

    @PostMapping("/managers")
    public Managers createManager(@RequestBody Managers manager) {
        return managersService.createManager(manager);
    }

    @PutMapping("/managers/{id}")
    public ResponseEntity<Managers> updateManager(@PathVariable("id") Integer id,
                                                  @RequestBody Managers managerDetails) {
        Managers updatedManager = managersService.updateManager(id, managerDetails);
        return ResponseEntity.ok(updatedManager);
    }

    @DeleteMapping("/managers/{id}")
    public ResponseEntity<Void> deleteManager(@PathVariable("id") Integer id) {
        managersService.deleteManager(id);
        return ResponseEntity.noContent().build();
    }
}
