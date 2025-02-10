package madstodolist.controller;

import madstodolist.dto.UsuarioData;
import madstodolist.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
public class AdminController {

    @Autowired
    private UsuarioService usuarioService;

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
        System.out.println(usuarios);
        model.addAttribute("usuarios", usuarios);
        return "listaUsuarios";
    }
}