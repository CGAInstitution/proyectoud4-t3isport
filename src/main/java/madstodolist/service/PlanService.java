package madstodolist.service;

import madstodolist.model.PlanesEntrenamiento;
import madstodolist.repository.PlanRepository;
import madstodolist.repository.PlanesEntrenamientoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PlanService {

    @Autowired
    private PlanRepository planRepository;

    @Autowired
    private PlanesEntrenamientoRepository planesEntrenamientoRepository;

    public PlanesEntrenamiento findById(Long id) {
        return planesEntrenamientoRepository.findById(id)
                .orElse(null);  // Retorna null si no encuentra el plan
    }


    public void guardarPlan(PlanesEntrenamiento plan) {
        planRepository.save(plan);
    }


}
