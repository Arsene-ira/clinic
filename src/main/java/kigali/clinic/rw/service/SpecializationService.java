package kigali.clinic.rw.service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import kigali.clinic.rw.domain.Doctor;
import kigali.clinic.rw.domain.Specialization;
import kigali.clinic.rw.repository.DoctorRepository;
import kigali.clinic.rw.repository.SpecializationRepository;

@Service
public class SpecializationService {

    private final SpecializationRepository specRepo;
    private final DoctorRepository doctorRepo;

    public SpecializationService(SpecializationRepository specRepo, DoctorRepository doctorRepo) {
        this.specRepo = specRepo;
        this.doctorRepo = doctorRepo;
    }

    @Transactional
    public Specialization create(Specialization incoming) {
        checkNameIsFree(incoming.getName(), null);
        Specialization spec = new Specialization();
        spec.setName(incoming.getName());
        return specRepo.save(spec);
    }

    @Transactional(readOnly = true)
    public List<Specialization> findAll() {
        return specRepo.findAll();
    }

    @Transactional(readOnly = true)
    public Specialization findById(UUID id) {
        return getOrThrow(id);
    }

    @Transactional
    public Specialization update(UUID id, Specialization incoming) {
        Specialization spec = getOrThrow(id);
        checkNameIsFree(incoming.getName(), id);
        spec.setName(incoming.getName());
        return specRepo.save(spec);
    }

    @Transactional
    public void delete(UUID id) {
        Specialization spec = getOrThrow(id);
        
        for (Doctor doctor : new ArrayList<>(spec.getDoctors())) {
            doctor.removeSpecialization(spec);
        }
        specRepo.delete(spec);
    }

    

    @Transactional
    public Specialization addDoctor(UUID specId, UUID doctorId) {
        Specialization spec = getOrThrow(specId);
        Doctor doctor = doctorRepo.findById(doctorId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Doctor not found"));
        doctor.addSpecialization(spec);
        return spec;
    }

    @Transactional
    public Specialization removeDoctor(UUID specId, UUID doctorId) {
        Specialization spec = getOrThrow(specId);
        Doctor doctor = doctorRepo.findById(doctorId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Doctor not found"));
        doctor.removeSpecialization(spec);
        return spec;
    }

    
    @Transactional(readOnly = true)
    public List<Specialization> findUnused() {
        return specRepo.findWithoutDoctors();
    }

    private Specialization getOrThrow(UUID id) {
        return specRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Specialization not found"));
    }

    private void checkNameIsFree(String name, UUID currentId) {
        if (name == null) {
            return;
        }
        specRepo.findByName(name)
                .filter(existing -> !existing.getId().equals(currentId))
                .ifPresent(existing -> {
                    throw new ResponseStatusException(HttpStatus.CONFLICT,
                            "Specialization '" + name + "' already exists");
                });
    }
}
