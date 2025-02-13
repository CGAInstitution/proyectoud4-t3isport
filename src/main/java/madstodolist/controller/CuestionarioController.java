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
            return "redirect:/login";
        }


        //Debbugin para ver las ids si las preguntas y respuestas se añaden correctamente
        respuestas.forEach((preguntaId, respuestaId) ->
                System.out.println("Pregunta: " + preguntaId + " - Respuesta: " + respuestaId));


        //Llamada al metodo con el algoritmo para determinar el plan
        Long planId = determinarPlan(respuestas);
        if (planId == null) {
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
        return "redirect:/"+ usuarioId + "/userhub";
    }

    private Long determinarPlan(Map<String, String> respuestas) {
        //añado las combinaciones de respuestas y el id del plan al mapa
        Map<String, Long> mapaPlanes = new HashMap<>();
        mapaPlanes.put("1-9", 1L);
        mapaPlanes.put("2-9", 8L);
        mapaPlanes.put("3-9", 8L);
        mapaPlanes.put("4-9", 15L);
        mapaPlanes.put("1-10-16", 2L);
        mapaPlanes.put("2-10-16", 9L);
        mapaPlanes.put("3-10-16", 9L);
        mapaPlanes.put("4-10-16", 16L);
        mapaPlanes.put("1-11-16", 3L);
        mapaPlanes.put("2-11-16", 10L);
        mapaPlanes.put("3-11-16", 10L);
        mapaPlanes.put("4-11-16", 17L);
        mapaPlanes.put("1-12-16", 4L);
        mapaPlanes.put("2-12-16", 11L);
        mapaPlanes.put("3-12-16", 11L);
        mapaPlanes.put("4-12-16", 18L);
        mapaPlanes.put("1-13-16", 5L);
        mapaPlanes.put("2-13-16", 12L);
        mapaPlanes.put("3-13-16", 12L);
        mapaPlanes.put("4-13-16", 19L);
        mapaPlanes.put("1-14-16", 6L);
        mapaPlanes.put("2-14-16", 13L);
        mapaPlanes.put("3-14-16", 13L);
        mapaPlanes.put("4-14-16", 20L);
        mapaPlanes.put("1-15-16", 7L);
        mapaPlanes.put("2-15-16", 14L);
        mapaPlanes.put("3-15-16", 14L);
        mapaPlanes.put("4-15-16", 21L);

        //saco los valores de las ids de las respuestas del Map<String, String> respuestas
        List<String> seleccionadas = new ArrayList<>(respuestas.values());
        //ordenamos para que coincida con lo que meti en el mapa
        Collections.sort(seleccionadas);
        // le damos formato a "seleccionadas" para que coincida con un posible valor del Map<String, Long> mapaPlanes
        String clave = String.join("-", seleccionadas);

        //busca un id para esa comnbinacion "clave" en el mapaPlanes, si no asigna por defecto el entrenamiento fisico.
        return mapaPlanes.getOrDefault(clave, 22L);
    }
}
