package madstodolist.repository;

import madstodolist.model.UsuarioCuestionario;
import madstodolist.model.UsuarioCuestionarioId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsuarioCuestionarioRepository extends JpaRepository<UsuarioCuestionario, UsuarioCuestionarioId> {
}
