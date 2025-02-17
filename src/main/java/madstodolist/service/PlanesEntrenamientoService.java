package madstodolist.service;

import madstodolist.model.PlanesEntrenamiento;
import madstodolist.repository.PlanesEntrenamientoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class PlanesEntrenamientoService {
    @Autowired
    private PlanesEntrenamientoRepository planesEntrenamientoRepository;

    public PlanesEntrenamiento obtenerPlan(Long id) {
        return planesEntrenamientoRepository.findById(id).isPresent() ? planesEntrenamientoRepository.findById(id).get() : null;
    }


    public List<PlanesEntrenamiento> findAllWithContenido() {
        return planesEntrenamientoRepository.findAllWithContenido();
    }
}
