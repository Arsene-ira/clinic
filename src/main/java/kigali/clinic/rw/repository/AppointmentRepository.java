package kigali.clinic.rw.repository;

import java.sql.Date;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import kigali.clinic.rw.domain.Appointment;
import kigali.clinic.rw.domain.AppointmentStatus;
import kigali.clinic.rw.dto.OverdueAppointmentDto;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, UUID> {

    List<Appointment> findByStatus(AppointmentStatus status);

    
    
    
    
    List<Appointment> findByStatusOrderByAppointmentDateAsc(AppointmentStatus status);

    
    
    
    List<Appointment> findByAppointmentDateBetweenOrderByAppointmentDateAsc(Date start, Date end);

    
    
    
    
    
    
    
    boolean existsByDoctorIdAndAppointmentDateAndStatusNot(UUID doctorId, Date appointmentDate, AppointmentStatus status);

    List<Appointment> findByPatientId(UUID patientId);

    List<Appointment> findByDoctorId(UUID doctorId);

    List<Appointment> findByAppointmentDate(Date date);

    List<Appointment> findByAppointmentDateBetween(Date from, Date to);

    List<Appointment> findByAppointmentDateAfterOrderByAppointmentDateAsc(Date date);

    List<Appointment> findByDoctorIdAndStatus(UUID doctorId, AppointmentStatus status);

    List<Appointment> findByPatientIdAndStatus(UUID patientId, AppointmentStatus status);

    List<Appointment> findByReasonContainingIgnoreCase(String part);

    long countByStatus(AppointmentStatus status);

    long countByDoctorId(UUID doctorId);

    
    @Query("""
            select a from Appointment a
            where a.appointmentDate >= :today
              and a.status in (kigali.clinic.rw.domain.AppointmentStatus.SCHEDULED,
                               kigali.clinic.rw.domain.AppointmentStatus.CONFIRMED)
            order by a.appointmentDate asc
            """)
    List<Appointment> findUpcoming(Date today);

    @Query("select a from Appointment a where a.doctor.id = :doctorId and a.appointmentDate = :date")
    List<Appointment> findDoctorSchedule(UUID doctorId, Date date);

    
    

    
    List<Appointment> findByDoctorIdAndStatusOrderByAppointmentDateAsc(UUID doctorId, AppointmentStatus status);

    
    @Query("""
            select new kigali.clinic.rw.dto.OverdueAppointmentDto(
                concat(p.firstName, ' ', p.lastName),
                concat(d.firstName, ' ', d.lastName),
                a.appointmentDate)
            from Appointment a
            join a.patient p
            join a.doctor d
            where a.status = kigali.clinic.rw.domain.AppointmentStatus.SCHEDULED
              and a.appointmentDate < :today
            order by a.appointmentDate asc
            """)
    List<OverdueAppointmentDto> findOverdueScheduled(Date today);

    
    @Query("""
            select distinct a from Appointment a
            join a.doctor d
            join d.specializations s
            where s.name = :specialization
            order by a.appointmentDate asc
            """)
    List<Appointment> findByDoctorSpecialization(String specialization);

    
    Page<Appointment> findByDoctorId(UUID doctorId, Pageable pageable);

    

    
    
    
    
    
    @Query("select a.status, count(a) from Appointment a group by a.status")
    List<Object[]> countByStatusGrouped();

    
    
    
    
    
    
    
    
    @Query("""
            select o.name, o.officeNumber, count(a)
            from Appointment a
            join a.doctor d
            join d.office o
            group by o.id, o.name, o.officeNumber
            order by count(a) desc
            """)
    List<Object[]> findBusiestOffice(Pageable pageable);

    
    
    
    
    
    
    
    
    @Modifying(clearAutomatically = true)
    @Query("""
            update Appointment a
            set a.status = kigali.clinic.rw.domain.AppointmentStatus.CANCELLED
            where a.doctor.id = :doctorId
              and a.appointmentDate = :date
              and a.status <> kigali.clinic.rw.domain.AppointmentStatus.COMPLETED
            """)
    int cancelDoctorDay(UUID doctorId, Date date);

    
    
    
    @Modifying(clearAutomatically = true)
    @Query("""
            delete from Appointment a
            where a.status = kigali.clinic.rw.domain.AppointmentStatus.CANCELLED
              and a.appointmentDate < :date
            """)
    int deleteCancelledBefore(Date date);
}
