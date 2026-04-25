# Proyecto Sistemas Operativos
## Sistema Concurrente de Gestion de Reservas de Auditorio Universitario Ramon Cabezas

### Descripcion
Este proyecto implementa un sistema concurrente cliente-servidor para la gestion de reservas del auditorio universitario Ramon Cabezas. El sistema busca coordinar a varios clientes conectados al mismo tiempo, evitando conflictos al consultar disponibilidad, crear reservas y apartar temporalmente espacios del calendario.

La solucion combina sockets, objetos serializados, hilos por cliente y control de concurrencia sobre recursos compartidos como el calendario, los equipos y las reservas temporales. Ademas, incluye una base para notificaciones en tiempo real hacia clientes que esten visualizando el estado de las reservas.

### Objetivo
El objetivo principal es administrar reservas del auditorio por fecha y seccion del dia, junto con la reserva de equipos asociados, de forma consistente bajo escenarios concurrentes. El sistema intenta evitar dobles reservas, bloquear temporalmente espacios mientras un cliente completa su seleccion y liberar automaticamente esos bloqueos cuando expira el tiempo de espera.

### Arquitectura Cliente-Servidor
La arquitectura del proyecto es cliente-servidor:

- El servidor escucha conexiones por sockets en el puerto `8000`.
- Cada cliente conectado es atendido por un `ClientHandler`.
- La comunicacion usa un protocolo basado en `command + payload`.
- El `payload` se envia como objeto serializado.
- El servidor responde con un objeto `Response`.

Conceptualmente, el intercambio sigue este patron:

```text
Cliente -> "COMANDO"
Cliente -> Objeto serializado con la solicitud
Servidor -> Response { success, message, data }
```

Este proyecto no expone una API REST ni usa HTTP; la comunicacion es por sockets y objetos Java serializados.

### Concurrencia
La parte concurrente del proyecto se apoya en varias ideas del curso:

- Un hilo por cliente para permitir multiples conexiones simultaneas.
- Secciones criticas sobre calendario, equipos y reservas.
- Uso de semaforos para evitar que dos clientes modifiquen el mismo recurso al mismo tiempo.
- Reservas temporales con TTL para reducir colisiones mientras un cliente completa su proceso.

### Estructura Del Proyecto
El repositorio esta dividido en dos aplicaciones principales y algunos archivos compartidos:

- `ClienteReservas/`
  Contiene la aplicacion cliente. Incluye la interfaz JavaFX, controladores de pantalla, modelos compartidos para comunicacion y la logica desde donde se envian comandos al servidor.

- `ClienteReservas/ClienteReservas`
  Dentro de esta ruta se encuentra la aplicacion cliente JavaFX actualmente activa, con sus vistas, controladores y clases de arranque.

- `ServidorReservas/`
  Contiene la aplicacion servidor. Aqui estan las clases de sockets, los `controller`, `database`, `Model`, `service`, `utils` y el punto de entrada del servidor.

- `ServidorReservas/src/main/java/controller`
  Implementa la logica de negocio de clientes, administradores, reservas, calendario y drafts.

- `ServidorReservas/src/main/java/database`
  Incluye los DAO y el acceso a base de datos. Aqui se centralizan las consultas SQL y las validaciones de disponibilidad.

- `ServidorReservas/src/main/java/Model`
  Contiene los modelos que viajan entre cliente y servidor, por ejemplo `Response`, `Reservation`, `RXE`, `CalendarBlock` y las solicitudes de draft.

- `ServidorReservas/src/main/java/server`
  Implementa el socket server, la lista de clientes conectados y el `ClientHandler` que procesa peticiones.

- `ServidorReservas/src/main/java/service`
  Reune servicios transversales como el manejo de notificaciones y la limpieza automatica de drafts expirados.

- `auditorium.sql`
  Esquema base de la base de datos del proyecto.

- `README.md`
  Documentacion general del sistema.

### Modelos Principales
Algunos modelos importantes para la API actual son:

- `Response`
  Objeto estandar de respuesta del servidor. Usa la forma:
  `Response { success, message, data }`

- `Reservation`
  Representa la reserva base del auditorio con fecha y seccion.

- `RXE`
  Representa un equipo solicitado dentro de una reserva, junto con la cantidad.

- `EquipmentReservationRequest`
  Agrupa la reserva base, el cliente y la lista de equipos de una reserva confirmada.

- `EquipmentReservationDraft`
  Representa una reserva temporal con fecha de creacion, expiracion y equipos en proceso.

- `EquipmentReservationDraftRequest`
  Solicitud usada para crear o actualizar el draft temporal.

- `CalendarBlock`
  Bloque minimo que frontend puede usar para pintar el calendario. Incluye:
  `reservationDate`, `idSection`, `status`

### API Y Protocolo De Comunicacion
La API del proyecto esta modelada como comandos enviados por socket. El cliente manda un comando como texto y luego el objeto asociado. El servidor procesa la solicitud y devuelve un `Response`.

Ejemplo conceptual:

```text
Comando: CREATE_CLIENT
Payload: ClientRequest
Respuesta: Response
```

### Reservas, Calendario Y TTL
La parte de reservas trabaja por fecha y seccion del dia. Sobre esa base, el sistema permite:

- consultar bloques del calendario por mes y ano;
- apartar temporalmente un bloque mediante drafts con TTL;
- confirmar una reserva definitiva junto con los equipos asociados;
- actualizar la vista del calendario cuando cambia el estado de un bloque.

El frontend consume estos cambios mediante objetos `CalendarBlock`, que representan la fecha, la seccion y el estado visual del bloque dentro del calendario.

### Broadcast Y Notificaciones
El servidor incluye un mecanismo de notificacion selectiva para clientes conectados que esten visualizando reservas.

La notificacion se implementa en `NotificationService` y funciona recorriendo la lista global de clientes conectados:

```text
Server.clients
```

Luego envia un `Response` a cada cliente que cumpla la condicion:

```text
client.isViewingReservations()
```

En otras palabras, no es un broadcast global a todos los sockets, sino un broadcast selectivo a quienes estan consultando reservas.

La notificacion viaja como un `Response` cuyo `data` contiene un `CalendarBlock`. Para frontend, lo importante es leer la fecha, la seccion y el estado del bloque para repintar el calendario.

### Base De Datos
La base de datos se define en `auditorium.sql` y contiene la estructura principal para usuarios, clientes, administradores, equipos, reservas y tablas asociadas al calendario y a los drafts temporales. El esquema ha evolucionado junto con la API, por lo que conviene revisar siempre la version actual del archivo antes de recrear la base desde cero.

### Como Ejecutar
#### Servidor
1. Configurar la conexion a base de datos en el servidor.
2. Ejecutar la clase `App` del proyecto `ServidorReservas`.
3. Esta clase abre una ventana grafica de super administrador (`superAdmin.fxml`).
4. Desde esa ventana se puede:
   - prender y apagar el servidor;
   - crear un super administrador;
   - seleccionar que usuario administrador tendra permisos de super admin;
   - revocar esos permisos.
5. Al presionar el boton de encendido, la interfaz inicia el servidor socket en el puerto `8000` y tambien activa el servicio de limpieza automatica de drafts.

#### Cliente
1. Ejecutar la aplicacion cliente despues de que el servidor haya sido encendido desde la ventana de super administrador.
2. Iniciar sesion o navegar por la interfaz grafica del cliente.
3. Las acciones del cliente se traducen en comandos y objetos serializados enviados al servidor.

### Observaciones
- El proyecto documenta una API por sockets, no una API REST.
- Frontend debe guiarse principalmente por `Response` para reaccionar a cambios.
