package madstodolist.repository;

import madstodolist.model.MensajeTicket;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MensajeTicketRepository extends JpaRepository<MensajeTicket, Long> {
    List<MensajeTicket> findByTicketId(Long ticketId);
}