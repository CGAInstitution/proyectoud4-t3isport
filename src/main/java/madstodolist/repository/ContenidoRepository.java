package madstodolist.repository;

import madstodolist.model.Contenido;
import madstodolist.model.PlanesEntrenamiento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContenidoRepository extends JpaRepository<Contenido, PlanesEntrenamiento> {

    List<Contenido> findByPlan(PlanesEntrenamiento plan);
}