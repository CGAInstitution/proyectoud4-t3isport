package madstodolist.repository;

import madstodolist.model.PlanesEntrenamiento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface PlanesEntrenamientoRepository extends JpaRepository<PlanesEntrenamiento, Long> {



    @Query("SELECT p FROM PlanesEntrenamiento p LEFT JOIN FETCH p.contenidos")
    List<PlanesEntrenamiento> findAllWithContenido();



    Optional<PlanesEntrenamiento> findById(Long id);
}
