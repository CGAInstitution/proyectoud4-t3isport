package madstodolist.controller;

import madstodolist.authentication.ManagerUserSession;
import madstodolist.dto.LoginData;
import madstodolist.dto.RegistroData;
import madstodolist.dto.UsuarioData;
import madstodolist.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

@Controller
public class LoginController {

    @Autowired
    UsuarioService usuarioService;

    @Autowired
    ManagerUserSession managerUserSession;

    @GetMapping("/index")
    public String index(Model model) {
        return "index";
    }

    @GetMapping("/index/usuarios/{id}")
    public String index(@PathVariable Long id, Model model, HttpSession session) {
        Long sessionUserId = (Long) session.getAttribute("userId");

        if (sessionUserId == null || !sessionUserId.equals(id)) {
            return "redirect:/login";
        }

        model.addAttribute("userId", id);
        return "index";
    }

//    @GetMapping("/usuarios/{id}/userhub")
//    public String userHub(@PathVariable Long id, Model model, HttpSession session) {
//        Long sessionUserId = (Long) session.getAttribute("userId");
//
//        // Si no hay sesión iniciada o el ID de la sesión no coincide, redirigir a login
//        if (sessionUserId == null || !sessionUserId.equals(id)) {
//            return "redirect:/login";
//        }
//
//        model.addAttribute("userId", id);
//        return "userHub";
//    }

    @PostMapping("/login")
    public String loginSubmit(@ModelAttribute LoginData loginData, Model model, HttpSession session) {
        // Llamada al servicio para comprobar si el login es correcto

        UsuarioService.LoginStatus loginStatus = usuarioService.login(loginData.geteMail(), loginData.getPassword());
        if (loginStatus == UsuarioService.LoginStatus.LOGIN_OK) {
            UsuarioData usuario = usuarioService.findByEmail(loginData.geteMail());
            // Almacena el userId en la sesión
            session.setAttribute("userId", usuario.getId());
            System.out.println("SessionUserId: " + usuario.getId());

            return "redirect:/usuarios/" + usuario.getId() + "/userhub";

        } else if (loginStatus == UsuarioService.LoginStatus.USER_NOT_FOUND) {
            model.addAttribute("error", "No existe usuario");
            return "formLogin";
        } else if (loginStatus == UsuarioService.LoginStatus.ERROR_PASSWORD) {
            model.addAttribute("error", "Contraseña incorrecta");
            return "formLogin";
        }
        return "formLogin";
    }

//    @GetMapping("/usuarios/{id}/userhub")
//    public String userHub(@PathVariable Long id, Model model, HttpSession session) {
//        Long sessionUserId = (Long) session.getAttribute("userId");
//        if (sessionUserId == null || !sessionUserId.equals(id)) {
//            return "redirect:/login";
//        }
//        model.addAttribute("userId", id);
//        return "userHub";
//    }

    @PostMapping("/registro")
    public String registroSubmit(@Valid RegistroData registroData, BindingResult result, Model model) {

        if (result.hasErrors()) {
            return "formRegistro";
        }

        if (usuarioService.findByEmail(registroData.getEmail()) != null) {
            model.addAttribute("registroData", registroData);
            model.addAttribute("error", "El usuario " + registroData.getEmail() + " ya existe");
            return "formRegistro";
        }

        UsuarioData usuario = new UsuarioData();
        usuario.setEmail(registroData.getEmail());
        usuario.setPassword(registroData.getPassword());
        usuario.setNombre(registroData.getNombre());

        usuarioService.registrar(usuario);
        return "redirect:/login";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        managerUserSession.logout();
        return "redirect:/login";
    }
}