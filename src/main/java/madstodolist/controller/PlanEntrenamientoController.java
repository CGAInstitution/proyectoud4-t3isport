package madstodolist.controller;

import madstodolist.dto.RegistroData;
import madstodolist.model.PlanesEntrenamiento;
import madstodolist.service.PlanesEntrenamientoService;
import madstodolist.service.UsuarioPlanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class PlanEntrenamientoController {

    @Autowired
    private PlanesEntrenamientoService planesEntrenamientoService;

    @GetMapping("{id}/plan_entrenamiento")
    public String registroForm(@PathVariable Long id, Model model) {
        PlanesEntrenamiento planEntrenamiento = planesEntrenamientoService.obtenerPlan(id);
        model.addAttribute("planEntrenamiento", planEntrenamiento);
        return "planEntrenamiento";
    }

}
