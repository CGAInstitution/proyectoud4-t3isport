package madstodolist.controller;

import madstodolist.dto.UsuarioData;
import madstodolist.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

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

        UsuarioData usuario = usuarioService.findById(id);

        if (usuario == null) {
            model.addAttribute("error", "Usuario no encontrado");
            return "redirect:/login";
        }




        model.addAttribute("userId", sessionUserId);
        model.addAttribute("usuario", usuario);

        return "perfilUser";
    }

    @PostMapping("/usuarios/{id}/perfil")
    public String actualizarPerfil(@PathVariable Long id,
                                   @ModelAttribute UsuarioData usuario,
                                   HttpSession session, Model model) {
        Long sessionUserId = (Long) session.getAttribute("userId");

        if (sessionUserId == null || !sessionUserId.equals(id)) {
            return "redirect:/login";
        }

        System.out.println(usuario);

        try {
            // Llamamos al servicio para actualizar el usuario con los nuevos datos
            usuarioService.actualizarUsuario(usuario);
            model.addAttribute("success", "Perfil actualizado con éxito.");

            return "redirect:/usuarios/" + id + "/perfil";  // Redirige al perfil actualizado
        } catch (Exception e) {
            model.addAttribute("error", "Ocurrió un error al actualizar el perfil.");
            return "perfilUser";  // Regresamos a la página del perfil en caso de error
        }
    }
}
