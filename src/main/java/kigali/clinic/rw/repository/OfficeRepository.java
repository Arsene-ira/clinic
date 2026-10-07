package kigali.clinic.rw.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import kigali.clinic.rw.domain.Office;

@Repository
public interface OfficeRepository extends JpaRepository<Office, UUID> {

    List<Office> findByName(String name);

    List<Office> findByNameContainingIgnoreCase(String part);

    Optional<Office> findByOfficeNumber(int officeNumber);

    List<Office> findByOfficeNumberBetween(int from, int to);

    List<Office> findByOfficeNumberGreaterThan(int number);

    List<Office> findAllByOrderByOfficeNumberAsc();

    boolean existsByOfficeNumber(int officeNumber);

    
    
    @Query("select o from Office o where cast(o.officeNumber as string) like concat(:prefix, '%')")
    List<Office> findByOfficeNumberStartsWith(String prefix);

    @Query("select o from Office o where cast(o.officeNumber as string) like concat('%', :suffix)")
    List<Office> findByOfficeNumberEndsWith(String suffix);

    @Query("select o from Office o where o.doctor is null")
    List<Office> findFreeOffices();
}
