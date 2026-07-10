package com.project.back_end.models;

import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "prescriptions")
public class Prescription {

  // @Document annotation:
    //    - Marks the class as a MongoDB document (a collection in MongoDB).
    //    - The collection name is specified as "prescriptions" to map this class to the "prescriptions" collection in MongoDB.

    // 1. 'id' field:
    //    - Type: private String
    //    - Description:
    //      - Represents the unique identifier for each prescription.
    //      - The @Id annotation marks it as the primary key in the MongoDB collection.
    //      - The id is of type String, which is commonly used for MongoDB's ObjectId as it stores IDs as strings in the database.
    @Id
    private String id;
    // 2. 'patientName' field:
    //    - Type: private String
    //    - Description:
    //      - Represents the name of the patient receiving the prescription.
    //      - The @NotNull annotation ensures that the patient name is required.
    //      - The @Size(min = 3, max = 100) annotation ensures that the name length is between 3 and 100 characters, ensuring a reasonable name length.
    @NotNull(message = "El nombre del paciente no puede ser nulo")
    @Size(
            min = 3,
            max = 100,
            message = "El nombre del paciente debe tener entre 3 y 100 caracteres"
    )
    private String patientName;
    // 3. 'appointmentId' field:
    //    - Type: private Long
    //    - Description:
    //      - Represents the ID of the associated appointment where the prescription was given.
    //      - The @NotNull annotation ensures that the appointment ID is required for the prescription.
    @NotNull(message = "El ID de la cita no puede ser nulo")
    private Long appointmentId;
    // 4. 'medication' field:
    //    - Type: private String
    //    - Description:
    //      - Represents the medication prescribed to the patient.
    //      - The @NotNull annotation ensures that the medication name is required.
    //      - The @Size(min = 3, max = 100) annotation ensures that the medication name is between 3 and 100 characters, which ensures meaningful medication names.
    @NotNull(message = "El medicamento no puede ser nulo")
    @Size(
            min = 3,
            max = 100,
            message = "El medicamento debe tener entre 3 y 100 caracteres"
    )
    private String medication;
    // 5. 'dosage' field:
    //    - Type: private String
    //    - Description:
    //      - Represents the dosage information for the prescribed medication.
    //      - The @NotNull annotation ensures that the dosage information is provided.
    @NotNull(message = "La dosis no puede ser nula")
    @Size(
            min = 3,
            max = 20,
            message = "La dosis debe tener entre 3 y 20 caracteres"
    )
    private String dosage;
    // 6. 'doctorNotes' field:
    //    - Type: private String
    //    - Description:
    //      - Represents any additional notes or instructions from the doctor regarding the prescription.
    //      - The @Size(max = 200) annotation ensures that the doctor's notes do not exceed 200 characters, providing a reasonable limit for additional notes.
    @Size(
            max = 200,
            message = "Las notas del doctor no pueden superar los 200 caracteres"
    )
    private String doctorNotes;
    // 7. Constructors:
    //    - The class includes a no-argument constructor (default constructor) and a parameterized constructor that initializes the fields: patientName, medication, dosage, doctorNotes, and appointmentId.
    public Prescription() {

    }
    // 8. Getters and Setters:
    //    - Standard getter and setter methods are provided for all fields: id, patientName, medication, dosage, doctorNotes, and appointmentId.
    //    - These methods allow access and modification of the fields of the Prescription class.
    public Prescription(
            String patientName,
            Long appointmentId,
            String medication,
            String dosage,
            String doctorNotes
    ) {
        this.patientName = patientName;
        this.appointmentId = appointmentId;
        this.medication = medication;
        this.dosage = dosage;
        this.doctorNotes = doctorNotes;
    }
    // 9. Getter and setter for 'id':
    // - Allows access to the prescription identifier.
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    // 10. Getter and setter for 'patientName':
    // - Allows reading or changing the patient's name.
    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    // 11. Getter and setter for 'appointmentId':
    // - Allows reading or changing the related appointment identifier.

    public Long getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(Long appointmentId) {
        this.appointmentId = appointmentId;
    }

    // 12. Getter and setter for 'medication':
    // - Allows reading or changing the prescribed medication.
    public String getMedication() {
        return medication;
    }

    public void setMedication(String medication) {
        this.medication = medication;
    }

    // 13. Getter and setter for 'dosage':
    // - Allows reading or changing the dosage information.
    public String getDosage() {
        return dosage;
    }

    public void setDosage(String dosage) {
        this.dosage = dosage;
    }

    // 14. Getter and setter for 'doctorNotes':
    // - Allows reading or changing the doctor's optional notes.
    public String getDoctorNotes() {
        return doctorNotes;
    }

    public void setDoctorNotes(String doctorNotes) {
        this.doctorNotes = doctorNotes;
    }

}