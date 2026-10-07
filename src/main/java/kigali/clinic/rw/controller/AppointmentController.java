package kigali.clinic.rw.controller;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import kigali.clinic.rw.domain.Appointment;
import kigali.clinic.rw.domain.AppointmentStatus;
import kigali.clinic.rw.dto.OverdueAppointmentDto;
import kigali.clinic.rw.repository.AppointmentRepository;
import kigali.clinic.rw.service.AppointmentService;

@RestController


@RequestMapping({"/api/appointments", "/api/appointment"})
public class AppointmentController {

    private final AppointmentService service;
    private final AppointmentRepository repo;

    public AppointmentController(AppointmentService service, AppointmentRepository repo) {
        this.service = service;
        this.repo = repo;
    }

    

    
    
    
    
    
    @PostMapping({"", "/save"})
    public ResponseEntity<Object> create(@RequestBody Appointment appointment) {
        return service.create(appointment)
                .<ResponseEntity<Object>>map(saved -> ResponseEntity.status(HttpStatus.CREATED).body(saved))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.CONFLICT)
                        .body("Doctor is already booked on that date"));
    }

    @GetMapping
    public List<Appointment> findAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public Appointment findById(@PathVariable UUID id) {
        return service.findById(id);
    }

    @PutMapping("/{id}")
    public Appointment update(@PathVariable UUID id, @RequestBody Appointment appointment) {
        return service.update(id, appointment);
    }

    
    @PatchMapping("/{id}/status")
    public Appointment changeStatus(@PathVariable UUID id, @RequestParam AppointmentStatus status) {
        return service.changeStatus(id, status);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    

    
    
    
    @GetMapping("/by-status")
    public List<Appointment> byStatusParam(@RequestParam AppointmentStatus status) {
        return service.findByStatus(status);
    }

    @GetMapping("/by-status/{status}")
    public List<Appointment> byStatus(@PathVariable AppointmentStatus status) {
        return repo.findByStatus(status);
    }

    @GetMapping("/by-patient/{patientId}")
    public List<Appointment> byPatient(@PathVariable UUID patientId) {
        return repo.findByPatientId(patientId);
    }

    @GetMapping("/by-doctor/{doctorId}")
    public List<Appointment> byDoctor(@PathVariable UUID doctorId) {
        return repo.findByDoctorId(doctorId);
    }

    @GetMapping("/by-doctor/{doctorId}/status/{status}")
    public List<Appointment> byDoctorAndStatus(@PathVariable UUID doctorId, @PathVariable AppointmentStatus status) {
        return repo.findByDoctorIdAndStatus(doctorId, status);
    }

    @GetMapping("/by-patient/{patientId}/status/{status}")
    public List<Appointment> byPatientAndStatus(@PathVariable UUID patientId, @PathVariable AppointmentStatus status) {
        return repo.findByPatientIdAndStatus(patientId, status);
    }

    @GetMapping("/on-date")
    public List<Appointment> onDate(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return repo.findByAppointmentDate(Date.valueOf(date));
    }

    
    
    
    
    
    
    @GetMapping("/between")
    public List<Appointment> between(@RequestParam String start, @RequestParam String end) {
        LocalDate startDate = LocalDate.parse(start);
        LocalDate endDate = LocalDate.parse(end);
        return service.findBetween(Date.valueOf(startDate), Date.valueOf(endDate));
    }

    @GetMapping("/after")
    public List<Appointment> after(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return repo.findByAppointmentDateAfterOrderByAppointmentDateAsc(Date.valueOf(date));
    }

    @GetMapping("/upcoming")
    public List<Appointment> upcoming() {
        return repo.findUpcoming(Date.valueOf(LocalDate.now()));
    }

    @GetMapping("/schedule")
    public List<Appointment> doctorSchedule(
            @RequestParam UUID doctorId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return repo.findDoctorSchedule(doctorId, Date.valueOf(date));
    }

    @GetMapping("/search")
    public List<Appointment> searchByReason(@RequestParam String reason) {
        return repo.findByReasonContainingIgnoreCase(reason);
    }

    @GetMapping("/count-by-status/{status}")
    public long countByStatus(@PathVariable AppointmentStatus status) {
        return repo.countByStatus(status);
    }

    @GetMapping("/count-by-doctor/{doctorId}")
    public long countByDoctor(@PathVariable UUID doctorId) {
        return repo.countByDoctorId(doctorId);
    }

    

    
    @GetMapping("/by-doctor/{doctorId}/pending")
    public List<Appointment> pendingOfDoctor(@PathVariable UUID doctorId) {
        return repo.findByDoctorIdAndStatusOrderByAppointmentDateAsc(doctorId, AppointmentStatus.SCHEDULED);
    }

    
    @GetMapping("/overdue")
    public List<OverdueAppointmentDto> overdue() {
        return repo.findOverdueScheduled(Date.valueOf(LocalDate.now()));
    }

    
    @GetMapping("/by-specialization")
    public List<Appointment> bySpecialization(@RequestParam String name) {
        return repo.findByDoctorSpecialization(name);
    }

    
    @GetMapping("/by-doctor/{doctorId}/page")
    public Page<Appointment> pageOfDoctor(@PathVariable UUID doctorId,
                                          @RequestParam(defaultValue = "0") int page,
                                          @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("appointmentDate"));
        return repo.findByDoctorId(doctorId, pageable);
    }

    

    
    
    
    @GetMapping("/stats/by-status")
    public List<Object[]> statsByStatus() {
        return service.statsByStatus();
    }

    
    
    @PatchMapping("/cancel-day")
    public String cancelDay(@RequestParam UUID doctorId, @RequestParam String date) {
        return service.cancelDay(doctorId, Date.valueOf(LocalDate.parse(date)));
    }

    
    
    
    
    @GetMapping("/page")
    public Page<Appointment> page(@RequestParam(defaultValue = "0") int page,
                                  @RequestParam(defaultValue = "5") int size,
                                  @RequestParam(defaultValue = "appointmentDate,asc") String sort) {
        String[] parts = sort.split(",");
        Sort.Direction direction = parts.length > 1
                ? Sort.Direction.fromString(parts[1].trim())
                : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, parts[0].trim()));
        return service.findPage(pageable);
    }

    
    
    @DeleteMapping("/cancelled-before")
    public String deleteCancelledBefore(@RequestParam String date) {
        return service.deleteCancelledBefore(Date.valueOf(LocalDate.parse(date)));
    }
}
