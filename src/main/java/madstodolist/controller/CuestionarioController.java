package madstodolist.controller;

import madstodolist.dto.UsuarioData;
import madstodolist.model.*;
import madstodolist.repository.CuestionarioRepository;
import madstodolist.service.PlanService;
import madstodolist.service.UsuarioPlanService;
import madstodolist.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.time.Instant;
import java.util.*;

@Controller
@RequestMapping("/cuestionario")
public class CuestionarioController {

    @Autowired
    private CuestionarioRepository cuestionarioRepository;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private UsuarioPlanService usuarioPlanService;

    @Autowired
    private PlanService planService;

    @GetMapping("/{id}")
    public String mostrarCuestionario(@PathVariable Long id, Model model) {
        Optional<Cuestionario> cuestionarioOpt = cuestionarioRepository.findById(id);
        if (!cuestionarioOpt.isPresent()) {
            return "error";
        }
        Cuestionario cuestionario = cuestionarioOpt.get();
        cuestionario.getPreguntas().size();
        model.addAttribute("cuestionario", cuestionario);
        model.addAttribute("preguntas", cuestionario.getPreguntas());
        return "cuestionario";
    }

    @PostMapping("/guardar-cuestionario")
    public String guardarCuestionario(@RequestParam Map<String, String> respuestas, Model model, HttpSession session) {
        Long usuarioId = (Long) session.getAttribute("userId");

        //Debuguear mañana en clasee
        System.out.println("Sesión actual: " + session);
        System.out.println("Usuario en sesión: " + session.getAttribute("userId"));

        if (usuarioId == null) {
            System.out.println("Usuario no encontrado");
            return "redirect:/login";
        }


        //Debbugin para ver las ids si las preguntas y respuestas se añaden correctamente
        respuestas.forEach((preguntaId, respuestaId) ->
                System.out.println("Pregunta: " + preguntaId + " - Respuesta: " + respuestaId));


        //Llamada al metodo con el algoritmo para determinar el plan
        List<Long> planId = determinarPlan(respuestas);
        if (planId == null) {
            System.out.println("PLAN NO ENCONTRADO");
            return "redirect:/error";
        }


        //uso usuariodata por seguridad, pero creo que es menos eficiente. Ademas el data incluye la contraseña,
        //no sé muy bien por qué
        UsuarioData usuarioData = usuarioService.findById(usuarioId);
        Usuario usuario = new Usuario();
        usuario.setId(usuarioData.getId());
        usuario.setEmail(usuarioData.getEmail());
        usuario.setNombre(usuarioData.getNombre());
        usuario.setPassword(usuarioData.getPassword());
        usuario.setTipouser(usuarioData.getTipouser());

        PlanesEntrenamiento plan = planService.findById(planId);

        UsuarioPlan usuarioPlan = new UsuarioPlan();
        usuarioPlan.setId(new UsuarioPlanId());
        usuarioPlan.getId().setUsuarioId(usuarioId);
        usuarioPlan.getId().setPlanId(planId);
        usuarioPlan.setUsuario(usuario);
        usuarioPlan.setPlan(plan);
        usuarioPlan.setFechaInicio(Instant.now());
        usuarioPlan.setEstado("Asignado");

        usuarioPlanService.guardarUsuarioPlan(usuarioPlan);
        return "redirect:/usuarios/" + usuarioId + "/userhub";

    }

    private List<Long> determinarPlan(Map<String, String> respuestas) {
        // Mapa de combinaciones de respuestas con los planes asignados (principal y adicional)
        Map<String, List<Long>> mapaPlanes = new HashMap<>();

        // Asignación de planes principales junto con los planes adicionales (según la última respuesta)
        mapaPlanes.put("1-9-16", Arrays.asList(1L, 24L)); // Básico Portero + Táctico
        mapaPlanes.put("1-9-17", Arrays.asList(1L, 22L)); // Básico Portero + Físico
        mapaPlanes.put("1-9-18", Arrays.asList(1L, 25L)); // Básico Portero + Técnico
        mapaPlanes.put("1-9-19", Arrays.asList(1L, 23L)); // Básico Portero + Portero

        mapaPlanes.put("2-9-16", Arrays.asList(8L, 24L));
        mapaPlanes.put("2-9-17", Arrays.asList(8L, 22L));
        mapaPlanes.put("2-9-18", Arrays.asList(8L, 25L));
        mapaPlanes.put("2-9-19", Arrays.asList(8L, 23L));

        mapaPlanes.put("3-9-16", Arrays.asList(8L, 24L));
        mapaPlanes.put("3-9-17", Arrays.asList(8L, 22L));
        mapaPlanes.put("3-9-18", Arrays.asList(8L, 25L));
        mapaPlanes.put("3-9-19", Arrays.asList(8L, 23L));

        mapaPlanes.put("4-9-16", Arrays.asList(15L, 24L));
        mapaPlanes.put("4-9-17", Arrays.asList(15L, 22L));
        mapaPlanes.put("4-9-18", Arrays.asList(15L, 25L));
        mapaPlanes.put("4-9-19", Arrays.asList(15L, 23L));

        mapaPlanes.put("1-10-16", Arrays.asList(2L, 24L));
        mapaPlanes.put("1-10-17", Arrays.asList(2L, 22L));
        mapaPlanes.put("1-10-18", Arrays.asList(2L, 25L));
        mapaPlanes.put("1-10-19", Arrays.asList(2L, 23L));

        mapaPlanes.put("2-10-16", Arrays.asList(9L, 24L));
        mapaPlanes.put("2-10-17", Arrays.asList(9L, 22L));
        mapaPlanes.put("2-10-18", Arrays.asList(9L, 25L));
        mapaPlanes.put("2-10-19", Arrays.asList(9L, 23L));

        mapaPlanes.put("3-10-16", Arrays.asList(9L, 24L));
        mapaPlanes.put("3-10-17", Arrays.asList(9L, 22L));
        mapaPlanes.put("3-10-18", Arrays.asList(9L, 25L));
        mapaPlanes.put("3-10-19", Arrays.asList(9L, 23L));

        mapaPlanes.put("4-10-16", Arrays.asList(16L, 24L));
        mapaPlanes.put("4-10-17", Arrays.asList(16L, 22L));
        mapaPlanes.put("4-10-18", Arrays.asList(16L, 25L));
        mapaPlanes.put("4-10-19", Arrays.asList(16L, 23L));

        // Repite la misma lógica para las demás posiciones (11, 12, 13, 14, 15)

        // Extraer respuestas
        List<String> seleccionadas = new ArrayList<>(respuestas.values());

        // Ordenamos para que coincida con las claves del mapa
        Collections.sort(seleccionadas);

        // Clave para buscar los planes
        String clave = String.join("-", seleccionadas);

        // Buscar los planes asignados, si no hay coincidencia, se asigna un plan por defecto (Físico)
        return mapaPlanes.getOrDefault(clave, Arrays.asList(22L, 24L));
    }

}