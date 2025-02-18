# Indice 
 - [Supuesto](#Supuesto)
 - [Diagrama de Clases](#Diagrama-de-clases)
 - [Manual Técnico para Desarrolladores](#Manual-técnico-para-desarrolladores)
 - [Manual de Usuario](#Manual-de-Usuario)
 - [GitProject & Issues](#GitProject-e-Issues)
 - [Extras Realizados](#Extras-realizados)
 - [Propuestas de Mejora](#Propuestas-de-mejora)
 - [Conclusiones](#Conclusiones)
 - [Dedicación Temporal](#Dedicación-temporal)


# Supuesto

Nuestro proyecto de Springboot Teisport lo hemos planteado de forma que fuese útil para la persona creada con el mapa de
empatía utilizado en clase. En nuestro caso , Carlitos era un adolescente de 16 años que jugaba en un equipo de fútbol 
con sus amigos, pero solía estar más en el banquillo que jugando. 

Para poner solución a su problema planteamos Teisport. Una plataforma de cursos gratuitos y de pago en la que ofrecemos 
cursos con vídeos, guías y documentación creada por técnicos para ayudar a la mejora técnica, física y táctica de
jóvenes jugadores. 


# Diagrama de clases

Para ello hemos planteado la siguiente BD , representada en este diagrama ER.

![Esquema](img/teisport.png)

La entidad principal es la de Usuario. En el momento en el que se registra este accede a su Cuestionario Inicial. 
Una vez lo responde se lñe asignarán varios Planes de Entrenamiento en función de sus respuestas. Cada plan por su parte
tendrá un Contenido específico. A mayores los Usuarios podrán tener un Código de Descuento y podrán poner Tickets 
con Mensajes en caso de necesitar sporte por parte de los Usuarios Administradores.

# Manual Técnico para Desarrolladores

Para el desarrollo de nuestra aplicación web son necesarias las siguientes herramientas: 

- Springboot 2.7.14 

- Java 17.0.3 o superior.

- MySQL 8.0.33 (BD Local)

- RDS en AWS con verisión MySQL 8.0.33

- IntelliJ u otro IDE que permita el desarollo con las herramientas mencionadas.

## Estructura 

Para la realización del proyecto hemos utilizado un patrón por Capas.

Este modelo se suele usar en Spring Boot para aplicaciones web tradicionales con vistas renderizadas en el servidor. Se organizarían en los siguientes apartados que veremos a cotninuación.

A continuación mostaremos algunas de las partes más interesantes del código de las capas de nuestro proyecto.

## Presentación

Maneja las solicitudes HTTP e interactúa con la capa de servicios.
Devuelve respuestas al cliente . En nuestro caso vistas , ya que es una aplicación web.

- Admin Controller
````java
@PostMapping("/panelAdmin/{id}/addUser")
    public String addUser(@RequestParam("correo") String correo,
                          @RequestParam("nombre") String nombre,
                          @RequestParam("contrasena") String contrasena,
                          @PathVariable Long id,
                          RedirectAttributes redirectAttributes, HttpSession session) {
        Long sessionUserId = (Long) session.getAttribute("userId");

        if (sessionUserId == null || !sessionUserId.equals(id)) {
            return "redirect:/login";
        }

        // Comprobar si el usuario ya existe
        UsuarioData usuarioExistente = usuarioService.findByEmail(correo);
        if (usuarioExistente != null) {
            redirectAttributes.addFlashAttribute("error", "El correo ya está en uso.");
            return "redirect:/panelAdmin/" + id + "/listaUsuarios";
        }

        // Crear nuevo usuario y asignarle valores
        UsuarioData nuevoUsuario = new UsuarioData();
        nuevoUsuario.setEmail(correo);
        nuevoUsuario.setNombre(nombre);
        nuevoUsuario.setPassword(contrasena);
        nuevoUsuario.setTipouser("user");  // Por defecto
        nuevoUsuario.setPlan(TipoPlan.GRATUITO); // Por defecto

        // Registrar usuario en la base de datos
        usuarioService.registrar(nuevoUsuario);

        redirectAttributes.addFlashAttribute("success", "Usuario creado correctamente.");
        return "redirect:/panelAdmin/" + id + "/listaUsuarios";
    }

````

- LoginController
```java

@Controller
@RequestMapping("/cuestionario")
public class CuestionarioController {

    @Autowired
    private CuestionarioService cuestionarioService;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private UsuarioPlanService usuarioPlanService;

    @Autowired
    private PlanService planService;

    @GetMapping("/{id}")
    public String mostrarCuestionario(@PathVariable Long id, Model model) {
        Cuestionario cuestionario = cuestionarioService.findById(id)
                .orElseThrow(() -> new RuntimeException("Cuestionario no encontrado"));

        // Cargar las preguntas antes de pasarlas a la vista
        cuestionario.getPreguntas().size();

        model.addAttribute("cuestionario", cuestionario);
        model.addAttribute("preguntas", cuestionario.getPreguntas());
        return "cuestionario";
    }

    @PostMapping("/guardar-cuestionario")
    public String guardarCuestionario(@RequestParam Map<String, String> respuestas, Model model, HttpSession session) {
        Long usuarioId = (Long) session.getAttribute("userId");

        if (usuarioId == null) {
            System.out.println("Usuario no encontrado");
            return "redirect:/login";
        }

        // Debugging: Mostrar respuestas seleccionadas
        respuestas.forEach((preguntaId, respuestaId) ->
                System.out.println("Pregunta: " + preguntaId + " - Respuesta: " + respuestaId));

        // Llamada al método con el algoritmo para determinar el plan
        List<Long> planIds = determinarPlan(respuestas);
        if (planIds.isEmpty()) {
            System.out.println("PLAN NO ENCONTRADO");
            return "redirect:/error";
        }

        // Obtener usuario desde el servicio y manejar si no existe
        UsuarioData usuarioData = usuarioService.findById(usuarioId);
        if (usuarioData == null) {
            throw new RuntimeException("Usuario no encontrado en la BD");
        }

        Usuario usuario = usuarioData.toUsuario(); // Convertir DTO a entidad

        // Asignar todos los planes determinados al usuario
        for (Long planId : planIds) {
            // Buscar plan de entrenamiento
            PlanesEntrenamiento plan = planService.findById(planId);
            if (plan == null) {
                throw new RuntimeException("Plan con ID " + planId + " no encontrado");
            }

            UsuarioPlan usuarioPlan = new UsuarioPlan();
            usuarioPlan.setId(new UsuarioPlanId());
            usuarioPlan.getId().setUsuarioId(usuarioId);
            usuarioPlan.getId().setPlanId(planId);
            usuarioPlan.setUsuario(usuario);
            usuarioPlan.setPlan(plan);
            usuarioPlan.setFechaInicio(Instant.now());
            usuarioPlan.setEstado("Asignado");

            usuarioPlanService.guardarUsuarioPlan(usuarioPlan);
        }


        return "redirect:/usuarios/" + usuarioId + "/userhub";
    }


    private List<Long> determinarPlan(Map<String, String> respuestas) {
        // Definir categorías, divisiones, posiciones y mejoras
        int[] categorias = {1, 2, 3, 4};
        int[] divisiones = {5, 6, 7, 8};
        int[] posiciones = {9, 10, 11, 12, 13, 14, 15};
        int[] mejoras = {16, 17, 18, 19};

        // Mapa de planes
        Map<String, List<Long>> mapaPlanes = new HashMap<>();

        for (int cat : categorias) {
            for (int div : divisiones) {
                for (int pos : posiciones) {
                    for (int mejora : mejoras) {
                        List<Long> planes = new ArrayList<>();

                        // Asignar plan principal según categoría y posición
                        if (pos == 9) planes.add(cat == 1 ? 1L : (cat == 2 || cat == 3) ? 8L : 15L);
                        else if (pos == 10) planes.add(cat == 1 ? 2L : (cat == 2 || cat == 3) ? 9L : 16L);
                        else if (pos == 11) planes.add(cat == 1 ? 3L : (cat == 2 || cat == 3) ? 10L : 17L);
                        else if (pos == 12) planes.add(cat == 1 ? 4L : (cat == 2 || cat == 3) ? 11L : 18L);
                        else if (pos == 13) planes.add(cat == 1 ? 5L : (cat == 2 || cat == 3) ? 12L : 19L);
                        else if (pos == 14) planes.add(cat == 1 ? 6L : (cat == 2 || cat == 3) ? 13L : 20L);
                        else if (pos == 15) planes.add(cat == 1 ? 7L : (cat == 2 || cat == 3) ? 14L : 21L);

                        // Asignar plan adicional según aspecto a mejorar
                        planes.add(mejora == 16 ? 24L : mejora == 17 ? 22L : mejora == 18 ? 25L : 23L);

                        // Guardar combinación en el mapa
                        String clave = cat + "-" + div + "-" + pos + "-" + mejora;
                        mapaPlanes.put(clave, planes);
                    }
                }
            }
        }

        // Construir clave con las respuestas seleccionadas
        List<String> seleccionadas = new ArrayList<>(respuestas.values());
        String clave = String.join("-", seleccionadas);

        // Obtener lista de planes asignados o planes predeterminados si no se encuentra la clave
        List<Long> resultado = new ArrayList<>(mapaPlanes.getOrDefault(clave, new ArrayList<>()));

        // Añadir siempre los planes 26, 27 y 28
        resultado.addAll(Arrays.asList(26L, 27L, 28L));

        return resultado;
    }
}



```

- Código Descuento Controller 

```java 

@Controller
public class CodigosController {

    @Autowired
    CodigoDescuentoService codigoDescuentoService;

    @Autowired
    UsuarioService usuarioService;

    @GetMapping("/panelAdmin/{id}/listaCodigos")
    public String listaCodigos(@PathVariable Long id, Model model, HttpSession session) {
        Long sessionUserId = (Long) session.getAttribute("userId");

        if (sessionUserId == null || !sessionUserId.equals(id)) {
            return "redirect:/login";
        }

        List<CodigoDescuento> codigos = codigoDescuentoService.listarCodigos();
        model.addAttribute("codigos", codigos);
        model.addAttribute("userId", id);
        return "listaCodigos";
    }

    @PostMapping("/panelAdmin/{id}/addCodigo")
    public String addUser(@RequestParam("codigo") String codigo,
                          @RequestParam("descuento") BigDecimal descuento,
                          @RequestParam("usuario") Long usuario,
                          @PathVariable Long id,
                          RedirectAttributes redirectAttributes, HttpSession session) {
        Long sessionUserId = (Long) session.getAttribute("userId");
        if (sessionUserId == null || !sessionUserId.equals(id)) {
            return "redirect:/login";
        }
        try {
            codigoDescuentoService.buscarPorCodigo(codigo);
            redirectAttributes.addFlashAttribute("error", "El código ya existe.");
            return "redirect:/panelAdmin/" + id + "/listaCodigos";
        } catch (RuntimeException e) {
            // Código no encontrado, podemos crearlo
        }

        Usuario usuarionuevo = usuarioService.buscarPorId(usuario).get();
        codigoDescuentoService.nuevoCodigo(codigo , descuento, usuarionuevo);
        redirectAttributes.addFlashAttribute("success", "Código creado correctamente.");
        return "redirect:/panelAdmin/" + id + "/listaCodigos";
    }

    @PostMapping("/panelAdmin/{id}/updateCodigo")
    public String updateUser(@RequestParam("idCodigoUpdate") Long idCodigoUpdate,
                             @RequestParam("codigo") String codigo,
                             @RequestParam("descuento") BigDecimal descuento,
                             @RequestParam("usuario") Long usuarioid,
                             @PathVariable Long id,
                             RedirectAttributes redirectAttributes, HttpSession session) {
        Long sessionUserId = (Long) session.getAttribute("userId");
        if (sessionUserId == null || !sessionUserId.equals(id)) {
            return "redirect:/login";
        }

        CodigoDescuento codigoExistente = codigoDescuentoService.buscarPorId(idCodigoUpdate);
        codigoExistente.setCodigo(codigo);
        codigoExistente.setDescuento(descuento);
        codigoExistente.setUsuario(usuarioService.buscarPorId(usuarioid).get());
        codigoDescuentoService.actualizarCodigo(codigoExistente);

        redirectAttributes.addFlashAttribute("success", "Código actualizado correctamente.");
        return "redirect:/panelAdmin/" + id + "/listaCodigos";
    }

    @PostMapping("/panelAdmin/{id}/deleteCodigo")
    public String deleteUser(@RequestParam("idCodigo") Long idCodigo,
                             @PathVariable Long id,
                             RedirectAttributes redirectAttributes, HttpSession session) {
        Long sessionUserId = (Long) session.getAttribute("userId");
        if (sessionUserId == null || !sessionUserId.equals(id)) {
            return "redirect:/login";
        }

        try {
            codigoDescuentoService.eliminarCodigo(idCodigo);
            redirectAttributes.addFlashAttribute("success", "Código borrado correctamente.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al borrar el código.");
        }
        return "redirect:/panelAdmin/" + id + "/listaCodigos";
    }
}
```

- Usuario COntroller

```java
@GetMapping("/usuarios/{userId}/dashboard")
    public String userDashboard(@PathVariable Long userId, Model model, HttpSession session) {
        Long sessionUserId = (Long) session.getAttribute("userId");

        // Verificar si el usuario está autenticado y si coincide con el id del path
        if (sessionUserId == null || !sessionUserId.equals(userId)) {
            return "redirect:/login";
        }

        // Recuperar los tickets del usuario desde la base de datos
        List<Ticket> tickets = ticketService.findTicketsByUserId(userId);
        UsuarioData usuarioData = usuarioService.findById(sessionUserId);
        model.addAttribute("usuario", usuarioData);

        // Verificar si la lista de tickets está vacía
        if (tickets.isEmpty()) {
            model.addAttribute("message", "No tienes tickets asociados.");
        } else {
            // Ordenar los mensajes de cada ticket por fecha de envío
            for (Ticket ticket : tickets) {
                List<MensajeTicket> mensajesOrdenados = ticketService.getMensajesByTicketId(ticket.getId());
                ticket.setMensajes(mensajesOrdenados);
            }
            model.addAttribute("tickets", tickets);
        }

        model.addAttribute("userId", userId);
        return "dashboardUsuario";  // Devolver el nombre de la vista
    }
```

## Servicio

Contiene la lógica de negocio y actúa como intermediario entre los controladores y los repositorios.

```java
@Service
public class UsuarioService {

    Logger logger = LoggerFactory.getLogger(UsuarioService.class);

    public enum LoginStatus {LOGIN_OK, USER_NOT_FOUND, ERROR_PASSWORD}

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Transactional(readOnly = true)
    public LoginStatus login(String eMail, String password) {
        Usuario usuario = usuarioRepository.findByEmail(eMail)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (!usuario.getPassword().equals(password)) {
            return LoginStatus.ERROR_PASSWORD;
        }
        return LoginStatus.LOGIN_OK;
    }

    @Transactional
    public UsuarioData registrar(UsuarioData usuarioData) {
        if (usuarioRepository.findByEmail(usuarioData.getEmail()).isPresent()) {
            throw new UsuarioServiceException("El usuario " + usuarioData.getEmail() + " ya está registrado");
        }
        if (usuarioData.getEmail() == null || usuarioData.getPassword() == null) {
            throw new UsuarioServiceException("El usuario debe tener email y password");
        }

        // Asignar 'user' como tipo de usuario por defecto si no se especifica
        if (usuarioData.getTipouser() == null) {
            usuarioData.setTipouser("user");
        }

        // Asignar plan por defecto si no se especifica en el formulario
        if (usuarioData.getPlan() == null) {
            usuarioData.setPlan(TipoPlan.GRATUITO);  // Se asigna "GRATUITO" como valor predeterminado
        }

        Usuario usuarioNuevo = modelMapper.map(usuarioData, Usuario.class);

        // Debugging para verificar el plan antes de guardar en la base de datos
        System.out.println("Plan asignado al usuario antes de guardar: " + usuarioNuevo.getPlan());

        usuarioNuevo = usuarioRepository.save(usuarioNuevo);

        return modelMapper.map(usuarioNuevo, UsuarioData.class);
    }


    public UsuarioData findByEmail(String email) {
        return usuarioRepository.findByEmail(email)
                .map(usuario -> {
                    System.out.println("Usuario encontrado: " + usuario);
                    return modelMapper.map(usuario, UsuarioData.class);
                })
                .orElse(null); // Retorna null en lugar de lanzar excepción
    }


    @Transactional(readOnly = true)
    public UsuarioData findById(Long usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        return modelMapper.map(usuario, UsuarioData.class);
    }

    @Transactional(readOnly = true)
    public Optional<Usuario> buscarPorId(Long usuarioId) {
        return usuarioRepository.findById(usuarioId);
    }

    public List<UsuarioData> findAll() {
        return StreamSupport.stream(usuarioRepository.findAll().spliterator(), false)
                .map(usuario -> modelMapper.map(usuario, UsuarioData.class))
                .collect(Collectors.toList());
    }

    @Transactional
    public UsuarioData save(UsuarioData usuarioData) {
        Usuario usuario = modelMapper.map(usuarioData, Usuario.class);
        Usuario usuarioGuardado = usuarioRepository.save(usuario);
        return modelMapper.map(usuarioGuardado, UsuarioData.class);
    }

    @Transactional
    public void eliminarUsuario(Long id) {
        usuarioRepository.deleteById(id);
    }

    @Transactional
    public void actualizarUsuario(UsuarioData usuarioData) {
        Usuario usuarioExistente = usuarioRepository.findById(usuarioData.getId())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (usuarioData.getEmail() != null) {
            usuarioExistente.setEmail(usuarioData.getEmail());
        }
        if (usuarioData.getNombre() != null) {
            usuarioExistente.setNombre(usuarioData.getNombre());
        }
        if (usuarioData.getApellidos() != null) {
            usuarioExistente.setApellidos(usuarioData.getApellidos());
        }
        if (usuarioData.getBio() != null) {
            usuarioExistente.setBio(usuarioData.getBio());
        }
        if (usuarioData.getTipouser() != null) {
            usuarioExistente.setTipouser(usuarioData.getTipouser());
        }

        usuarioRepository.save(usuarioExistente);
    }
}

```

## Acceso a Datos

Se comunica con la base de datos y ejecuta las operaciones CRUD (Create, Read, Update, Delete) mediante JPA o consultas personalizadas
en el caso de que necesitemos alguna consulta de campos concretos, ciertas relaciones ,etc.

- Menasje Ticket Repository

```java

public interface MensajeTicketRepository extends JpaRepository<MensajeTicket, Long> {
//    List<MensajeTicket> findByTicketId(Long ticketId);

    @Query("SELECT m FROM MensajeTicket m WHERE m.ticket.id = :ticketId ORDER BY m.fechaEnvio ASC")
    List<MensajeTicket> findByTicketId(Long ticketId);
}

```

- Planes Entreamiento Repository

```java
@Repository
public interface PlanesEntrenamientoRepository extends JpaRepository<PlanesEntrenamiento, Long> {
    
    @Query("SELECT p FROM PlanesEntrenamiento p LEFT JOIN FETCH p.contenidos")
    List<PlanesEntrenamiento> findAllWithContenido();
    
    Optional<PlanesEntrenamiento> findById(Long id);
}
```

## Modelo o Dominio

Representa los datos del negocio. Se mapea a tablas en la base de datos con JPA.

- Entity Usuario
```java

@Entity
@Table(name = "usuarios")
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;


    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;

    @Column(name = "email", nullable = false, unique = true)
    private String email;


    @Column(name = "apellidos",nullable = true, length = 200)
    private String apellidos;

    @Column(name = "bio", nullable = true)
    private String bio;

    @Lob
    @Column(name = "foto", nullable = true)
    private String foto;

    @Column(name = "password", nullable = false)
    private String password;

    @Lob
    @Column(name = "tipouser", nullable = false)
    private String tipouser = "user";

    @Column(name = "plan", nullable = false)
    @Enumerated(EnumType.STRING)
    private TipoPlan plan = TipoPlan.GRATUITO;

    @OneToOne(mappedBy = "usuario", cascade = CascadeType.REMOVE)
    private CodigoDescuento codigoDescuento;

    //getters & setters 
}
```
- Entity Cuestionario

```java
@Entity
@Table(name = "cuestionario")
public class Cuestionario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;

    @OneToMany(mappedBy = "cuestionario", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Pregunta> preguntas = new ArrayList<>();
}
```
- Entity Preguntas

```java
@Entity
@Table(name = "preguntas")
public class Pregunta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "cuestionario_id", nullable = false)
    private Cuestionario cuestionario;

    @Column(name = "texto", nullable = false)
    private String texto;

    @OneToMany(mappedBy = "pregunta", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Respuesta> respuestas = new ArrayList<>();
}
```

- Entity Respuestas

```java

@Entity
@Table(name = "respuestas")
public class Respuesta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "pregunta_id", nullable = false)
    private Pregunta pregunta;

    @Lob
    @Column(name = "respuesta", nullable = false)
    private String respuesta;
}
```


# Manual de Usuario

Os dejamos un pequeño video tutorial del funcionamiento de la aplicación tanto desde la parte de usuarios como administradores.

![Tutorial_Usuarios](enlace video)

# GitProject e Issues

Para el reparto de tareas usamos el sistema de Issues de GitHub. Asignando a las Issues un miembro del gurpo o varios 
y asignando una pull request para que pasase a completada en el momento en el que alguno de los miembros revisase la pull
y la aceptase. 

Trabajmos en común para la creación de la BD, las Entities, los DTOs. Después cada uno trabajó 
de froma individual en sus views, CRUDs , etc. Obviamente con la ayuda de sus compañeros si era necesario y consultándonos 
todos para temas de diseño, estructuración etc. Hemos trabajado en común varios días cuando nos lo permitian nuestros horarios
y de forma individual otros , pero siempre nos hemos puesto al día y comentado puntos del proyecto los días de clase.


- **Issues Jorge.**

![IssuesJorge](img/IssuesJorge.png)

- **Issues Miguel.**

![IssuesMiguel](img/IssuesMiguel.png)

- **Issues Guille.**

![IssuesGuille](img/IssuesGuille.png)

# Propuestas de mejora

Como propuestas de mejeora de nuestro proyecto:

- Filtrado de algunos campos en distintas vistas que no hemos podido implementar por tiempo.


- Referenciar distintas vistas en el header para tener acceso a distintas partes de la app desde este.


- Implementar alguna forma de subir contenido multimedia para los planes de entrenamiento.


- Personalizar las fotos de perfil del usuario. (Intentamos hacerlo , pero se dejó como tarea secundaria 
para los ultimos días de trabajo)


- Validación de más campos de los formularios.

# Extras realizados

- Dashboard para el administrador de usuarios que acceden a nuestra aplicación (podrá crear, modificar, bloquear y borrar usuarios).


- Despliegue con Docker: la aplicación estará publicada en una instancia dockerizada EC2 de AWS o Azure, conectándose a una base de datos MySQL publicada en Azure o AWS-RDS.
  En nuestro caso hemos usado un [servidor Ubuntu en AWS](http://54.154.35.152:8080/).

![Despliegue_Docker_1](img/Despliegue_Docker_1.png)

![Despliegue_Docker_2](img/Despliegue_Docker_2.png)

# Conclusiones

Hemos sacado varias conclusiones con la finalización de este proyecto: 

- Al inicio del proyecto nos encontrábamos bastante perdidos en cuanto a conocimientos de como funcionaba Springboot. 
Pero a medida que hemos ido avanzando creo que hemos comprendido mejor su funcionamiento y potencial.


- A comparación de JavaFX , Springboot hace mucho más sencillo y felixble el diseño de la interfaz de la app. 
Además de tener muchas más opciones de diseño.


- En general la parte de Controllers es también mucho más sencilla. Al igual que la forma en la 
que se hacen los CRUD con los Repository y Services es mucho más sencilla.


- De la misma forma que en el primer proyecto tuvimos dificultades con JavaFX , las tuvimos aquí con Springboot.
Pero a medida que fuimos avanzando en el proyecto y solucionando errores fuimos aprendiendo a como manejar mejor 
Springboot. Probar nuevas tecnologias, construir un proyecto con ellas, cometer errores y solucionarlos ayuda mucho a
aprender como manejarla.

# Dedicación temporal

Desde el inicio del proyecto hemos dedicado entre las horas de clase y trabajo en casa una media estimada de unas 40h 
cada uno más o menos.

# Cualificación esperada

Teniendo en cuenta los puntos exigidos entre tareas Obligatorias y Opcionales la cualificación estimada del grupo es 
un 80 sobre 100.
