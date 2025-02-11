package madstodolist.controller;

import madstodolist.dto.UsuarioData;
import madstodolist.model.Usuario;
import madstodolist.repository.UsuarioRepository;
import madstodolist.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
public class AdminController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @GetMapping("/panelAdmin/{id}")
    public String panelAdmin(@PathVariable Long id, Model model, HttpSession session) {
        Long sessionUserId = (Long) session.getAttribute("userId");

        if (sessionUserId == null || !sessionUserId.equals(id)) {
            return "redirect:/login";
        }

        UsuarioData usuario = usuarioService.findById(id);
        model.addAttribute("usuario", usuario);
        return "panelAdmin";
    }

    @GetMapping("/panelAdmin/{id}/listaUsuarios")
    public String listaUsuarios(@PathVariable Long id, Model model, HttpSession session) {
        Long sessionUserId = (Long) session.getAttribute("userId");

        if (sessionUserId == null || !sessionUserId.equals(id)) {
            return "redirect:/login";
        }

        List<UsuarioData> usuarios = usuarioService.findAll();
        model.addAttribute("usuarios", usuarios);
        model.addAttribute("userId", id);
        return "listaUsuarios";
    }

    @PostMapping("/panelAdmin/{id}/addUser")
    public String addUser(@RequestParam("correo") String correo,
                          @RequestParam("nombre") String nombre,
                          @RequestParam("contrasena") String contrasena,
                          @PathVariable Long id,
                          RedirectAttributes redirectAttributes, HttpSession session) {
        Long sessionUserId = (Long) session.getAttribute("userId");
        if (sessionUserId == null || !sessionUserId.equals(id)) {
            return "redirect:/login";
        }

        try {
            Usuario nuevoUsuario = new Usuario();
            nuevoUsuario.setEmail(correo);
            nuevoUsuario.setNombre(nombre);
            nuevoUsuario.setPassword(contrasena);
            nuevoUsuario.setTipouser("user");

            usuarioRepository.save(nuevoUsuario);

            redirectAttributes.addFlashAttribute("success", "Usuario creado correctamente.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al crear el usuario.");
        }

        return "redirect:/panelAdmin/" + id + "/listaUsuarios";
    }
}