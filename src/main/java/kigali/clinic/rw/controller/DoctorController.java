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

import kigali.clinic.rw.domain.Doctor;
import kigali.clinic.rw.dto.DoctorAppointmentCount;
import kigali.clinic.rw.repository.DoctorRepository;
import kigali.clinic.rw.service.DoctorService;

@RestController

@RequestMapping({"/api/doctors", "/api/doctor"})
public class DoctorController {

    private final DoctorService service;
    private final DoctorRepository repo;

    public DoctorController(DoctorService service, DoctorRepository repo) {
        this.service = service;
        this.repo = repo;
    }

    

    
    @PostMapping({"", "/save"})
    public ResponseEntity<Doctor> create(@RequestBody Doctor doctor) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(doctor));
    }

    @GetMapping
    public List<Doctor> findAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public Doctor findById(@PathVariable UUID id) {
        return service.findById(id);
    }

    @PutMapping("/{id}")
    public Doctor update(@PathVariable UUID id, @RequestBody Doctor doctor) {
        return service.update(id, doctor);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    

    @PostMapping("/{doctorId}/specializations/{specId}")
    public Doctor addSpecialization(@PathVariable UUID doctorId, @PathVariable UUID specId) {
        return service.addSpecialization(doctorId, specId);
    }

    @DeleteMapping("/{doctorId}/specializations/{specId}")
    public Doctor removeSpecialization(@PathVariable UUID doctorId, @PathVariable UUID specId) {
        return service.removeSpecialization(doctorId, specId);
    }

    

    @GetMapping("/by-first-name/{name}")
    public List<Doctor> byFirstName(@PathVariable String name) {
        return repo.findByFirstName(name);
    }

    @GetMapping("/by-last-name/{name}")
    public List<Doctor> byLastName(@PathVariable String name) {
        return repo.findByLastNameIgnoreCase(name);
    }

    @GetMapping("/by-full-name")
    public List<Doctor> byFullName(@RequestParam String firstName, @RequestParam String lastName) {
        return repo.findByFirstNameAndLastName(firstName, lastName);
    }

    @GetMapping("/last-name-starts-with/{prefix}")
    public List<Doctor> lastNameStartsWith(@PathVariable String prefix) {
        return repo.findByLastNameStartingWith(prefix);
    }

    @GetMapping("/born-before")
    public List<Doctor> bornBefore(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return repo.findByDateOfBirthBefore(Date.valueOf(date));
    }

    
    
    @GetMapping("/by-specialization")
    public List<Doctor> bySpecialization(@RequestParam String name) {
        return service.findBySpecialization(name);
    }

    @GetMapping("/by-office-number/{number}")
    public List<Doctor> byOfficeNumber(@PathVariable int number) {
        return repo.findByOfficeOfficeNumber(number);
    }

    @GetMapping("/sorted")
    public List<Doctor> sortedByLastName() {
        return repo.findAllByOrderByLastNameAsc();
    }

    @GetMapping("/count-by-specialization/{specId}")
    public long countBySpecialization(@PathVariable UUID specId) {
        return repo.countBySpecializationsId(specId);
    }

    @GetMapping("/without-appointments")
    public List<Doctor> withoutAppointments() {
        return repo.findDoctorsWithoutAppointments();
    }

    
    @GetMapping("/without-office")
    public List<Doctor> withoutOffice() {
        return service.findWithoutOffice();
    }

    @GetMapping("/busy")
    public List<Doctor> busy(@RequestParam(defaultValue = "1") int min) {
        return repo.findDoctorsWithAtLeast(min);
    }

    
    @GetMapping("/appointment-counts")
    public List<DoctorAppointmentCount> appointmentCounts() {
        return repo.countAppointmentsPerDoctor();
    }
}
