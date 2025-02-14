package madstodolist.controller;

import madstodolist.dto.RegistroData;
import madstodolist.dto.UsuarioData;
import madstodolist.model.Contenido;
import madstodolist.model.PlanesEntrenamiento;
import madstodolist.service.ContenidoService;
import madstodolist.service.PlanesEntrenamientoService;
import madstodolist.service.UsuarioPlanService;
import madstodolist.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
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

        UsuarioData usuarioData = usuarioService.findById(userId);
        model.addAttribute("usuario", usuarioData);
        model.addAttribute("userId", userId);
        PlanesEntrenamiento planEntrenamiento = planesEntrenamientoService.obtenerPlan(id);
        model.addAttribute("planEntrenamiento", planEntrenamiento);

        List<Contenido> contenidos = contenidoService.obtenerContenidosPlan(planEntrenamiento);
        model.addAttribute("contenidos", contenidos);
        return "planEntrenamiento";
    }

}
