package madstodolist.service;

import madstodolist.model.PlanesEntrenamiento;
import madstodolist.repository.PlanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PlanService {

    @Autowired
    private PlanRepository planRepository;

    public PlanesEntrenamiento findById(Long id) {
        return planRepository.findById(id).orElse(null);
    }

    public void guardarPlan(PlanesEntrenamiento plan) {
        planRepository.save(plan);
    }
}
