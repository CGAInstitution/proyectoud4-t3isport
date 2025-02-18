package madstodolist.controller;

import madstodolist.dto.UsuarioData;
import madstodolist.model.MensajeTicket;
import madstodolist.model.Ticket;
import madstodolist.model.Usuario;
import madstodolist.model.UsuarioPlan;
import madstodolist.service.TicketService;
import madstodolist.service.UsuarioPlanService;
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
import java.time.LocalDateTime;
import java.util.List;

@Controller
public class DashboardUsuarioController {
    @Autowired
    private UsuarioPlanService usuarioPlanService;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private TicketService ticketService;

    //email:joel@joel   contra:123
    @GetMapping("/usuarios/{id}/dashboard")
    public String dashboardUsuario(@PathVariable Long id, Model model, HttpSession session,
                          RedirectAttributes redirectAttributes) {
        Long sessionUserId = (Long) session.getAttribute("userId");

        if (sessionUserId == null || !sessionUserId.equals(id)) {
            return "redirect:/login";
        }

        // Obtener usuario
        UsuarioData usuario = usuarioService.findById(id);
        if (usuario == null) {
            return "redirect:/login"; // Redirige si el usuario no existe
        }

        List<Ticket> tickets = ticketService.findTicketsByUserId(id);
        if (tickets.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "No existen tickets.");
            return "redirect:/usuarios/" + sessionUserId + "/dashboard";
        }

        System.out.println(usuario);
        model.addAttribute("userId", sessionUserId);
        model.addAttribute("usuario", usuario); // Ahora Thymeleaf recibe directamente el objeto
        model.addAttribute("tickets", tickets); // Añadir la lista de tickets al modelo
        System.out.println(tickets);
        // Obtener la lista de planes del usuario
        List<UsuarioPlan> usuarioPlanes = usuarioPlanService.obtenerPlanesUsuario(id);
        model.addAttribute("usuarioPlanes", usuarioPlanes);
        System.out.println(usuarioPlanes);
        return "dashboardUsuario";
    }


     //VISTA USUARIO LISTAJE DE TICKETS
    @PostMapping("/usuarios/{id}/dashboard/addTicketRespuestaUsuario")
    public String addTicketRespuesta(@PathVariable Long id,
                                     @RequestParam("ticketId") Long ticketId,
                                     @RequestParam("content") String content,
                                     HttpSession session,
                                     RedirectAttributes redirectAttributes) {
        Long sessionUserId = (Long) session.getAttribute("userId");
        if (sessionUserId == null) {
            return "redirect:/login";
        }

        Ticket ticket = ticketService.findTicketById(ticketId);
        if (ticket == null) {
            redirectAttributes.addFlashAttribute("error", "El ticket no existe.");
            return "redirect:/panelAdmin/" + sessionUserId + "/listaTickets";
        }

        UsuarioData usuarioData = usuarioService.findById(sessionUserId);
        if (usuarioData == null) {
            redirectAttributes.addFlashAttribute("error", "El usuario no existe.");
            return "redirect:/panelAdmin/" + sessionUserId + "/listaTickets";
        }

        Usuario usuario = new Usuario();
        usuario.setId(usuarioData.getId());
        usuario.setNombre(usuarioData.getNombre());

        MensajeTicket mensajeTicket = new MensajeTicket();
        mensajeTicket.setMensaje(content);
        mensajeTicket.setFechaEnvio(LocalDateTime.now());
        mensajeTicket.setUsuario(usuario);
        mensajeTicket.setTicket(ticket);

        ticketService.saveMensajeTicket(mensajeTicket);

        redirectAttributes.addFlashAttribute("success", "Respuesta enviada correctamente.");
        return "redirect:/usuarios/" + sessionUserId + "/dashboard/";
    }
}
