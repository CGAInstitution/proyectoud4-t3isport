package madstodolist.controller;

import madstodolist.dto.UsuarioData;
import madstodolist.model.Contenido;
import madstodolist.model.PlanesEntrenamiento;
import madstodolist.service.ContenidoService;
import madstodolist.service.PlanesEntrenamientoService;
import madstodolist.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
public class PlanEntrenamientoController {

    @Autowired
    private PlanesEntrenamientoService planesEntrenamientoService;

    @Autowired
    private ContenidoService contenidoService;

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping("/usuarios/{userId}/userhub/{id}/plan_entrenamiento")
    public String registroForm(@PathVariable Long userId, @PathVariable Long id, Model model, HttpSession session) {
        Long sessionUserId = (Long) session.getAttribute("userId");

        if (sessionUserId == null || !sessionUserId.equals(userId)) {
            return "redirect:/login"; // Si no coincide, redirige al login
        }

        // Obtener usuario y lanzar error si no existe
        UsuarioData usuarioData = usuarioService.findById(userId);

        if (usuarioData == null) {
            throw new RuntimeException("Usuario no encontrado");
        }


        // Obtener plan de entrenamiento
        PlanesEntrenamiento planEntrenamiento = planesEntrenamientoService.obtenerPlan(id);
        if (planEntrenamiento == null) {
            return "redirect:/error"; // Redirige si el plan no existe
        }

        // Obtener contenidos del plan
        List<Contenido> contenidos = contenidoService.obtenerContenidosPlan(planEntrenamiento);

        // Agregar atributos al modelo
        model.addAttribute("usuario", usuarioData);
        model.addAttribute("userId", userId);
        model.addAttribute("planEntrenamiento", planEntrenamiento);
        model.addAttribute("contenidos", contenidos);

        return "planEntrenamiento";
    }
}
