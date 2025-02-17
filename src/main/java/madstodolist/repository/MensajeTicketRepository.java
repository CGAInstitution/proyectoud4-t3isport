package madstodolist.repository;

import madstodolist.model.MensajeTicket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface MensajeTicketRepository extends JpaRepository<MensajeTicket, Long> {
//    List<MensajeTicket> findByTicketId(Long ticketId);

    @Query("SELECT m FROM MensajeTicket m WHERE m.ticket.id = :ticketId ORDER BY m.fechaEnvio ASC")
    List<MensajeTicket> findByTicketId(Long ticketId);
}