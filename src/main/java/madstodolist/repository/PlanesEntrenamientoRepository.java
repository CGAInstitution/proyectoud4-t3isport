package madstodolist.repository;

import madstodolist.model.PlanesEntrenamiento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface PlanesEntrenamientoRepository extends JpaRepository<PlanesEntrenamiento, Long> {

    Optional<PlanesEntrenamiento> findById(Long id);
}
