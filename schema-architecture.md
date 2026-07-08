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