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

    /**
     * Método que muestra el panel de administración de un usuario.
     * @param id Identificador del usuario administrador.
     *           Se recibe como parámetro en la URL.
     *           Se recupera de la sesión el identificador del usuario logueado.
     *           Si no coincide con el identificador recibido, se redirige a la página de login.
     *           Se recupera el usuario con el identificador recibido.
     * */
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

    /**
     * Método que muestra la lista de usuarios.
     * @param id Identificador del usuario administrador.
     *           Se recibe como parámetro en la URL.
     * @param model Modelo de la vista.
     *              Se añade la lista de usuarios al modelo.
     *              Se añade el identificador del usuario administrador al modelo.
     * @param session Sesión HTTP.
     *                Se recupera el identificador del usuario logueado.
     *                Si no coincide con el identificador recibido, se redirige a la página de login.
     *
     * */
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

    /**
     * Método que muestra el formulario para añadir un usuario.
     * @param id Identificador del usuario administrador.
     *           Se recibe como parámetro en la URL.
     * @param correo Correo del usuario.
     *               Se recibe como parámetro en la URL.
     * @param nombre Nombre del usuario.
     *               Se recibe como parámetro en la URL.
     * @param contrasena Contraseña del usuario.
     *               Se recibe como parámetro en la URL.
     * @param redirectAttributes Atributos para la redirección.
     *               Se añade un mensaje de error o de éxito.
     * @param session Sesión HTTP.
     *               Se recupera el identificador del usuario logueado.
     *
     * */
    @PostMapping("/panelAdmin/{id}/addUser")
    public String addUser(@RequestParam("correo") String correo,
                          @RequestParam("nombre") String nombre,
                          @RequestParam("contrasena") String contrasena,
                          @PathVariable Long id,
                          RedirectAttributes redirectAttributes, HttpSession session) {

//        System.out.println("Correo: " + correo);
//        System.out.println("Nombre: " + nombre);
//        System.out.println("Contraseña: " + contrasena);
//        System.out.println(usuarioRepository.findByEmail(correo).isPresent());
        Long sessionUserId = (Long) session.getAttribute("userId");
        if (sessionUserId == null || !sessionUserId.equals(id)) {
            return "redirect:/login";
        }

        if (usuarioRepository.findByEmail(correo).isPresent()) {
            System.out.println("El correo ya está en uso.");
            redirectAttributes.addFlashAttribute("error", "El correo ya está en uso.");
            return "redirect:/panelAdmin/" + id + "/listaUsuarios";
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

    /**
     * Método que muestra el formulario para actualizar un usuario.
     * @param idUsuarioUpdate Identificador del usuario a actualizar.
     *                        Se recibe como parámetro en el formulario.
     * @param correo Correo del usuario.
     *               Se recibe como parámetro en el formulario.
     * @param nombre Nombre del usuario.
     *               Se recibe como parámetro en el formulario.
     * @param contrasena Contraseña del usuario.
     *               Se recibe como parámetro en el formulario.
     * @param id Identificador del usuario administrador.
     *           Se recibe como parámetro en la URL.
     * @param redirectAttributes Atributos para la redirección.
     *               Se añade un mensaje de error o de éxito.
     * @param session Sesión HTTP.
     *               Se recupera el identificador del usuario logueado.
     *
     * */
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

        Usuario usuarioExistente = usuarioRepository.findById(idUsuarioUpdate).orElse(null);
        if (usuarioExistente == null) {
            redirectAttributes.addFlashAttribute("error", "Usuario no encontrado.");
            return "redirect:/panelAdmin/" + id + "/listaUsuarios";
        }

        if (!usuarioExistente.getEmail().equals(correo) && usuarioRepository.findByEmail(correo).isPresent()) {
            redirectAttributes.addFlashAttribute("error", "El correo ya está en uso.");
            return "redirect:/panelAdmin/" + id + "/listaUsuarios";
        }

        try {
            usuarioExistente.setEmail(correo);
            usuarioExistente.setNombre(nombre);
            usuarioExistente.setPassword(contrasena);

            usuarioRepository.save(usuarioExistente);

            redirectAttributes.addFlashAttribute("success", "Usuario actualizado correctamente.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al actualizar el usuario.");
        }

        return "redirect:/panelAdmin/" + id + "/listaUsuarios";
    }

    /**
     * Método que borra un usuario.
     * @param idUsuario Identificador del usuario a borrar.
     *                  Se recibe como parámetro en el formulario.
     *                  Se recupera el usuario con el identificador recibido.
     *                  Si no existe, se añade un mensaje de error y se redirige a la lista de usuarios.
     *                  Se borra el usuario.
     * @param id Identificador del usuario administrador.
     *                  Se recibe como parámetro en la URL.
     * @param redirectAttributes Atributos para la redirección.
     *                 Se añade un mensaje de error o de éxito.
     * @param session Sesión HTTP.
     *                 Se recupera el identificador del usuario logueado.
     * */
    @PostMapping("/panelAdmin/{id}/deleteUser")
    public String deleteUser(@RequestParam("idUsuario") Long idUsuario,
                             @PathVariable Long id,
                             RedirectAttributes redirectAttributes, HttpSession session) {
        Long sessionUserId = (Long) session.getAttribute("userId");
        if (sessionUserId == null || !sessionUserId.equals(id)) {
            return "redirect:/login";
        }

        Usuario usuarioExistente = usuarioRepository.findById(idUsuario).orElse(null);
        if (usuarioExistente == null) {
            redirectAttributes.addFlashAttribute("error", "Usuario no encontrado.");
            return "redirect:/panelAdmin/" + id + "/listaUsuarios";
        }

        try {
            usuarioRepository.delete(usuarioExistente);
            redirectAttributes.addFlashAttribute("success", "Usuario borrado correctamente.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al borrar el usuario.");
        }

        return "redirect:/panelAdmin/" + id + "/listaUsuarios";
    }
}