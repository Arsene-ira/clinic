package kigali.clinic.rw.service;

import java.sql.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import kigali.clinic.rw.domain.Appointment;
import kigali.clinic.rw.domain.AppointmentStatus;
import kigali.clinic.rw.domain.Doctor;
import kigali.clinic.rw.domain.Patient;
import kigali.clinic.rw.repository.AppointmentRepository;
import kigali.clinic.rw.repository.DoctorRepository;
import kigali.clinic.rw.repository.PatientRepository;

@Service
public class AppointmentService {

    private final AppointmentRepository appointmentRepo;
    private final PatientRepository patientRepo;
    private final DoctorRepository doctorRepo;

    public AppointmentService(AppointmentRepository appointmentRepo, PatientRepository patientRepo,
                              DoctorRepository doctorRepo) {
        this.appointmentRepo = appointmentRepo;
        this.patientRepo = patientRepo;
        this.doctorRepo = doctorRepo;
    }

    
    
    
    @Transactional
    public Optional<Appointment> create(Appointment incoming) {
        Appointment appointment = new Appointment();
        
        
        copyFields(appointment, incoming);
        if (appointment.getStatus() == null) {
            appointment.setStatus(AppointmentStatus.SCHEDULED);
        }

        
        
        boolean alreadyBooked = appointmentRepo.existsByDoctorIdAndAppointmentDateAndStatusNot(
                appointment.getDoctor().getId(),
                appointment.getAppointmentDate(),
                AppointmentStatus.CANCELLED);
        if (alreadyBooked) {
            return Optional.empty(); 
        }
        return Optional.of(appointmentRepo.save(appointment));
    }

    @Transactional(readOnly = true)
    public List<Appointment> findAll() {
        return appointmentRepo.findAll();
    }

    @Transactional(readOnly = true)
    public Appointment findById(UUID id) {
        return getOrThrow(id);
    }

    @Transactional
    public Appointment update(UUID id, Appointment incoming) {
        Appointment appointment = getOrThrow(id);
        copyFields(appointment, incoming);
        return appointmentRepo.save(appointment);
    }

    @Transactional
    public Appointment changeStatus(UUID id, AppointmentStatus status) {
        Appointment appointment = getOrThrow(id);
        appointment.setStatus(status);
        return appointmentRepo.save(appointment);
    }

    @Transactional
    public void delete(UUID id) {
        appointmentRepo.delete(getOrThrow(id));
    }

    
    @Transactional(readOnly = true)
    public List<Appointment> findByStatus(AppointmentStatus status) {
        return appointmentRepo.findByStatusOrderByAppointmentDateAsc(status);
    }

    
    @Transactional(readOnly = true)
    public List<Appointment> findBetween(Date start, Date end) {
        return appointmentRepo.findByAppointmentDateBetweenOrderByAppointmentDateAsc(start, end);
    }

    
    @Transactional(readOnly = true)
    public List<Object[]> statsByStatus() {
        return appointmentRepo.countByStatusGrouped();
    }

    
    
    
    
    @Transactional
    public String cancelDay(UUID doctorId, Date date) {
        int updated = appointmentRepo.cancelDoctorDay(doctorId, date);
        return updated + " appointments cancelled";
    }

    
    @Transactional(readOnly = true)
    public Page<Appointment> findPage(Pageable pageable) {
        
        
        return appointmentRepo.findAll(pageable);
    }

    
    @Transactional
    public String deleteCancelledBefore(Date date) {
        int deleted = appointmentRepo.deleteCancelledBefore(date);
        return deleted + " appointments deleted";
    }

    private Appointment getOrThrow(UUID id) {
        return appointmentRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Appointment not found"));
    }

    private void copyFields(Appointment target, Appointment source) {
        target.setAppointmentDate(source.getAppointmentDate());
        target.setReason(source.getReason());
        target.setStatus(source.getStatus());
        target.setPatient(resolvePatient(source.getPatient()));
        target.setDoctor(resolveDoctor(source.getDoctor()));
    }

    private Patient resolvePatient(Patient ref) {
        if (ref == null || ref.getId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "patient id is required");
        }
        return patientRepo.findById(ref.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Patient not found"));
    }

    private Doctor resolveDoctor(Doctor ref) {
        if (ref == null || ref.getId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "doctor id is required");
        }
        return doctorRepo.findById(ref.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Doctor not found"));
    }
}
