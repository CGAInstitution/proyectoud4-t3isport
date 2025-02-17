package madstodolist.controller;

import madstodolist.dto.UsuarioData;
import madstodolist.model.*;
import madstodolist.service.CuestionarioService;
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
