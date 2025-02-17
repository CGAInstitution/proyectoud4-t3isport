package madstodolist.controller;

import madstodolist.dto.TicketData;
import madstodolist.dto.UsuarioData;
import madstodolist.model.MensajeTicket;
import madstodolist.model.Ticket;
import madstodolist.model.Usuario;
import madstodolist.repository.MensajeTicketRepository;
import madstodolist.repository.TicketRepository;
import madstodolist.repository.UsuarioRepository;
import madstodolist.service.TicketService;
import madstodolist.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

//FALTA TERMINAR Y HACER EL TICKETSERVICE
@Controller
public class TicketController {

    @Autowired
    private TicketService ticketService;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private MensajeTicketRepository mensajeTicketRepository;
//    @PostMapping("/tickets/{userId}")
//    public String saveTicket(@PathVariable Long userId, @ModelAttribute Ticket ticket) {
//        UsuarioData usuarioData = usuarioService.findById(userId);
//        Usuario usuario = new Usuario();
//        usuario.setId(usuarioData.getId());
//        usuario.setNombre(usuarioData.getNombre());
//        System.out.println("TICKET " + ticket);
//        ticket.setUsuario(usuario);
//        ticketService.saveTicket(ticket);
//        return "redirect:/index/usuarios/" + userId;
//    }

    @PostMapping("/tickets/{userId}")
    public String saveTicket(@PathVariable Long userId, @ModelAttribute Ticket ticket) {
        UsuarioData usuarioData = usuarioService.findById(userId);
        Usuario usuario = new Usuario();
        usuario.setId(usuarioData.getId());
        usuario.setNombre(usuarioData.getNombre());

        ticket.setUsuario(usuario);
        ticketService.saveTicket(ticket);

        return "redirect:/index/usuarios/" + userId;
    }

    //VISTA ADMINISTRADOR LISTAJE DE TICKETS
    @GetMapping("/panelAdmin/{id}/listaTickets")
    public String listaTickets(@PathVariable Long id, Model model, HttpSession session) {
        Long sessionUserId = (Long) session.getAttribute("userId");

        if (sessionUserId == null || !sessionUserId.equals(id)) {
            return "redirect:/login";
        }

        List<Ticket> tickets = ticketRepository.findAll();
        for (Ticket ticket : tickets) {
            List<MensajeTicket> mensajesOrdenados = ticket.getMensajes().stream()
                    .sorted(Comparator.comparing(MensajeTicket::getFechaEnvio))
                    .collect(Collectors.toList());
            ticket.setMensajes(mensajesOrdenados);
//            System.out.println("ss" + ticket.getMensajes());
        }
        model.addAttribute("tickets", tickets);
        model.addAttribute("userId", id);

        return "listaTickets";
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

        // Buscar el ticket por ID
        Optional<Ticket> optionalTicket = ticketRepository.findById(ticketId);
        if (!optionalTicket.isPresent()) {
            redirectAttributes.addFlashAttribute("error", "El ticket no existe.");
            return "redirect:/panelAdmin/" + sessionUserId + "/listaTickets";
        }

        Ticket ticket = optionalTicket.get();
        Optional<Usuario> optionalUsuario = usuarioRepository.findById(sessionUserId);

        if (!optionalUsuario.isPresent()) {
            redirectAttributes.addFlashAttribute("error", "El usuario no existe.");
            return "redirect:/panelAdmin/" + sessionUserId + "/listaTickets";
        }

        Usuario usuario = optionalUsuario.get();

        // Crear el nuevo mensaje
        MensajeTicket mensajeTicket = new MensajeTicket();
        mensajeTicket.setMensaje(content);
        mensajeTicket.setFechaEnvio(LocalDateTime.now());
        mensajeTicket.setUsuario(usuario);
        mensajeTicket.setTicket(ticket);

        // Guardar el mensaje en la base de datos
        mensajeTicketRepository.save(mensajeTicket);

        // Ordenar los mensajes por fecha de envío
        List<MensajeTicket> mensajesOrdenados = ticket.getMensajes().stream()
                .sorted(Comparator.comparing(MensajeTicket::getFechaEnvio))
                .collect(Collectors.toList());
        ticket.setMensajes(mensajesOrdenados);

        redirectAttributes.addFlashAttribute("success", "Respuesta enviada correctamente.");
        return "redirect:/panelAdmin/" + sessionUserId + "/listaTickets";
    }


}