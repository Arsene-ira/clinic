package kigali.clinic.rw.controller;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.springframework.format.annotation.DateTimeFormat;
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

import kigali.clinic.rw.domain.Patient;
import kigali.clinic.rw.repository.PatientRepository;
import kigali.clinic.rw.service.PatientService;

@RestController

@RequestMapping({"/api/patients", "/api/patient"})
public class PatientController {

    private final PatientService service;
    private final PatientRepository repo;

    public PatientController(PatientService service, PatientRepository repo) {
        this.service = service;
        this.repo = repo;
    }

    

    
    @PostMapping({"", "/save"})
    public ResponseEntity<Patient> create(@RequestBody Patient patient) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(patient));
    }

    @GetMapping
    public List<Patient> findAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public Patient findById(@PathVariable UUID id) {
        return service.findById(id);
    }

    @PutMapping("/{id}")
    public Patient update(@PathVariable UUID id, @RequestBody Patient patient) {
        return service.update(id, patient);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    

    @GetMapping("/by-first-name/{name}")
    public List<Patient> byFirstName(@PathVariable String name) {
        return repo.findByFirstName(name);
    }

    @GetMapping("/by-last-name/{name}")
    public List<Patient> byLastName(@PathVariable String name) {
        return repo.findByLastNameIgnoreCase(name);
    }

    @GetMapping("/by-full-name")
    public List<Patient> byFullName(@RequestParam String firstName, @RequestParam String lastName) {
        return repo.findByFirstNameAndLastName(firstName, lastName);
    }

    @GetMapping("/first-name-starts-with/{prefix}")
    public List<Patient> firstNameStartsWith(@PathVariable String prefix) {
        return repo.findByFirstNameStartingWith(prefix);
    }

    @GetMapping("/last-name-ends-with/{suffix}")
    public List<Patient> lastNameEndsWith(@PathVariable String suffix) {
        return repo.findByLastNameEndingWith(suffix);
    }

    @GetMapping("/search")
    public List<Patient> search(@RequestParam String lastName) {
        return repo.findByLastNameContainingIgnoreCase(lastName);
    }

    @GetMapping("/born-between")
    public List<Patient> bornBetween(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        return repo.findByDateOfBirthBetween(Date.valueOf(from), Date.valueOf(to));
    }

    @GetMapping("/born-before")
    public List<Patient> bornBefore(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return repo.findByDateOfBirthBefore(Date.valueOf(date));
    }

    @GetMapping("/sorted")
    public List<Patient> sortedByLastName() {
        return repo.findAllByOrderByLastNameAsc();
    }

    @GetMapping("/count-by-last-name/{name}")
    public long countByLastName(@PathVariable String name) {
        return repo.countByLastName(name);
    }

    @GetMapping("/without-appointments")
    public List<Patient> withoutAppointments() {
        return repo.findPatientsWithoutAppointments();
    }

    
    
    
    
    @GetMapping("/of-doctor/{doctorId}")
    public ResponseEntity<Object> ofDoctor(@PathVariable UUID doctorId) {
        return service.findPatientsOfDoctor(doctorId)
                .<ResponseEntity<Object>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("The doctor with that id does not exist"));
    }

    
    
    @GetMapping("/by-last-name")
    public List<Patient> byLastNameParam(@RequestParam String lastName) {
        return service.findByLastName(lastName);
    }

    
    
    @GetMapping("/frequent")
    public List<Patient> frequent(@RequestParam long min) {
        return service.findFrequent(min);
    }
}
