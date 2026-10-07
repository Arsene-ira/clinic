package kigali.clinic.rw.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import kigali.clinic.rw.domain.Specialization;
import kigali.clinic.rw.repository.SpecializationRepository;
import kigali.clinic.rw.service.SpecializationService;

@RestController

@RequestMapping({"/api/specializations", "/api/specialization"})
public class SpecializationController {

    private final SpecializationService service;
    private final SpecializationRepository repo;

    public SpecializationController(SpecializationService service, SpecializationRepository repo) {
        this.service = service;
        this.repo = repo;
    }

    

    @PostMapping
    public ResponseEntity<Specialization> create(@RequestBody Specialization specialization) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(specialization));
    }

    @GetMapping
    public List<Specialization> findAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public Specialization findById(@PathVariable UUID id) {
        return service.findById(id);
    }

    @PutMapping("/{id}")
    public Specialization update(@PathVariable UUID id, @RequestBody Specialization specialization) {
        return service.update(id, specialization);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    

    @PostMapping("/{specId}/doctors/{doctorId}")
    public Specialization addDoctor(@PathVariable UUID specId, @PathVariable UUID doctorId) {
        return service.addDoctor(specId, doctorId);
    }

    @DeleteMapping("/{specId}/doctors/{doctorId}")
    public Specialization removeDoctor(@PathVariable UUID specId, @PathVariable UUID doctorId) {
        return service.removeDoctor(specId, doctorId);
    }

    

    @GetMapping("/by-name/{name}")
    public Specialization byName(@PathVariable String name) {
        return repo.findByName(name)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Specialization not found"));
    }

    @GetMapping("/search")
    public List<Specialization> search(@RequestParam String name) {
        return repo.findByNameContainingIgnoreCase(name);
    }

    @GetMapping("/name-starts-with/{prefix}")
    public List<Specialization> nameStartsWith(@PathVariable String prefix) {
        return repo.findByNameStartingWith(prefix);
    }

    @GetMapping("/sorted")
    public List<Specialization> sortedByName() {
        return repo.findAllByOrderByNameAsc();
    }

    @GetMapping("/of-doctor/{doctorId}")
    public List<Specialization> ofDoctor(@PathVariable UUID doctorId) {
        return repo.findByDoctorsId(doctorId);
    }

    
    
    @GetMapping("/unused")
    public List<Specialization> unused() {
        return service.findUnused();
    }

    @GetMapping("/without-doctors")
    public List<Specialization> withoutDoctors() {
        return repo.findWithoutDoctors();
    }

    @GetMapping("/exists")
    public boolean exists(@RequestParam String name) {
        return repo.existsByNameIgnoreCase(name);
    }
}
