package kigali.clinic.rw.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import kigali.clinic.rw.domain.Office;
import kigali.clinic.rw.repository.AppointmentRepository;
import kigali.clinic.rw.repository.OfficeRepository;

@Service
public class OfficeService {

    private final OfficeRepository offRepo;
    
    private final AppointmentRepository appointmentRepo;

    public OfficeService(OfficeRepository offRepo, AppointmentRepository appointmentRepo) {
        this.offRepo = offRepo;
        this.appointmentRepo = appointmentRepo;
    }

    @Transactional
    public Office create(Office incoming) {
        checkNumberIsFree(incoming.getOfficeNumber(), null);
        Office office = new Office();
        copyFields(office, incoming);
        return offRepo.save(office);
    }

    @Transactional(readOnly = true)
    public List<Office> findAll() {
        return offRepo.findAll();
    }

    @Transactional(readOnly = true)
    public Office findById(UUID id) {
        return getOrThrow(id);
    }

    @Transactional
    public Office update(UUID id, Office incoming) {
        Office office = getOrThrow(id);
        checkNumberIsFree(incoming.getOfficeNumber(), id);
        copyFields(office, incoming);
        return offRepo.save(office);
    }

    @Transactional
    public void delete(UUID id) {
        Office office = getOrThrow(id);
        if (office.getDoctor() != null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Office is assigned to a doctor; reassign or delete the doctor first");
        }
        offRepo.delete(office);
    }

    
    
    
    
    
    @Transactional(readOnly = true)
    public Optional<Object[]> findBusiest() {
        List<Object[]> rows = appointmentRepo.findBusiestOffice(PageRequest.of(0, 1));
        return rows.stream().findFirst();
    }

    private Office getOrThrow(UUID id) {
        return offRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Office not found"));
    }

    
    private void checkNumberIsFree(int officeNumber, UUID currentId) {
        offRepo.findByOfficeNumber(officeNumber)
                .filter(existing -> !existing.getId().equals(currentId))
                .ifPresent(existing -> {
                    throw new ResponseStatusException(HttpStatus.CONFLICT,
                            "Office number " + officeNumber + " already exists");
                });
    }

    private void copyFields(Office target, Office source) {
        target.setName(source.getName());
        target.setOfficeNumber(source.getOfficeNumber());
    }
}
