package madstodolist.controller;

import madstodolist.authentication.ManagerUserSession;
import madstodolist.dto.LoginData;
import madstodolist.dto.RegistroData;
import madstodolist.dto.UsuarioData;
import madstodolist.model.Cuestionario;
import madstodolist.model.TipoPlan;
import madstodolist.model.UsuarioCuestionario;
import madstodolist.model.UsuarioCuestionarioId;
import madstodolist.service.CuestionarioService;
import madstodolist.service.UsuarioCuestionarioService;
import madstodolist.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.beans.PropertyEditorSupport;
import java.util.Optional;

@Controller
public class LoginController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private CuestionarioService cuestionarioService;

    @Autowired
    private UsuarioCuestionarioService usuarioCuestionarioService;

    @Autowired
    private ManagerUserSession managerUserSession;

    // -------------------- LOGIN --------------------
    @PostMapping("/login")
    public String loginSubmit(@ModelAttribute LoginData loginData, Model model, HttpSession session) {
        try {
            UsuarioService.LoginStatus loginStatus = usuarioService.login(loginData.geteMail(), loginData.getPassword());

            if (loginStatus == UsuarioService.LoginStatus.LOGIN_OK) {
                UsuarioData usuario = usuarioService.findByEmail(loginData.geteMail());

                if (usuario == null) {
                    model.addAttribute("error", "No existe usuario");
                    return "formLogin";
                }

                session.setAttribute("userId", usuario.getId());

                if ("admin".equals(usuario.getTipouser())) {
                    return "redirect:/panelAdmin/" + usuario.getId();
                }

                return "redirect:/usuarios/" + usuario.getId() + "/userhub";
            } else if (loginStatus == UsuarioService.LoginStatus.USER_NOT_FOUND) {
                model.addAttribute("error", "No existe usuario");
            } else if (loginStatus == UsuarioService.LoginStatus.ERROR_PASSWORD) {
                model.addAttribute("error", "Contraseña incorrecta");
            }
        } catch (Exception e) {
            model.addAttribute("error", "Ocurrió un error: " + e.getMessage());
        }

        return "formLogin";
    }


    // INIT BINDER para convertir String a TipoPlan automáticamente
    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(TipoPlan.class, new PropertyEditorSupport() {
            @Override
            public void setAsText(String text) {
                setValue(TipoPlan.valueOf(text.toUpperCase())); // Convierte String a Enum
            }
        });
    }


    // -------------------- REGISTRO --------------------
    @PostMapping("/registro")
    public String registroSubmit(@Valid RegistroData registroData, BindingResult result, Model model, HttpSession session) {
        if (result.hasErrors()) {
            return "formRegistro";
        }

        // Buscar si el usuario ya existe
        UsuarioData usuarioExistente = usuarioService.findByEmail(registroData.getEmail());
        if (usuarioExistente != null) {
            model.addAttribute("registroData", registroData);
            model.addAttribute("error", "El usuario " + registroData.getEmail() + " ya existe");
            return "formRegistro";
        }

        //Debug combobox
        System.out.println("Plan seleccionado en el formulario: " + registroData.getPlan());

        // Crear usuario y guardarlo en la BD
        UsuarioData usuarioData = new UsuarioData();
        usuarioData.setEmail(registroData.getEmail());
        usuarioData.setPassword(registroData.getPassword());
        usuarioData.setNombre(registroData.getNombre());
        usuarioData.setTipouser("user");  // Asignar rol por defecto
        usuarioData.setPlan(registroData.getPlan());

        //Comprobamos si se asigna el valor de combo
        System.out.println("Plan asignado al usuario: " + usuarioData.getPlan());


        UsuarioData nuevoUsuario = usuarioService.registrar(usuarioData);

        //Comprobamos que se guardo en la bd
        System.out.println(" Plan guardado en BD: " + nuevoUsuario.getPlan());


        // Guardar usuario en la sesión
        session.setAttribute("userId", nuevoUsuario.getId());


        System.out.println("Usuario registrado con ID: " + nuevoUsuario.getId());
        System.out.println("Usuario en sesión después del registro: " + session.getAttribute("userId"));

        // Recuperar el cuestionario con id = 1
        Optional<Cuestionario> cuestionarioOpt = cuestionarioService.findById(1L);
        if (!cuestionarioOpt.isPresent()) {
            model.addAttribute("error", "No se encontró el cuestionario.");
            return "error"; // O redirecciona a una vista de error
        }
        Cuestionario cuestionario = cuestionarioOpt.get();


        // Crear clave primaria compuesta
        UsuarioCuestionarioId usuarioCuestionarioId = new UsuarioCuestionarioId();
        usuarioCuestionarioId.setUsuarioId(nuevoUsuario.getId());
        usuarioCuestionarioId.setCuestionarioId(1L);

        // Crear la relación en la tabla intermedia UsuarioCuestionario
        UsuarioCuestionario usuarioCuestionario = new UsuarioCuestionario();
        usuarioCuestionario.setId(usuarioCuestionarioId);
        usuarioCuestionario.setUsuario(nuevoUsuario.toUsuario()); // Conversión de DTO a entidad
        usuarioCuestionario.setCuestionario(cuestionario);

        usuarioCuestionarioService.guardarUsuarioCuestionario(usuarioCuestionario);

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
