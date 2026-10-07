package kigali.clinic.rw.repository;

import java.sql.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import kigali.clinic.rw.domain.Doctor;
import kigali.clinic.rw.dto.DoctorAppointmentCount;

@Repository
public interface DoctorRepository extends JpaRepository<Doctor, UUID> {

    List<Doctor> findByFirstName(String firstName);

    List<Doctor> findByLastNameIgnoreCase(String lastName);

    List<Doctor> findByFirstNameAndLastName(String firstName, String lastName);

    List<Doctor> findByLastNameStartingWith(String prefix);

    List<Doctor> findByDateOfBirthBefore(Date date);

    List<Doctor> findByOfficeOfficeNumber(int officeNumber);

    Optional<Doctor> findByOfficeId(UUID officeId);

    List<Doctor> findAllByOrderByLastNameAsc();

    long countBySpecializationsId(UUID specializationId);

    @Query("select d from Doctor d where d.appointments is empty")
    List<Doctor> findDoctorsWithoutAppointments();

    @Query("select d from Doctor d where size(d.appointments) >= :min order by size(d.appointments) desc")
    List<Doctor> findDoctorsWithAtLeast(int min);

    
    
    
    
    
    
    
    @Query("""
            select distinct d
            from Doctor d
            join d.specializations s
            where lower(s.name) = lower(:name)
            """)
    List<Doctor> findBySpecializationName(String name);

    
    
    
    @Query("select d from Doctor d where d.office is null order by d.lastName asc")
    List<Doctor> findDoctorsWithoutOffice();

    
    @Query("""
            select new kigali.clinic.rw.dto.DoctorAppointmentCount(d.id, d.firstName, d.lastName, count(a))
            from Doctor d
            left join d.appointments a
            group by d.id, d.firstName, d.lastName
            order by count(a) desc
            """)
    List<DoctorAppointmentCount> countAppointmentsPerDoctor();
}
