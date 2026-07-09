Esta aplicación de Spring Boot utiliza tanto controladores MVC como REST. Se utilizan plantillas de Thymeleaf para los paneles de administración y de doctor, mientras que las API REST sirven a todos los demás módulos. La aplicación interactúa con dos bases de datos: MySQL (para datos de pacientes, doctores, citas y administración) y MongoDB (para recetas). Todos los controladores dirigen las solicitudes a través de una capa de servicio común, que a su vez delega en los repositorios apropiados. MySQL utiliza entidades JPA mientras que MongoDB utiliza modelos de documentos.

Flujo de datos de la arquitectura
1. Interacción desde la capa de presentación
    El flujo comienza cuando el usuario interactúa con alguno de los módulos de la aplicación, como AdminDashboard, DoctorDashboard, Appointments, PatientDashboard o PatientRecord. Los dashboards tradicionales utilizan vistas renderizadas en el servidor, mientras que los módulos REST consumen información mediante una API JSON.
2. Recepción de solicitudes en los controladores
    Las solicitudes provenientes de los dashboards son recibidas por los controladores de Thymeleaf. Por otro lado, las solicitudes realizadas desde los módulos que consumen la API son atendidas por los controladores REST. Ambos tipos de controladores validan la entrada y delegan el procesamiento a la capa de servicios.
3. Procesamiento en la capa de servicios
    La capa de servicios contiene la lógica de negocio de la aplicación. Aquí se coordinan operaciones como registrar pacientes, consultar doctores, administrar citas y recuperar expedientes médicos. Esta capa también determina qué repositorio debe utilizarse según el tipo de información solicitada.
4. Acceso a datos mediante repositorios
    La capa de servicios utiliza los repositorios correspondientes para consultar o modificar la información. Los repositorios MySQL gestionan los datos relacionales de pacientes, doctores, citas y administradores, mientras que el repositorio MongoDB administra información documental, como las prescripciones médicas.
5. Comunicación con las bases de datos
    Los repositorios ejecutan las operaciones necesarias sobre la base de datos correspondiente. MySQL se utiliza para almacenar datos estructurados y relacionados, mientras que MongoDB almacena documentos que requieren una estructura más flexible.
6. Conversión entre registros y modelos de la aplicación
    Los resultados obtenidos de las bases de datos se convierten en modelos que la aplicación puede utilizar. En MySQL, los registros se transforman mediante las entidades administradas por JPA. En MongoDB, los documentos se convierten en modelos documentales de Spring Data MongoDB.
7. Definición de entidades y documentos persistentes
    Los modelos de persistencia representan la estructura de los datos almacenados. Las clases Patient, Doctor, Appointment y Admin funcionan como entidades JPA relacionadas con MySQL. La clase Prescription funciona como un documento de MongoDB. Estos modelos permiten mapear los datos entre la aplicación y las bases de datos.

# Smart Clinic Database Design

## 1. Overview

The Smart Clinic Management System uses a hybrid database architecture.

MySQL is used to store structured and relational information such as patients, doctors, appointments, and administrators.

MongoDB is used to store flexible document-based information such as medical prescriptions, where each document may contain nested objects and arrays.

---

## 2. MySQL Database Design

### 2.1 Patients Table

The `patients` table stores the personal and authentication information of patients registered in the clinic system.

| Column | Data Type | Constraints | Description |
|---|---|---|---|
| patient_id | BIGINT | PRIMARY KEY, AUTO_INCREMENT | Unique identifier for the patient |
| first_name | VARCHAR(100) | NOT NULL | Patient's first name |
| last_name | VARCHAR(100) | NOT NULL | Patient's last name |
| email | VARCHAR(150) | NOT NULL, UNIQUE | Patient's email address |
| password | VARCHAR(255) | NOT NULL | Encrypted patient password |
| phone | VARCHAR(20) | NULL | Patient's phone number |
| date_of_birth | DATE | NULL | Patient's date of birth |
| created_at | TIMESTAMP | NOT NULL, DEFAULT CURRENT_TIMESTAMP | Record creation date |
| updated_at | TIMESTAMP | NOT NULL, DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP | Record update date |

#### Suggested SQL definition

```sql
CREATE TABLE patients (
    patient_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    phone VARCHAR(20),
    date_of_birth DATE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
        ON UPDATE CURRENT_TIMESTAMP
);
```

---

### 2.2 Doctors Table

The `doctors` table stores the professional and contact information of doctors.

| Column | Data Type | Constraints | Description |
|---|---|---|---|
| doctor_id | BIGINT | PRIMARY KEY, AUTO_INCREMENT | Unique identifier for the doctor |
| first_name | VARCHAR(100) | NOT NULL | Doctor's first name |
| last_name | VARCHAR(100) | NOT NULL | Doctor's last name |
| email | VARCHAR(150) | NOT NULL, UNIQUE | Doctor's email address |
| password | VARCHAR(255) | NOT NULL | Encrypted doctor password |
| specialization | VARCHAR(150) | NOT NULL | Doctor's medical specialization |
| phone | VARCHAR(20) | NULL | Doctor's phone number |
| active | BOOLEAN | NOT NULL, DEFAULT TRUE | Indicates whether the doctor is active |
| created_at | TIMESTAMP | NOT NULL, DEFAULT CURRENT_TIMESTAMP | Record creation date |
| updated_at | TIMESTAMP | NOT NULL, DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP | Record update date |

#### Suggested SQL definition

```sql
CREATE TABLE doctors (
    doctor_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    specialization VARCHAR(150) NOT NULL,
    phone VARCHAR(20),
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
        ON UPDATE CURRENT_TIMESTAMP
);
```

---

### 2.3 Appointments Table

The `appointments` table stores consultation bookings between patients and doctors.

| Column | Data Type | Constraints | Description |
|---|---|---|---|
| appointment_id | BIGINT | PRIMARY KEY, AUTO_INCREMENT | Unique appointment identifier |
| patient_id | BIGINT | NOT NULL, FOREIGN KEY | Patient who booked the appointment |
| doctor_id | BIGINT | NOT NULL, FOREIGN KEY | Doctor assigned to the appointment |
| appointment_date | DATE | NOT NULL | Appointment date |
| start_time | TIME | NOT NULL | Appointment start time |
| end_time | TIME | NOT NULL | Appointment end time |
| status | VARCHAR(30) | NOT NULL, DEFAULT 'SCHEDULED' | Current appointment status |
| reason | VARCHAR(500) | NULL | Reason for the medical consultation |
| created_at | TIMESTAMP | NOT NULL, DEFAULT CURRENT_TIMESTAMP | Record creation date |
| updated_at | TIMESTAMP | NOT NULL, DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP | Record update date |

#### Foreign keys

- `patient_id` references `patients.patient_id`.
- `doctor_id` references `doctors.doctor_id`.

#### Appointment constraints

- A patient must exist before an appointment can be created.
- A doctor must exist before an appointment can be created.
- The end time must be later than the start time.
- A doctor should not have two appointments during the same time period.

#### Suggested SQL definition

```sql
CREATE TABLE appointments (
    appointment_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    patient_id BIGINT NOT NULL,
    doctor_id BIGINT NOT NULL,
    appointment_date DATE NOT NULL,
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    status VARCHAR(30) NOT NULL DEFAULT 'SCHEDULED',
    reason VARCHAR(500),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
        ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT fk_appointments_patient
        FOREIGN KEY (patient_id)
        REFERENCES patients(patient_id),

    CONSTRAINT fk_appointments_doctor
        FOREIGN KEY (doctor_id)
        REFERENCES doctors(doctor_id),

    CONSTRAINT chk_appointment_time
        CHECK (end_time > start_time),

    CONSTRAINT uq_doctor_appointment_slot
        UNIQUE (doctor_id, appointment_date, start_time)
);
```

---

### 2.4 Administrators Table

The `administrators` table stores users who manage the Smart Clinic platform.

| Column | Data Type | Constraints | Description |
|---|---|---|---|
| admin_id | BIGINT | PRIMARY KEY, AUTO_INCREMENT | Unique administrator identifier |
| username | VARCHAR(100) | NOT NULL, UNIQUE | Administrator username |
| email | VARCHAR(150) | NOT NULL, UNIQUE | Administrator email address |
| password | VARCHAR(255) | NOT NULL | Encrypted administrator password |
| active | BOOLEAN | NOT NULL, DEFAULT TRUE | Indicates whether the account is active |
| created_at | TIMESTAMP | NOT NULL, DEFAULT CURRENT_TIMESTAMP | Record creation date |
| updated_at | TIMESTAMP | NOT NULL, DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP | Record update date |

#### Suggested SQL definition

```sql
CREATE TABLE administrators (
    admin_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(100) NOT NULL UNIQUE,
    email VARCHAR(150) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
        ON UPDATE CURRENT_TIMESTAMP
);
```

---

## 3. MySQL Relationships

The relational database contains the following relationships:

1. One patient can have many appointments.
2. One doctor can have many appointments.
3. Each appointment belongs to one patient.
4. Each appointment belongs to one doctor.
5. Administrators manage the clinic platform but are not directly linked to appointments.

Relationship summary:

```text
patients 1 -------- N appointments N -------- 1 doctors
```

---

## 4. MongoDB Collection Design

### 4.1 Prescriptions Collection

The `prescriptions` collection stores medical prescriptions created by doctors for patients.

MongoDB is appropriate for prescriptions because a prescription can contain multiple medications, instructions, notes, and nested medical information. The number of medications may vary between prescriptions.

### Example prescription document

```json
{
  "_id": "prescription_1001",
  "patientId": 15,
  "doctorId": 4,
  "appointmentId": 120,
  "diagnosis": {
    "name": "Acute respiratory infection",
    "code": "J06.9",
    "notes": "Patient presented with cough and mild fever."
  },
  "medications": [
    {
      "name": "Paracetamol",
      "dosage": "500 mg",
      "frequency": "Every 8 hours",
      "duration": "5 days",
      "instructions": "Take after meals"
    },
    {
      "name": "Cough syrup",
      "dosage": "10 ml",
      "frequency": "Every 12 hours",
      "duration": "7 days",
      "instructions": "Shake before use"
    }
  ],
  "patientInstructions": [
    "Drink plenty of water",
    "Rest for at least three days",
    "Return to the clinic if symptoms worsen"
  ],
  "followUp": {
    "required": true,
    "recommendedDate": "2026-07-20"
  },
  "status": "ACTIVE",
  "createdAt": "2026-07-09T10:30:00Z",
  "updatedAt": "2026-07-09T10:30:00Z"
}
```

### MongoDB document considerations

- `patientId`, `doctorId`, and `appointmentId` connect the document with records stored in MySQL.
- `diagnosis` is nested because it contains related medical details.
- `medications` is an array because one prescription can contain multiple medications.
- `patientInstructions` is an array because a doctor can provide multiple recommendations.
- `followUp` is nested because its fields belong to the same follow-up concept.

---

## 5. Design Justification

MySQL is used for patients, doctors, appointments, and administrators because these entities contain structured data and have clearly defined relationships.

Relational integrity is important for appointments because every appointment must be associated with an existing patient and doctor. Primary keys, foreign keys, unique constraints, and validation constraints help maintain consistent data.

MongoDB is used for prescriptions because prescription documents can contain a variable number of medications, nested diagnosis information, instructions, and follow-up data.

Using both databases allows the application to combine the reliability of relational data with the flexibility of document-based storage.
