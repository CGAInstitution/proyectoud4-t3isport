package madstodolist.repository;

import madstodolist.model.CodigoDescuento;
import madstodolist.model.Contenido;
import madstodolist.model.PlanesEntrenamiento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CodigoDescuentoRepository extends JpaRepository<CodigoDescuento, Long> {

    Optional<CodigoDescuento> findByCodigo(String codigo);
}
