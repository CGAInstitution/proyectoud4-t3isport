package madstodolist.controller;

import madstodolist.dto.UsuarioData;
import madstodolist.model.UsuarioPlan;
import madstodolist.service.UsuarioPlanService;
import madstodolist.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
public class UserHubController {

    @Autowired
    private UsuarioPlanService usuarioPlanService;

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping("/usuarios/{id}/userhub")
    public String userHub(@PathVariable Long id, Model model, HttpSession session) {
        Long sessionUserId = (Long) session.getAttribute("userId");

        if (sessionUserId == null || !sessionUserId.equals(id)) {
            return "redirect:/login";
        }

        // Obtener usuario
        UsuarioData usuario = usuarioService.findById(id);
        if (usuario == null) {
            return "redirect:/login"; // Redirige si el usuario no existe
        }

        model.addAttribute("userId", sessionUserId);
        model.addAttribute("usuario", usuario); // Ahora Thymeleaf recibe directamente el objeto

        // Obtener la lista de planes del usuario
        List<UsuarioPlan> usuarioPlanes = usuarioPlanService.obtenerPlanesUsuario(id);
        model.addAttribute("usuarioPlanes", usuarioPlanes);

        return "userHub";
    }

}
