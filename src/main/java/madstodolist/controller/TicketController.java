package madstodolist.controller;

import madstodolist.dto.UsuarioData;
import madstodolist.model.MensajeTicket;
import madstodolist.model.Ticket;
import madstodolist.model.Usuario;
import madstodolist.service.TicketService;
import madstodolist.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.List;

import java.util.Optional;

@Controller
public class TicketController {

    @Autowired
    private TicketService ticketService;

    @Autowired
    private UsuarioService usuarioService;

    @PostMapping("/tickets/{userId}")
    public String saveTicket(@PathVariable Long userId, @ModelAttribute Ticket ticket) {
        // Obtener el usuario con verificación de existencia
        UsuarioData usuarioData = usuarioService.findById(userId);

        if (usuarioData == null) {
            throw new RuntimeException("Usuario no encontrado");
        }


        // Crear la entidad Usuario usando UsuarioData
        Usuario usuario = new Usuario();
        usuario.setId(usuarioData.getId());
        usuario.setNombre(usuarioData.getNombre());

        // Asociar el ticket con el usuario
        ticket.setUsuario(usuario);
        ticketService.saveTicket(ticket);

        return "redirect:/usuarios/" + userId + "/dashboard";
    }

    //VISTA ADMINISTRADOR LISTAJE DE TICKETS
    @GetMapping("/panelAdmin/{id}/listaTickets")
    public String listaTickets(@PathVariable Long id, Model model, HttpSession session) {
        Long sessionUserId = (Long) session.getAttribute("userId");

        // Verificar si el usuario está autenticado y si coincide con el id del path
        if (sessionUserId == null || !sessionUserId.equals(id)) {
            return "redirect:/login";
        }

        // Recuperar todos los tickets desde la base de datos
        List<Ticket> tickets = ticketService.findAllTickets();

        // Ordenar los mensajes de cada ticket por fecha de envío
        for (Ticket ticket : tickets) {
            // Obtener y ordenar los mensajes por fecha de envío
            List<MensajeTicket> mensajesOrdenados = ticketService.getMensajesByTicketId(ticket.getId());

            // Establecemos la lista ordenada en el ticket
            ticket.setMensajes(mensajesOrdenados);
        }

        // Pasamos la lista de tickets al modelo para que sea accesible en la vista
        model.addAttribute("tickets", tickets);
        model.addAttribute("userId", id);

        return "listaTickets";  // Devolver el nombre de la vista
    }


    @PostMapping("/panelAdmin/{id}/addTicketRespuesta")
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
        return "redirect:/panelAdmin/" + sessionUserId + "/listaTickets";
    }

}