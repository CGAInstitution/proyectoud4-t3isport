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
    @GetMapping("/usuarios/{userId}/dashboard")
    public String userDashboard(@PathVariable Long userId, Model model, HttpSession session) {
        Long sessionUserId = (Long) session.getAttribute("userId");

        // Verificar si el usuario está autenticado y si coincide con el id del path
        if (sessionUserId == null || !sessionUserId.equals(userId)) {
            return "redirect:/login";
        }

        // Recuperar los tickets del usuario desde la base de datos
        List<Ticket> tickets = ticketService.findTicketsByUserId(userId);
        UsuarioData usuarioData = usuarioService.findById(sessionUserId);
        model.addAttribute("usuario", usuarioData);

        // Verificar si la lista de tickets está vacía
        if (tickets.isEmpty()) {
            model.addAttribute("message", "No tienes tickets asociados.");
        } else {
            // Ordenar los mensajes de cada ticket por fecha de envío
            for (Ticket ticket : tickets) {
                List<MensajeTicket> mensajesOrdenados = ticketService.getMensajesByTicketId(ticket.getId());
                ticket.setMensajes(mensajesOrdenados);
            }
            model.addAttribute("tickets", tickets);
        }

        model.addAttribute("userId", userId);
        return "dashboardUsuario";  // Devolver el nombre de la vista
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
