package madstodolist.service;

import madstodolist.model.MensajeTicket;
import madstodolist.model.Ticket;
import madstodolist.repository.MensajeTicketRepository;
import madstodolist.repository.TicketRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TicketService {
    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private MensajeTicketRepository mensajeTicketRepository;

    public Ticket saveTicket(Ticket ticket) {
        return ticketRepository.save(ticket);
    }

    public MensajeTicket saveMensajeTicket(MensajeTicket mensajeTicket) {
        return mensajeTicketRepository.save(mensajeTicket);
    }

    public List<MensajeTicket> getMensajesByTicketId(Long ticketId) {
    return mensajeTicketRepository.findByTicketId(ticketId)
            .stream()
            .sorted(Comparator.comparing(MensajeTicket::getFechaEnvio))
            .collect(Collectors.toList());
}


    public List<MensajeTicket> findMensajesByTicketId(Long ticketId) {
        return mensajeTicketRepository.findByTicketId(ticketId);
    }

    public List<Ticket> findAllTickets() {
        return ticketRepository.findAll();
    }

    public Ticket findTicketById(Long ticketId) {
        return ticketRepository.findById(ticketId).orElse(null);
    }

    public List<Ticket> findTicketsByUserId(Long userId) {
        return ticketRepository.findByUsuario_Id(userId);
    }
}