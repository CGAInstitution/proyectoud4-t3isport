package madstodolist.controller;

import madstodolist.authentication.ManagerUserSession;
import madstodolist.dto.LoginData;
import madstodolist.dto.RegistroData;
import madstodolist.dto.UsuarioData;
import madstodolist.model.Cuestionario;
import madstodolist.model.Usuario;
import madstodolist.model.UsuarioCuestionario;
import madstodolist.model.UsuarioCuestionarioId;
import madstodolist.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import madstodolist.repository.*;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.Optional;

@Controller
public class LoginController {

    @Autowired
    UsuarioService usuarioService;

    @Autowired
    ManagerUserSession managerUserSession;

    @Autowired
    private CuestionarioRepository cuestionarioRepository;

    @Autowired
    private UsuarioCuestionarioRepository usuarioCuestionarioRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    // -------------------- LOGIN --------------------
    @PostMapping("/login")
    public String loginSubmit(@ModelAttribute LoginData loginData, Model model, HttpSession session) {
        UsuarioService.LoginStatus loginStatus = usuarioService.login(loginData.geteMail(), loginData.getPassword());

        if (loginStatus == UsuarioService.LoginStatus.LOGIN_OK) {
            UsuarioData usuario = usuarioService.findByEmail(loginData.geteMail());

            // Guardar usuario en sesión
            session.setAttribute("userId", usuario.getId());

            System.out.println("🔹 Usuario en sesión tras login: " + usuario.getId());
            System.out.println("🔹 Tipo de usuario: " + usuario.getTipouser());

            if (usuario.getTipouser().equals("admin")) {
                return "redirect:/panelAdmin/" + usuario.getId();
            }

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

    // -------------------- REGISTRO --------------------
    @PostMapping("/registro")
    public String registroSubmit(@Valid RegistroData registroData, BindingResult result, Model model, HttpSession session) {
        if (result.hasErrors()) {
            return "formRegistro";
        }

        if (usuarioService.findByEmail(registroData.getEmail()) != null) {
            model.addAttribute("registroData", registroData);
            model.addAttribute("error", "El usuario " + registroData.getEmail() + " ya existe");
            return "formRegistro";
        }

        // Crear usuario y guardarlo en la BD
        Usuario usuario = new Usuario();
        usuario.setEmail(registroData.getEmail());
        usuario.setPassword(registroData.getPassword());
        usuario.setNombre(registroData.getNombre());

        usuario = usuarioRepository.save(usuario);

        // 🔹 Guardar usuario en la sesión
        session.setAttribute("userId", usuario.getId());

        System.out.println("🔹 Usuario registrado con ID: " + usuario.getId());
        System.out.println("🔹 Usuario en sesión después del registro: " + session.getAttribute("userId"));

        // Recuperar el cuestionario con id = 1
        Optional<Cuestionario> cuestionarioOpt = cuestionarioRepository.findById(1L);
        if (!cuestionarioOpt.isPresent()) {
            throw new RuntimeException("⚠️ El cuestionario con ID 1 no existe en la base de datos.");
        }
        Cuestionario cuestionario = cuestionarioOpt.get();

        // Crear clave primaria compuesta
        UsuarioCuestionarioId usuarioCuestionarioId = new UsuarioCuestionarioId();
        usuarioCuestionarioId.setUsuarioId(usuario.getId());
        usuarioCuestionarioId.setCuestionarioId(1L);

        // Crear la relación en la tabla intermedia UsuarioCuestionario
        UsuarioCuestionario usuarioCuestionario = new UsuarioCuestionario();
        usuarioCuestionario.setId(usuarioCuestionarioId);
        usuarioCuestionario.setUsuario(usuario);
        usuarioCuestionario.setCuestionario(cuestionario);

        usuarioCuestionarioRepository.save(usuarioCuestionario);

        // Redirigir al cuestionario
        return "redirect:/cuestionario/1";
    }

    // -------------------- LOGOUT --------------------
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();  // Cerrar sesión
        return "redirect:/login";
    }
}
