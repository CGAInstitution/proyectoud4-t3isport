package madstodolist.controller;

import madstodolist.dto.LoginData;
import madstodolist.dto.UsuarioData;
import madstodolist.model.Usuario;
import madstodolist.model.UsuarioPlan;
import madstodolist.service.UsuarioPlanService;
import madstodolist.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
@RequestMapping("/usuarios")
public class UserHubController {

    @Autowired
    private UsuarioPlanService usuarioPlanService;

    @GetMapping("{id}/userhub")
    public String userHub(@PathVariable Long id, Model model, HttpSession session) {
        // Lógica para obtener los detalles del usuario, si es necesario
        UsuarioData usuario = usuarioService.findById(id);
        Long sessionUserId = (Long) session.getAttribute("userId");

        if (sessionUserId == null || !sessionUserId.equals(id)) {
            return "redirect:/login";
        }

        // Puedes pasar información del usuario al modelo si es necesario
        model.addAttribute("usuarioID", id);
        List<UsuarioPlan> usuarioPlanes = usuarioPlanService.obtenerPlanesUsuario(id);

        // Pasar la lista de usuarioPlanes al modelo
        model.addAttribute("usuarioPlanes", usuarioPlanes);
        // Retorna la vista para "userhub"
        return "userHub";  // Nombre de la vista userHub.html
    }

}
