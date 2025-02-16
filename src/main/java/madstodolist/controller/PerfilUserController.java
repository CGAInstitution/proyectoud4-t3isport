package madstodolist.controller;

import madstodolist.dto.UsuarioData;
import madstodolist.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


import javax.servlet.http.HttpSession;

@Controller
public class PerfilUserController {

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping("/usuarios/{id}/perfil")
    public String perfilUser(@PathVariable Long id, Model model, HttpSession session) {
        Long sessionUserId = (Long) session.getAttribute("userId");

        if (sessionUserId == null || !sessionUserId.equals(id)) {
            return "redirect:/login";
        }
        // Lógica para obtener los detalles del usuario, si es necesario
        UsuarioData usuario = usuarioService.findById(id);

        model.addAttribute("userId", sessionUserId);
        // Puedes pasar información del usuario al modelo si es necesario
        model.addAttribute("usuario", usuario);
        return "perfilUser";
    }
}
