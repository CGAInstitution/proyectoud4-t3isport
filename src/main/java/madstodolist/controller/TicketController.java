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
    private TicketRepository ticketRepository; //cambiarlo a service, repository no !

    @Autowired
    private UsuarioRepository usuarioRepository; //sobra porque ya esta en el service

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

        // Verificar si el usuario está autenticado y si coincide con el id del path
        if (sessionUserId == null || !sessionUserId.equals(id)) {
            return "redirect:/login";
        }

        // Recuperar todos los tickets desde la base de datos
        List<Ticket> tickets = ticketService.findAllTickets();

        // Ordenar los mensajes de cada ticket por fecha de envío
        for (Ticket ticket : tickets) {
            System.out.println(ticketService.getMensajesByTicketId(ticket.getId()));

            List<MensajeTicket> mensajesOrdenados = ticketService.getMensajesByTicketId(ticket.getId());

            // Establecemos la lista ordenada en el ticket
            ticket.setMensajes(mensajesOrdenados);

            // Imprimir para depuración (esto te ayudará a verificar que el orden se mantiene)
            // System.out.println("aa (ordenados): " + mensajesOrdenados);
            // System.out.println("ss (original): " + ticket.getMensajes());
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

        // Buscar el ticket por ID
        Optional<Ticket> optionalTicket = Optional.ofNullable(ticketService.findTicketById(ticketId));
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

        // Obtener y ordenar los mensajes por fecha de envío
//        List<MensajeTicket> mensajesOrdenados = ticketService.getMensajesByTicketId(ticketId).stream()
//                .sorted(Comparator.comparing(MensajeTicket::getFechaEnvio))
//                .collect(Collectors.toList());


        // Reemplazar los mensajes del ticket con la lista ordenada
//        ticket.setMensajes(mensajesOrdenados);

        // Añadir mensaje de éxito y redirigir
        redirectAttributes.addFlashAttribute("success", "Respuesta enviada correctamente.");
        return "redirect:/panelAdmin/" + sessionUserId + "/listaTickets";
    }


}