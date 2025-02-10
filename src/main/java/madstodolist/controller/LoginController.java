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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

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

//    @GetMapping("/")
//    public String home(Model model) {
//        return "redirect:/login";
//    }

//  @GetMapping("/login")
//  public String loginForm(Model model) {
//      model.addAttribute("loginData", new LoginData());
//      return "formLogin";
//  }

    @PostMapping("/login")
    public String loginSubmit(@ModelAttribute LoginData loginData, Model model, HttpSession session) {

        // Llamada al servicio para comprobar si el login es correcto
        UsuarioService.LoginStatus loginStatus = usuarioService.login(loginData.geteMail(), loginData.getPassword());

        if (loginStatus == UsuarioService.LoginStatus.LOGIN_OK) {
            UsuarioData usuario = usuarioService.findByEmail(loginData.geteMail());

            managerUserSession.logearUsuario(usuario.getId());

            return "redirect:/usuarios/" + usuario.getId() + "/tareas";
        } else if (loginStatus == UsuarioService.LoginStatus.USER_NOT_FOUND) {
            model.addAttribute("error", "No existe usuario");
            return "formLogin";
        } else if (loginStatus == UsuarioService.LoginStatus.ERROR_PASSWORD) {
            model.addAttribute("error", "Contraseña incorrecta");
            return "formLogin";
        }
        return "formLogin";
    }

//   @GetMapping("/registro")
//   public String registroForm(Model model) {
//       model.addAttribute("registroData", new RegistroData());
//       return "formRegistro";
//   }

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

        // Convertir RegistroData a Usuario y guardarlo en la BD
        Usuario usuario = new Usuario();
        usuario.setEmail(registroData.getEmail());
        usuario.setPassword(registroData.getPassword());
        usuario.setNombre(registroData.getNombre());

        usuario = usuarioRepository.save(usuario); // Ahora se guarda como entidad Usuario y se obtiene su ID

        // Recuperar el cuestionario con id = 1
        Optional<Cuestionario> cuestionarioOpt = cuestionarioRepository.findById(1L);
        if (!cuestionarioOpt.isPresent()) {
            throw new RuntimeException("El cuestionario con ID 1 no existe en la base de datos.");
        }
        Cuestionario cuestionario = cuestionarioOpt.get();

        // Crear clave primaria compuesta
        UsuarioCuestionarioId usuarioCuestionarioId = new UsuarioCuestionarioId();
        usuarioCuestionarioId.setUsuarioId(usuario.getId());
        usuarioCuestionarioId.setCuestionarioId(1L);

        // Crear la relación en la tabla intermedia UsuarioCuestionario
        UsuarioCuestionario usuarioCuestionario = new UsuarioCuestionario();
        usuarioCuestionario.setId(usuarioCuestionarioId); // Asignar ID compuesto
        usuarioCuestionario.setUsuario(usuario);
        usuarioCuestionario.setCuestionario(cuestionario);

        usuarioCuestionarioRepository.save(usuarioCuestionario); // Guardar relación

        // Redirigir al cuestionario con su ID
        return "redirect:/cuestionario/1";
    }




    @GetMapping("/logout")
   public String logout(HttpSession session) {
        managerUserSession.logout();
        return "redirect:/login";
   }
}
