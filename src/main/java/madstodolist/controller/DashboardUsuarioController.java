package madstodolist.controller;

import madstodolist.dto.UsuarioData;
import madstodolist.model.Ticket;
import madstodolist.model.UsuarioPlan;
import madstodolist.service.TicketService;
import madstodolist.service.UsuarioPlanService;
import madstodolist.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
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
    public String userHub(@PathVariable Long id, Model model, HttpSession session,
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
            return "redirect:/panelAdmin/" + sessionUserId + "/listaTickets";
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


//    //VISTA USUARIO LISTAJE DE TICKETS

}
