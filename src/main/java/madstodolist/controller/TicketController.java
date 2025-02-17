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

        return "redirect:/index/usuarios/" + userId;
    }
}
