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

import kigali.clinic.rw.domain.Office;
import kigali.clinic.rw.repository.OfficeRepository;
import kigali.clinic.rw.service.OfficeService;

@RestController

@RequestMapping({"/api/offices", "/api/office"})
public class OfficeController {

    private final OfficeService service;
    private final OfficeRepository repo;

    public OfficeController(OfficeService service, OfficeRepository repo) {
        this.service = service;
        this.repo = repo;
    }

    

    
    @PostMapping({"", "/save"})
    public ResponseEntity<Office> create(@RequestBody Office office) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(office));
    }

    @GetMapping
    public List<Office> findAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public Office findById(@PathVariable UUID id) {
        return service.findById(id);
    }

    @PutMapping("/{id}")
    public Office update(@PathVariable UUID id, @RequestBody Office office) {
        return service.update(id, office);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    

    @GetMapping("/by-name/{name}")
    public List<Office> byName(@PathVariable String name) {
        return repo.findByName(name);
    }

    @GetMapping("/search")
    public List<Office> search(@RequestParam String name) {
        return repo.findByNameContainingIgnoreCase(name);
    }

    @GetMapping("/by-number/{number}")
    public Office byNumber(@PathVariable int number) {
        return repo.findByOfficeNumber(number)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Office not found"));
    }

    @GetMapping("/number-starts-with/{prefix}")
    public List<Office> numberStartsWith(@PathVariable String prefix) {
        return repo.findByOfficeNumberStartsWith(prefix);
    }

    @GetMapping("/number-ends-with/{suffix}")
    public List<Office> numberEndsWith(@PathVariable String suffix) {
        return repo.findByOfficeNumberEndsWith(suffix);
    }

    @GetMapping("/number-between")
    public List<Office> numberBetween(@RequestParam int from, @RequestParam int to) {
        return repo.findByOfficeNumberBetween(from, to);
    }

    @GetMapping("/number-greater-than/{number}")
    public List<Office> numberGreaterThan(@PathVariable int number) {
        return repo.findByOfficeNumberGreaterThan(number);
    }

    @GetMapping("/sorted")
    public List<Office> sortedByNumber() {
        return repo.findAllByOrderByOfficeNumberAsc();
    }

    
    
    
    @GetMapping("/busiest")
    public Object busiest() {
        return service.findBusiest()
                .<Object>map(row -> row)
                .orElse("No appointments yet");
    }

    @GetMapping("/free")
    public List<Office> freeOffices() {
        return repo.findFreeOffices();
    }
}
