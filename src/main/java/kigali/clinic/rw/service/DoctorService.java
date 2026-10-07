package kigali.clinic.rw.service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import kigali.clinic.rw.domain.Doctor;
import kigali.clinic.rw.domain.Office;
import kigali.clinic.rw.domain.Specialization;
import kigali.clinic.rw.repository.DoctorRepository;
import kigali.clinic.rw.repository.OfficeRepository;
import kigali.clinic.rw.repository.SpecializationRepository;

@Service
public class DoctorService {

    private final DoctorRepository doctorRepo;
    private final OfficeRepository officeRepo;
    private final SpecializationRepository specRepo;

    public DoctorService(DoctorRepository doctorRepo, OfficeRepository officeRepo,
                         SpecializationRepository specRepo) {
        this.doctorRepo = doctorRepo;
        this.officeRepo = officeRepo;
        this.specRepo = specRepo;
    }

    @Transactional
    public Doctor create(Doctor incoming) {
        Doctor doctor = new Doctor();
        copyFields(doctor, incoming);
        doctor = doctorRepo.save(doctor);
        replaceSpecializations(doctor, incoming.getSpecializations());
        return doctor;
    }

    @Transactional(readOnly = true)
    public List<Doctor> findAll() {
        return doctorRepo.findAll();
    }

    @Transactional(readOnly = true)
    public Doctor findById(UUID id) {
        return getOrThrow(id);
    }

    
    @Transactional
    public Doctor update(UUID id, Doctor incoming) {
        Doctor doctor = getOrThrow(id);
        copyFields(doctor, incoming);
        replaceSpecializations(doctor, incoming.getSpecializations());
        return doctorRepo.save(doctor);
    }

    @Transactional
    public void delete(UUID id) {
        Doctor doctor = getOrThrow(id);
        if (!doctor.getAppointments().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Doctor has appointments and cannot be deleted");
        }
        for (Specialization spec : new ArrayList<>(doctor.getSpecializations())) {
            doctor.removeSpecialization(spec);
        }
        doctorRepo.delete(doctor);
    }

    

    @Transactional
    public Doctor addSpecialization(UUID doctorId, UUID specId) {
        Doctor doctor = getOrThrow(doctorId);
        doctor.addSpecialization(getSpecOrThrow(specId));
        return doctor;
    }

    @Transactional
    public Doctor removeSpecialization(UUID doctorId, UUID specId) {
        Doctor doctor = getOrThrow(doctorId);
        doctor.removeSpecialization(getSpecOrThrow(specId));
        return doctor;
    }

    
    @Transactional(readOnly = true)
    public List<Doctor> findBySpecialization(String name) {
        return doctorRepo.findBySpecializationName(name);
    }

    
    @Transactional(readOnly = true)
    public List<Doctor> findWithoutOffice() {
        return doctorRepo.findDoctorsWithoutOffice();
    }

    private Doctor getOrThrow(UUID id) {
        return doctorRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Doctor not found"));
    }

    private Specialization getSpecOrThrow(UUID id) {
        return specRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Specialization not found"));
    }

    private void copyFields(Doctor target, Doctor source) {
        target.setFirstName(source.getFirstName());
        target.setLastName(source.getLastName());
        target.setDateOfBirth(source.getDateOfBirth());
        target.setOffice(resolveOffice(target, source.getOffice()));
    }

    
    private Office resolveOffice(Doctor doctor, Office ref) {
        if (ref == null || ref.getId() == null) {
            return null;
        }
        Office office = officeRepo.findById(ref.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Office not found"));
        Doctor occupant = office.getDoctor();
        if (occupant != null && !occupant.getId().equals(doctor.getId())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Office is already assigned to another doctor");
        }
        return office;
    }

    
    private void replaceSpecializations(Doctor doctor, List<Specialization> wanted) {
        for (Specialization old : new ArrayList<>(doctor.getSpecializations())) {
            doctor.removeSpecialization(old);
        }
        if (wanted == null) {
            return;
        }
        for (Specialization ref : wanted) {
            if (ref.getId() == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Specialization id is required");
            }
            doctor.addSpecialization(getSpecOrThrow(ref.getId()));
        }
    }
}
