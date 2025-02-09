package madstodolist.service;

import madstodolist.model.Ticket;
import madstodolist.repository.TicketRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TicketService {

    @Autowired
    private TicketRepository ticketRepository;

    public Ticket saveTicket(Ticket ticket) {
//        if (ticket.getId() == null) {
//            ticket.setId(generateTicketId());
//        }
        return ticketRepository.save(ticket);
    }
}