package kigali.clinic.rw.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import kigali.clinic.rw.domain.Patient;
import kigali.clinic.rw.repository.DoctorRepository;
import kigali.clinic.rw.repository.PatientRepository;

@Service
public class PatientService {

    private final PatientRepository patientRepo;
    
    private final DoctorRepository doctorRepo;

    public PatientService(PatientRepository patientRepo, DoctorRepository doctorRepo) {
        this.patientRepo = patientRepo;
        this.doctorRepo = doctorRepo;
    }

    @Transactional
    public Patient create(Patient incoming) {
        Patient patient = new Patient();
        copyFields(patient, incoming);
        return patientRepo.save(patient);
    }

    @Transactional(readOnly = true)
    public List<Patient> findAll() {
        return patientRepo.findAll();
    }

    @Transactional(readOnly = true)
    public Patient findById(UUID id) {
        return getOrThrow(id);
    }

    @Transactional
    public Patient update(UUID id, Patient incoming) {
        Patient patient = getOrThrow(id);
        copyFields(patient, incoming);
        return patientRepo.save(patient);
    }

    @Transactional
    public void delete(UUID id) {
        Patient patient = getOrThrow(id);
        if (!patient.getAppointments().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Patient has appointments and cannot be deleted");
        }
        patientRepo.delete(patient);
    }

    
    
    @Transactional(readOnly = true)
    public List<Patient> findByLastName(String lastName) {
        return patientRepo.findByLastNameIgnoreCaseOrderByFirstNameAsc(lastName);
    }

    
    
    
    
    @Transactional(readOnly = true)
    public Optional<List<Patient>> findPatientsOfDoctor(UUID doctorId) {
        if (!doctorRepo.existsById(doctorId)) {
            return Optional.empty();
        }
        return Optional.of(patientRepo.findPatientsOfDoctor(doctorId));
    }

    
    @Transactional(readOnly = true)
    public List<Patient> findFrequent(long min) {
        return patientRepo.findFrequentPatients(min);
    }

    private Patient getOrThrow(UUID id) {
        return patientRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Patient not found"));
    }

    private void copyFields(Patient target, Patient source) {
        target.setFirstName(source.getFirstName());
        target.setLastName(source.getLastName());
        target.setDateOfBirth(source.getDateOfBirth());
    }
}
