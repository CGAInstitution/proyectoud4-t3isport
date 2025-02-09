package madstodolist.controller;

import madstodolist.dto.LoginData;
import madstodolist.dto.UsuarioData;
import madstodolist.model.Usuario;
import madstodolist.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/usuarios")
public class UserHubController {

    @Autowired
    UsuarioService usuarioService;

    @GetMapping("{id}/userhub")
    public String userHub(@PathVariable("id") Long id, Model model) {
        // Lógica para obtener los detalles del usuario, si es necesario
        UsuarioData usuario = usuarioService.findById(id);

        // Puedes pasar información del usuario al modelo si es necesario
        model.addAttribute("usuario", usuario);

        // Retorna la vista para "userhub"
        return "userHub";  // Nombre de la vista userHub.html
    }

}
