# Smart Clinic Database Design

## Overview

The Smart Clinic Management System uses a hybrid persistence approach:

- **MySQL** stores structured, validated, and relational operational data.
- **MongoDB** stores flexible document-based data that may contain nested objects, arrays, optional fields, and changing structures.

This design supports the main clinic workflows: registering patients and doctors, scheduling appointments, managing doctor availability, creating prescriptions, and preserving historical medical information.

---

## MySQL Database Design

MySQL is used for the core entities because they have clear relationships and require referential integrity.

### Table: patients

Stores the personal and account information of each patient.

- `id`: BIGINT, Primary Key, Auto Increment
- `first_name`: VARCHAR(100), Not Null
- `last_name`: VARCHAR(100), Not Null
- `email`: VARCHAR(150), Not Null, Unique
- `password_hash`: VARCHAR(255), Not Null
- `phone`: VARCHAR(20), Nullable
- `date_of_birth`: DATE, Nullable
- `active`: BOOLEAN, Not Null, Default `TRUE`
- `created_at`: TIMESTAMP, Not Null, Default `CURRENT_TIMESTAMP`
- `updated_at`: TIMESTAMP, Not Null, Default `CURRENT_TIMESTAMP`, updated automatically

#### Design notes

- The email is unique so that two patients cannot register with the same account.
- Passwords must be stored as secure hashes, never as plain text.
- Email and phone formats should be validated in the application layer.
- The `active` field supports deactivation without deleting the patient's history.

```sql
CREATE TABLE patients (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    phone VARCHAR(20),
    date_of_birth DATE,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
        ON UPDATE CURRENT_TIMESTAMP
);
```

---

### Table: doctors

Stores doctor account, professional, and contact information.

- `id`: BIGINT, Primary Key, Auto Increment
- `first_name`: VARCHAR(100), Not Null
- `last_name`: VARCHAR(100), Not Null
- `email`: VARCHAR(150), Not Null, Unique
- `password_hash`: VARCHAR(255), Not Null
- `specialization`: VARCHAR(150), Not Null
- `license_number`: VARCHAR(80), Not Null, Unique
- `phone`: VARCHAR(20), Nullable
- `active`: BOOLEAN, Not Null, Default `TRUE`
- `created_at`: TIMESTAMP, Not Null, Default `CURRENT_TIMESTAMP`
- `updated_at`: TIMESTAMP, Not Null, Default `CURRENT_TIMESTAMP`, updated automatically

#### Design notes

- The medical license number is unique.
- Doctors should normally be deactivated instead of deleted when they already have appointment history.
- Email, phone, and license formats should be validated by application code.

```sql
CREATE TABLE doctors (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    specialization VARCHAR(150) NOT NULL,
    license_number VARCHAR(80) NOT NULL UNIQUE,
    phone VARCHAR(20),
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
        ON UPDATE CURRENT_TIMESTAMP
);
```

---

### Table: admin

Stores administrator accounts used to manage the platform.

- `id`: BIGINT, Primary Key, Auto Increment
- `username`: VARCHAR(100), Not Null, Unique
- `email`: VARCHAR(150), Not Null, Unique
- `password_hash`: VARCHAR(255), Not Null
- `active`: BOOLEAN, Not Null, Default `TRUE`
- `created_at`: TIMESTAMP, Not Null, Default `CURRENT_TIMESTAMP`
- `updated_at`: TIMESTAMP, Not Null, Default `CURRENT_TIMESTAMP`, updated automatically

#### Design notes

- Usernames and email addresses must be unique.
- Administrator passwords must be stored as secure hashes.
- An administrator can be disabled using `active` without deleting audit-related information.

```sql
CREATE TABLE admin (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(100) NOT NULL UNIQUE,
    email VARCHAR(150) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
        ON UPDATE CURRENT_TIMESTAMP
);
```

---

### Table: appointments

Stores scheduled consultations between patients and doctors.

- `id`: BIGINT, Primary Key, Auto Increment
- `doctor_id`: BIGINT, Foreign Key → `doctors(id)`, Not Null
- `patient_id`: BIGINT, Foreign Key → `patients(id)`, Not Null
- `appointment_time`: DATETIME, Not Null
- `end_time`: DATETIME, Not Null
- `status`: TINYINT, Not Null, Default `0`
  - `0` = Scheduled
  - `1` = Completed
  - `2` = Cancelled
- `reason`: VARCHAR(500), Nullable
- `created_at`: TIMESTAMP, Not Null, Default `CURRENT_TIMESTAMP`
- `updated_at`: TIMESTAMP, Not Null, Default `CURRENT_TIMESTAMP`, updated automatically

#### Design notes

- Every appointment must reference an existing patient and doctor.
- Historical appointments should be preserved.
- If a patient or doctor has appointments, the related account should be deactivated instead of physically deleted.
- A doctor must not have two appointments with the same start time.
- Complete overlap validation should also be performed in the service layer because overlapping ranges can occur even with different start times.
- The end time must be later than the start time.

```sql
CREATE TABLE appointments (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    doctor_id BIGINT NOT NULL,
    patient_id BIGINT NOT NULL,
    appointment_time DATETIME NOT NULL,
    end_time DATETIME NOT NULL,
    status TINYINT NOT NULL DEFAULT 0,
    reason VARCHAR(500),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
        ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT fk_appointments_doctor
        FOREIGN KEY (doctor_id)
        REFERENCES doctors(id)
        ON DELETE RESTRICT
        ON UPDATE CASCADE,

    CONSTRAINT fk_appointments_patient
        FOREIGN KEY (patient_id)
        REFERENCES patients(id)
        ON DELETE RESTRICT
        ON UPDATE CASCADE,

    CONSTRAINT chk_appointments_status
        CHECK (status IN (0, 1, 2)),

    CONSTRAINT chk_appointments_time
        CHECK (end_time > appointment_time),

    CONSTRAINT uq_doctor_appointment_start
        UNIQUE (doctor_id, appointment_time)
);
```

---

### Table: doctor_availability

Stores the working or available time ranges configured by each doctor.

- `id`: BIGINT, Primary Key, Auto Increment
- `doctor_id`: BIGINT, Foreign Key → `doctors(id)`, Not Null
- `day_of_week`: TINYINT, Not Null
  - `1` = Monday
  - `2` = Tuesday
  - `3` = Wednesday
  - `4` = Thursday
  - `5` = Friday
  - `6` = Saturday
  - `7` = Sunday
- `start_time`: TIME, Not Null
- `end_time`: TIME, Not Null
- `active`: BOOLEAN, Not Null, Default `TRUE`

#### Design notes

- Each doctor can define multiple availability ranges.
- The application must verify that an appointment falls inside an active availability range.
- The end time must be later than the start time.

```sql
CREATE TABLE doctor_availability (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    doctor_id BIGINT NOT NULL,
    day_of_week TINYINT NOT NULL,
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,

    CONSTRAINT fk_availability_doctor
        FOREIGN KEY (doctor_id)
        REFERENCES doctors(id)
        ON DELETE CASCADE
        ON UPDATE CASCADE,

    CONSTRAINT chk_availability_day
        CHECK (day_of_week BETWEEN 1 AND 7),

    CONSTRAINT chk_availability_time
        CHECK (end_time > start_time)
);
```

---

## MySQL Relationships

The main relationships are:

1. One patient can have many appointments.
2. One doctor can have many appointments.
3. Each appointment belongs to exactly one patient.
4. Each appointment belongs to exactly one doctor.
5. One doctor can have many availability ranges.
6. Administrators manage the platform but do not need a direct foreign key relationship with appointments.

```text
patients 1 -------- N appointments N -------- 1 doctors
                                      |
                                      |
                                      1
                                      |
                                      N
                           doctor_availability
```

---

## MySQL Deletion and History Decisions

### Patient deletion

Patients should not be physically deleted when they have appointments. The system should set `active = FALSE` so that appointment history remains available.

The `appointments.patient_id` foreign key uses `ON DELETE RESTRICT` to prevent accidental deletion.

### Doctor deletion

Doctors should also be deactivated when they have appointment history. The `appointments.doctor_id` foreign key uses `ON DELETE RESTRICT`.

Availability records may be deleted automatically if a doctor without relevant history is deleted because `doctor_availability.doctor_id` uses `ON DELETE CASCADE`.

### Appointment history

Completed and cancelled appointments should be preserved because they may be needed for:

- Medical history
- Reporting
- Auditing
- Follow-up consultations
- Prescription references

### Overlapping appointments

A unique constraint prevents duplicate appointment start times for the same doctor. The service layer must also reject any new appointment whose time range overlaps an existing scheduled appointment.

Example overlap condition:

```text
new_start < existing_end AND new_end > existing_start
```

---

## MongoDB Collection Design

### Collection: prescriptions

The `prescriptions` collection stores flexible prescription documents created by doctors.

MongoDB is suitable because a prescription may contain:

- A variable number of medications
- Nested diagnosis information
- Optional doctor notes
- Tags
- Metadata
- Follow-up instructions
- Attachments
- A structure that may evolve over time

The document stores only the MySQL identifiers for the patient, doctor, and appointment instead of duplicating their complete records. This avoids inconsistent duplicate data.

### Example document

```json
{
  "_id": "prescription_12345",
  "patientId": 67890,
  "doctorId": 54321,
  "appointmentId": 51,
  "diagnosis": {
    "code": "J06.9",
    "name": "Acute upper respiratory infection",
    "severity": "mild"
  },
  "medications": [
    {
      "name": "Amoxicillin",
      "dosage": "500 mg",
      "frequency": "Every 8 hours",
      "duration": "7 days",
      "route": "oral",
      "instructions": "Take with food"
    },
    {
      "name": "Ibuprofen",
      "dosage": "200 mg",
      "frequency": "Every 6 hours as needed",
      "duration": "3 days",
      "route": "oral",
      "instructions": "Do not exceed the recommended daily dose"
    }
  ],
  "tags": [
    "antibiotic",
    "pain-relief",
    "respiratory"
  ],
  "doctorNotes": "Patient should return if symptoms worsen or persist.",
  "patientInstructions": [
    "Drink plenty of water",
    "Rest for at least three days",
    "Complete the full antibiotic treatment"
  ],
  "followUp": {
    "required": true,
    "recommendedDate": "2026-07-20",
    "reason": "Review symptoms and treatment response"
  },
  "attachments": [
    {
      "type": "document",
      "fileName": "lab-results.pdf",
      "storageUrl": "/clinic-files/lab-results.pdf"
    }
  ],
  "metadata": {
    "createdAt": "2026-07-09T12:00:00Z",
    "updatedAt": "2026-07-09T12:00:00Z",
    "status": "active",
    "version": 1
  }
}
```

---

## MongoDB Design Decisions

### Why store only identifiers?

The prescription document stores `patientId`, `doctorId`, and `appointmentId` rather than the complete patient and doctor objects.

This approach:

- Avoids duplicating personal information
- Reduces the risk of outdated copies
- Keeps MySQL as the source of truth for structured entities
- Makes the MongoDB document smaller

### How does the schema support future changes?

MongoDB allows new optional fields to be added without migrating every existing document. For example, future prescriptions could include:

- Digital signatures
- Pharmacy details
- Refill history
- Insurance authorization
- Additional attachments
- Medication interaction warnings

The `metadata.version` field can be used to identify different document structures over time.

### Is the prescription linked to an appointment?

In this design, a prescription is normally linked to an appointment through `appointmentId`. This supports traceability.

The field could be nullable in a future implementation if the clinic allows prescriptions to be created outside a scheduled appointment.

---

## Final Design Justification

MySQL is the best option for patients, doctors, administrators, appointments, and availability because these entities require:

- Strong relationships
- Primary and foreign keys
- Unique constraints
- Referential integrity
- Consistent operational transactions

MongoDB is the best option for prescriptions because they contain:

- Nested objects
- Arrays
- Optional fields
- Flexible structures
- Information that may evolve over time

The hybrid approach combines the reliability of relational storage with the flexibility of document-based storage.
