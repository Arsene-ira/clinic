# Clinic quiz: study notes

## Run it
1. PostgreSQL running with a `clinic` database (see `application.properties`).
2. `./mvnw spring-boot:run -Dspring-boot.run.profiles=seed`
   The `seed` profile fills the database (quiz Step 0) and prints the doctor and patient ids in the console.
3. Import `clinic-quiz.postman_collection.json` in Postman and paste the ids into the collection variables
   `doctorId` (Alice) and `patientId` (Jean).

`ddl-auto=create` rebuilds the tables on every start, so the UUIDs change each run.

## Where each requirement lives
| ID | Endpoint | Type | Repository method |
|----|----------|------|-------------------|
| A1 | GET /api/patients/by-last-name?lastName= | DERIVED | `PatientRepository.findByLastNameIgnoreCaseOrderByFirstNameAsc` |
| A2 | GET /api/appointments/by-status?status= | DERIVED | `AppointmentRepository.findByStatusOrderByAppointmentDateAsc` |
| A3 | GET /api/appointments/between?start=&end= | DERIVED | `AppointmentRepository.findByAppointmentDateBetweenOrderByAppointmentDateAsc` |
| A4 | POST /api/appointments/save | DERIVED | `AppointmentRepository.existsByDoctorIdAndAppointmentDateAndStatusNot` |
| B1 | GET /api/doctors/by-specialization?name= | JPQL | `DoctorRepository.findBySpecializationName` |
| B2 | GET /api/doctors/without-office | JPQL | `DoctorRepository.findDoctorsWithoutOffice` |
| B3 | GET /api/specializations/unused | JPQL | `SpecializationRepository.findWithoutDoctors` |
| B4 | GET /api/patients/of-doctor/{doctorId} | JPQL | `PatientRepository.findPatientsOfDoctor` |
| C1 | GET /api/appointments/stats/by-status | JPQL | `AppointmentRepository.countByStatusGrouped` |
| C2 | GET /api/patients/frequent?min= | JPQL | `PatientRepository.findFrequentPatients` |
| C3 | GET /api/offices/busiest | JPQL | `AppointmentRepository.findBusiestOffice` |
| C4 | PATCH /api/appointments/cancel-day?doctorId=&date= | JPQL | `AppointmentRepository.cancelDoctorDay` |
| +5 | GET /api/appointments/page | Pageable | `JpaRepository.findAll(Pageable)` |
| +5 | DELETE /api/appointments/cancelled-before?date= | JPQL | `AppointmentRepository.deleteCancelledBefore` |

The flow is always **controller -> service -> repository**. Read the comments above each method.

## Expected results with the seed data
- A1 `uwase` -> Jean, Marie (Jean first)
- B1 `cardiology` -> Alice | B2 -> Grace | B3 -> Neurology
- B4 Alice -> Jean, Marie, Paul (each once) | unknown id -> 404
- C1 -> SCHEDULED 3, CONFIRMED 2, COMPLETED 2, CANCELLED 3
- C2 `min=3` -> Jean only (Paul, Marie and Aline have 2 each) | C3 -> Heart Wing, 101, 5 appointments
- C4 Alice + 2026-10-20 -> "2 appointments cancelled" (the COMPLETED one is skipped)
- Bonus delete before 2026-10-15 -> "2 appointments deleted"
  (run C4 / the delete AFTER the read-only checks, they change the data)

## Notes on choices
- Controllers answer on both `/api/<thing>s/...` (quiz) and `/api/<thing>/...` (older exercises).
- Spring Data's `findBy...` with a collection (`Specializations`) or a bulk `UPDATE` can't be "derived" the same way,
  which is why B and C use `@Query` (JPQL).
- JPQL talks about entities and fields (`Appointment a`, `a.doctor.office`), never tables and columns.
