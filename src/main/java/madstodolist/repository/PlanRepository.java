package madstodolist.repository;

import madstodolist.model.PlanesEntrenamiento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlanRepository extends JpaRepository<PlanesEntrenamiento, Long> {
}
