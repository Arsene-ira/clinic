package kigali.clinic.rw.repository;

import java.sql.Date;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import kigali.clinic.rw.domain.Patient;

@Repository
public interface PatientRepository extends JpaRepository<Patient, UUID> {

    List<Patient> findByFirstName(String firstName);

    List<Patient> findByLastNameIgnoreCase(String lastName);

    
    
    
    
    
    
    
    List<Patient> findByLastNameIgnoreCaseOrderByFirstNameAsc(String lastName);

    List<Patient> findByFirstNameAndLastName(String firstName, String lastName);

    List<Patient> findByFirstNameStartingWith(String prefix);

    List<Patient> findByLastNameEndingWith(String suffix);

    List<Patient> findByLastNameContainingIgnoreCase(String part);

    List<Patient> findByDateOfBirthBetween(Date from, Date to);

    List<Patient> findByDateOfBirthBefore(Date date);

    List<Patient> findAllByOrderByLastNameAsc();

    long countByLastName(String lastName);

    boolean existsByFirstNameAndLastName(String firstName, String lastName);

    
    @Query("select p from Patient p where p.appointments is empty")
    List<Patient> findPatientsWithoutAppointments();

    
    
    
    
    
    
    @Query("select distinct a.patient from Appointment a where a.doctor.id = :doctorId")
    List<Patient> findPatientsOfDoctor(UUID doctorId);

    
    
    
    
    
    
    
    
    @Query("""
            select p
            from Appointment a
            join a.patient p
            group by p
            having count(a) >= :min
            order by count(a) desc
            """)
    List<Patient> findFrequentPatients(long min);
}
