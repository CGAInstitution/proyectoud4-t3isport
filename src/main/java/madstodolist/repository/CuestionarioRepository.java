package madstodolist.repository;

import madstodolist.model.Cuestionario;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface CuestionarioRepository extends CrudRepository<Cuestionario, Integer> {
    Optional<Cuestionario> findByNombre(String nombre);

    Optional<Cuestionario> findById(Long id);
}
