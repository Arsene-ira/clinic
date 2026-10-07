package kigali.clinic.rw.config;

import java.sql.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import kigali.clinic.rw.domain.Appointment;
import kigali.clinic.rw.domain.AppointmentStatus;
import kigali.clinic.rw.domain.Doctor;
import kigali.clinic.rw.domain.Office;
import kigali.clinic.rw.domain.Patient;
import kigali.clinic.rw.domain.Specialization;
import kigali.clinic.rw.repository.AppointmentRepository;
import kigali.clinic.rw.repository.DoctorRepository;
import kigali.clinic.rw.repository.OfficeRepository;
import kigali.clinic.rw.repository.PatientRepository;
import kigali.clinic.rw.repository.SpecializationRepository;





















@Component
@Profile("seed")
public class DataSeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataSeeder.class);

    private final SpecializationRepository specRepo;
    private final OfficeRepository officeRepo;
    private final DoctorRepository doctorRepo;
    private final PatientRepository patientRepo;
    private final AppointmentRepository appointmentRepo;

    public DataSeeder(SpecializationRepository specRepo, OfficeRepository officeRepo,
                      DoctorRepository doctorRepo, PatientRepository patientRepo,
                      AppointmentRepository appointmentRepo) {
        this.specRepo = specRepo;
        this.officeRepo = officeRepo;
        this.doctorRepo = doctorRepo;
        this.patientRepo = patientRepo;
        this.appointmentRepo = appointmentRepo;
    }

    
    @Override
    @Transactional
    public void run(String... args) {
        if (specRepo.count() > 0) {
            log.info("Seed skipped: data already present");
            return;
        }

        
        Specialization cardiology = specialization("Cardiology");
        Specialization pediatrics = specialization("Pediatrics");
        Specialization dermatology = specialization("Dermatology");
        specialization("Neurology");

        
        Office office101 = office("Heart Wing", 101);
        Office office102 = office("Skin Wing", 102);
        office("Spare Room", 103);

        
        Doctor alice = doctor("Alice", "Mukamana", "1980-03-12", office101);
        Doctor eric = doctor("Eric", "Habimana", "1985-07-25", office102);
        Doctor grace = doctor("Grace", "Ingabire", "1990-11-02", null); 

        
        alice.addSpecialization(cardiology);
        alice.addSpecialization(pediatrics); 
        eric.addSpecialization(dermatology);
        grace.addSpecialization(pediatrics);

        
        Patient jean = patient("Jean", "Uwase", "1995-01-15");
        Patient marie = patient("Marie", "Uwase", "1998-05-30");
        Patient paul = patient("Paul", "Niyonzima", "1988-09-09");
        Patient aline = patient("Aline", "Mutesi", "2001-12-01");
        Patient david = patient("David", "Kamanzi", "1975-04-20");

        
        appointment(jean, alice, "2026-10-05", AppointmentStatus.COMPLETED, "Heart check");
        appointment(jean, eric, "2026-10-12", AppointmentStatus.CANCELLED, "Skin rash");
        appointment(jean, alice, "2026-10-20", AppointmentStatus.SCHEDULED, "Follow-up");
        appointment(marie, alice, "2026-10-20", AppointmentStatus.CONFIRMED, "Chest pain");
        appointment(paul, alice, "2026-10-20", AppointmentStatus.COMPLETED, "Blood pressure");
        appointment(aline, eric, "2026-10-14", AppointmentStatus.CANCELLED, "Acne");
        appointment(david, eric, "2026-10-28", AppointmentStatus.SCHEDULED, "Skin check");
        appointment(marie, grace, "2026-11-03", AppointmentStatus.SCHEDULED, "Child vaccine");
        appointment(aline, grace, "2026-11-10", AppointmentStatus.CONFIRMED, "Growth check");
        appointment(paul, alice, "2026-11-17", AppointmentStatus.CANCELLED, "Routine visit");

        
        log.info("SEED DONE. doctorId Alice = {}", alice.getId());
        log.info("SEED DONE. doctorId Eric  = {}", eric.getId());
        log.info("SEED DONE. doctorId Grace = {}", grace.getId());
        log.info("SEED DONE. patientId Jean = {}", jean.getId());
    }

    private Specialization specialization(String name) {
        Specialization s = new Specialization();
        s.setName(name);
        return specRepo.save(s);
    }

    private Office office(String name, int number) {
        Office o = new Office();
        o.setName(name);
        o.setOfficeNumber(number);
        return officeRepo.save(o);
    }

    private Doctor doctor(String first, String last, String dob, Office office) {
        Doctor d = new Doctor();
        d.setFirstName(first);
        d.setLastName(last);
        d.setDateOfBirth(Date.valueOf(dob));
        d.setOffice(office);
        return doctorRepo.save(d);
    }

    private Patient patient(String first, String last, String dob) {
        Patient p = new Patient();
        p.setFirstName(first);
        p.setLastName(last);
        p.setDateOfBirth(Date.valueOf(dob));
        return patientRepo.save(p);
    }

    private void appointment(Patient patient, Doctor doctor, String date,
                             AppointmentStatus status, String reason) {
        Appointment a = new Appointment();
        a.setPatient(patient);
        a.setDoctor(doctor);
        a.setAppointmentDate(Date.valueOf(date));
        a.setStatus(status);
        a.setReason(reason);
        appointmentRepo.save(a);
    }
}
