package madstodolist.repository;

import madstodolist.model.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {
    List<Ticket> findByUsuario_Id(Long usuarioId);

    // Métodos adicionales de consulta si es necesario
}