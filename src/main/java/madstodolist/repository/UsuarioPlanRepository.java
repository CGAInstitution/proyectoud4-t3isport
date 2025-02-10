package madstodolist.repository;

import madstodolist.model.UsuarioPlan;
import madstodolist.model.UsuarioPlanId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UsuarioPlanRepository extends JpaRepository<UsuarioPlan, UsuarioPlanId> {

    List<UsuarioPlan> findByUsuarioId(Long usuarioId);
}

