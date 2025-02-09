package madstodolist.controller;

import madstodolist.dto.UsuarioData;
import madstodolist.model.Ticket;
import madstodolist.model.Usuario;
import madstodolist.service.TicketService;
import madstodolist.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

//FALTA TERMINAR Y HACER EL TICKETSERVICE
@Controller
public class TicketController {

    @Autowired
    private TicketService ticketService;

    @Autowired
    private UsuarioService usuarioService;

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


}