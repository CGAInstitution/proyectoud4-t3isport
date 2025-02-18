package madstodolist.controller;

import madstodolist.dto.UsuarioData;
import madstodolist.model.PlanesEntrenamiento;
import madstodolist.model.TipoPlan;
import madstodolist.model.UsuarioPlan;
import madstodolist.service.PlanesEntrenamientoService;
import madstodolist.service.UsuarioPlanService;
import madstodolist.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
public class AdminController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private UsuarioPlanService usuarioPlanService;

    @Autowired
    private PlanesEntrenamientoService planesEntrenamientoService;

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

        // Comprobar si el usuario ya existe
        UsuarioData usuarioExistente = usuarioService.findByEmail(correo);
        if (usuarioExistente != null) {
            redirectAttributes.addFlashAttribute("error", "El correo ya está en uso.");
            return "redirect:/panelAdmin/" + id + "/listaUsuarios";
        }

        // Crear nuevo usuario y asignarle valores
        UsuarioData nuevoUsuario = new UsuarioData();
        nuevoUsuario.setEmail(correo);
        nuevoUsuario.setNombre(nombre);
        nuevoUsuario.setPassword(contrasena);
        nuevoUsuario.setTipouser("user");  // Por defecto
        nuevoUsuario.setPlan(TipoPlan.GRATUITO); // Por defecto

        // Registrar usuario en la base de datos
        usuarioService.registrar(nuevoUsuario);

        redirectAttributes.addFlashAttribute("success", "Usuario creado correctamente.");
        return "redirect:/panelAdmin/" + id + "/listaUsuarios";
    }


        @PostMapping("/panelAdmin/{id}/updateUser")
    public String updateUser(@RequestParam("idUsuarioUpdate") Long idUsuarioUpdate,
                             @RequestParam("correo") String correo,
                             @RequestParam("nombre") String nombre,
                             @RequestParam("contrasena") String contrasena,
                             @PathVariable Long id,
                             RedirectAttributes redirectAttributes, HttpSession session) {
        Long sessionUserId = (Long) session.getAttribute("userId");
        if (sessionUserId == null || !sessionUserId.equals(id)) {
            return "redirect:/login";
        }

        UsuarioData usuarioExistente = usuarioService.findById(idUsuarioUpdate);
        if (usuarioExistente == null) {
            redirectAttributes.addFlashAttribute("error", "Usuario no encontrado.");
            return "redirect:/panelAdmin/" + id + "/listaUsuarios";
        }

        usuarioExistente.setEmail(correo);
        usuarioExistente.setNombre(nombre);
        usuarioExistente.setPassword(contrasena);

        try {
            usuarioService.actualizarUsuario(usuarioExistente);
            redirectAttributes.addFlashAttribute("success", "Usuario actualizado correctamente.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al actualizar el usuario.");
        }

        return "redirect:/panelAdmin/" + id + "/listaUsuarios";
    }

    @PostMapping("/panelAdmin/{id}/deleteUser")
    public String deleteUser(@RequestParam("idUsuario") Long idUsuario,
                             @PathVariable Long id,
                             RedirectAttributes redirectAttributes, HttpSession session) {
        Long sessionUserId = (Long) session.getAttribute("userId");
        if (sessionUserId == null || !sessionUserId.equals(id)) {
            return "redirect:/login";
        }

        try {
            usuarioService.eliminarUsuario(idUsuario);
            redirectAttributes.addFlashAttribute("success", "Usuario borrado correctamente.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al borrar el usuario.");
        }
        return "redirect:/panelAdmin/" + id + "/listaUsuarios";
    }

    @GetMapping("/panelAdmin/{id}/listaUsuariosPlanes")
    public String listaUsuariosPlanes(@PathVariable Long id, Model model, HttpSession session) {
        Long sessionUserId = (Long) session.getAttribute("userId");

        if (sessionUserId == null || !sessionUserId.equals(id)) {
            return "redirect:/login";
        }

        List<PlanesEntrenamiento> planes = planesEntrenamientoService.findAllWithContenido();
        List<UsuarioPlan> planesUsuarios = usuarioPlanService.findAllUsuarioPlanes();

        model.addAttribute("planes", planes);
        model.addAttribute("planesUsuarios", planesUsuarios);
        model.addAttribute("userId", id);
        return "listaUsuariosPlanes";
    }


}
