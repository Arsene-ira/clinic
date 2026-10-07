package kigali.clinic.rw.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import kigali.clinic.rw.domain.Specialization;

@Repository
public interface SpecializationRepository extends JpaRepository<Specialization, UUID> {

    Optional<Specialization> findByName(String name);

    List<Specialization> findByNameContainingIgnoreCase(String part);

    List<Specialization> findByNameStartingWith(String prefix);

    List<Specialization> findAllByOrderByNameAsc();

    boolean existsByNameIgnoreCase(String name);

    
    List<Specialization> findByDoctorsId(UUID doctorId);

    
    
    
    
    
    @Query("select s from Specialization s where s.doctors is empty")
    List<Specialization> findWithoutDoctors();
}
