package com.project.back_end.models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "appointments")
public class Appointment {

    // @Entity:
    // - Marks this class as a JPA entity.
    // - The class represents the "appointments" table in the database.
    // - Hibernate uses this annotation to map objects to database records.

    // 1. 'id' field:
    // - Type: Long
    // - Represents the unique identifier of an appointment.
    // - @Id marks this field as the primary key.
    // - @GeneratedValue automatically generates the identifier using the
    //   database auto-increment mechanism.
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 2. 'doctor' field:
    // - Type: Doctor
    // - Represents the doctor assigned to the appointment.
    // - @ManyToOne indicates that many appointments can belong to one doctor.
    // - @JoinColumn defines "doctor_id" as the foreign key column.
    // - @NotNull ensures that every appointment has an assigned doctor.
    @NotNull(message = "El doctor no puede ser nulo")
    @ManyToOne
    @JoinColumn(name = "doctor_id", nullable = false)
    private Doctor doctor;

    // 3. 'patient' field:
    // - Type: Patient
    // - Represents the patient assigned to the appointment.
    // - @ManyToOne indicates that many appointments can belong to one patient.
    // - @JoinColumn defines "patient_id" as the foreign key column.
    // - @NotNull ensures that every appointment has an assigned patient.
    @NotNull(message = "El paciente no puede ser nulo")
    @ManyToOne
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    // 4. 'appointmentTime' field:
    // - Type: LocalDateTime
    // - Stores both the date and start time of the appointment.
    // - @NotNull ensures that the date and time are provided.
    // - @Future ensures that new appointments are scheduled in the future.
    @NotNull(message = "La hora de la cita no puede ser nula")
    @Future(message = "La hora de la cita debe ser en el futuro")
    private LocalDateTime appointmentTime;

    // 5. 'status' field:
    // - Type: int
    // - Represents the current appointment status.
    // - 0 means scheduled.
    // - 1 means completed.
    // - @Min and @Max restrict the accepted values to 0 or 1.
    //
    // Note:
    // - @NotNull is not necessary because int is a primitive type and
    //   cannot contain null.
    @Min(value = 0, message = "El estado debe ser 0 o 1")
    @Max(value = 1, message = "El estado debe ser 0 o 1")
    private int status;

    // 6. No-argument constructor:
    // - Required by JPA so Hibernate can create entity instances.
    public Appointment() {
    }

    // 7. Parameterized constructor:
    // - Allows creating an appointment with its main required values.
    public Appointment(
            Doctor doctor,
            Patient patient,
            LocalDateTime appointmentTime,
            int status
    ) {
        this.doctor = doctor;
        this.patient = patient;
        this.appointmentTime = appointmentTime;
        this.status = status;
    }

    // 8. 'getEndTime' method:
    // - Return type: LocalDateTime
    // - Calculates the appointment end time by adding one hour to the
    //   appointment start time.
    // - @Transient prevents JPA from mapping this calculated value to a
    //   database column.
    // - Returns null when appointmentTime has not been assigned.
    @Transient
    public LocalDateTime getEndTime() {
        return appointmentTime == null
                ? null
                : appointmentTime.plusHours(1);
    }

    // 9. 'getAppointmentDate' method:
    // - Return type: LocalDate
    // - Extracts only the date portion from appointmentTime.
    // - @Transient prevents this calculated value from being persisted.
    // - Returns null when appointmentTime has not been assigned.
    @Transient
    public LocalDate getAppointmentDate() {
        return appointmentTime == null
                ? null
                : appointmentTime.toLocalDate();
    }

    // 10. 'getAppointmentTimeOnly' method:
    // - Return type: LocalTime
    // - Extracts only the time portion from appointmentTime.
    // - @Transient prevents this calculated value from being persisted.
    // - Returns null when appointmentTime has not been assigned.
    @Transient
    public LocalTime getAppointmentTimeOnly() {
        return appointmentTime == null
                ? null
                : appointmentTime.toLocalTime();
    }

    // 11. Getter and setter for 'id':
    // - Allows access to the generated appointment identifier.
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    // 12. Getter and setter for 'doctor':
    // - Allows reading or changing the doctor assigned to the appointment.
    public Doctor getDoctor() {
        return doctor;
    }

    public void setDoctor(Doctor doctor) {
        this.doctor = doctor;
    }

    // 13. Getter and setter for 'patient':
    // - Allows reading or changing the patient assigned to the appointment.
    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    // 14. Getter and setter for 'appointmentTime':
    // - Allows reading or changing the appointment start date and time.
    public LocalDateTime getAppointmentTime() {
        return appointmentTime;
    }

    public void setAppointmentTime(LocalDateTime appointmentTime) {
        this.appointmentTime = appointmentTime;
    }

    // 15. Getter and setter for 'status':
    // - Allows reading or changing the current appointment status.
    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}