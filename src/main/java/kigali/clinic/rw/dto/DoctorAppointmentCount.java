package kigali.clinic.rw.dto;

import java.util.UUID;


public record DoctorAppointmentCount(UUID doctorId, String firstName, String lastName, Long total) {
}
